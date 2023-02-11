package com.heima.admin.service.impl;

import com.heima.admin.service.AdLoginService;
import com.heima.admin.service.AdSensitiveService;
import com.heima.apis.article.IWeMediaClient;
import com.heima.model.admin.dtos.AdSensitive;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 8:39
 */
@Service
@Transactional
@Slf4j
public class AdSensitiveServiceImpl implements AdSensitiveService {

    @Autowired
    private IWeMediaClient weMediaClient;

    /**
     * 更新敏感词
     *
     * @param adSensitive
     * @return
     */
    @Override
    public ResponseResult updateSensitive(AdSensitive adSensitive) {

        // 1. 参数检查
        if(adSensitive.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        return weMediaClient.updateSensitive(adSensitive);
    }

    /**
     * 保存敏感词
     *
     * @param adSensitive
     * @return
     */
    @Override
    public ResponseResult saveSensitive(AdSensitive adSensitive) {
        if(adSensitive.getSensitives() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ResponseResult responseResult = weMediaClient.saveSensitive(adSensitive);

        return responseResult;
    }

    /**
     * 查询敏感词列表
     * @return
     */
    @Override
    public ResponseResult getAllList(SensitiveDto dto) {
        ResponseResult responseResult = weMediaClient.getSensitiveList(dto);
        if(responseResult.getCode().equals(200)){
            return ResponseResult.okResult(responseResult.getData());
        }

        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    /**
     * 敏感词删除
     * @param id
     * @return
     */
    @Override
    public ResponseResult delSensitive(Integer id) {

        // 1. 参数检查
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 2. 查询数据库
        ResponseResult result = weMediaClient.delSensitive(id);
        if(result.getCode().equals(200)){
            if(result.getData().equals(1)){
                return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
            }
        }
        return ResponseResult.errorResult(HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT.getReasonPhrase());
    }





}
