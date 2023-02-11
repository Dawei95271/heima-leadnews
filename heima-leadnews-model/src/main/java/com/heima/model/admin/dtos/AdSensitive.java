package com.heima.model.admin.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 11:29
 */
@Data
public class AdSensitive implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 敏感词
     */
    private String sensitives;

    /**
     * 创建时间
     */
    private String createdTime;
}
