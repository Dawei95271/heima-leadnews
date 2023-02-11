package com.heima.admin.service.impl;

import com.heima.admin.service.AdNewsAuthService;
import com.heima.apis.article.IWeMediaClient;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 13:49
 */
@Service
@Transactional
@Slf4j
public class AdNewsAuthServiceImpl implements AdNewsAuthService {

    @Autowired
    private IWeMediaClient weMediaClient;

    /**
     * 审核失败
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult authFail(NewsAuthDto dto) {
        if(dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        return weMediaClient.authNewsFail(dto);
    }

    /**
     * 审核成功
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult authPass(NewsAuthDto dto) {

        if(dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 修改状态 创建app端文章信息
        ResponseResult responseResult = weMediaClient.authNewsPass(dto);

        return responseResult;

    }

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult getOneVo(Integer id) {
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        return weMediaClient.getOneNews(id);
    }

    /**
     * 分页查询审核文章列表
     * 1. 分页查询
     * 2. 标题模糊查询
     * 3. 可按状态精确检索
     * 4. 时间倒序
     * 5. 显示作者名
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult getList(NewsAuthDto dto) {

        dto.checkParam();

        return weMediaClient.getNewsList(dto);

    }





}
