package com.heima.article.controller.v1;

import com.heima.article.service.BehaviorService;
import com.heima.model.article.pojos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/10 7:49
 */

@RestController
@RequestMapping("/api/v1")
public class BehaviorController {


    @Autowired
    private BehaviorService behaviorService;

    /**
     * 文章收藏
     * @param dto
     * @return
     */
    @PostMapping("/collection_behavior")
    public ResponseResult collectionBehavior(@RequestBody CollectionBehaviorDto dto){
        return behaviorService.collectionBehavior(dto);
    }



}
