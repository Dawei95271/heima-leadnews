package com.heima.article.job;

import com.heima.article.service.HotArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 17:43
 */
@Component
@Slf4j
public class ComputeHotArticleJob {


    @Autowired
    private HotArticleService hotArticleService;

    @XxlJob("computeHotArticleJob")
    public void handle(){
        log.info("热文章分值计算任务调度开始...");
        hotArticleService.computeHotArticle();
        log.info("热文章分值计算任务调度结束...");
    }


}
