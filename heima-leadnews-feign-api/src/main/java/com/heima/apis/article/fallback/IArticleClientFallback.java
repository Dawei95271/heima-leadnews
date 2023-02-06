package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ApArticleDto;
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
    @Override
    public ResponseResult saveAirticle(ApArticleDto dto) {
        System.out.println("IArticleClient---获取数据失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "获取数据失败");
    }
}
