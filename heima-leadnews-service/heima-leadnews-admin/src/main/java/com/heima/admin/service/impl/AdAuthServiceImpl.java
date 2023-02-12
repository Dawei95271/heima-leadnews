package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.ApUserRealnameMapper;
import com.heima.admin.service.AdAuthService;
import com.heima.common.constants.AdminConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.admin.pojos.ApUserRealname;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 13:00
 */
@Service
@Transactional
@Slf4j
public class AdAuthServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements AdAuthService {


    /**
     * 审核失败
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult authFail(AuthDto dto) {

        updateStatus(dto, AdminConstants.AuthStatus.FAIL_PASS.getCode());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 审核通过
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult authPass(AuthDto dto) {

        updateStatus(dto, AdminConstants.AuthStatus.FAIL_PASS.getCode());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 更新审核状态
     * @param dto
     * @param type
     * @return
     */
    private void updateStatus(AuthDto dto, Short type) {

        if(dto.getId() == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUserRealname apUserRealname = new ApUserRealname();
        apUserRealname.setId(dto.getId());
        apUserRealname.setStatus(type);

        updateById(apUserRealname);

    }

    /**
     * 分页查询审核列表
     *  1. 可根据状态查询
     * @param dto
     * @return
     */
    @Override
    public ResponseResult getList(AuthDto dto) {

        dto.checkParam();

        QueryWrapper<ApUserRealname> qw = new QueryWrapper<>();
        if(dto.getStatus() != null){
            qw.eq("status", dto.getStatus());
        }

        Page<ApUserRealname> myPage = new Page<>(dto.getPage(), dto.getSize());
        page(myPage);

        return ResponseResult.okResult(myPage.getRecords());

    }






}
