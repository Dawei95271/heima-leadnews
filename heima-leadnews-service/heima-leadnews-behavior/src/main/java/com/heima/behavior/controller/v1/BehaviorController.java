package com.heima.behavior.controller.v1;

import com.heima.behavior.service.BehaviorService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 15:52
 */
@RestController
@RequestMapping("/api/v1/")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    /**
     * 不喜欢
     * @param dto
     * @return
     */
    @PostMapping("/un_likes_behavior")
    public ResponseResult unLikesBehavior(@RequestBody UnLikesBehaviorDto dto){
        return behaviorService.unLikesBehavior(dto);
    }

    /**
     * 阅读
     * @param dto
     * @return
     */
    @PostMapping("/read_behavior")
    public ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto){
        return behaviorService.readBehavior(dto);
    }

    /**
     * 点赞
     * @param dto
     * @return
     */
    @PostMapping("/likes_behavior")
    public ResponseResult likesBehavior(@RequestBody LikesBehaviorDto dto){
        return behaviorService.likesBehavior(dto);
    }

}
