package com.wjy.consumerfeign.controller;

import com.wjy.consumerfeign.client.OrderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @Autowired
    private OrderClient orderClient;

    @GetMapping("/test")
    public Object getDiscovery(){
        String s = orderClient.xxx1();
        return s;
    }

}
