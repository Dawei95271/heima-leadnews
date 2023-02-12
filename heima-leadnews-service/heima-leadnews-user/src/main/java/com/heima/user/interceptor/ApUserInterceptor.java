package com.heima.user.interceptor;

import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.ApUserThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 17:44
 */
public class ApUserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("userId");
        if(StringUtils.isNotBlank(userId)){
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            ApUserThreadLocalUtil.setUser(apUser);

        }
        return true;

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ApUserThreadLocalUtil.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
