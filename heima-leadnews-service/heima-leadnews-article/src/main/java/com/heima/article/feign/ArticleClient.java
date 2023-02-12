package com.heima.article.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


    /**
     * 获取文章所属频道
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/un_like_behavior")
    public ResponseResult getOne(@RequestBody UnLikesBehaviorDto dto) {
        if(dto.getType() == null || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle article = articleService.getById(dto.getArticleId());

        return ResponseResult.okResult(article.getChannelId().toString());
    }

    /**
     * 更新文章阅读量
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/read_behavior")
    public ResponseResult updateArticle(@RequestBody ReadBehaviorDto dto) {

        if(dto.getArticleId() == null || dto.getCount() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = articleService.getById(dto.getArticleId());
        apArticle.setViews(apArticle.getViews() + dto.getCount());
        articleService.updateById(apArticle);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    /**
     * 更新文章的点赞数量
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/likes_behavior")
    public ResponseResult saveBehavior(@RequestBody LikesBehaviorDto dto) {
        if(dto.getArticleId() == null){
            return  ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取文章
        ApArticle article = articleService.getById(dto.getArticleId());

        updateBehavior(dto, article);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 更新article的 点赞
     * @param dto
     * @param article
     */
    private void updateBehavior(LikesBehaviorDto dto, ApArticle article){
        // 更新文章点赞
        if(dto.getType() == 0){
            // 更新文章点赞
            if(dto.getOperation() != null){

                // 修改likes数量
                if(dto.getOperation() == 0){
                    // 点赞
                    article.setLikes(article.getLikes() + 1);
                } else {
                    // 取消点赞
                    article.setLikes(article.getLikes() - 1);
                }

                articleService.updateById(article);
            }
        }

        // 2. 更新动态点赞
        // 3. 更新评论点赞
    }

    /**
     * 保存文章
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article/save")
    @Override
    public ResponseResult saveAirticle(ApArticleDto dto) {
        return articleService.saveAirticle(dto);
    }
}
