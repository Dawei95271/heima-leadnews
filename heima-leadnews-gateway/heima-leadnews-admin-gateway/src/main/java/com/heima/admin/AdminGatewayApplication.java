package com.heima.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/7 18:53
 */

@SpringBootApplication
@EnableDiscoveryClient
public class AdminGatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(AdminGatewayApplication.class, args);

    }


}
