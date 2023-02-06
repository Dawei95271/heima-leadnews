package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

public interface TaskService {

    /**
     * 添加任务
     * @param task 任务
     * @return 任务id
     */
    long addTask(Task task);

    /**
     * 取消任务
     * @param taskId 任务id
     * @return  取消结果
     */
    boolean cancelTask(long taskId);

    /**
     * 按照类型和优先级拉取任务
     * @param type 类型
     * @param priority 优先级
     * @return task
     */
    Task poll(int type, int priority);

    /**
     * 将未来5分钟数据刷新到消费队列list中
     */
    void refresh();

    /**
     * 数据库同步任务到缓存zset
     */
    void reloadData();
}
