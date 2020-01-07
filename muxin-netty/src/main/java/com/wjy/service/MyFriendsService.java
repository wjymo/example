package com.wjy.service;

import com.wjy.dao.FriendsRequestDAO;
import com.wjy.dao.MyFriendsDAO;
import com.wjy.entity.FriendsRequest;
import com.wjy.entity.MyFriends;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class MyFriendsService {

    @Autowired
    private MyFriendsDAO myFriendsDAO;
    @Autowired
    private Sid sid;


    public void passRequest(String senderId,String accepterId){
        String id1 = sid.nextShort();
        MyFriends myFriends1 = new MyFriends().setId(id1).setMyUserId(senderId).setMyFriendUserId(accepterId);
        String id2 = sid.nextShort();
        MyFriends myFriends2 = new MyFriends().setId(id2).setMyUserId(accepterId).setMyFriendUserId(senderId);
        myFriendsDAO.insertList(Arrays.asList(myFriends1,myFriends2));
    }



}
