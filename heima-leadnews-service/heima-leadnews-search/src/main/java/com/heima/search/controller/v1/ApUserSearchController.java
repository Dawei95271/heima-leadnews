package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;
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
 * @time: 2023/2/6 14:20
 */
@RestController
@RequestMapping("/api/v1/history")
@Slf4j
public class ApUserSearchController {

    @Autowired
    private ApUserSearchService apUserSearchService;

    /**
     * 删除用户搜索历史
     * @param dto
     * @return
     */
    @PostMapping("/del")
    public ResponseResult delHistory(@RequestBody HistorySearchDto dto){
        return apUserSearchService.delHistory(dto);
    }


    /**
     * 载入用户搜索历史
     * @return
     */
    @PostMapping("/load")
    public ResponseResult loadHistory(){
        return apUserSearchService.loadHistory();
    }



}
