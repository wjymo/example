package com.wjy.xunwu;

import com.wjy.xunwu.es.BaiduMapLocation;
import com.wjy.xunwu.response.ServiceResult;
import com.wjy.xunwu.service.house.AddressService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaiduMapTest {
    @Autowired
    private AddressService addressService;

    @Test
    public void testBaiduMap(){
        ServiceResult<BaiduMapLocation> baiduMapLocation = addressService.getBaiduMapLocation("武汉", "武汉市武昌区中山路450号");
        System.out.println(1);
    }
}
