package com.heima.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.StringUtils;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/5 14:33
 */
public class HelloListener {


    /**
     * 监听器
     * @param message
     */
    @KafkaListener(topics = "kafka-test")
    public void onMessage(String message){

        if(! StringUtils.isEmpty(message)){

            System.out.println(message);

        }

    }
}
