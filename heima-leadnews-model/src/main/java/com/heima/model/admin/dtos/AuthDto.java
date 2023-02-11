package com.heima.model.admin.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 11:54
 */
@Data
public class AuthDto implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * message
     */
    private String msg;

    /**
     * page
     */
    private Integer page = 1;

    /**
     * size
     */
    private Integer size = 10;

    /**
     * 状态
     */
    private Short status;

    public void checkParam(){
        if(page <= 0){
            page = 1;
        }
        if(size <= 0 || size > 20){
            page = 20;
        }
    }

}
