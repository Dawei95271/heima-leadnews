package com.heima.admin.controler.v1;

import com.heima.admin.service.AdChannelService;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 9:59
 */
@RestController
@RequestMapping("/api/v1/channel")
@Slf4j
public class AdChannelController {


    @Autowired
    private AdChannelService adChannelService;

    /**
     * 删除频道
     * @param id
     * @return
     */
    @GetMapping("/del/{id}")
    public ResponseResult delChannel(@PathVariable("id") Integer id){
        return adChannelService.delChannel(id);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody AdChannel adChannel){
        return adChannelService.updateChannel(adChannel);
    }

    /**
     * 分页查询频道列表
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult getList(@RequestBody ChannelDto dto){
        return adChannelService.getList(dto);
    }

    /**
     * 新增频道
     * @param adChannel
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody AdChannel adChannel){
        return adChannelService.saveChannel(adChannel);
    }


}
