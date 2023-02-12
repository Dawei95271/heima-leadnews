package com.heima.model.article.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/10 7:59
 */
@Data
@TableName("ap_colletion")
public class ApCollection implements Serializable {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 实体id
     */
    @TableField("entry_id")
    private Integer userId;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 类型
     */
    private Short type;

    /**
     * 创建时间
     */
    private Date created_time;

    /**
     * 发布时间
     */
    private Date published_time;

}
