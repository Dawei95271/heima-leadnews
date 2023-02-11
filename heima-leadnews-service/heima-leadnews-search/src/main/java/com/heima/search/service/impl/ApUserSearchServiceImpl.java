package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.HistorySearchDto;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.search.pojos.ApUserSearch;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.thread.ApUserThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 13:33
 */
@Service
@Slf4j
public class ApUserSearchServiceImpl implements ApUserSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 删除用户历史记录
     * @param dto
     * @return
     */
    @Override
    public ResponseResult delHistory(HistorySearchDto dto) {

        // 1. 参数检查
        if(dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 是否登录
        ApUser apUser = ApUserThreadLocalUtil.getUser();
        if(apUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 3. 删除
        mongoTemplate.remove(Query.query(Criteria.where("id").is(dto.getId())
                .and("userId").is(apUser.getId())), ApUserSearch.class);


        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 载入用户搜索历史
     */
    @Override
    public ResponseResult loadHistory() {
        // 获取当前用户
        ApUser apUser = ApUserThreadLocalUtil.getUser();
        if(apUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 查询用户搜索记录，按时间倒序
        Query query = Query.query(Criteria.where("userId").is(apUser.getId()))
                .with(Sort.by(Sort.Direction.DESC, "createTime"));

        List<ApUserSearch> userSearchList = mongoTemplate.find(query, ApUserSearch.class);

        return ResponseResult.okResult(userSearchList);

    }

    /**
     * 保存用户搜索历史
     * @param keyword
     * @param userId
     */
    @Override
    @Async
    public void insertSearchHistory(String keyword, Integer userId) {
        // 1. 参数检查
        if(keyword == null || userId == null){
            return;
        }

        // 2. 查询搜索关键词
        ApUserSearch userSearch = mongoTemplate.findOne(Query.query(Criteria.where("userId").size(userId)
                .and("keyword").is(keyword)), ApUserSearch.class);

        // 2.1 存在，更新搜索时间
        if(userSearch != null){
            userSearch.setCreatedTime(new Date());
            mongoTemplate.save(userSearch);
            return;
        }

        // 2.2 不存在，添加或替换
        userSearch = new ApUserSearch();
        userSearch.setUserId(userId);
        userSearch.setKeyword(keyword);
        userSearch.setCreatedTime(new Date());

        Query query = Query.query(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "createTime"));

        List<ApUserSearch> userSearchList = mongoTemplate.find(query, ApUserSearch.class);
        if(userSearchList == null || userSearchList.size() < 10){
            // 历史记录小于10，则添加
            mongoTemplate.save(userSearch);
        } else {
            // 替换
            ApUserSearch lastUserSearch = userSearchList.get(userSearchList.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearch.getUserId())),
                    userSearch);
        }

    }







}
