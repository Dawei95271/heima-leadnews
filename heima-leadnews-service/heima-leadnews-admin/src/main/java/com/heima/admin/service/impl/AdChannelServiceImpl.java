package com.heima.admin.service.impl;

import com.heima.admin.service.AdChannelService;
import com.heima.apis.article.IWeMediaClient;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 10:07
 */
@Service
@Transactional
@Slf4j
public class AdChannelServiceImpl implements AdChannelService {

    @Autowired
    private IWeMediaClient weMediaClient;

    /**
     * 删除频道
     *  1.被引用的频道禁止删除
     * @param id
     * @return
     */
    @Override
    public ResponseResult delChannel(Integer id) {

        // 1. 参数检查
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ResponseResult responseResult = weMediaClient.delChannel(id);

        return responseResult;

    }

    /**
     * 修改频道
     * @param adChannel
     * @return
     */
    @Override
    public ResponseResult updateChannel(AdChannel adChannel) {
        // 1. 参数检查
        if(adChannel.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ResponseResult responseResult = weMediaClient.updateChannel(adChannel);
        return responseResult;

    }

    /**
     * 分页查询、模糊查询、倒序
     * @param dto
     * @return
     */
    @Override
    public ResponseResult getList(ChannelDto dto) {

        dto.checkParams();

        ResponseResult responseResult = weMediaClient.getListByPage(dto);

        return responseResult;
    }

    /**
     * 保存频道
     * @param channel
     * @return
     */
    @Override
    public ResponseResult saveChannel(AdChannel channel) {

        // 1. 参数检查
        if(channel.getName() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        return weMediaClient.saveChannel(channel);

    }






}
