package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.user.service.ApUserBehaviorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 16:52
 */
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class ApUserBehaviorController {

    @Autowired
    private ApUserBehaviorService apUserBehaviorService;

    /**
     * 用户行为---关注
     * @param dto
     * @return
     */
    @PostMapping("/user_follow")
    public ResponseResult userFollow(@RequestBody UserRelationDto dto){
        return apUserBehaviorService.userFollow(dto);
    }

}
