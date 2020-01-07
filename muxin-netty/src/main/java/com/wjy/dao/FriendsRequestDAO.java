package com.wjy.dao;

import com.wjy.entity.FriendsRequest;
import com.wjy.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FriendsRequestDAO {

    @Insert("insert into friends_request (id,send_user_id,accept_user_id,request_date_time) values " +
            " (#{id},#{sendUserId},#{acceptUserId},#{requestDateTime})")
    void insert(FriendsRequest friendsRequest);


//    @Select("select * from friends_request f join user u on f. where accept_user_id=#{acceptUserId}")
//    List<FriendsRequest> getByAcceptUserId(@Param("acceptUserId")String acceptUserId);

    @Delete("delete from friends_request where send_user_id=#{sendUserId} and accept_user_id=#{acceptUserId}")
    void deleteBySnederAndAccepter(@Param("sendUserId")String sendUserId
            ,@Param("acceptUserId")String acceptUserId);
}
