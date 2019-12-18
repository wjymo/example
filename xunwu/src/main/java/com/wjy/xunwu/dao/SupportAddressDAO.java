package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.SupportAddress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SupportAddressDAO {

    @Select("select * from support_address where level=#{level}")
    List<SupportAddress> findAllByLevel(@Param("level") String level);

    @Select("select *,baidu_map_lng baiduMapLongitude,baidu_map_lat baiduMapLatitude" +
            " from support_address where level=#{level} and belong_to=#{belongTo}")
    List<SupportAddress> findAllByLevelAndBelongTo(@Param("level") String level, @Param("belongTo") String cityName);

    @Select("select * from support_address where level=#{level} and en_name=#{cityEnName}")
    SupportAddress findByEnNameAndLevel(@Param("cityEnName") String cityEnName, @Param("level") String value);

    @Select("select * from support_address where belong_to=#{belongTo} and en_name=#{enName}")
    SupportAddress findByEnNameAndBelongTo(@Param("enName") String enName, @Param("belongTo") String belongTo);
}
