package com.heima.wemedia.service;

import java.util.Date;

public interface WmNewsTaskService {

    /**
     * 添加任务到延迟队列
     * @param id 文章id
     * @param publishTime 发布时间
     */
    void addNewsToTask(Integer id, Date publishTime);

    /**
     * 消费任务
     */
    void scanNewsByTask();
}
