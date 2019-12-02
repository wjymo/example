package com.wjy.order1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/discovery")
    public DiscoveryClient discovery(){
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("ORDER");
        for (ServiceInstance serviceInstance : serviceInstances) {
            String host = serviceInstance.getHost();
            String instanceId = serviceInstance.getInstanceId();
            Map<String, String> metadata = serviceInstance.getMetadata();
            int port = serviceInstance.getPort();
            String scheme = serviceInstance.getScheme();
            log.info("scheme:{},host:{},port:{},instanceId:{},metadata:{}",scheme,host,port,instanceId,metadata);
        }
        return discoveryClient;
    }

    @GetMapping("/testLoadBalanced")
    public String testLoadBalanced(){
        return "1";
    }
}
