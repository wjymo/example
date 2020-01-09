package com.wjy.upload.service;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * 文件上传服务层
 */
@Service
public class FileManage {

//    @Value("${basePath}")
//    private String basePath;
//
//    @Value("${file-url}")
//    private String fileUrl;
//
//    /**
//     * 分块上传
//     * 第一步：获取RandomAccessFile,随机访问文件类的对象
//     * 第二步：调用RandomAccessFile的getChannel()方法，打开文件通道 FileChannel
//     * 第三步：获取当前是第几个分块，计算文件的最后偏移量
//     * 第四步：获取当前文件分块的字节数组，用于获取文件字节长度
//     * 第五步：使用文件通道FileChannel类的 map（）方法创建直接字节缓冲器  MappedByteBuffer
//     * 第六步：将分块的字节数组放入到当前位置的缓冲区内  mappedByteBuffer.put(byte[] b);
//     * 第七步：释放缓冲区
//     * 第八步：检查文件是否全部完成上传
//     * @param param
//     * @return
//     * @throws IOException
//     */
//    public String chunkUploadByMappedByteBuffer(MultipartFileParam param) throws IOException {
//        if(param.getTaskId() == null || "".equals(param.getTaskId())){
//            param.setTaskId(UUID.randomUUID().toString());
//        }
//        /**
//         * basePath是我的路径，可以替换为你的
//         * 1：原文件名改为UUID
//         * 2：创建临时文件，和源文件一个路径
//         * 3：如果文件路径不存在重新创建
//         */
//        String fileName = param.getFile().getOriginalFilename();
//     //fileName.substring(fileName.lastIndexOf(".")) 这个地方可以直接写死 写成你的上传路径
//        String tempFileName = param.getTaskId() + fileName.substring(fileName.lastIndexOf(".")) + "_tmp";
//        String filePath = basePath + getFilePathByType(param.getObjectType()) + "/original";
//        File fileDir = new File(filePath);
//        if(!fileDir.exists()){
//            fileDir.mkdirs();
//        }
//        File tempFile = new File(filePath,tempFileName);
//        //第一步
//        RandomAccessFile raf = new RandomAccessFile(tempFile,"rw");
//        //第二步
//        FileChannel fileChannel = raf.getChannel();
//        //第三步
//        long offset = param.getChunk() * param.getSize();
//        //第四步
//        byte[] fileData = param.getFile().getBytes();
//        //第五步
//        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,offset,fileData.length);
//        //第六步
//        mappedByteBuffer.put(fileData);
//        //第七步
//        FileUtil.freeMappedByteBuffer(mappedByteBuffer);
//        fileChannel.close();
//        raf.close();
//        //第八步
//        boolean isComplete = checkUploadStatus(param,fileName,filePath);
//        if(isComplete){
//            renameFile(tempFile,fileName);
//        }
//        return "";
//    }
//
//    /**
//     * 文件重命名
//     * @param toBeRenamed   将要修改名字的文件
//     * @param toFileNewName 新的名字
//     * @return
//     */
//    public boolean renameFile(File toBeRenamed, String toFileNewName) {
//        //检查要重命名的文件是否存在，是否是文件
//        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
//            return false;
//        }
//        String p = toBeRenamed.getParent();
//        File newFile = new File(p + File.separatorChar + toFileNewName);
//        //修改文件名
//        return toBeRenamed.renameTo(newFile);
//    }
//
//    /**
//     * 检查文件上传进度
//     * @return
//     */
//    public boolean checkUploadStatus(MultipartFileParam param,String fileName,String filePath) throws IOException {
//        File confFile = new File(filePath,fileName+".conf");
//        RandomAccessFile confAccessFile = new RandomAccessFile(confFile,"rw");
//        //设置文件长度
//        confAccessFile.setLength(param.getChunkTotal());
//        //设置起始偏移量
//        confAccessFile.seek(param.getChunk());
//        //将指定的一个字节写入文件中 127，
//        confAccessFile.write(Byte.MAX_VALUE);
//        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
//        byte isComplete = Byte.MAX_VALUE;
//     //这一段逻辑有点复杂，看的时候思考了好久，创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认的0,已上传的就是Byte.MAX_VALUE 127
//        for(int i = 0; i<completeStatusList.length && isComplete==Byte.MAX_VALUE; i++){
//       // 按位与运算，将&两边的数转为二进制进行比较，有一个为0结果为0，全为1结果为1  eg.3&5  即 0000 0011 & 0000 0101 = 0000 0001   因此，3&5的值得1。
//            isComplete = (byte)(isComplete & completeStatusList[i]);
//            System.out.println("check part " + i + " complete?:" + completeStatusList[i]);
//        }
//        if(isComplete == Byte.MAX_VALUE){
//        //如果全部文件上传完成，删除conf文件
//        confFile.delete();
//            return true;
//        }
//        return false;
//    }
//
//    /**
//   * 根据主体类型，获取每个主题所对应的文件夹路径 我项目内的需求可以忽略
//   * @param objectType
//   * @return filePath 文件路径
//   */
//  private String getFilePathByType(Integer objectType){
//      //不同主体对应的文件夹
//      Map<Integer,String> typeMap = new HashMap<>();
//      typeMap.put(1,"Article");
//      typeMap.put(2,"Question");
//      typeMap.put(3,"Answer");
//      typeMap.put(4,"Courseware");
//      typeMap.put(5,"Lesson");
//      String objectPath = typeMap.get(objectType);
//      if(objectPath==null || "".equals(objectPath)){
//          throw  new ServiceException("主体类型不存在");
//      }
//      return objectPath;
//  }
//
//    /**
//     * 在MappedByteBuffer释放后再对它进行读操作的话就会引发jvm crash，在并发情况下很容易发生
//     * 正在释放时另一个线程正开始读取，于是crash就发生了。所以为了系统稳定性释放前一般需要检 查是否还有线程在读或写
//     * @param mappedByteBuffer
//     */
//    public static void freedMappedByteBuffer(final MappedByteBuffer mappedByteBuffer) {
//        try {
//            if (mappedByteBuffer == null) {
//                return;
//            }
//            mappedByteBuffer.force();
//            AccessController.doPrivileged(new PrivilegedAction<Object>() {
//                @Override
//                public Object run() {
//                    try {
//                        Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner", new Class[0]);
//                        //可以访问private的权限
//                        getCleanerMethod.setAccessible(true);
//                        //在具有指定参数的 方法对象上调用此 方法对象表示的底层方法
//                        sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(mappedByteBuffer,
//                                new Object[0]);
//                        cleaner.clean();
//                    } catch (Exception e) {
//                        logger.error("clean MappedByteBuffer error!!!", e);
//                    }
//                    logger.info("clean MappedByteBuffer completed!!!");
//                    return null;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}