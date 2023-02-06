package com.heima.wemedia.feign;

import com.heima.apis.article.IWeMediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 17:16
 */

@RestController
public class WemediaClient implements IWeMediaClient {

    @Autowired
    private WmChannelService wmChannelService;

    /**
     * 查询所有channel
     * @return
     */
    @Override
    @GetMapping("/api/v1/channel/all")
    public ResponseResult getAllChannels() {
        return ResponseResult.okResult(wmChannelService.findAll());
    }
}
