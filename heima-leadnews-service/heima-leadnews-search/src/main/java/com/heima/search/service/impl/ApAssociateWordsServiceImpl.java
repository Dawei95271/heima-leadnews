package com.heima.search.service.impl;

import cn.hutool.core.util.StrUtil;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.search.pojos.ApAssociateWords;
import com.heima.search.service.ApAssociateWordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 14:47
 */
@Service
@Slf4j
public class ApAssociateWordsServiceImpl implements ApAssociateWordsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 用户搜索联想词
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findAssociateWords(UserSearchDto dto) {

        // 1. 参数检查
        if(dto == null || StrUtil.isBlank(dto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 分页检查
        if(dto.getPageNum() > 20){
            dto.setPageNum(20);
        }

        // 3. 查询
        Query query = Query.query(Criteria.where("associateWords")
                .regex(".*?\\" + dto.getSearchWords() + ".*"));

        // 设置分页大小
        query.limit(dto.getPageSize());
        List<ApAssociateWords> apAssociateWordsList = mongoTemplate.find(query, ApAssociateWords.class);

        return ResponseResult.okResult(apAssociateWordsList);

    }







}
