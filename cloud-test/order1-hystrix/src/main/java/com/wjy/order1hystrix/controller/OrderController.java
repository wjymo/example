package com.wjy.order1hystrix.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    @GetMapping("/map/{id}")
    @HystrixCommand(fallbackMethod = "processHystrix_Get")
    public Map<String,String> getMap(@PathVariable("id") String id){
        Map<String,String> map=new HashMap<>();
        if(1==1){
            throw new RuntimeException("xxxx");
        }
        map.put("name","huyao");
        map.put("age","28");
        map.put("id",id);
        return map;
    }
    public Map<String,String> processHystrix_Get(@PathVariable("id") String id) {
        Map<String,String> map=new HashMap<String, String>();
        map.put("id",id+": 没有对应信息");
        return map;
    }
}
