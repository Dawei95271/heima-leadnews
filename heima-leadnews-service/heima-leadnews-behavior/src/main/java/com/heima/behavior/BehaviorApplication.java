package com.heima.behavior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/9 15:47
 */
@SpringBootApplication
@EnableFeignClients("com.heima.apis")
@EnableDiscoveryClient
public class BehaviorApplication {

    public static void main(String[] args) {

        SpringApplication.run(BehaviorApplication.class, args);

    }

}
