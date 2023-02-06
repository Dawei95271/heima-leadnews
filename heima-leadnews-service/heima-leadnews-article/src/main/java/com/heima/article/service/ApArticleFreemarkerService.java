package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

public interface ApArticleFreemarkerService {

    /**
     * 生成静态文件到minIO中
     * @param apArticle
     * @param content
     */
    void buildArticleToMinIO(ApArticle apArticle, String content);
}
