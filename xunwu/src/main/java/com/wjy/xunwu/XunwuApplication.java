package com.wjy.xunwu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.wjy.xunwu.dao")
@SpringBootApplication
public class XunwuApplication {
    public static void main(String[] args) {
        SpringApplication.run(XunwuApplication.class,args);
    }
}
