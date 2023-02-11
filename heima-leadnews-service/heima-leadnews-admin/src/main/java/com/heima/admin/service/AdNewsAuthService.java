package com.heima.admin.service;

import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdNewsAuthService {

    /**
     * 分页查询审核文章列表
     *  1. 分页查询
     *  2. 标题模糊查询
     *  3. 可按状态精确检索
     *  4. 时间倒序
     *  5. 显示作者名
     * @param dto
     * @return
     */
    ResponseResult getList(NewsAuthDto dto);

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    ResponseResult getOneVo(Integer id);

    /**
     * 审核失败
     * @param dto
     * @return
     */
    ResponseResult authFail(NewsAuthDto dto);

    /**
     * 审核成功
     * @param dto
     * @return
     */
    ResponseResult authPass(NewsAuthDto dto);
}
