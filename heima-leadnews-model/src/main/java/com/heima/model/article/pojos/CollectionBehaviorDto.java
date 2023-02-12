package com.heima.model.article.pojos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/10 7:50
 */
@Data
public class CollectionBehaviorDto implements Serializable {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 0 收藏  1 取消收藏
     */
    private Short operation;

    /**
     * 发布时间
     */
    private Date publishedTime;

    /**
     * 类型 0 文章  1 动态
     */
    private Short type;

}
