package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;


/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 17:47
 */
public class WmThreadLocalUtil {

    public static final ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 保存WmUser
     * @param wmUser
     */
    public static void setUser(WmUser wmUser){
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    /**
     * 获取WmUser
     * @return
     */
    public static WmUser getUser(){
        return WM_USER_THREAD_LOCAL.get();
    }


    /**
     * 销毁
     */
    public static void clear(){
        WM_USER_THREAD_LOCAL.remove();
    }
}
