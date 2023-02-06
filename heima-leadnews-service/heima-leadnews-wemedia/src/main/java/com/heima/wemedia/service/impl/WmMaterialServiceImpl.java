package com.heima.wemedia.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;

import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 18:07
 */

@Service
@Slf4j
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;


    /**
     * 取消收藏素材
     * @param id
     * @return
     */
    @Override
    public ResponseResult cancelCollect(Integer id) {
        // 0. 参数检查
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 数据存在，则修改收藏字段
        boolean update = update(Wrappers.<WmMaterial>lambdaUpdate().eq(WmMaterial::getId, id).set(WmMaterial::getIsCollection, WmMaterial.Type.noCollect.getCode()));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 收藏素材
     * @param id
     * @return
     */
    @Override
    public ResponseResult collect(Integer id) {
        // 0. 参数检查
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 数据存在，则修改收藏字段
        boolean update = update(Wrappers.<WmMaterial>lambdaUpdate().eq(WmMaterial::getId, id).set(WmMaterial::getIsCollection, WmMaterial.Type.collect.getCode()));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 删除素材图片
     * @param id
     * @return
     */
    @Override
    public ResponseResult delPicTure(Integer id) {
        // 0. 参数检查
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmMaterial material = getById(id);
        if(material == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 1. 查询素材是否被使用，
        List<WmNewsMaterial> list = wmNewsMaterialMapper.selectList(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getMaterialId, id));

        // 2. 是，不能删除
        if(CollUtil.isNotEmpty(list)){
            return ResponseResult.errorResult(501, "删除文件失败" );
        }
        // 2.1 否， 可以删除
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    /**
     * 分页查询素材
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmMaterialDto dto) {
        // 1. 参数检查
        dto.checkParam();
        // 2. 分页查询，设置参数
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmMaterial> qw = new LambdaQueryWrapper<>();
        // 是否收藏
        if(dto.getIsCollection() != null && dto.getIsCollection() == 1){
            qw.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }
        // 按用户查询
        qw.eq(WmMaterial::getUserId, WmThreadLocalUtil.getUser().getId());
        // 时间排序
        qw.orderByDesc(WmMaterial::getCreatedTime);


        page(page, qw);
        // 3. 返回结果
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 图片上传
     * @param file
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile file) {
        // 1. 参数检查
        if(file == null || file.getSize() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 图片上传
        String filename = UUID.randomUUID().toString().replace("-", "");
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", filename + extName, file.getInputStream());
            log.info("上传到minIO中，fileId:{}", fileId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WmMaterialServiceImpl---文件上传失败");
        }
        // 3. 保存到数据库
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUrl(fileId);
        wmMaterial.setType((short) 0);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);
        // 返回结果
        return ResponseResult.okResult(wmMaterial);
    }


}
