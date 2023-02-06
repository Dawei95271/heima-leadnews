package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 18:03
 */
@RestController
@RequestMapping("api/v1/material")
@Slf4j
public class WmMaterialController {


    @Autowired
    private WmMaterialService wmMaterialService;


    /**
     * 取消收藏蔬菜
     * @param id
     * @return
     */
    @GetMapping("cancel_collect/{id}")
    public ResponseResult cancelCollect(@PathVariable Integer id){
        return wmMaterialService.cancelCollect(id);
    }

    /**
     * 收藏蔬菜
     * @param id
     * @return
     */
    @GetMapping("collect/{id}")
    public ResponseResult collect(@PathVariable Integer id){
        return wmMaterialService.collect(id);
    }

    /**
     * 删除素材图片
     * @param id
     * @return
     */
    @GetMapping("del_picture/{id}")
    public ResponseResult delPicTure(@PathVariable Integer id){
        return wmMaterialService.delPicTure(id);
    }

    /**
     * 分页查询素材
     * @param dto
     * @return
     */
    @PostMapping("list")
    public ResponseResult findList(@RequestBody WmMaterialDto dto){
        return wmMaterialService.findList(dto);
    }

    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("upload_picture")
    public ResponseResult uploadPicture(@RequestParam("multipartFile") MultipartFile file){
        return wmMaterialService.uploadPicture(file);
    }


}
