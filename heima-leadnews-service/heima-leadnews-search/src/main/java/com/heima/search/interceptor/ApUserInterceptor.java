package com.heima.search.interceptor;

import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.ApUserThreadLocalUtil;
import com.heima.utils.thread.WmThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 14:08
 */
@Slf4j
public class ApUserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("userId");

        Optional<String> optional = Optional.ofNullable(userId);
        if(optional.isPresent()){
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            ApUserThreadLocalUtil.setUser(apUser);
            log.info("WmTokenInteceptor----将用户id存入ThreadLocal...");
        }
        return true;
    }



    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocalUtil.clear();
        log.info("apTokenInteceptor---清理ThreadLocal...");
    }
}
