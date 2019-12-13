package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.HouseSubscribe;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SubscribeDAO {

    @Select("select * from house_subscribe where house_id=#{houseId} and user_id=#{userId}")
    HouseSubscribe findByHouseIdAndUserId(@Param("houseId") Long houseId, @Param("userId") long userId);
}
