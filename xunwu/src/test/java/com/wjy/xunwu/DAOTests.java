package com.wjy.xunwu;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.wjy.xunwu.dao.HouseDAO;
import com.wjy.xunwu.dao.HouseTagDAO;
import com.wjy.xunwu.entity.House;
import com.wjy.xunwu.entity.HouseTag;
import com.wjy.xunwu.service.house.QiNiuService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wjy.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DAOTests {
    @Autowired
    private HouseTagDAO houseTagDAO;

    @Autowired
    private HouseDAO houseDAO;
    @Test
    public void testInsert() {
        HouseTag houseTag1=new HouseTag();
        houseTag1.setHouseId(1l).setName("擎天圣");
        HouseTag houseTag2=new HouseTag();
        houseTag2.setHouseId(1l).setName("红蜘蛛");
        HouseTag houseTag3=new HouseTag();
        houseTag3.setHouseId(1l).setName("惊破天");

        houseTagDAO.saveList(Arrays.asList(houseTag1,houseTag2,houseTag3));
        System.out.println(1);
    }

    @Test
    public void testInIds(){
        List<House> allInIds = houseDAO.findAllInIds(Arrays.asList(29l, 30l, 31l));
        System.out.println(1);
    }


}
