package com.wjy.upload.service;

import ch.qos.logback.core.joran.event.InPlayListener;
import com.wjy.upload.runner.MergeRunnable;
import com.wjy.upload.runner.SplitNioRunnable;
import com.wjy.upload.runner.SplitRunnable;
import com.wjy.upload.util.FileUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Log4j2
public class UploadService {
    private static final String uploadDir="D:\\hebin\\";
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 分块上传
     * @param filePath
     * @param byteSize
     */
    public Map<String,Object>  multiUpload(String filePath, Integer byteSize){
        File file=new File(filePath);
        //获取文件原信息及摘要
        String absolutePath = file.getAbsolutePath();
        String name = file.getName();
        long length = file.length();
//        String fileSha1 = FileUtils.getFileSha1(file);

        int count = (int) Math.ceil(length / (double) byteSize);
        String uploadId= UUID.randomUUID().toString().substring(0,5)+System.currentTimeMillis();

        //将分块上传信息存入redis
        redisTemplate.opsForHash().putIfAbsent("MP_"+uploadId,"chunkcount", count);
//        redisTemplate.opsForHash().putIfAbsent("MP_"+uploadId,"filehash", fileSha1);
        redisTemplate.opsForHash().putIfAbsent("MP_"+uploadId,"filesize", length);
        redisTemplate.opsForHash().putIfAbsent("MP_"+uploadId,"filepath", absolutePath);
        redisTemplate.opsForHash().putIfAbsent("MP_"+uploadId,"byteSize", byteSize);

//        String path = absolutePath.substring(0, absolutePath.lastIndexOf("\\")+1)+uploadId;
        String path = uploadDir+uploadId;
        File fileDir=new File(path);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        long longByteSize = byteSize.longValue();
        for (int i = 0; i < count; i++) {
            String partFilePath = path + "\\"+(i+1);
            long startPos=i * longByteSize;
            if(i==count-1){
                long l = length - startPos;
                if(longByteSize>l){
                    byteSize=(int)l;
                }
            }
            threadPoolExecutor.execute(new SplitNioRunnable(byteSize, partFilePath,file,startPos,
                     redisTemplate,uploadId,i));
        }
        Map<String,Object> map=new HashMap<>();
        map.put("uploadId", uploadId);
        map.put("count", count);
        return map;
    }

    /**
     * 断点续传
     * @param uploadId
     * @param byteSize
     * @param filepath
     */
    public void breakpointUpload(String uploadId, Integer byteSize,String filepath){
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("MP_" + uploadId);
        Integer chunkcount=0;
        List<Integer> currentCompleteIndexs=new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if(StringUtils.equals(key,"chunkcount")){
                chunkcount=(Integer)value;
            }else if(StringUtils.startsWith(key,"chkidx_")){
                Integer isDone=(Integer)value;
                if(isDone!=null && isDone==1){
                    //当前已完成的文件块索引
                    Integer currentCompleteIndex=Integer.parseInt(StringUtils.substringAfter(key,"chkidx_"));
                    currentCompleteIndexs.add(currentCompleteIndex);
                }
            }
        }
        //遍历所有已完成的文件块索引，获取最大的那个，如果拿不到，说明没有一个文件块完成，那么就走完整的分块上传的流程，把max置为-1
        Integer max = currentCompleteIndexs.stream().max(Comparator.naturalOrder()).orElse(-1);
        if(max!=chunkcount){
            //说明文件没上传完，要从currentCompleteIndex的下一个文件块开始上传
            String path = uploadDir+uploadId;
            if(StringUtils.isEmpty(filepath)){
                //通过uploadId在redis中获取文件路径
                filepath = (String)redisTemplate.opsForHash().get("MP_" + uploadId, "filepath");
            }
            File file=new File(filepath);
            if(byteSize==null){
                //通过uploadId在redis中获取文件块设定的大小
                Object o = redisTemplate.opsForHash().get("MP_" + uploadId, "byteSize");
                if(o==null){
                    throw new RuntimeException("redis中没byteSize，uploadID为："+uploadId);
                }
                byteSize=Integer.parseInt(o.toString());
            }
            for (int i = max+1; i < chunkcount; i++) {
                String partFilePath = path + "\\"+(i+1);
                threadPoolExecutor.execute(new SplitRunnable(byteSize, partFilePath,file,i * byteSize,
                        redisTemplate,uploadId,i));
            }
        }
    }
    /**
     * 合并文件
     *
     * @param uploadId 上传id
     * @param partFileSize 拆分文件的字节数大小
     * @param mergeFilePath 合并后的文件完整路径
     * @throws IOException
     */
    public void mergeFile(String uploadId,int partFileSize, String mergeFilePath) throws IOException {
        //先获取上传id目录下的所有文件块
        List<File> partFiles = FileUtils.getDirFiles(uploadDir + uploadId);
        //对文件块进行排序，否则合并的内容是错的
        Collections.sort(partFiles, new FileUtils.FileComparator());

        //合并后文件的路径，如果父目录没有则创建
        String path = null;
        try {
            path = mergeFilePath.substring(0, mergeFilePath.lastIndexOf("\\"));
        } catch (StringIndexOutOfBoundsException e) {
            path = mergeFilePath.substring(0, mergeFilePath.lastIndexOf("/"));
        }
        File fileDir=new File(path);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }

        //将合并后的文件创建出来并指定大小，目前不执行这一步也没问题
//        RandomAccessFile randomAccessFile = new RandomAccessFile(mergeFilePath, "rw");
//        randomAccessFile.setLength(partFileSize * (partFiles.size() - 1)+ partFiles.get(partFiles.size() - 1).length());
//        randomAccessFile.close();

        //将文件块中的内容并行写入到合并后的文件
        for (int i = 0; i < partFiles.size(); i++) {
            threadPoolExecutor.execute(new MergeRunnable(i * partFileSize,
                    mergeFilePath, partFiles.get(i)));
        }
    }

    /**
     * 获取上传进度
     * @param uploadId
     * @return
     */
    public double getUploadRate(String uploadId){
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("MP_" + uploadId);
        Integer chunkcount=0;
        Integer completeChunkcount=0;
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if(StringUtils.equals(key,"chunkcount")){
                chunkcount=(Integer)value;
            }else if(StringUtils.startsWith(key,"chkidx_")){
                Integer isDone=(Integer)value;
                if(isDone!=null && isDone==1){
                    completeChunkcount++;
                }
            }
        }
        log.info("completeChunkcount:{}",completeChunkcount);
        log.info("chunkcount:{}",chunkcount);
        double rate = BigDecimal.valueOf((double) completeChunkcount / chunkcount)
                .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return rate;
    }
}
