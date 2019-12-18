package com.wjy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages= {"com.wjy", "org.n3r.idworker"})
public class MuxinNettyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MuxinNettyApplication.class,args);
    }
}
