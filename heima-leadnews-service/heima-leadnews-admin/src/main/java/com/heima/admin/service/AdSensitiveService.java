package com.heima.admin.service;

import com.heima.model.admin.dtos.AdSensitive;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.models.auth.In;

public interface AdSensitiveService {

    /**
     * 敏感词删除
     * @param id
     * @return
     */
    ResponseResult delSensitive(Integer id);

    /**
     * 查询敏感词列表
     * @return
     */
    ResponseResult getAllList(SensitiveDto dto);

    /**
     * 保存敏感词
     * @param adSensitive
     * @return
     */
    ResponseResult saveSensitive(AdSensitive adSensitive);

    /**
     * 更新敏感词
     * @param adSensitive
     * @return
     */
    ResponseResult updateSensitive(AdSensitive adSensitive);
}
