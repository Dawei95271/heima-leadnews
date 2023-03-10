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
     * ??????????????????
     * @param id ???????????????id
     */
    @Override
    @Async
    public void autoScanWmNews(Integer id) {
        if(id == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 1. ?????????????????????
        WmNews wmNews = wmNewsMapper.selectById(id);
        if(wmNews == null){
            throw new RuntimeException("WmNewsAutoScanServiceImpl---?????????????????????");
        }
        // 2. ????????????????????????
        if(wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())){
            // ??????????????????
            // ?????????????????????
            Map<String, Object> textAndImages = handleTextAndImages(wmNews);

            // ???????????????????????????
            boolean isNotSensitive = handleSensitiveScan((String) textAndImages.get("content"), wmNews);
            if(! isNotSensitive) return;

            // ???????????? ???????????????
//            boolean isTextScan = handleTextScan((String) textAndImages.get("text"), wmNews);
            // ??????
//            if(! isTextScan) return;

            // ???????????? ???????????????
//            boolean isImageScan = handlerImageScan((List)textAndImages.get("images"), wmNews);
            // ??????
//            if(! isImageScan) return;

            // 3. ??????????????????????????????app???
            ResponseResult responseResult = saveAppArticle(wmNews);
            if(! responseResult.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-?????????????????????app???????????????????????????");
            }
            // 4. ??????app?????????id
            wmNews.setArticleId((Long) responseResult.getData());
            // 5. ??????????????????
            updateWmNews(wmNews, (short)9, "????????????");

        }


    }

    /**
     * ????????????????????????
     * @param content
     * @param wmNews
     * @return true ????????????  false ???????????????
     */
    private boolean handleSensitiveScan(String content, WmNews wmNews) {
        // ??????????????????
        boolean flag = true;

        // ?????????????????????
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        // ?????????dfa??????
        SensitiveWordUtil.initMap(sensitiveList);
        // ???????????????
        Map<String, Integer> retMap = SensitiveWordUtil.matchWords(content);
        if(retMap.size() > 0) {
            updateWmNews(wmNews, (short)2, "??????????????????????????????" + retMap);
            flag = false;
        }
        return flag;
    }

    /**
     * ???????????????app???
     * @param wmNews
     * @return
     */
    public ResponseResult saveAppArticle(WmNews wmNews) {
        ApArticleDto apArticleDto = new ApArticleDto();
        BeanUtils.copyProperties(wmNews, apArticleDto);

        // ????????????
        apArticleDto.setAuthorId(wmNews.getUserId().longValue());
        // ?????????
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if(wmUser != null){
            apArticleDto.setAuthorName(wmUser.getName());
        }
        // ???????????????
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if(wmChannel != null){
            apArticleDto.setChannelName(wmChannel.getName());
        }
        // ????????????
        apArticleDto.setLayout(wmNews.getType());
        if(wmNews.getType() == 3){
            apArticleDto.setLayout((short) 2);
        }
        // ????????????id
        apArticleDto.setId(null);
        if(wmNews.getArticleId() !=null){
            apArticleDto.setId(wmNews.getArticleId());
        }
        apArticleDto.setCreatedTime(new Date());

        ResponseResult responseResult = articleClient.saveAirticle(apArticleDto);
        return responseResult;

    }

    /**
     * ???????????? ???????????????
     * @param images
     * @param wmNews
     * @return
     */
    private boolean handlerImageScan(List<String> images, WmNews wmNews) {
        // ????????????
        boolean flag = true;

        // ?????????????????????
        if(images == null || images.size() == 0){
            return true;
        }

        // ????????????
        images  = images.stream().distinct().collect(Collectors.toList());

        // ????????????
        List<byte[]> imageList = new ArrayList<>();
        for (String imageURL : images) {
            byte[] bytes;
            try {
                bytes = fileStorageService.downLoadFile(imageURL);

                // ???????????????????????? --- begin---
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                BufferedImage image = ImageIO.read(in);
                // ????????????
                String result = tess4jClient.doOCR(image);
                boolean isNotSensitive = handleSensitiveScan(result, wmNews);
                if(! isNotSensitive){
                    // ?????????????????? ????????????
                    return isNotSensitive;
                }

                imageList.add(bytes);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // ????????????
        try {
            Map map = greenImageScan.imageScan(imageList);
            if(map != null){
                // ????????????
                if("block".equals(map.get("suggestion"))){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "??????????????????????????????");
                }
                // ????????????????????????????????????
                if("review".equals(map.get("suggestion"))){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "?????????????????????????????????");
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }

    /**
     *  ???????????? ???????????????
     * @param text
     * @param wmNews
     * @return
     */
    private boolean handleTextScan(String text, WmNews wmNews) {
        boolean flag = true;

        // ????????????????????????
        if(text == null || text.length() == 0){
            return true;
        }

        try {
            Map map = greenTextScan.greeTextScan(text);
            if(map != null){
                // ????????????
                if("block".equals(map.get("suggestion"))){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "??????????????????????????????");
                }
                // ????????????????????????????????????
                if("review".equals(map.get("suggestion"))){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "?????????????????????????????????");
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * ??????????????????
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
     * ?????????????????????
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        // ??? text
        StringBuilder textStr = new StringBuilder();
        // ???image
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

        // ????????????
        String title = wmNews.getTitle();
        textStr.append(title);

        // ????????????
        if(StrUtil.isNotBlank(wmNews.getImages())){
            String[] split = wmNews.getImages().split(",");
            imageList.addAll(Arrays.asList(split));
        }
        // ????????????
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("text", textStr.toString());
        retMap.put("images", imageList);

        return retMap;
    }


}
