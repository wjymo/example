package com.wjy.upload;

import com.wjy.upload.runner.SplitRunnable;
import com.wjy.upload.service.UploadService;
import com.wjy.upload.util.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TestUpload extends UploadApplicationTests {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void  testUploadLikeGo(){

    }

    @Test
    public void  testUploadUseRandomAccess(){
//        List<String> list = splitBySize("D:\\hebin\\蘑菇(mrna和dna库)修改sql.txt", 1024);
        List<String> list = splitBySize("I:\\tmp\\蘑菇(mrna和dna库)修改sql.txt", 1024);
        System.out.println(list);

    }
    /**
     * 拆分文件
     * @param fileName 待拆分的完整文件名
     * @param byteSize 按多少字节大小拆分
     * @return 拆分后的文件名列表
     */
    public List<String> splitBySize(String fileName, int byteSize) {
        List<String> parts = new ArrayList<>();
        File file = new File(fileName);
        //获取文件原信息及摘要
        String absolutePath = file.getAbsolutePath();
        String name = file.getName();
        long length = file.length();
        String fileSha1 = FileUtils.getFileSha1(file);


        int count = (int) Math.ceil(length / (double) byteSize);
//        int countLen = (count + "").length();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(count,
                count * 3, 1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(count * 2));

        String uploadId= UUID.randomUUID().toString().substring(0,5)+System.currentTimeMillis();


        String path = absolutePath.substring(0, absolutePath.lastIndexOf("\\")+1)+uploadId;
        File fileDir=new File(path);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        for (int i = 0; i < count; i++) {
//            String partFileName = path + "\\"+"."
//                    + leftPad((i + 1) + "", countLen, '0') + ".part";
            String partFileName = path + "\\"+(i+1);
            threadPool.execute(new SplitRunnable(byteSize, partFileName,file,i * byteSize,
                    redisTemplate,uploadId,i));
            parts.add(partFileName);
        }
        return parts;
    }

    @Autowired
    private UploadService uploadService;
    @Test
    public void testService() throws IOException, InterruptedException {
        int byteSize=1024;
        Map<String, Object> map = uploadService.multiUpload("D:\\hebin\\蘑菇(mrna和dna库)修改sql.txt", byteSize);
        String uploadId = (String)map.get("uploadId");
//        System.out.println(uploadId);
//        double uploadRate=0;
//        do{
//            uploadRate = uploadService.getUploadRate(uploadId);
//        }while (uploadRate !=1);
//        uploadService.mergeFile(uploadId,byteSize, "D:\\merge\\xx1.txt");
    }

    @Test
    public void testBreakpointUpload() throws InterruptedException {
        uploadService.breakpointUpload("b9bc51578552552231", 1024, "D:\\hebin\\蘑菇(mrna和dna库)修改sql.txt");
        //要阻塞一下，否则主线程退出，子线程还没执行完！！！！！！！！！
        TimeUnit.SECONDS.sleep(3);
//        List<Integer> list = new ArrayList<>();
//        Optional<Integer> max = list.stream().max(Comparator.naturalOrder());
//        System.out.println(max);
    }

    @Test
    public void testRedis(){
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2,
                2 * 3, 1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2 * 2));
        for (int i = 10; i < 12; i++) {
            int finalI = i;
            threadPool.execute(()->{
                Boolean aBoolean = redisTemplate.opsForHash().putIfAbsent("MP_b052c1578538259153", "chkidx_"+ finalI, 1);
                System.err.println(aBoolean);
            });
        }
        Boolean aBoolean = redisTemplate.opsForHash().putIfAbsent("MP_b052c1578538259153", "chkidx_13", 1);
        System.err.println(aBoolean);

    }




}
