package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleFreemarkerService;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;

import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.article.dtos.ApArticleHomeDto;
import com.heima.model.article.dtos.HotArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/7 21:37
 */
@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    // 最大分页条数
    private static final int MAX_PAGE_SIZE = 10;

    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleFreemarkerService apArticleFreemarkerService;

    @Autowired
    private CacheService cacheService;


    /**
     * 加载文章列表
     * @param dto
     * @param type    1 加载更多 2 加载最新
     * @param isFirst true 首页
     * @return
     */
    @Override
    public ResponseResult load2(ApArticleHomeDto dto, Short type, boolean isFirst) {
        if(isFirst){
            // 首页则加载热点文章
            String jsonStr = null;
            if(dto.getTag() == null){
                // 加载推荐热点文章
                jsonStr = cacheService.get(ArticleConstants.DEFAULT_TAG);
            }else {
                jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            }

            if(StringUtils.isNotEmpty(jsonStr)){
                List<HotArticleDto> hotArticleDtoList = JSON.parseArray(jsonStr, HotArticleDto.class);
                return ResponseResult.okResult(hotArticleDtoList);
            }

            // 缓存加载失败，从数据库加载啊
            return load(dto, type);
        }
        // 非首页
        return load(dto, type);
    }

    /**
     * 保存或修改app端相关文章
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveAirticle(ApArticleDto dto) {
        // 1. 参数检查
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        // 2. 是否存在文章id，
        if(dto.getId() == null){
            // 2.1 是，保存
            // 保存article
            save(apArticle);

            // 保存ApArticleConfig
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfigMapper.insert(apArticleConfig);

            // 保存content
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);

        } else{
            // 2.2 否，修改
            // 修改文章
            updateById(apArticle);

            // 修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.
                    <ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, apArticle.getId()));

            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        // 生成或修改 静态文件到minIO
        apArticleFreemarkerService.buildArticleToMinIO(apArticle, dto.getContent());

        // 3. 返回文章id
        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 加载文章列表
     * @param dto
     * @param type 1 加载更多 2 加载最新
     * @return
     */
    @Override
    public ResponseResult load(ApArticleHomeDto dto, Short type) {

        // 参数校验
        // 分页校验
        Integer size = dto.getSize();
        if(size == null || size == 0){
            // 默认为10
            size = 10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        // 类型校验
        if(type != ArticleConstants.LOADTYPE_LOAD_MORE && type != ArticleConstants.LOADTYPE_LOAD_NEW){
            // 加载更多
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        // 频道校验
        if(StringUtils.isBlank(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        // 时间校验
        if(dto.getMaxBehotTime() == null){
            dto.setMaxBehotTime(new Date());
        }
        if(dto.getMinBehotTime() == null){
            dto.setMinBehotTime(new Date());
        }

        // 查询数据
        List<ApArticle> list = apArticleMapper.loadArticleList(dto, type);
        // 返回结果
        return ResponseResult.okResult(list);
    }
}
