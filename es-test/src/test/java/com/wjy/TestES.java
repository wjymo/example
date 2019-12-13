package com.wjy;

import com.google.common.collect.ImmutableMap;
import com.wjy.entity.User;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.frameworkset.elasticsearch.entity.suggest.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestES {
    @Autowired
    private BBossESStarter bbossESStarter;


    @Test
    public void test(){
        Map<String,Object> parms=new HashMap<>();
        parms.put("faceScore",4.5);
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/user.xml");
        ESDatas<User> response = clientUtil.searchList("user/_search", "getByFaceScore", parms, User.class);
        List<User> datas = response.getDatas();
//        ESDatas<Map> response = clientUtil.searchList("user/_search", "getByFaceScore", parms, Map.class);
//        List<Map> datas = response.getDatas();
        System.out.println(datas);
    }

    @Test
    public void testCompletion(){
        Map<String,Object> parms=new HashMap<>();
        parms.put("prefix","大话西游");
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/news.xml");
        CompleteRestResponse completeRestResponse = clientUtil.complateSuggest("news/_search", "completionByTitle", parms);
        Map<String, List<CompleteSuggest>> suggests = completeRestResponse.getSuggests();
        List<CompleteSuggest> completeSuggests = suggests.get("my-suggest");
//        CompleteSuggest completeSuggest = completeSuggests.get(0);
        List<String> words=new ArrayList<>();
        completeSuggests.forEach(completeSuggest -> {
            List<CompleteOption> options = completeSuggest.getOptions();
            options.stream().forEach(completeOption ->  words.add(completeOption.getText()));
        });
        System.out.println(words);
    }

    @Test
    public void testTermSuggest() {
        String suggestKey="my-suggest";
        Map<String, Object> parms = new HashMap<>();
        parms.put("text", "lucne rock");
        parms.put("suggestKey", "my-suggest");
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/blogs.xml");
        TermRestResponse termRestResponse = clientUtil.termSuggest("blogs/_search",
                "termSuggestByTitle", parms);
        Map<String, List<TermSuggest>> suggests = termRestResponse.getSuggests();
        List<TermSuggest> termSuggests = suggests.get(suggestKey);
        termSuggests.forEach(termSuggest ->{
            List<TermOption> options = termSuggest.getOptions();
            options.forEach(termOption -> System.out.println(termOption.getText()));
        });

        System.out.println(1);

    }

    @Test
    public void testRest(){
//        ClientInterface restClientUtil = ElasticSearchHelper.getRestClientUtil();
//        String result = restClientUtil.executeHttp("", ClientInterface.HTTP_GET);

        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/user.xml");
        User user=new User();
        user.setFaceScore(4.0).setJob("product");
        User user1 = clientUtil.searchObject("user/_search", "getByFaceScore", user, User.class);

        ImmutableMap<Object, Object> map = ImmutableMap.builder().put("faceScore", 4.0).put("job", "product").build();
        ESDatas<User> getByFaceScore = clientUtil.searchList("user/_search", "getByFaceScoreFunctionScore", user, User.class);
        List<User> datas = getByFaceScore.getDatas();


        System.out.println(1);
    }


    @Test
    public  void testActiviti7(){
        File file=new File("G:\\BaiduNetdiskDownload\\ES资料\\3-1 Activiti7工作流引擎");
        File[] files = file.listFiles((dir, name) -> isNumeric(name));
        Integer total=0;
        for (File fileItem : files) {
            total+=fileItem.listFiles().length;
        }
        System.out.println(total);
    }

    public static boolean isNumeric(String str){
        return str.matches("-?[0-9]+.*[0-9]*");
    }
    @Test
    public  void testActiviti7x(){
        boolean numeric = isNumeric("23");
        boolean numeric2 = isNumeric("01");
        boolean numeric3 = isNumeric("1");
        boolean numeric4 = isNumeric("sd");
        System.out.println(1);

    }

}
