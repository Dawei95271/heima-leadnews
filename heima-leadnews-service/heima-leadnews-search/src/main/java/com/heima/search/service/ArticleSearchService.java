package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

public interface ArticleSearchService {

    /**
     * ES文章搜索
     * @param userSearchDto
     * @return
     */
    ResponseResult search(UserSearchDto userSearchDto);


}
