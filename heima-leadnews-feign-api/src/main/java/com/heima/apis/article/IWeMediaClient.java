package com.heima.apis.article;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient("leadnews-wemedia")
public interface IWeMediaClient {

    @GetMapping("/api/v1/channel/all")
    ResponseResult getAllChannels();

}
