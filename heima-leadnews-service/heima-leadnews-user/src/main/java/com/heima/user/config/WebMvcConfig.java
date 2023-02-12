package com.heima.user.config;

import com.heima.user.interceptor.ApUserInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 17:46
 */
@Component
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApUserInterceptor()).addPathPatterns("/**");
    }
}
