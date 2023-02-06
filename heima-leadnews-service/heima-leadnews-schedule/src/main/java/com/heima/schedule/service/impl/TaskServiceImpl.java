package com.heima.schedule.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/4 23:09
 */
@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;
    @Autowired
    private CacheService cacheService;


    /**
     * 数据库同步任务到缓存zset
     */
    @Override
    @Scheduled(cron = "* */5 * * * ?")
    @PostConstruct //servlet启动执行
    public void reloadData() {

        // 清空缓存
        clearCache();

        log.info("从数据库同步任务到缓存---begin---");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        // 从数据库获取未来5分钟任务
        List<Taskinfo> taskinfoList = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery()
                .lt(Taskinfo::getExecuteTime, calendar.getTime()));
        if(taskinfoList != null && taskinfoList.size() > 0){
            // 同步数据到缓存(zset)
            for (Taskinfo taskinfo : taskinfoList) {
                Task task = new Task();
                BeanUtil.copyProperties(taskinfo, task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                // 添加到缓存
                addTaskToCache(task);
            }
        }
    }

    /**
     * 清空缓存中的任务，防止任务重复执行
     */
    private void clearCache() {
        // 删除缓存中未来数据集合和当前消费者队列的所有key
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        cacheService.delete(futureKeys);
        cacheService.delete(topicKeys);

    }

    /**
     * 将未来5分钟数据刷新到消费队列list中
     */
    @Override
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh() {

        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);
        if(StrUtil.isNotBlank(token)){
            // 加锁成功，则同步数据
            log.info("未来数据定时刷新---begin---");

            // 获取未来所有数据的key集合
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) {
                // future_11_2
                // 遍历，获取所有任务，添加到相应的消费者队列中
                String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];
                // 获取需要消费的任务
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                if(! tasks.isEmpty()){
                    // task从zset中删除，同时添加到消费队列
                    cacheService.refreshWithPipeline(futureKey, topicKey, tasks);
                    log.info("成功的将" + futureKey + "下的当前需要执行的任务数据刷新到" + topicKey + "下");
                }
            }
            log.info("未来数据定时刷新---end---");

        }

    }

    /**
     * 按照类型和优先级拉取任务
     * @param type     类型
     * @param priority 优先级
     * @return task
     */
    @Override
    public Task poll(int type, int priority) {

        Task task = null;

        try {
            // 获取缓存中task
            String key = type + "_" + priority;
            String task_json = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
            if(StrUtil.isNotBlank(task_json)){
                task = JSON.parseObject(task_json, Task.class);
                // 更新数据库
                // 删除task, 更新日志
                updateDb(task.getTaskId(), ScheduleConstants.EXECUTED);
            }
        } catch (Exception e) {

            log.error("poll task exception, taskId = {}", task.getTaskId());
            e.printStackTrace();
        }

        return task;
    }

    /**
     * 取消任务
     * @param taskId 任务id
     * @return 取消结果
     */
    @Override
    public boolean cancelTask(long taskId) {

        boolean flag = false;
        // 删除任务，更新日志
        Task task = updateDb(taskId, ScheduleConstants.CANCELLED);
        // 删除缓存中的任务
        if(task != null){
            removeTaskFromCache(task);
            flag = true;
        }
        return flag;
    }

    /**
     * 删除缓存任务
     * @param task
     */
    private void removeTaskFromCache(Task task) {

        String key = task.getTaskType() + "_" + task.getPriority();

        if(task.getExecuteTime()  <= System.currentTimeMillis()){
            // 消费队列中
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));

        }

    }

    /**
     * 更新数据库中任务
     * @param taskId
     * @param type 任务状态
     * @return
     */
    private Task updateDb(long taskId, int type) {

        Task task = null;

        try {
            // 删除任务
            taskinfoMapper.deleteById(taskId);
            // 更新日志
            TaskinfoLogs log = taskinfoLogsMapper.selectById(taskId);
            log.setStatus(type);
            taskinfoLogsMapper.updateById(log);

            task = new Task();
            BeanUtil.copyProperties(log, task);
            task.setExecuteTime(log.getExecuteTime().getTime());
        } catch (Exception e) {
            log.error("task cancel exception, taskId = {}", taskId);
            e.printStackTrace();
        }

        return task;

    }

    /**
     * 添加任务
     * @param task
     * @return 任务id
     */
    @Override
    public long addTask(Task task) {

        // 1. 添加任务到db
        boolean success = addTaskToDB(task);

        if(success){
            // 2. 添加任务到redis
            addTaskToCache(task);
        }

        return task.getTaskId();

    }

    /**
     * 添加任务到缓存
     * @param task
     */
    private void addTaskToCache(Task task) {

        // 设置队列和zset通用key
        String key = task.getTaskType() + "_" + task.getPriority();

        // 获取5分钟后的时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();

        if(task.getExecuteTime() <= System.currentTimeMillis()){
            // 2.1 任务时间小于当前时间，则添加到消费队列，list
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if(task.getExecuteTime() <= nextScheduleTime){
            // 2.2 任务时间在未来5分钟以内，则添加到zset中
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
        }

    }

    /**
     * 添加任务到数据库
     * @param task
     * @return
     */
    private boolean addTaskToDB(Task task) {

        boolean flag = true;

        try {
            // 保存任务
            Taskinfo taskinfo = new Taskinfo();
            BeanUtil.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));

            taskinfoMapper.insert(taskinfo);

            // 设置taskId
            task.setTaskId(taskinfo.getTaskId());

            // 保存任务日志
            TaskinfoLogs log = new TaskinfoLogs();
            BeanUtil.copyProperties(task, log);
            log.setExecuteTime(new Date(task.getExecuteTime()));
            log.setVersion(1);
            log.setStatus(ScheduleConstants.SCHEDULED); // 初始化状态

            taskinfoLogsMapper.insert(log);
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;

    }


}
