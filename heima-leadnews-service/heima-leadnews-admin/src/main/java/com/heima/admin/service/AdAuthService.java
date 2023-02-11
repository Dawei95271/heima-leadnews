package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.admin.pojos.ApUserRealname;
import com.heima.model.common.dtos.ResponseResult;

public interface AdAuthService extends IService<ApUserRealname> {

    /**
     * 分页查询审核列表
     *  1. 可根据状态查询
     * @param dto
     * @return
     */
    ResponseResult getList(AuthDto dto);

    /**
     * 审核失败
     * @param dto
     * @return
     */
    ResponseResult authFail(AuthDto dto);

    /**
     * 审核通过
     * @param dto
     * @return
     */
    ResponseResult authPass(AuthDto dto);
}
