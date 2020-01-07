package com.wjy.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class FriendsRequest {

    private String id;


    private String sendUserId;


    private String acceptUserId;

    /**
     * 发送请求的事件
     */
    private Date requestDateTime;


}