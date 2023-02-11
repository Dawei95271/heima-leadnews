package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/12 16:40
 */
@RestController
@RequestMapping("/api/v1/channel")
@Slf4j
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;



    /**
     * 查询频道列表
     * @return
     */
    @GetMapping("/channels")
    public ResponseResult findAll(){
        return wmChannelService.findAll();
    }

}
