package com.wjy.dao;

import com.wjy.entity.MyFriends;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MyFriendsDAO {

    void insertList(@Param("list") List<MyFriends> myFriendsList);

}
