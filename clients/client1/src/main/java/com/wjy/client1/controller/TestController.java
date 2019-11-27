package com.wjy.client1.controller;

import com.google.common.collect.Maps;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/ok")
    public ResponseEntity test(){
        Map<String,String> map= Maps.newHashMap();
        map.put("result","你赢了");
        return ResponseEntity.ok(map);
    }

    @GetMapping("/ok2")
    public ResponseEntity test2(){
        Map<String,String> map= Maps.newHashMap();
        map.put("result","王亚平赢了");
        return ResponseEntity.ok(map);
    }
}
