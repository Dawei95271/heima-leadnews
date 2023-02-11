package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserFan;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.user.mapper.ApUserFanMapper;
import com.heima.user.mapper.ApUserFollowMapper;
import com.heima.user.service.ApUserBehaviorService;
import com.heima.user.service.ApUserService;
import com.heima.utils.thread.ApUserThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rx.internal.schedulers.NewThreadWorker;

import java.util.*;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 16:58
 */
@Service
@Transactional
@Slf4j
public class ApUserBehaviorServiceImpl implements ApUserBehaviorService {

    @Autowired
    private ApUserService apUserService;

    @Autowired
    private ApUserFollowMapper apUserFollowMapper;

    @Autowired
    private ApUserFanMapper apUserFanMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * 用户行为关注
     *  1 .关注，则插入数据， 取消，则删除
     * @param dto
     * @return
     */
    @Override
    public ResponseResult userFollow(UserRelationDto dto) {

        ApUser user = ApUserThreadLocalUtil.getUser();
        // 未登录
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 1. 更新用户关注表

        ApUser author = apUserService.getById(dto.getAuthorId());
        ApUser apUser = apUserService.getById(user.getId());

        // 之前未关注，则插入
        if(dto.getOperation() == 0){
            // 插入关注表
            ApUserFollow apUserFollow = new ApUserFollow();
            apUserFollow.setUserId(user.getId());
            apUserFollow.setFollowId(dto.getAuthorId());
            apUserFollow.setFollowName(apUser.getName());
            apUserFollow.setLevel((short) 0);
            apUserFollow.setIsNotice((short) 1);
            apUserFollow.setCreatedTime(new Date());

            apUserFollowMapper.insert(apUserFollow);

            // 插入粉丝表
            ApUserFan apUserFan = new ApUserFan();
            apUserFan.setUser_id(dto.getAuthorId());
            apUserFan.setFans_id(user.getId());
            apUserFan.setFans_name(apUser.getName());
            apUserFan.setLevel((short) 0);
            apUserFan.setCreated_time(new Date());
            apUserFan.setIsDisplay((short) 1);
            apUserFan.setIsShieldComment((short) 1);
            apUserFan.setIsShieldLetter((short) 1);

            apUserFanMapper.insert(apUserFan);

        } else {
            // 删除 关注表
            ApUserFollow userFollow = apUserFollowMapper.selectList(Wrappers.<ApUserFollow>lambdaQuery()
                    .eq(ApUserFollow::getUserId, user.getId())
                    .eq(ApUserFollow::getFollowId, dto.getAuthorId())).get(0);
            apUserFollowMapper.deleteById(userFollow.getId());

            // 删除粉丝表
            apUserFanMapper.delete(Wrappers.<ApUserFan>lambdaQuery()
                    .eq(ApUserFan::getUser_id, dto.getAuthorId())
                    .eq(ApUserFan::getFans_id, apUser.getId()));


        }

        // 2. 缓存关注信息
        String redisKey = BehaviorConstants.BEHAVIOR_PREFIX + user.getId();
        String hashKey = dto.getArticleId() + "";

        Object o = cacheService.hGet(redisKey, hashKey);

        List<String> list = null;
        if(o == null){
            // 新添加
            list = new ArrayList<>();
        } else{
            // 已有
             list = JSON.parseArray((String) o, String.class);
        }

        list.add("follow");
        cacheService.hPut(redisKey, dto.getArticleId() + "", JSON.toJSONString(list));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }







}
