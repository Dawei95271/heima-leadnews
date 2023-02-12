package com.heima.model.admin.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 13:45
 */
@Data
public class NewsAuthDto extends BasePage {


    /**
     * id
     */
    private Integer id;

    /**
     * message
     */
    private String msg;

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 标题
     */
    private String title;


}
