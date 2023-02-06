package com.heima.article.feign;

import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/26 19:19
 */

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService articleService;

    @PostMapping("/api/v1/article/save")
    @Override
    public ResponseResult saveAirticle(ApArticleDto dto) {
        return articleService.saveAirticle(dto);
    }
}
