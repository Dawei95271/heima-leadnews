package com.heima.article.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.article.dtos.ApArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

public interface ApArticleService extends IService<ApArticle> {

    /**
     * 加载文章列表
     * @param dto
     * @param type 1 加载更多 2 加载最新
     * @return
     */
    ResponseResult load(ApArticleHomeDto dto, Short type);

    /**
     * 加载文章列表
     * @param dto
     * @param type 1 加载更多 2 加载最新
     * @param isFirst true 首页
     * @return
     */
    ResponseResult load2(ApArticleHomeDto dto, Short type, boolean isFirst);

    /**
     * 保存或修改app端相关文章
     * @param dto
     * @return
     */
    ResponseResult saveAirticle(ApArticleDto dto);
}
