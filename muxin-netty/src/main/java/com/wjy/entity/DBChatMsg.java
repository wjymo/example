package com.wjy.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class DBChatMsg {
    private String id;

    private String sendUserId;

    private String acceptUserId;

    private String msg;

    /**
     * 消息是否签收状态
1：签收
0：未签收

     */
    private Integer signFlag;

    /**
     * 发送请求的事件
     */
    private Date createTime;

}