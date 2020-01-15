package com.wjy.upload.runner;

import io.lettuce.core.RedisConnectionException;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 通过bio分割文件
 */
@Slf4j
public class SplitRunnable implements Runnable {
    int byteSize;
    String partFilePath;
    File originFile;
    int startPos;
    private RedisTemplate<String, Object> redisTemplate;
    private String uploadId;
    private int index;

    public SplitRunnable(int byteSize, String partFilePath, File originFile, int startPos,
                         RedisTemplate<String, Object> redisTemplate, String uploadId, int index) {
        this.byteSize = byteSize;
        this.partFilePath = partFilePath;
        this.originFile = originFile;
        this.startPos = startPos;
        this.redisTemplate = redisTemplate;
        this.uploadId = uploadId;
        this.index = index;
    }

    public void run() {
        try (RandomAccessFile rFile = new RandomAccessFile(originFile, "r");
             OutputStream os = new FileOutputStream(partFilePath);) {
            byte[] b = new byte[byteSize];
            rFile.seek(startPos);// 移动指针到每“段”开头
            int len = rFile.read(b);
            os.write(b, 0, len);
            os.flush();
            log.debug("当前线程为：{}，uploadId:{},index为：{} ,文件：{}录入完毕",Thread.currentThread().getName()
                    ,uploadId,index,partFilePath);
            //写入完成在redis中标记一下
            Boolean aBoolean = redisTemplate.opsForHash().putIfAbsent("MP_" + uploadId, "chkidx_" + index, 1);
            log.debug("当前线程为：{}，uploadId:{},index为：{} ,存进redis，是否成功：{}",Thread.currentThread().getName()
                    ,uploadId,index,aBoolean);
        } catch (RedisConnectionException  |IllegalStateException e) {
            log.error("当前线程为：{}，uploadId:{},index为：{} 报错：{}", Thread.currentThread().getName(),
                    uploadId, index, e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}