package com.heima.model.article.dtos;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 16:59
 */
@Data
public class HotArticleDto extends ApArticle {

    /**
     * 文章分值
     */
    private Integer score;
}
