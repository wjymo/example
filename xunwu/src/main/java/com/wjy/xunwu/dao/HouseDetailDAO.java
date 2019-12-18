package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.HouseDetail;
import com.wjy.xunwu.entity.HousePicture;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HouseDetailDAO {

    @Insert("insert into house_detail (house_id,  " +
            "    description,  " +
            "    layout_desc,  " +
            "    traffic,  " +
            "    round_service,  " +
            "    rent_way,  " +
            "    address,  " +
            "    subway_line_id,  " +
            "    subway_station_id,  " +
            "    subway_line_name,  " +
            "    subway_station_name) values (#{detail.houseId},  " +
            "            #{detail.description},  " +
            "            #{detail.layoutDesc},  " +
            "            #{detail.traffic},  " +
            "            #{detail.roundService},  " +
            "            #{detail.rentWay},  " +
            "            #{detail.detailAddress},  " +
            "            #{detail.subwayLineId},  " +
            "            #{detail.subwayStationId},  " +
            "            #{detail.subwayLineName},  " +
            "            #{detail.subwayStationName})" )
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void save(@Param("detail") HouseDetail detail);


    @Select("select *,address detailAddress from house_detail where house_id=#{houseId} ")
    HouseDetail findByHouseId(@Param("houseId") Long id);


    List<HouseDetail> findAllByHouseIdIn(@Param("houseIds") List<Long> houseIds);
}
