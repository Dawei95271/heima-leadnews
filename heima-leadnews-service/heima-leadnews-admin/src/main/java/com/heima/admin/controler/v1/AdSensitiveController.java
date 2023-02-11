package com.heima.admin.controler.v1;

import com.heima.admin.service.AdSensitiveService;
import com.heima.model.admin.dtos.AdSensitive;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 8:35
 */
@RestController
@RequestMapping("/api/v1/sensitive")
public class AdSensitiveController {

    @Autowired
    private AdSensitiveService adSensitiveService;

    @PostMapping("/update")
    public ResponseResult update(@RequestBody AdSensitive adSensitive){
        return adSensitiveService.updateSensitive(adSensitive);
    }

    /**
     * 保存敏感词
     * @param adSensitive
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody AdSensitive adSensitive){
        return adSensitiveService.saveSensitive(adSensitive);
    }

    /**
     * 查询敏感词列表
     * @return
     */
    @PostMapping("/list")
    public ResponseResult getAllList(@RequestBody SensitiveDto dto){
        return adSensitiveService.getAllList(dto);
    }

    /**
     * 敏感词删除
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public ResponseResult delSensitive(@PathVariable("id") Integer id){
        return adSensitiveService.delSensitive(id);
    }
}
