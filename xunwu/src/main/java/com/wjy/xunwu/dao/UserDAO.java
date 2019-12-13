package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserDAO {
    @Select("select * from user where id=#{id}")
    User findOne(@Param("id") Long userId);
}
