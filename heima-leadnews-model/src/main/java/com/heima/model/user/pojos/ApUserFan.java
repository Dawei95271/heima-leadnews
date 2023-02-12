package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 21:33
 */
@Data
@TableName("ap_user_fan")
public class ApUserFan {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer user_id;

    /**
     * 粉丝id
     */
    private Integer fans_id;

    /**
     * 粉丝名
     */
    private String fans_name;

    /**
     * 粉丝忠实度
     */
    private Short level;

    /**
     * 创建时间
     */
    private Date created_time;

    /**
     * 是否可见我动态
     */
    private Short isDisplay;

    /**
     * 是否屏蔽私信
     */
    private Short isShieldLetter;

    /**
     * 是否屏蔽评论
     */
    private Short isShieldComment;


}


