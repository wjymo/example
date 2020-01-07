package com.wjy.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ChatMsgUser {
    private String msg;

    /**
     * 消息是否签收状态
     1：签收
     0：未签收
     */
    private Integer signFlag;

    private String nickname;
    private Date createTime;
}
