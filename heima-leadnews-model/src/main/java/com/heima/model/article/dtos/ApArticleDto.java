package com.heima.model.article.dtos;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

@Data
public class ApArticleDto extends ApArticle {

    /**
     * 文章内容
     */
    private String content;
}
