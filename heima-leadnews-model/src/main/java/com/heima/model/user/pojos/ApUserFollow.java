package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 17:05
 */
@Data
@TableName("ap_user_follow")
public class ApUserFollow implements Serializable {

    /**
     * 主键
     */
    @TableId("id")
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 关注作者ID
     */
    private Integer followId;

    /**
     * 粉丝昵称
     */
    private String followName;

    /**
     * 关注度
     *             0 偶尔感兴趣
     *             1 一般
     *             2 经常
     *             3 高度
     */
    private Short level;

    /**
     * 是否动态通知
     */
    private Short isNotice;

    /**
     * 创建时间
     */
    private Date createdTime;
}
