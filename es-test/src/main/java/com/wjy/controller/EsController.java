package com.wjy.controller;

import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/es")
public class EsController {
    @GetMapping("/title/{title}")
    public List<Map> getByTitle(@PathVariable("title")String title){
        Map<String,String> parms=new HashMap<>();
        parms.put("title",title);
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/movie.xml");
        ESDatas<Map> response = clientUtil.searchList("movie/_search", "getByTitle", parms, Map.class);
        List<Map> dataList = response.getDatas();
        return dataList;
    }
}
