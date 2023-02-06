package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.search.service.ApAssociateWordsService;
import com.heima.search.service.ApUserSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 14:42
 */
@RestController
@RequestMapping("/api/v1/associate")
@Slf4j
public class ApAssociateWordsController {


    @Autowired
    private ApAssociateWordsService apAssociateWordsService;


    /**
     * 用户联想词搜索
     * @param dto
     * @return
     */
    @PostMapping("/search")
    public ResponseResult findAssociateWords(@RequestBody UserSearchDto dto){
        return apAssociateWordsService.findAssociateWords(dto);
    }



}
