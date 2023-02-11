package com.heima.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdLoginService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/7 20:27
 */
@Service
@Transactional
@Slf4j
public class AdLoginServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdLoginService {


    /**
     * 用户登录
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loginIn(AdUserDto dto) {

        // 1. 参数检查
        if (dto == null || StrUtil.isBlank(dto.getName()) || StrUtil.isBlank(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 查询数据库
        AdUser user = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, dto.getName()));
        if(user == null){
            // 2.1 用户不存在
            return ResponseResult.errorResult(HttpStatus.NOT_FOUND.value(), "用户不存在");
        }

        // 2.2 禁止登录
        if(user.getStatus() != 9){
            return ResponseResult.errorResult(AppHttpCodeEnum.FORBIDDEN);
        }

        // 2.3 比较密码
        String pwdDb = DigestUtils.md5DigestAsHex((dto.getPassword() + user.getSalt()).getBytes());

        // 2.4 Unauthorized
        if(! user.getPassword().equals(pwdDb)){
            return ResponseResult.errorResult(HttpStatus.UNAUTHORIZED.value(), "密码错误");
        }

        // 2.5 密码争取，返回token
        String token = AppJwtUtil.getToken(user.getId().longValue());

        // 隐私信息设null
        user.setPassword(null);
        user.setSalt(null);

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("token", token);
        retMap.put("user", user);
        return ResponseResult.okResult(retMap);

    }


}
