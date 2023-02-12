package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @description: 降级处理
 * @author: 16420
 * @time: 2023/1/26 21:31
 */

@Component
public class IArticleClientFallback implements IArticleClient {

    /**
     * 更新文章阅读量
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult updateArticle(ReadBehaviorDto dto) {
        System.out.println("IArticleClient---获取数据失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "获取数据失败");
    }

    /**
     * 获取文章所属频道
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult getOne(UnLikesBehaviorDto dto) {
        System.out.println("IArticleClient---获取数据失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "获取数据失败");
    }

    @Override
    public ResponseResult saveAirticle(ApArticleDto dto) {
        System.out.println("IArticleClient---获取数据失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "获取数据失败");
    }

    /**
     * 更新文章的点赞数量
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveBehavior(LikesBehaviorDto dto) {
        System.out.println("IArticleClient---更新文章点赞失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "获取数据失败");
    }
}
