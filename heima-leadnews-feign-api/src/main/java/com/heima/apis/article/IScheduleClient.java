package com.heima.apis.article;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.Task;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-schedule")
public interface IScheduleClient {

    /**
     * 添加任务
     * @param task 任务
     * @return ResponseResult
     */
    @PostMapping("/api/v1/task/add")
    ResponseResult addTask(@RequestBody Task task);

    /**
     * 取消任务
     * @param taskId 任务id
     * @return  ResponseResult
     */
    @GetMapping("/api/v1/task/cancel/{taskId}")
    ResponseResult cancelTask(@PathVariable("taskId") long taskId);

    /**
     * 按照类型和优先级拉取任务
     * @param type 类型
     * @param priority 优先级
     * @return ResponseResult
     */
    @GetMapping("/api/v1/task/poll/{type}/{priority}")
    ResponseResult poll(@PathVariable("type") int type, @PathVariable("priority") int priority);


}
