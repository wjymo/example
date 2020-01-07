package com.wjy.dao;

import com.wjy.entity.ChatMsgUser;
import com.wjy.entity.DBChatMsg;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChatMsgDAO {

    @Insert("insert into chat_msg (id,send_user_id,accept_user_id,msg,sign_flag,create_time) " +
            " values (#{id},#{sendUserId},#{acceptUserId},#{msg},#{signFlag},#{createTime})")
    void insert(DBChatMsg dbChatMsg);

    void batchUpdateMsgSigned(@Param("msgIds") List<String> msgIdList);

    @Select("select u.nickname,c.sign_flag,c.msg,c.create_time " +
            " from chat_msg c JOIN `user` u ON c.send_user_id =u.id " +
            " where accept_user_id=#{id} ")
    List<ChatMsgUser> getByAcceptUserId(@Param("id") String acceptUserId);
}
