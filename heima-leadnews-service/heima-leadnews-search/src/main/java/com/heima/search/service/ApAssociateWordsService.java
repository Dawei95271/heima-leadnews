package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

public interface ApAssociateWordsService {

    /**
     * 用户联想词搜索
     * @param dto
     * @return
     */
    ResponseResult findAssociateWords(UserSearchDto dto);
}
