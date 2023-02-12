package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.article.IArticleClient;
import com.heima.behavior.service.BehaviorService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.ApUserThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 15:56
 */
@Service
@Transactional
@Slf4j
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private CacheService cacheService;


    /**
     * 不喜欢  取消不喜欢
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult unLikesBehavior(UnLikesBehaviorDto dto) {

        ApUser user = ApUserThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        if(dto.getArticleId() == null || dto.getType() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询文章所属频道
        ResponseResult responseResult = articleClient.getOne(dto);
        if(responseResult.getCode().equals(200)){
            String ChannelIdJson = (String) responseResult.getData();
            Long channelId = JSON.parseObject(ChannelIdJson, Long.class);

            // 添加缓存
            String redisKey = BehaviorConstants.BEHAVIOR_PREFIX + user.getId();
            String hashKey = BehaviorConstants.DISLIKE;

            Map<String, Object> map = null;
            // 保存喜欢
            if(dto.getType() == 0){
                if(! cacheService.hExists(redisKey, hashKey)){
                    // 不存在 hashKey，则创建
                    map = new HashMap<>();
                    List<Long> list = new ArrayList<>();
                    list.add(channelId);
                    map.put("channel", list);

                } else{
                    Object o = cacheService.hGet(redisKey, hashKey);
                    map = JSON.parseObject((String) o, Map.class);
                    List<Long> list = (List) map.get("channel");
                    if(list == null) {
                        // 不喜欢频道为创建
                        list = new ArrayList<>();
                    }

                    list.add(channelId);

                }
            } else {
                // 取消不喜欢
                Object o = cacheService.hGet(redisKey, hashKey);
                map = JSON.parseObject((String) o, Map.class);
                List<Long> list = (List) map.get("channel");
                list = list.stream()
                        .filter(s -> s != channelId)
                        .collect(Collectors.toList());

                map.put("channel", list);
            }


            cacheService.hPut(redisKey, hashKey, JSON.toJSONString(map));

            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        return responseResult;

    }

    /**
     * 用户行为---阅读
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult readBehavior(ReadBehaviorDto dto) {

        ApUser apUser = ApUserThreadLocalUtil.getUser();

        if(apUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        if(dto.getArticleId() == null || dto.getCount() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 1. 文章阅读量 + 1
        ResponseResult responseResult = articleClient.updateArticle(dto);
        if(responseResult.getCode().equals(200)){
            // 2. 该文章缓存 + 1

            String redisKey = BehaviorConstants.BEHAVIOR_PREFIX + apUser.getId();
            String hashKey = dto.getArticleId() + "";

            Map<String, Object> map = null;
            if(! cacheService.hExists(redisKey, hashKey)){
                //缓存中不存在，则添加
              map = new HashMap<>();
              map.put("read", dto.getCount());
            } else {
                String cache = (String) cacheService.hGet(redisKey, hashKey);
                map = JSON.parseObject(cache, Map.class);
                // 有则更新，无则添加
                Integer count = map.get("read") == null ?  dto.getCount() : (Integer) map.get("read") + dto.getCount();
                map.put("read",count);
            }
            cacheService.hPut(redisKey, hashKey, JSON.toJSONString(map));

            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        return responseResult;

    }

    /**
     * 用户行为---点赞
     *  1. 更新文章点赞数量
     *  2. 缓存到redis
     * @param dto
     * @return
     */
    @Override
    public ResponseResult likesBehavior(LikesBehaviorDto dto) {

        ApUser user = ApUserThreadLocalUtil.getUser();

        if(dto.getArticleId() == null || dto.getType() == null || dto.getOperation() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 更新文章点赞数量
        ResponseResult responseResult = articleClient.saveBehavior(dto);
        // 缓存到redis
        String redisKey = BehaviorConstants.BEHAVIOR_PREFIX + user.getId();
        String hashKey = dto.getArticleId() + "";

        Object o = cacheService.hGet(redisKey, hashKey);

        List<String> list = null;
        if(o == null){
            // 新添加
            list = new ArrayList<>();
        } else{
            // 已有
            list = JSON.parseArray((String) o, String.class);
        }

        list.add("like");
        cacheService.hPut(redisKey, dto.getArticleId() + "", JSON.toJSONString(list));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }




}
