package com.heima.admin.controler.v1;

import com.heima.admin.service.AdAuthService;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 12:57
 */
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AdAuthController {


    @Autowired
    private AdAuthService adAuthService;

    /**
     * 审核通过
     * @param dto
     * @return
     */
    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDto dto){
        return adAuthService.authPass(dto);
    }

    /**
     * 审核驳回
     * @param dto
     * @return
     */
    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDto dto){
        return adAuthService.authFail(dto);
    }

    /**
     * 分页查询
     *  1. 可根据状态查询
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult list(@RequestBody AuthDto dto){
        return adAuthService.getList(dto);
    }


}
