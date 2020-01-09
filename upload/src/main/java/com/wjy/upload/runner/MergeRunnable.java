package com.wjy.upload.runner;

import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MergeRunnable implements Runnable {
        long startPos;
        String mergeFilePath;
        File partFile;



        public MergeRunnable(long startPos, String mergeFilePath, File partFile) {
            //文件起始位置，就是每个文件块的索引乘以指定的文件块的大小
            this.startPos = startPos;
            //合并后文件的绝对路径
            this.mergeFilePath = mergeFilePath;
            //每一个文件块
            this.partFile = partFile;
        }

        public void run() {
            try (RandomAccessFile rFile = new RandomAccessFile(mergeFilePath, "rw");
                 FileInputStream fs = new FileInputStream(partFile);){
                //将要合并的文件seek（指定）到当前文件块索引乘以指定的文件块的大小的那个位置开始进行读/写操作
                rFile.seek(startPos);
                byte[] b = new byte[fs.available()];
                //将文件块内容读取到字节数组中
                fs.read(b);
                fs.close();
                //将包含文件块内容的字节数组写入到要合并的文件中，从startPos开始写入
                rFile.write(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }