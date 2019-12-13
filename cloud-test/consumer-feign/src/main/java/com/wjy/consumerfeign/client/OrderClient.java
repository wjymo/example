package com.wjy.consumerfeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "order",fallbackFactory = OrderClientFallbackFactory.class)
public interface OrderClient {
    @GetMapping("/order/testLoadBalanced")
    String xxx1();

    @GetMapping("/order/map/{id}")
    Map<String,String> xxx2(@PathVariable("id") String id);
}
