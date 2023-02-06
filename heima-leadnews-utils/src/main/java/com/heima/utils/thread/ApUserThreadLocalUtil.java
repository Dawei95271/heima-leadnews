package com.heima.utils.thread;

import com.heima.model.user.pojos.ApUser;



/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 17:47
 */
public class ApUserThreadLocalUtil {

    public static final ThreadLocal<ApUser> AP_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 保存WmUser
     * @param apUser
     */
    public static void setUser(ApUser apUser){
        AP_USER_THREAD_LOCAL.set(apUser);
    }

    /**
     * 获取WmUser
     * @return
     */
    public static ApUser getUser(){
        return AP_USER_THREAD_LOCAL.get();
    }


    /**
     * 销毁
     */
    public static void clear(){
        AP_USER_THREAD_LOCAL.remove();
    }
}
