package com.heima.admin.controler.v1;

import com.heima.admin.service.AdLoginService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/7 20:22
 */

@RestController
@RequestMapping("/login")
public class AdLoginController {


    @Autowired
    private AdLoginService adLoginService;


    /**
     * 用户登录
     * @param
     * @return
     */
    @PostMapping("/in")
    public ResponseResult loginIn(@RequestBody AdUserDto dto){
        return adLoginService.loginIn(dto);
    }






}
