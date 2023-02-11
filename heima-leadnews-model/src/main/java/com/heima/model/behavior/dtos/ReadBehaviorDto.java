package com.heima.model.behavior.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 21:52
 */
@Data
public class ReadBehaviorDto implements Serializable {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 阅读次数
     */
    private Integer count;
}
