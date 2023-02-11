package com.heima.behavior.interceptor;

import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.ApUserThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 16:16
 */
public class TokenInterceptor implements HandlerInterceptor {


    /**
     * 保存用户id 到 THreadLocal
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     * @throws Exception
     */
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
        // ThreadLocal
        ApUserThreadLocalUtil.clear();

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
