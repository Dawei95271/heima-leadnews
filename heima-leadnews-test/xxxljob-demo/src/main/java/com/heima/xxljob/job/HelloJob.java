package com.heima.xxljob.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/6 15:54
 */
@Component
public class HelloJob {


    @XxlJob("demoJobHandler")
    public void helloJob(){
        System.out.println("hello job");
    }



}
