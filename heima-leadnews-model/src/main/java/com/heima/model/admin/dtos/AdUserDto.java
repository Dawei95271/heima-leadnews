package com.heima.model.admin.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/7 20:35
 */
@Data
public class AdUserDto implements Serializable {

    /**
     * 用户名
     */
    private String name;

    /**
     * 密码
     */
    private String password;
}
