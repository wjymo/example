package com.wjy.xunwu;

import com.github.pagehelper.Page;
import com.wjy.xunwu.dto.HouseDTO;
import com.wjy.xunwu.entity.House;
import com.wjy.xunwu.es.HouseIndexTemplate;
import com.wjy.xunwu.form.DatatableSearch;
import com.wjy.xunwu.form.RentSearch;
import com.wjy.xunwu.response.ServiceMultiResult;
import com.wjy.xunwu.service.house.HouseService;
import com.wjy.xunwu.service.search.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {

    @Autowired
    private HouseService houseService;
    @Test
    public void testHouse(){
        DatatableSearch datatableSearch=new DatatableSearch();
        datatableSearch.setDirection("desc").setOrderBy("createTime").setDraw(1).setStart(0).setLength(3);

        ServiceMultiResult<HouseDTO> houseDTOServiceMultiResult = houseService.adminQuery(datatableSearch);
        System.out.println(1);
    }

    @Autowired
    private SearchService searchService;
    @Test
    public void testSearch(){
//        searchService.index(29l);
        HouseIndexTemplate houseIndexTemplate=new HouseIndexTemplate().setCityEnName("xx").setHouseId(29l);
        searchService.deleteAndCreate(1,houseIndexTemplate);
    }

    @Test
    public void testSearchQuery(){
        RentSearch rentSearch=new RentSearch().setCityEnName("bj")
                .setRegionEnName("*")
                .setAreaBlock("30-50")
                ;
        searchService.query(rentSearch);
    }
}
