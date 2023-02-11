package com.heima.model.behavior.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 15:53
 */
@Data
public class LikesBehaviorDto implements Serializable {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 0 点赞  1 取消
     */
    private Short operation;

    /**
     * 0 文章 1 动态 2 评论
     */
    private Short type;

}
