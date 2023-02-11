package com.heima.article.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.heima.apis.article.IWeMediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.HotArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cache;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 16:45
 */
@Service
@Transactional
@Slf4j
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private IWeMediaClient weMediaClient;

    @Autowired
    private CacheService cacheService;


    /**
     * 计算热点文章，存入缓存
     */
    @Override
    public void computeHotArticle() {

        Date dayParam = DateTime.now().minusDays(5).toDate();
        // 查询5天之内所有文章
        List<ApArticle> allArticleList = apArticleMapper.findArticleListByLast5Days(dayParam);

        // 2. 计算文章分值
        List<HotArticleDto> hotArticleList = computeScore(allArticleList);
        // 3. 为每个频道添加缓存---30条分值高的文章
        cacheHotArticle(hotArticleList);

    }

    /**
     * 为每个频道添加缓存---30条分值高的文章
     * @param hotArticleList
     */
    private void cacheHotArticle(List<HotArticleDto> hotArticleList) {

        // 1. 获取频道分类
        ResponseResult responseResult = weMediaClient.getAllChannels();
        if(responseResult.getCode().equals(200)){
            String channelJson = JSON.toJSONString(responseResult.getData());
            List<WmChannel> channels = JSON.parseArray(channelJson, WmChannel.class);

            // 2. 获取相应频道的文章集合
            if(CollUtil.isNotEmpty(channels)){
                for (WmChannel channel : channels) {
                    List<HotArticleDto> oneChanneArticlelList = hotArticleList.stream()
                            .filter(x -> x.getChannelId().equals(channel.getId()))
                            .collect(Collectors.toList());
                    // 3. 排序，并添加到缓存
                    sortAndCache(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + channel.getName(), oneChanneArticlelList);

                }
            }

            // 为推荐频道添加缓存
            sortAndCache(ArticleConstants.DEFAULT_TAG, hotArticleList);

        }


    }

    /**
     * 排序，并添加缓存
     * @param key
     * @param oneChanneArticlelList
     */
    private void sortAndCache(String key, List<HotArticleDto> oneChanneArticlelList) {
        oneChanneArticlelList = oneChanneArticlelList.stream()
                .sorted(Comparator.comparing(HotArticleDto::getScore).reversed())
                .collect(Collectors.toList());
        if(oneChanneArticlelList.size() > 30){
            oneChanneArticlelList = oneChanneArticlelList.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(oneChanneArticlelList));
    }

    /**
     * 计算文章的分值
     * @param allArticleList
     * @return
     */
    private List<HotArticleDto> computeScore(List<ApArticle> allArticleList) {

        List<HotArticleDto> list = new ArrayList<>();

        if(allArticleList != null && allArticleList.size() > 0){
            for (ApArticle apArticle : allArticleList) {
                HotArticleDto vo = new HotArticleDto();
                BeanUtils.copyProperties(apArticle, vo);

                // 计算文章评分
                Integer score = computeSingleArticleScore(apArticle);
                vo.setScore(score);
                list.add(vo);
            }
        }
        return list;

    }

    /**
     * 计算单个文章的分值
     * @param apArticle
     * @return
     */
    private Integer computeSingleArticleScore(ApArticle apArticle) {

        // 相应的数量 * 权总
        int score = 0;
        if(apArticle.getViews() != null){
            score += apArticle.getViews() * ArticleConstants.HOT_ARTICLE_VIEW_WEIGHT;
        }
        if (apArticle.getLikes() != null){
            score += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if(apArticle.getComment() != null){
            score += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if(apArticle.getCollection() != null){
            score += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return score;

    }


}
