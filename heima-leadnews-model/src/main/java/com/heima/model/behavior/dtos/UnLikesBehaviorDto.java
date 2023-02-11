package com.heima.model.behavior.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 22:36
 */
@Data
public class UnLikesBehaviorDto implements Serializable {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 0 不喜欢  1 取消不喜欢
     */
    private Short type;
}
