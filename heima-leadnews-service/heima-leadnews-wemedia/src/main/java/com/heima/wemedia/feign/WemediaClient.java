package com.heima.wemedia.feign;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.apis.article.IWeMediaClient;
import com.heima.model.admin.dtos.AdSensitive;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.sensitive.dtos.WmSensitive;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vos.WmNewsVo;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmNewsAutoScanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 17:16
 */

@RestController
public class WemediaClient implements IWeMediaClient {

    @Autowired
    private WmChannelService wmChannelService;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 审核成功
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/auth/pass")
    public ResponseResult authNewsPass(@RequestBody NewsAuthDto dto){
        if(dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 修改状态
        WmNews wmNews = wmNewsMapper.selectById(dto.getId());
        wmNews.setType(WmNews.Status.ADMIN_SUCCESS.getCode());

        wmNewsMapper.updateById(wmNews);

        // 发布文章
        wmNewsAutoScanService.saveAppArticle(wmNews);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 审核失败
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/auth/fail")
    public ResponseResult authNewsFail(@RequestBody NewsAuthDto dto){
        if(dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        QueryWrapper<WmNews> qw = new QueryWrapper<>();
        WmNews wmNews = new WmNews();

        wmNews.setId(dto.getId());
        wmNews.setStatus(WmNews.Status.FAIL.getCode());

        if(StrUtil.isNotBlank(dto.getMsg())){
            // 驳回理由
            wmNews.setReason(dto.getMsg());
        }

        wmNewsMapper.updateById(wmNews);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    @GetMapping("/api/v1/news/oneNews/{id}")
    @Override
    public ResponseResult getOneNews(@PathVariable("id") Integer id){
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNewsVo vo = wmNewsMapper.getOneNews(id);
        return ResponseResult.okResult(vo);
    }

    /**
     * 分页查询审核文章列表
     * 1. 分页查询
     * 2. 标题模糊查询
     * 3. 可按状态精确检索
     * 4. 时间倒序
     * 5. 显示作者名
     *
     * @param dto
     */
    @Override
    @PostMapping("/api/v1/news/listByPage")
    public ResponseResult getNewsList(@RequestBody NewsAuthDto dto) {

        dto.checkParam();

        Page<WmNewsVo> myPage = new Page<>(dto.getPage(), dto.getSize());
        QueryWrapper<WmNews> qw = new QueryWrapper();

        // 按状态精确检索
        if(dto.getStatus() != null){
            qw.eq("wn.status", dto.getStatus());
        }

        // 标题模糊查询
        qw.like("wn.title", dto.getTitle());

        // 倒序
//        qw.orderByDesc("created_time");

        wmNewsMapper.getNewsAuthList(myPage, qw);

        return ResponseResult.okResult(myPage.getRecords());

    }

    /**
     * 更新敏感词
     *
     * @param adSensitive
     * @return
     */
    @Override
    @PostMapping("/api/v1/sensitive/update")
    public ResponseResult updateSensitive(@RequestBody AdSensitive adSensitive) {
        if(adSensitive.getId() == null || StrUtil.isBlank(adSensitive.getSensitives())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmSensitive wmSensitive = new WmSensitive();
        BeanUtils.copyProperties(adSensitive, wmSensitive);
        wmSensitiveMapper.updateById(wmSensitive);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    /**
     * 保存敏感词
     * @param adSensitive
     * @return
     */
    @Override
    @PostMapping("/api/v1/sensitive/save")
    public ResponseResult saveSensitive(AdSensitive adSensitive){

        // check param
        if(adSensitive.getSensitives() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 敏感词是否存在
        WmSensitive wmSensitive = wmSensitiveMapper.selectOne(Wrappers.<WmSensitive>lambdaQuery()
                .eq(WmSensitive::getSensitives, adSensitive.getSensitives()));
        if(wmSensitive != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.FORBIDDEN, "敏感词已存在");
        }

        // 插入数据库
        wmSensitive = new WmSensitive();
        wmSensitive.setSensitives(adSensitive.getSensitives());
        wmSensitive.setCreatedTime(new Date());

        wmSensitiveMapper.insert(wmSensitive);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 删除频道
     * 1.被引用的频道不能删除
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult delChannel(Integer id) {

        // 参数检查
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询是否被引用
        List<WmNews> wmNewsList = wmNewsMapper.selectList(Wrappers.<WmNews>lambdaQuery()
                .eq(WmNews::getChannelId, id));
        if(wmNewsList != null && wmNewsList.size() > 0){
            // 不能删除
            return ResponseResult.errorResult(AppHttpCodeEnum.FORBIDDEN, "频道被引用，不能删除");
        }

        wmChannelMapper.deleteById(id);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 更新channel
     * 如果频道被引用，则不能禁止
     * @param adChannel
     * @return
     */
    @Override
    @PostMapping("/api/v1/channel/update")
    public ResponseResult updateChannel(@RequestBody AdChannel adChannel){
        if(adChannel.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        if(! adChannel.getStatus()){
            // 频道被引用，则不能禁止
            List<WmNews> wmNewsList = wmNewsMapper.selectList(Wrappers.<WmNews>lambdaQuery()
                    .eq(WmNews::getChannelId, adChannel.getId()));
            if(wmNewsList != null && wmNewsList.size() > 0){
                return ResponseResult.errorResult(AppHttpCodeEnum.FORBIDDEN, "禁止修改状态");
            }
        }

        // 可以修改
        WmChannel wmChannel = new WmChannel();
        BeanUtils.copyProperties(adChannel, wmChannel);

        wmChannelMapper.updateById(wmChannel);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 分页查询、模糊查询、倒序
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/channel/list")
    public ResponseResult getListByPage(ChannelDto dto) {

        dto.checkParams();

        QueryWrapper<WmChannel> qw = new QueryWrapper<>();
        // 模糊查询
        if(dto.getName() != null){
            qw.like("name", dto.getName());
        }

        // 倒序
        qw.orderByDesc("created_time");

        // 分页
        Page<WmChannel> page = new Page<>(dto.getPage(), dto.getSize());
        wmChannelMapper.selectPage(page, qw);

        return ResponseResult.okResult(page.getRecords());

    }

    /**
     * 保存channel
     * @param channel
     * @return
     */
    @Override
    @PostMapping("/api/v1/channel/save")
    public ResponseResult saveChannel(@RequestBody AdChannel channel) {
        // 1. 参数检查
        if(channel.getName() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // name不存重复
        WmChannel wmChannel = wmChannelMapper.selectOne(Wrappers.<WmChannel>lambdaQuery()
                .eq(WmChannel::getName, channel.getName()));
        if(wmChannel != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }

        wmChannel = new WmChannel();
        BeanUtils.copyProperties(channel, wmChannel);

        // 2. 插入数据库
        if(wmChannelMapper.insert(wmChannel) == 1){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    /**
     * 分页查询、模糊查询
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/sensitive/list")
    public ResponseResult getSensitiveList(@RequestBody SensitiveDto dto) {

        // 1. 参数检查
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParams();

        // 2. 查询数据库
        QueryWrapper<WmSensitive> qw = new QueryWrapper<>();
        if(dto.getName() != null){
            qw.like("sensitives", dto.getName());
        }

        // 倒序
        qw.orderByDesc("created_time");

        // 分页
        Page<WmSensitive> page = new Page<>(dto.getPage(), dto.getSize());
        wmSensitiveMapper.selectPage(page, qw);

        return ResponseResult.okResult(page.getRecords());
    }

    /**
     * 删除敏感词
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("/api/v1/sensitive/del/{id}")
    public ResponseResult delSensitive(@PathVariable("id") Integer id) {

        return ResponseResult.okResult(wmSensitiveMapper.deleteById(id));

    }

    /**
     * 查询所有channel
     * @return
     */
    @Override
    @GetMapping("/api/v1/channel/all")
    public ResponseResult getAllChannels() {
        return ResponseResult.okResult(wmChannelService.findAll());
    }
}
