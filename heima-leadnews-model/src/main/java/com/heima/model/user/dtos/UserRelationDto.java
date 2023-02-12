package com.heima.model.user.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 16:53
 */
@Data
public class UserRelationDto implements Serializable {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 作者id
     */
    private Integer authorId;

    /**
     * 0 关注  1 取消
     */
    private Short operation;

}
