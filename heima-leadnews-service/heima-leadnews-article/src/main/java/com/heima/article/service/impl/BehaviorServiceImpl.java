package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.j2objc.annotations.AutoreleasePool;
import com.heima.article.mapper.ApCollectionMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.BehaviorService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojos.ApCollection;
import com.heima.model.article.pojos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.ApUserThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/10 7:53
 */
@Service
@Transactional
@Slf4j
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ApArticleService articleService;

    @Autowired
    private ApCollectionMapper apCollectionMapper;

    /**
     * 文章收藏
     *  1. 存入数据据库
     *  2. 添加缓存
     * @param dto
     * @return
     */
    @Override
    public ResponseResult collectionBehavior(CollectionBehaviorDto dto) {

        ApUser user = ApUserThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        if(dto.getArticleId() == null || dto.getOperation() == null || dto.getType() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        String redisKey = BehaviorConstants.BEHAVIOR_PREFIX + user.getId();
        String hashKey = BehaviorConstants.COLLECTION;

        if(dto.getOperation().equals(ArticleConstants.ARTICLE_COLLECTION)){
            // 收藏
            // 数据库
            ApCollection apCollection = new ApCollection();
            apCollection.setArticleId(dto.getArticleId());
            apCollection.setUserId(user.getId());
            apCollection.setType(ArticleConstants.ARTICLE_COLLECTION);
            apCollection.setCreated_time(new Date());
            apCollection.setPublished_time(dto.getPublishedTime());

            apCollectionMapper.insert(apCollection);

            // 缓存
            if(! cacheService.hExists(redisKey, hashKey)){
                Map<String, Object> map = new HashMap<>();
                List<String> list = new ArrayList<>();
                map.put("colletion", "1");
                cacheService.hPut(redisKey, hashKey, JSON.toJSONString(map));
            } else {
                Object o = cacheService.hGet(redisKey, hashKey);
                Map<String, Object> map = JSON.parseObject((String) o, Map.class);
                map.put("colletion", 1);
                cacheService.hPut(redisKey, hashKey, JSON.toJSONString(map));

            }

        } else{
            // 取消收藏
            apCollectionMapper.delete(Wrappers.<ApCollection>lambdaQuery()
                    .eq(ApCollection::getUserId, user.getId())
                    .eq(ApCollection::getArticleId, dto.getArticleId()));

            // 删除缓存
            Object o = cacheService.hGet(redisKey, hashKey);
            Map<String, Object> map = JSON.parseObject((String) o, Map.class);
            map.put("collection", 0);
            cacheService.hPut(redisKey, hashKey, JSON.toJSONString(map));

        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }



}
