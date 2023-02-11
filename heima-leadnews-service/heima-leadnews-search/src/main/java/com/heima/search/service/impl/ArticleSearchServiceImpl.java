package com.heima.search.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.service.ApUserSearchService;
import com.heima.search.service.ArticleSearchService;
import com.heima.utils.thread.ApUserThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/5 18:18
 */
@Service
@Slf4j
public class ArticleSearchServiceImpl implements ArticleSearchService {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ApUserSearchService apUserSearchService;

    /**
     * ES文章搜索
     * @param dto
     * @return
     */
    @Override
    public ResponseResult search(UserSearchDto dto) {
        // 1. 参数检查
        if(dto == null || StrUtil.isBlank(dto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID );
        }

        // 异步调用 保存搜索历史记录，只在第一页的时候保存
        ApUser apUser = ApUserThreadLocalUtil.getUser();
        if(apUser != null && dto.getFromIndex() == 0){
            apUserSearchService.insertSearchHistory(dto.getSearchWords(), apUser.getId());
        }


        // 2. 查询
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 布尔查询，多个条件查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 关键词分词查询
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(dto.getSearchWords())
                .field("title")
                .field("content")
                .defaultOperator(Operator.OR);

        boolQueryBuilder.must(queryStringQueryBuilder);

        //查询小于mindate的数据
        RangeQueryBuilder publishTime = QueryBuilders.rangeQuery("publishTime")
                .lt(dto.getMinBehotTime().getTime());

        boolQueryBuilder.filter(publishTime);

        // 倒序排列
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);

        // 分页查询
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(dto.getPageSize());

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        // 只对title做高亮
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color: red; font-size: inherit;'>");
        highlightBuilder.postTags("</font>");

        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 3. 结果封装
        List<Map> list = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            // json类型结果
            String json = hit.getSourceAsString();
            Map map = JSON.parseObject(json, Map.class);

            log.info("record = {}", map);

            // 高亮处理
            if(hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0){
                Text[] titles = hit.getHighlightFields().get("title").getFragments();
                String title = StrUtil.join(titles.toString());
                // 高亮标题
                map.put("h_title", title);

            } else {
                // 原始标题
                map.put("h_title", map.get("title"));
            }

            list.add(map);
        }

        return ResponseResult.okResult(list);
    }




}
