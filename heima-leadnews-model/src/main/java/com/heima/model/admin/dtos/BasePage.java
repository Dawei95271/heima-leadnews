package com.heima.model.admin.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 13:46
 */
@Data
public class BasePage implements Serializable {

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer size;

    /**
     * 参数检查
     */
    public void checkParam(){
        if(page <= 0){
            page = 1;
        }
        if(size <= 0 || size >= 20){
            size = 20;
        }
    }
}
