package com.wjy.upload.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileUtils extends org.apache.commons.io.FileUtils {
    public static String getFileSha1(File file) {
        try (FileInputStream in =new FileInputStream(file);){
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024 * 1024 * 10];

            int len = 0;
            while ((len = in.read(buffer)) > 0)
            {
                digest.update(buffer, 0, len);
            }
            String sha1 = new BigInteger(1, digest.digest()).toString(16);
            int length = 40 - sha1.length();
            if (length > 0) {
                for (int i = 0; i < length; i++)
                {
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
}
