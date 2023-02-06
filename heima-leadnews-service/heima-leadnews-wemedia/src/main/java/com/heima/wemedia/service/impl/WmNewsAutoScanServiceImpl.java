package com.heima.wemedia.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.exception.CustomException;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;

import com.heima.model.sensitive.dtos.WmSensitive;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/26 20:12
 */

@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private GreenImageScan greenImageScan;
    @Autowired
    private GreenTextScan greenTextScan;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private IArticleClient articleClient;
    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    private Tess4jClient tess4jClient;

    /**
     * 自动审核文章
     * @param id 自媒体文章id
     */
    @Override
    @Async
    public void autoScanWmNews(Integer id) {
        if(id == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 1. 查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if(wmNews == null){
            throw new RuntimeException("WmNewsAutoScanServiceImpl---文章内容不存在");
        }
        // 2. 文本和图片的审核
        if(wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())){
            // 待审核状态，
            // 提取文章和图片
            Map<String, Object> textAndImages = handleTextAndImages(wmNews);

            // 自管理的敏感词管理
            boolean isNotSensitive = handleSensitiveScan((String) textAndImages.get("content"), wmNews);
            if(! isNotSensitive) return;

            // 审核文本 阿里云接口
//            boolean isTextScan = handleTextScan((String) textAndImages.get("text"), wmNews);
            // 成功
//            if(! isTextScan) return;

            // 审核图片 阿里云接口
//            boolean isImageScan = handlerImageScan((List)textAndImages.get("images"), wmNews);
            // 成功
//            if(! isImageScan) return;

            // 3. 审核成功，发布文章到app端
            ResponseResult responseResult = saveAppArticle(wmNews);
            if(! responseResult.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }
            // 4. 回填app端文章id
            wmNews.setArticleId((Long) responseResult.getData());
            // 5. 修改文章状态
            updateWmNews(wmNews, (short)9, "审核成功");

        }


    }

    /**
     * 自定义敏感词检测
     * @param content
     * @param wmNews
     * @return true 无敏感词  false 存在敏感词
     */
    private boolean handleSensitiveScan(String content, WmNews wmNews) {
        // 默认无敏感词
        boolean flag = true;

        // 获取所有敏感词
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        // 初始化dfa算法
        SensitiveWordUtil.initMap(sensitiveList);
        // 敏感词检测
        Map<String, Integer> retMap = SensitiveWordUtil.matchWords(content);
        if(retMap.size() > 0) {
            updateWmNews(wmNews, (short)2, "当前文章中存在敏感词" + retMap);
            flag = false;
        }
        return flag;
    }

    /**
     * 文章发布到app端
     * @param wmNews
     * @return
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {
        ApArticleDto apArticleDto = new ApArticleDto();
        BeanUtils.copyProperties(wmNews, apArticleDto);

        // 设置作者
        apArticleDto.setAuthorId(wmNews.getUserId().longValue());
        // 作者名
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if(wmUser != null){
            apArticleDto.setAuthorName(wmUser.getName());
        }
        // 设置频道名
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if(wmChannel != null){
            apArticleDto.setChannelName(wmChannel.getName());
        }
        // 设置布局
        apArticleDto.setLayout(wmNews.getType());
        if(wmNews.getType() == 3){
            apArticleDto.setLayout((short) 2);
        }
        // 设置文章id
        apArticleDto.setId(null);
        if(wmNews.getArticleId() !=null){
            apArticleDto.setId(wmNews.getArticleId());
        }
        apArticleDto.setCreatedTime(new Date());

        ResponseResult responseResult = articleClient.saveAirticle(apArticleDto);
        return responseResult;

    }

    /**
     * 审核图片 阿里云接口
     * @param images
     * @param wmNews
     * @return
     */
    private boolean handlerImageScan(List<String> images, WmNews wmNews) {
        // 默认通过
        boolean flag = true;

        // 无图，直接通过
        if(images == null || images.size() == 0){
            return true;
        }

        // 图片去重
        images  = images.stream().distinct().collect(Collectors.toList());

        // 下载图片
        List<byte[]> imageList = new ArrayList<>();
        for (String imageURL : images) {
            byte[] bytes;
            try {
                bytes = fileStorageService.downLoadFile(imageURL);

                // 图片识别文字审核 --- begin---
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                BufferedImage image = ImageIO.read(in);
                // 提取文字
                String result = tess4jClient.doOCR(image);
                boolean isNotSensitive = handleSensitiveScan(result, wmNews);
                if(! isNotSensitive){
                    // 存在敏感词， 直接返回
                    return isNotSensitive;
                }

                imageList.add(bytes);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // 图片审核
        try {
            Map map = greenImageScan.imageScan(imageList);
            if(map != null){
                // 审核失败
                if("block".equals(map.get("suggestion"))){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "当前图片存在违规内容");
                }
                // 图片不确定，等待人工审核
                if("review".equals(map.get("suggestion"))){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "当前图片存在不确定信息");
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }

    /**
     *  审核文本 阿里云接口
     * @param text
     * @param wmNews
     * @return
     */
    private boolean handleTextScan(String text, WmNews wmNews) {
        boolean flag = true;

        // 无文本，直接通过
        if(text == null || text.length() == 0){
            return true;
        }

        try {
            Map map = greenTextScan.greeTextScan(text);
            if(map != null){
                // 审核失败
                if("block".equals(map.get("suggestion"))){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "当前文章存在违规内容");
                }
                // 不确定信息，需要人工审核
                if("review".equals(map.get("suggestion"))){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "当前文章存在不确定信息");
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 更新文章状态
     * @param wmNews
     * @param status
     * @param reason
     */
    private void updateWmNews(WmNews wmNews, Short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 提取文章和图片
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        // 存 text
        StringBuilder textStr = new StringBuilder();
        // 存image
        List<String> imageList = new ArrayList<>();

        List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);

        for (Map map : maps) {
            if(map.get("type").equals("text")){
                textStr.append((String) map.get("value"));
            }
            if(map.get("type").equals("image")){
                imageList.add((String) map.get("value"));
            }
        }

        // 提取标题
        String title = wmNews.getTitle();
        textStr.append(title);

        // 提取封面
        if(StrUtil.isNotBlank(wmNews.getImages())){
            String[] split = wmNews.getImages().split(",");
            imageList.addAll(Arrays.asList(split));
        }
        // 结果返回
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("text", textStr.toString());
        retMap.put("images", imageList);

        return retMap;
    }


}
