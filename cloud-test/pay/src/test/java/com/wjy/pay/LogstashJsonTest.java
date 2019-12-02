package com.wjy.pay;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.wjy.pay.service.PayService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LogstashJsonTest {



    @Test
    public void testJson() throws IOException {
        File file=new File("C:\\Users\\Administrator\\WebstormProjects\\untitled\\xx2.json");

//        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        FileOutputStream fos=FileUtils.openOutputStream(new File("D:\\json\\movies.json"),true);
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("D:\\json\\movies.json")));) {
            String s = FileUtils.readFileToString(file, "utf-8");
            JSONObject result = JSON.parseObject(s);
            JSONArray movies = result.getJSONArray("result");
            List<String> movieStrs = movies.stream().map(o -> {
                JSONObject movie = (JSONObject) o;
                String str = JSON.toJSONString(movie,false);
                str.concat("\n");
                try {
                    bufferedWriter.write(str);
                    bufferedWriter.newLine();
//                fos.write(str.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return str;
            }).collect(Collectors.toList());
        }
        System.out.println(1);



    }

}
