package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.Subway;
import com.wjy.xunwu.entity.SubwayStation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SubwayStationDAO {

    @Select("select * from subway_station where subway_id =#{subwayId}")
    List<SubwayStation> findAllBySubwayId(@Param("subwayId") Long subwayId);

    @Select("select * from subway_station where id=#{id}")
    SubwayStation findOne(@Param("id") Long subwayStationId);
}
