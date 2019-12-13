package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.Subway;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SubwayDAO {

    @Select("select * from subway where city_en_name=#{cityEnName}")
    List<Subway> findAllByCityEnName(@Param("cityEnName") String cityEnName);

    @Select("select * from subway where id=#{id}")
    Subway findOne(@Param("id") Long subwayLineId);
}
