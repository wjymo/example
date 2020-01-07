package com.wjy.dao;

import com.wjy.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDAO {

    @Insert("insert user (id,username,password,nickname,cid,qrcode,initial) values " +
            " (#{id},#{username},#{password},#{nickname},#{cid},#{qrcode},#{initial})")
    void insert(User user);

    @Select("select * from user where username=#{username}")
    User getByUsername(@Param("username")String username);

    @Select("SELECT * FROM user WHERE id in " +
            " (select send_user_id from friends_request WHERE accept_user_id=#{acceptUserId})")
    List<User> getAllRequestBySelfId(@Param("acceptUserId")String acceptUserId);

    @Select("SELECT * FROM user WHERE id in " +
            " (select my_friend_user_id from my_friends where my_user_id=#{id})")
    List<User> getAllMyFriends(@Param("id")String id);
}
