package com.heima.admin.controler.v1;

import com.heima.admin.service.AdNewsAuthService;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 13:44
 */
@RestController
@RequestMapping("/api/v1/news/")
@Slf4j
public class AdminNewsController {


    @Autowired
    private AdNewsAuthService adNewsAuthService;

    /**
     * 审核成功
     * @param dto
     * @return
     */
    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthDto dto){
        return adNewsAuthService.authPass(dto);
    }

    /**
     * 审核失败
     * @param dto
     * @return
     */
    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthDto dto){
        return adNewsAuthService.authFail(dto);
    }

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    @GetMapping("/one_vo/{id}")
    public ResponseResult getOne(@PathVariable("id") Integer id){
        return adNewsAuthService.getOneVo(id);
    }

    /**
     * 分页查询文章审核列表
     * @param dto
     * @return
     */
    @PostMapping("/list_vo")
    public ResponseResult list(@RequestBody NewsAuthDto dto){
        return adNewsAuthService.getList(dto);
    }


}
