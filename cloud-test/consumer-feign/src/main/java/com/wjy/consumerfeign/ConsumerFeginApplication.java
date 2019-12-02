package com.wjy.consumerfeign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@EnableFeignClients("com.wjy.consumerfeign.client")
@SpringBootApplication
//@ComponentScan("")
public class ConsumerFeginApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerFeginApplication.class,args);
    }
}
