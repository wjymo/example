package com.wjy.lucene;

import com.wjy.entity.User;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.store.FSDirectory;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLucene {



    @Test
    public void test() throws IOException {
        FSDirectory fsDirectory = FSDirectory.open(new File("D:\\temp\\index").toPath());

    }

    @Test
    public void testIKAn() throws IOException {
        Analyzer ikAnalyzer = new IKAnalyzer();
//        TokenStream tokenStream = ikAnalyzer.tokenStream("", "胡尧比杰森.斯坦森牛逼,haha，传智播客牛顿");
        TokenStream tokenStream = ikAnalyzer.tokenStream("", "志向(心理学)芳香-L-氨基酸脱羧酶类，atp柠檬酸（pro-S）裂合酶");
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()){
            System.out.println(charTermAttribute.toString());
        }
        tokenStream.close();

    }
}
