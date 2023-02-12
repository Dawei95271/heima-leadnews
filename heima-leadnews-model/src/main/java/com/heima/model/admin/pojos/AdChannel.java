package com.heima.model.admin.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 10:03
 */
@Data
public class AdChannel implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;

    /**
     * 频道名称
     */
    private String name;

    /**
     * 频道描述
     */
    private String description;

    /**
     * 是否默认频道
     * 1：默认     true
     * 0：非默认   false
     */
    private Boolean isDefault = true;

    /**
     * 是否启用
     * 1：启用   true
     * 0：禁用   false
     */
    private Boolean status = true;

    /**
     * 默认排序
     */
    private Integer ord = 0;

    /**
     * 创建时间
     */
    private Date createdTime = new Date();

}
