package com.wjy.upload.runner;

import com.sun.corba.se.impl.encoding.CodeSetConversion;
import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 通过nio和堆外内存分割文件
 */
@Slf4j
public class SplitNioRunnable implements Runnable {
    int byteSize;
    String partFilePath;
    File originFile;
    long startPos;
    private RedisTemplate<String, Object> redisTemplate;
    private String uploadId;
    private int index;

    public SplitNioRunnable(int byteSize, String partFilePath, File originFile, long startPos,
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
        try (FileChannel readFileChannel = FileChannel.open(Paths.get(originFile.getAbsolutePath()), StandardOpenOption.READ);
             FileChannel writeFileChannel =
                     FileChannel.open(Paths.get(partFilePath),StandardOpenOption.READ,
                             StandardOpenOption.WRITE,StandardOpenOption.CREATE);) {
            MappedByteBuffer readMap = readFileChannel.map(FileChannel.MapMode.READ_ONLY, startPos, byteSize);
            MappedByteBuffer writeMap = writeFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, byteSize);
//            byte[] bytes=new byte[readMap.limit()];
            byte[] bytes=new byte[byteSize];
            readMap.get(bytes);
            writeMap.put(bytes);

//            FileChannel channel = rFile.getChannel();
//            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, startPos, byteSize);
//            byte[] b = new byte[byteSize];
//            int len = rFile.read(b);
//            mappedByteBuffer.put(b);
            log.debug("当前线程为：{}，uploadId:{},index为：{} ,文件：{}录入完毕",Thread.currentThread().getName()
                    ,uploadId,index,partFilePath);
//            channel.close();
            //写入完成在redis中标记一下
            Boolean aBoolean = redisTemplate.opsForHash().putIfAbsent("MP_" + uploadId, "chkidx_" + index, 1);
            log.debug("当前线程为：{}，uploadId:{},index为：{} ,存进redis，是否成功：{}",Thread.currentThread().getName()
                    ,uploadId,index,aBoolean);
        } catch (RedisConnectionException  |IllegalStateException e) {
            log.error("当前线程为：{}，uploadId:{},index为：{} 报错：{}", Thread.currentThread().getName(),
                    uploadId, index, e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            log.error("当前线程为：{}，uploadId:{},index为：{},当前起始位置：{}， 报错：{}", Thread.currentThread().getName(),
                    uploadId, index, startPos,e.getMessage(), e);
        }
    }
}