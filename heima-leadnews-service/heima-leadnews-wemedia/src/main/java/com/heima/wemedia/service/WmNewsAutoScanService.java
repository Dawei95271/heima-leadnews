package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsAutoScanService {

    /**
     * 自动审核文章
     * @param id
     */
    void autoScanWmNews(Integer id);

    /**
     * 文章发布到app端
     * @param wmNews
     * @return
     */
    ResponseResult saveAppArticle(WmNews wmNews);
}
