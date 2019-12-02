package com.wjy.consumerfeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("order")
public interface OrderClient {
    @GetMapping("/order/testLoadBalanced")
    String xxx1();
}
