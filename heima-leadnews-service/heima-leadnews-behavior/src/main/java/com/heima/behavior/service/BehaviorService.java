package com.heima.behavior.service;

import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface BehaviorService {

    /**
     * 用户行为---点赞
     * @param dto
     * @return
     */
    ResponseResult likesBehavior(LikesBehaviorDto dto);

    /**
     * 用户行为---阅读
     * @param dto
     * @return
     */
    ResponseResult readBehavior(ReadBehaviorDto dto);

    /**
     * 不喜欢  取消不喜欢
     * @param dto
     * @return
     */
    ResponseResult unLikesBehavior(UnLikesBehaviorDto dto);
}
