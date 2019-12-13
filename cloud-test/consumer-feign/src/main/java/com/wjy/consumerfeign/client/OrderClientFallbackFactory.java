package com.wjy.consumerfeign.client;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {
    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {
            @Override
            public String xxx1() {
                return null;
            }

            @Override
            public Map<String, String> xxx2(String id) {
                Map<String,String> map=new HashMap<>();
                map.put("id",id+": 没有对应信息！！");
                return map;
            }
        };
    }
}
