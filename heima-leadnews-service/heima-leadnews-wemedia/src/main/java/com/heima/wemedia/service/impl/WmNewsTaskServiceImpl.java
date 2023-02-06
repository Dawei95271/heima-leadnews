package com.heima.wemedia.service.impl;

import cn.hutool.log.Log;
import com.heima.apis.article.IScheduleClient;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.enums.TaskTypeEnum;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.serialize.ProtostuffUtil;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/5 1:03
 */
@Service
@Slf4j
@Transactional
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Autowired
    private IScheduleClient scheduleClient;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 消费任务
     */
    @Override
    @Scheduled(fixedRate = 1000)
    @SneakyThrows
    public void scanNewsByTask() {

        log.info("文章审核---消费任务---begin---");

        ResponseResult responseResult = scheduleClient.poll(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if(responseResult.getCode().equals(200) && responseResult.getData() != null){
            Task task = (Task) responseResult.getData();
            byte[] parameters = task.getParameters();
            // 反序列化
            WmNews wmNews = ProtostuffUtil.deserialize(parameters, WmNews.class);
            // 自动审核
            wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        }

        log.info("文章审核---消费任务---end---");


    }

    /**
     * 添加任务到延迟队列
     * @param id          文章id
     * @param publishTime 发布时间
     */
    @Override
    public void addNewsToTask(Integer id, Date publishTime) {

        log.info("添加任务到延迟队列---begin---");

        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());

        // 设置执行参数
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));

        scheduleClient.addTask(task);
        log.info("添加任务到延迟任务中---end---");

    }



}
