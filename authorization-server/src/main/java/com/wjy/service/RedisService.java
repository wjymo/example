package com.wjy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


}
