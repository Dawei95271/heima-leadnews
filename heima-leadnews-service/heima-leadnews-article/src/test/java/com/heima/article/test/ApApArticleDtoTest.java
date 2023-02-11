package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 15:11
 */

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ApApArticleDtoTest {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private Configuration configuration;
    @Autowired
    private FileStorageService fileStorageService;

    // 手动添加静态html
    @Test
    public void test01() throws TemplateException, IOException {

        // 1. 获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, "1302862387124125698l"));
        if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
            // 2. 生成静态模板
            Template template = configuration.getTemplate("article.ftl");
            Map map = new HashMap<>();
            map.put("content", JSONArray.parseArray(apArticleContent.getContent(), Map.class));

            Writer out = new StringWriter();
            template.process(map, out);
            // 3. 上传到minIO
            String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", new ByteArrayInputStream(out.toString().getBytes()));
            // 4. 更新数据库
            ApArticle article = new ApArticle();
            article.setId(apArticleContent.getArticleId());
            article.setStaticUrl(path);
            apArticleMapper.updateById(article);
        }


    }
}
