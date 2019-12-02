package com.wjy.order1;


import com.wjy.order1.Order1Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class Order1Application {
    public static void main(String[] args) {
        SpringApplication.run(Order1Application.class,args);
    }
}
