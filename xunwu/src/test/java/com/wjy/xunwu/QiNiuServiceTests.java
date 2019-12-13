package com.wjy.xunwu;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.wjy.xunwu.service.house.QiNiuService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * Created by wjy.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class QiNiuServiceTests  {
    @Autowired
    private QiNiuService qiNiuService;

    @Test
    public void testUploadFile() {
        String fileName = "C:\\Users\\Administrator\\Desktop\\timg.jpg";
        File file = new File(fileName);

        Assert.assertTrue(file.exists());

        try {
            Response response = qiNiuService.uploadFile(file);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        String key = "FvyNceBAaZF6TBh6OZpcEKlhuACG";
        try {
            Response response = qiNiuService.delete(key);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }
}
