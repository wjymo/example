package com.wjy.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MyFriends {
    private String id;

    /**
     * 用户id
     */
    private String myUserId;

    /**
     * 用户的好友id
     */
    private String myFriendUserId;


}