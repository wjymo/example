package com.wjy.xunwu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wjy.xunwu.es.HouseBucketDTO;
import com.wjy.xunwu.response.ServiceMultiResult;
import com.wjy.xunwu.response.ServiceResult;
import com.wjy.xunwu.service.search.SearchService;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESTest {

    @Autowired
    private SearchService searchService;
    @Test
    public void testAnalyze(){
        ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
        String body="{\"field\": \"layoutDesc\", \"text\": \"王庚牛逼\"}";
        String s = clientUtil.executeHttp("xunwu/_analyze", body, ClientInterface.HTTP_POST);
        JSONObject jsonObject = JSON.parseObject(s);
        JSONArray tokens = jsonObject.getJSONArray("tokens");

        List<String> terms = tokens.stream().map(o -> {
            JSONObject token = (JSONObject) o;
            String term = token.getString("token");
            return term;
        }).collect(Collectors.toList());
        System.out.println(s);
    }

    @Test
    public void testSuggest(){
        ServiceResult<List<String>> suggest = searchService.suggest("融泽");
        System.out.println(1);
    }

    @Test
    public void testAgg(){
        ServiceMultiResult<HouseBucketDTO> bj = searchService.mapAggregate("bj");
        System.out.println(1);
    }


}
