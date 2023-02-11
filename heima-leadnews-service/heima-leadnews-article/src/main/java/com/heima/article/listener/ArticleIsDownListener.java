package com.heima.article.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleConfigService;
import com.heima.common.constants.WmNewsMessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/5 14:50
 */
@Component
@Slf4j
public class ArticleIsDownListener {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ApArticleConfigService apArticleConfigService;


    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void onMessage(String message){
        if(StrUtil.isNotBlank(message)){

            Map map = JSON.parseObject(message, Map.class);
            // 更新文章配置
            apArticleConfigService.updateByMap(map);

            log.info("article端文章配置修改，articleId = {}", map.get("articleId"));

        }
    }




}
