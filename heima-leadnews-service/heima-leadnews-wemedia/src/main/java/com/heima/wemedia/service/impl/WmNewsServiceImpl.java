package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/12 16:55
 */
@Service
@Transactional
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsAutoScanService  wmNewsAutoScanService;

    @Autowired
    private WmNewsTaskService wmNewsTaskService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 文章上下架
     * @param dto
     * @return
     */
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        // 1 . 参数检查
        if(dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章Id必不可少");
        }
        // 2. 查询
        WmNews wmNews = getById(dto.getId());
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }
        // 3. 未发布，不能上下架
        if(wmNews.getStatus() != WmNews.Status.PUBLISHED.getCode()){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前文章不是发布状态，不能上下架");
        }
        // 更新上下架
//        update(Wrappers.<WmNews>lambdaUpdate().eq(WmNews::getId, dto.getId()).set(WmNews::getEnable, dto.getEnable()));
        // 修改文章的上下架状态
        if(dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2){
            update(Wrappers.<WmNews>lambdaUpdate().set(WmNews::getEnable, dto.getEnable())
                    .eq(WmNews::getId, dto.getId()));

            // 发送消息，通知article端修改文章配置
            if(wmNews.getArticleId() != null){

                Map<String, Object> map = new HashMap<>();
                map.put("articleId", wmNews.getArticleId());
                map.put("enable", dto.getEnable());
                kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(map));
            }

        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 文章删除
     * @param id
     * @return
     */
    @Override
    public ResponseResult delNews(Integer id) {
        // 1. 参数检查
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章Id必不可少");
        }
        // 2. 查询文章
        WmNews wmNews = getById(id);
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }
        // 3. 已发布，不能删除
        if(wmNews.getStatus() == WmNews.Status.PUBLISHED.getCode()){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章已发布，不能删除");
        }
        // 4. 删除文章
        removeById(id);
        return ResponseResult.okResult("删除成功");
    }

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    @Override
    public ResponseResult getNews(Integer id) {
        // 1. 参数校验
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 2. 查询数据
        WmNews wmNews = getById(id);
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }

        return ResponseResult.okResult(wmNews);
    }

    /**
     * 文章发布、修改或者保存为草稿
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submitNews(WmNewsDto dto) {

        // 1. 参数检查
        if(dto == null || dto.getContent() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 保存或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);
        // 文章布局 自当布局则 设置为空
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }
        // 设置images
        if(dto.getImages() != null && dto.getImages().size() > 0){
            String imageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }

        saveOrUpdateWmNews(wmNews);

        // 判断是否为草稿， 是则直接结束
        if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 3. 保存内容与素材关系
        // 获取material urls
        List<String> materialUls = getMaterialUrls(wmNews.getContent());
        // 保存
        saveRelativeInfoForContent(materialUls, wmNews.getId());

        // 4. 保存封面月素材关系
        saveRelativeForCover(dto, wmNews, materialUls);

        // 5. 文章审核
//        wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        // 5.1 定时审核发布
        wmNewsTaskService.addNewsToTask(wmNews.getId(), wmNews.getPublishTime());


        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 1. 如果图片布局方式 设置为自动，则根据文章内容图片的数量设置布局（type）数据
     * 匹配规则
     * ①。大于等于3 多图            type=3
     * ②。大于等于1 小于3 单图      type=1
     * ③。等于0 无图               type=0
     *
     * 2. 保存封面与素材关系
     * @param dto
     * @param wmNews
     * @param materialUls
     */
    private void saveRelativeForCover(WmNewsDto dto, WmNews wmNews, List<String> materialUls) {

        List<String> images = dto.getImages();
        // 如果是自动布局方式，则设置封面数据
        if(dto.getType() != null && dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            int size = materialUls.size();
            if(size >= 3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materialUls.stream().limit(WemediaConstants.WM_NEWS_MANY_IMAGE).collect(Collectors.toList());
            } else if(size >= 1){
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materialUls.stream().limit(WemediaConstants.WM_NEWS_SINGLE_IMAGE).collect(Collectors.toList());
            }else {
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
                images = null;
            }

            // 设置images
            if(images != null && images.size() > 0){
                wmNews.setImages(StringUtils.join(images,","));
            }
            updateById(wmNews);
        }
        // 保存关系
        if(images != null && images.size() > 0){
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }

    }

    /**
     * 保存内容与素材关系
     * @param materialUrls
     * @param newId
     */
    private void saveRelativeInfoForContent(List<String> materialUrls, Integer newId) {
        saveRelativeInfo(materialUrls, newId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存文章中的图片到数据库， 保存内容图或封面图
     * @param materialUrls
     * @param newId
     * @param type
     */
    private void saveRelativeInfo(List<String> materialUrls, Integer newId, Short type) {
        // 空值判断
        if(materialUrls == null || materialUrls.size() == 0){
            return;
        }
        // 1. 获取素材ids
        List<WmMaterial> materialList = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery()
                .in(WmMaterial::getUrl, materialUrls));

        // 判断素材是否有效
        if(materialList == null || materialList.size() == 0){
            // 提示 回滚
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }
        if(materialList.size() != materialUrls.size()){
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }

        List<Integer> idList = materialList.stream().map(WmMaterial::getId).collect(Collectors.toList());
        // 2. 批量保存
        wmNewsMaterialMapper.saveRelations(idList, newId, type);
    }

    /**
     * 获取material的ids
     * @param content
     * @return
     */
    private List<String> getMaterialUrls(String content) {

        List<String> urlList = new ArrayList<>();
        List<Map> list = JSON.parseArray(content, Map.class);
        for (Map item : list) {
            if(item.get("type").equals("image")){
                urlList.add((String) item.get("value"));
            }
        }
        return urlList;
    }

    /**
     * 保存或修改文章
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        Integer userId = WmThreadLocalUtil.getUser().getId();
        wmNews.setUserId(userId);
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1);  // 默认上架

        if(wmNews.getId() == null){
            // 保存
            save(wmNews);
        } else {
            // 修改
            // 删除内容图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial> lambdaQuery()
                    .eq(WmNewsMaterial::getNewsId, wmNews.getId()));

            updateById(wmNews);
        }
    }

    /**
     * 查询文章列表
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {

        // 1. 参数检查
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmUser user = WmThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        dto.checkParam();

        // 2. 查询参数设置
        LambdaQueryWrapper<WmNews> qw = new LambdaQueryWrapper<>();
        // 文章状态 精确查询
        if(dto.getStatus() != null){
            qw.eq(WmNews::getStatus, dto.getStatus());
        }
        // 发布时间 精确查询
        if(dto.getBeginPubDate() != null && dto.getEndPubDate() != null){
            qw.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }
        // 文章所属的频道精确查询
        if(dto.getChannelId() != null){
            qw.eq(WmNews::getChannelId, dto.getChannelId());
        }
        // 关键字模糊查询
        if(dto.getKeyword() != null){
            qw.like(WmNews::getTitle, dto.getKeyword());
        }
        // 文章所有人id 精确查询
        qw.eq(WmNews::getUserId, user.getId());
        // 创建时间倒序排列
        qw.orderByDesc(WmNews::getCreatedTime);

        // 分页查询
        IPage<WmNews> page = new Page(dto.getPage(), dto.getSize());
        page(page, qw);

        // 3. 返回结果
        ResponseResult<List> responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }


}
