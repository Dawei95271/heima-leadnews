package com.heima.admin.filter;

import cn.hutool.jwt.JWTUtil;
import com.heima.admin.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/7 18:57
 */
@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {


    /**
     * token验证
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        log.error("request = {}", request.getURI());

        ServerHttpResponse response = exchange.getResponse();

        // 1. 登录请求，直接放行
        if(request.getURI().getPath().contains("/login")){
            return chain.filter(exchange);
        }

        // 2. 非登录请求，获取token
        String token = request.getHeaders().getFirst("token");
        if(StringUtils.isEmpty(token)){
            // 2.1 token为空，则结束请求
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        try {
            // 2.2 token不为空，则解析
            Claims claims = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claims);
            if(result == 1 || result == 2){
                // token无效 或者 过期
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            // 添加header，重置请求
            Object userId = claims.get("id");
            ServerHttpRequest serverHttpRequest = request.mutate().headers(headers -> {
                headers.add("userId", userId + "");
            }).build();

            exchange.mutate().request(serverHttpRequest);

            // 放行
            return chain.filter(exchange);

        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败，结束请求
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();

        }

    }

    /**
     * 过滤器排序
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
