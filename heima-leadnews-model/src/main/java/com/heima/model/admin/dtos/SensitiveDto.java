package com.heima.model.admin.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 9:34
 */
@Data
public class SensitiveDto implements Serializable {

    /**
     * 敏感词名
     */
    private String name;

    /**
     * 页数
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    public void checkParams(){
        if (page <= 0){
            this.page = 1;
        }
        if(size <= 0 || size > 20){
            this.size = 10;
        }
    }

}
