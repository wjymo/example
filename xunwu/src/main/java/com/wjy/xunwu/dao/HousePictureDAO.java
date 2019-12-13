package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.HousePicture;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HousePictureDAO {


    void saveList(List<HousePicture> pictures);

    @Select("select * from house_picture where house_id=#{houseId}")
    List<HousePicture> findAllByHouseId(@Param("houseId") Long id);
}
