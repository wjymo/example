package com.wjy.xunwu.dao;

import com.wjy.xunwu.entity.House;
import com.wjy.xunwu.form.DatatableSearch;
import com.wjy.xunwu.form.RentSearch;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface HouseDAO {
    List<House> findByCondition(@Param("searchBody") DatatableSearch searchBody);

    @Insert("insert into house (title, " +
            "   admin_id, " +
            "   price, " +
            "   area, " +
            "   room, " +
            "   parlour, " +
            "   bathroom, " +
            "   floor, " +
            "   total_floor, " +
            "   watch_times, " +
            "   build_year, " +
            "   status, " +
            "   create_time, " +
            "   last_update_time, " +
            "   city_en_name, " +
            "   region_en_name, " +
            "   street, " +
            "   district, " +
            "   direction, " +
            "   cover, " +
            "   distance_to_subway) " +
            " values ( " +
            "    #{house.title}, " +
            "    #{house.adminId}, " +
            "    #{house.price}, " +
            "    #{house.area}, " +
            "    #{house.room}, " +
            "    #{house.parlour}, " +
            "    #{house.bathroom}, " +
            "    #{house.floor}, " +
            "    #{house.totalFloor}, " +
            "    #{house.watchTimes}, " +
            "    #{house.buildYear}, " +
            "    #{house.status}, " +
            "    #{house.createTime}, " +
            "    #{house.lastUpdateTime}, " +
            "    #{house.cityEnName}, " +
            "    #{house.regionEnName}, " +
            "    #{house.street}, " +
            "    #{house.district}, " +
            "    #{house.direction}, " +
            "    #{house.cover}, " +
            "    #{house.distanceToSubway})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void save(@Param("house") House house);


    @Select("select * from house where id=#{id}")
    House findOne(@Param("id") Long id);

    @Update("update house set status=#{status} where id=#{id}")
    void updateStatus(@Param("id") Long id, @Param("status") int status);

    List<House> findByConditionForApi(@Param("rentSearch") RentSearch rentSearch);


    List<House> findAllInIds(@Param("ids") List<Long> houseIds);
}

