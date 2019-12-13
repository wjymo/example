package com.wjy.xunwu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESTest {

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


}
