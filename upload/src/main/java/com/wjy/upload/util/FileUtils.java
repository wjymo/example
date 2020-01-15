package com.wjy.upload.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class FileUtils extends org.apache.commons.io.FileUtils {

    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static MessageDigest messagedigest = null;

    public static String getFileSha1(File file) {
        try (FileInputStream in =new FileInputStream(file);){
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024 * 1024 * 10];

            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            String sha1 = new BigInteger(1, digest.digest()).toString(16);
            int length = 40 - sha1.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    sha1 = "0" + sha1;
                }
            }
            return sha1;
        }
        catch (IOException e) {
            System.out.println(e);
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * 对一个文件获取md5值
     *
     * @return md5串
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(File file) throws IOException,
            NoSuchAlgorithmException {

        messagedigest = MessageDigest.getInstance("MD5");
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                file.length());
        messagedigest.update(byteBuffer);
        return bufferToHex(messagedigest.digest());
    }

    /**
     * @param target 字符串 求一个字符串的md5值
     * @return md5 value
     */
    public static String StringMD5(String target) {
        return DigestUtils.md5Hex(target);
    }
    /***
     * 计算SHA1码
     *
     * @return String 适用于上G大的文件
     * @throws NoSuchAlgorithmException
     * */
    public static String getSha1ByNio(File file) throws OutOfMemoryError,
            IOException, NoSuchAlgorithmException {
        messagedigest = MessageDigest.getInstance("SHA-1");
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                file.length());
        messagedigest.update(byteBuffer);
        return bufferToHex(messagedigest.digest());
    }

    /***
     * 计算SHA1码
     *
     * @return String 适用于2G以上大的文件
     * @throws NoSuchAlgorithmException
     * */
    public static String getSha1ByNio2(File file) throws OutOfMemoryError,
            IOException, NoSuchAlgorithmException {
        messagedigest = MessageDigest.getInstance("SHA-1");
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        long _4G = 1L << 32;
        long _1G=_4G/4;
        long fileSize = file.length();
        if(fileSize>_1G){
            //512m
            long length = 1L << 29;
            long cur = 0L;
            MappedByteBuffer mappedByteBuffer;
            while (cur < fileSize) {
                mappedByteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, cur, length);
                messagedigest.update(mappedByteBuffer);
                cur += length;
            }
            return bufferToHex(messagedigest.digest());
        }else {
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            messagedigest.update(byteBuffer);
            return bufferToHex(messagedigest.digest());
        }
    }




    /**
     * @Description 计算二进制数据
     * @return String
     * */
    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    /**
     * 获取指定目录下的所有文件(不包括子文件夹)
     *
     * @param dirPath
     * @return
     */
    public static List<File> getDirFiles(String dirPath) {
        File path = new File(dirPath);
        File[] fileArr = path.listFiles();
        ArrayList<File> files = new ArrayList<>();

        for (File f : fileArr) {
            if (f.isFile()) {
                files.add(f);
            }
        }
        return files;
    }

    /**
     * 根据文件名，比较文件
     *
     * @author yjmyzz@126.com
     *
     */
    public static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
//            return o1.getName().compareToIgnoreCase(o2.getName());
            String o1Name = o1.getName();
            String o2Name = o2.getName();
            int intO1 = 0;
            int intO2 = 0;
            try {
                intO1 = Integer.parseInt(o1Name);
                intO2 = Integer.parseInt(o2Name);
            } catch (NumberFormatException e) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
            return intO1-intO2;
        }
    }





    /**
     * 大文件获取 SHA1 防止内存溢出
     * @param file
     * @return
     */
    public static String getSHA1Value(File file){
        StringBuilder builder = new StringBuilder();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            MappedByteBuffer mappedByteBuffer = null;
            long bufferSize = 1024*1024*2;//每2M 读取一次，防止内存溢出
            long fileLength = file.length();//文件大小
            long lastBuffer = fileLength%bufferSize;//文件最后不足2M 的部分
            long bufferCount = fileLength/bufferSize;//
            for(int b = 0; b < bufferCount; b++){//分块映射
                mappedByteBuffer = fileInputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, b*bufferSize, bufferSize);//使用内存映射而不是直接用IO读取文件，加快读取速度
                messageDigest.update(mappedByteBuffer);
            }
            if(lastBuffer != 0){
                mappedByteBuffer = fileInputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, bufferCount*bufferSize, lastBuffer);
                messageDigest.update(mappedByteBuffer);
            }
            byte[] digest =messageDigest.digest();
            String hexString = "";
            for(int i =0; i < digest.length; i ++){
                hexString = Integer.toHexString(digest[i]&0xFF);//转16进制数，再转成哈希码
                if(hexString.length()<2){
                    builder.append(0);
                }
                builder.append(hexString);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fileInputStream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return builder.toString();

    }
}
