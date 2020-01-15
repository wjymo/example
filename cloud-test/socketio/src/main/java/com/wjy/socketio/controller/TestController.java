package com.wjy.socketio.controller;

import com.wjy.socketio.pojo.PushMessage;
import com.wjy.socketio.service.SocketIOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private SocketIOServiceImpl socketIOService;
    @GetMapping("/send")
    public void send(@RequestParam("num")String num){
        PushMessage pushMessage = new PushMessage();
        pushMessage.setLoginUserNum(num);
        socketIOService.pushMessageToUser(pushMessage);
    }
}
