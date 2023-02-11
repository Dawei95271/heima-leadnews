package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ApArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    /**
     * 加载文章列表
     * @param dot
     * @param type  1 加载更多  2 加载最新
     * @return
     */
    List<ApArticle> loadArticleList(@Param("dto") ApArticleHomeDto dot, @Param("type") Short type);

    /**
     * 查询5天前的文章
     * @param dataParam
     * @return
     */
    List<ApArticle> findArticleListByLast5Days(@Param("dayParam") Date dataParam);
}
