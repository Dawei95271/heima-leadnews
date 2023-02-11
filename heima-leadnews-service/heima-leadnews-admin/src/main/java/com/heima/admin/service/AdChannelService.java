package com.heima.admin.service;

import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AdChannelService {

    /**
     * 修改频道
     * @param adChannel
     * @return
     */
    ResponseResult updateChannel(AdChannel adChannel);

    /**
     * 分页查询、模糊查询、倒序
     * @param dto
     * @return
     */
    ResponseResult getList(ChannelDto dto);

    /**
     * 保存频道
     * @param channel
     * @return
     */
    ResponseResult saveChannel(AdChannel channel);

    /**
     * 删除频道
     *      1.被引用的频道禁止删除
     * @param id
     * @return
     */
    ResponseResult delChannel(Integer id);
}
