package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 18:06
 */
public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 取消收藏素材
     * @param id
     * @return
     */
    ResponseResult cancelCollect(@PathVariable Integer id);


    /**
     * 收藏素材
     * @param id
     * @return
     */
    ResponseResult collect(@PathVariable Integer id);

    /**
     * 删除素材图片
     * @param id
     * @return
     */
    ResponseResult delPicTure(@PathVariable Integer id);

    /**
     * 图片上传
     * @param file
     * @return
     */
    ResponseResult uploadPicture(@RequestParam("multipartFile") MultipartFile file);

    /**
     * 分页查询素材
     * @param dto
     * @return
     */
    ResponseResult findList(WmMaterialDto dto);
}
