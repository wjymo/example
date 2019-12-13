package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.HouseDetail;
import com.wjy.xunwu.entity.HousePicture;
import com.wjy.xunwu.entity.HouseTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HouseTagDAO {

    void saveList(List<HouseTag> houseTags);

    @Insert("INSERT INTO house_tag (house_id,`name`) values (#{houseTag.houseId},#{houseTag.name})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void save(@Param("houseTag") HouseTag houseTag);

    @Select("select * from house_tag where house_id=#{houseId}")
    List<HouseTag> findAllByHouseId(@Param("houseId") Long id);

    @Select("select * from house_tag where name=#{name} and house_id=#{houseId}")
    HouseTag findByNameAndHouseId(@Param("name") String tag, @Param("houseId") Long houseId);


    List<HouseTag> findAllByHouseIdIn(@Param("houseIds") List<Long> houseIds);
}
