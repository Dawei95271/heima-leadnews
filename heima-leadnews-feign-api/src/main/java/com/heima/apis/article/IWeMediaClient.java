package com.heima.apis.article;

import com.heima.model.admin.dtos.AdSensitive;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient("leadnews-wemedia")
public interface IWeMediaClient {

    /**
     * 更新channel
     * 如果频道被引用，则不能禁止
     * @param adChannel
     * @return
     */
    @PostMapping("/api/v1/channel/update")
    ResponseResult updateChannel(@RequestBody AdChannel adChannel);

    /**
     * 分页查询、模糊查询、倒序
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/channel/list")
    ResponseResult getListByPage(@RequestBody ChannelDto dto);

    /**
     * 保存channel
     * @param channel
     * @return
     */
    @PostMapping("/api/v1/channel/save")
    ResponseResult saveChannel(@RequestBody AdChannel channel);

    /**
     * 查询所有频道
     * @return
     */
    @GetMapping("/api/v1/channel/all")
    ResponseResult getAllChannels();

    /**
     * 删除敏感词
     * @param id
     * @return
     */
    @DeleteMapping("/api/v1/sensitive/del/{id}")
    ResponseResult delSensitive(@PathVariable("id") Integer id);

    /**
     * 分页查询、模糊查询、时间倒序
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/sensitive/list")
    ResponseResult getSensitiveList(@RequestBody SensitiveDto dto);


    /**
     * 删除频道
     *  1.被引用的频道不能删除
     * @param id
     * @return
     */
    @GetMapping("/api/v1/channel/{id}")
    ResponseResult delChannel(@PathVariable("id") Integer id);

    /**
     * 保存敏感词
     * @param adSensitive
     * @return
     */
    @PostMapping("/api/v1/sensitive/save")
    ResponseResult saveSensitive(@RequestBody AdSensitive adSensitive);

    /**
     * 更新敏感词
     * @param adSensitive
     * @return
     */
    @PostMapping("/api/v1/sensitive/update")
    ResponseResult updateSensitive(@RequestBody AdSensitive adSensitive);

    /**
     * 分页查询审核文章列表
     * 1. 分页查询
     * 2. 标题模糊查询
     * 3. 可按状态精确检索
     * 4. 时间倒序
     * 5. 显示作者名
     */
    @PostMapping("/api/v1/news/listByPage")
    ResponseResult getNewsList(@RequestBody NewsAuthDto dto);

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    @GetMapping("/api/v1/news/oneNews/{id}")
    ResponseResult getOneNews(@PathVariable("id") Integer id);

    /**
     * 审核失败
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/auth/fail")
    ResponseResult authNewsFail(@RequestBody NewsAuthDto dto);

    /**
     * 审核成功
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/auth/pass")
    ResponseResult authNewsPass(@RequestBody NewsAuthDto dto);
}
