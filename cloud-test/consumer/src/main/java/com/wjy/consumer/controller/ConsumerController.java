package com.wjy.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test")
    public Object getDiscovery(){
        Object discoveryClient = restTemplate.getForObject("http://order/order/testLoadBalanced"
                , Object.class);
        return discoveryClient;
    }

}
