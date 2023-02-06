package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;

import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/12 16:52
 */
@RestController
@RequestMapping("/api/v1/news")
@Slf4j
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 文章上下架
     * @param dto
     * @return
     */
    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
        return wmNewsService.downOrUp(dto);
    }

    /**
     * 文章删除
     * @param id
     * @return
     */
    @GetMapping("/del_news/{id}")
    public ResponseResult delNews(@PathVariable Integer id){
        return wmNewsService.delNews(id);
    }


    /**
     * 查看详情
     * @param id
     * @return
     */
    @GetMapping("/one/{id}")
    public ResponseResult getNews(@PathVariable Integer id){
        return wmNewsService.getNews(id);
    }

    /**
     * 文章发布、修改或者保存为草稿
     * @param dto
     * @return
     */
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.submitNews(dto);
    }

    /**
     * 查询文章列表
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmNewsPageReqDto dto){
        return wmNewsService.findList(dto);
    }
}
