package com.heima.apis.article;


import com.heima.apis.article.fallback.IArticleClientFallback;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-article", fallback = IArticleClientFallback.class)
public interface IArticleClient {

    /**
     * 保存文章
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article/save")
    ResponseResult saveAirticle(@RequestBody ApArticleDto dto);

    /**
     * 更新文章的点赞数量
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/likes_behavior")
    ResponseResult saveBehavior(@RequestBody LikesBehaviorDto dto);

    /**
     * 更新文章阅读量
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/read_behavior")
    ResponseResult updateArticle(@RequestBody ReadBehaviorDto dto);

    /**
     * 获取文章所属频道
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/un_likes_behavior")
    ResponseResult getOne(@RequestBody UnLikesBehaviorDto dto);
}
