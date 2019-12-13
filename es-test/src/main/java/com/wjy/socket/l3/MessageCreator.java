package com.wjy.socket.l3;

import org.apache.commons.lang3.StringUtils;

public class MessageCreator {
    final private static String SN_HEADER="收到暗号，我是(SN):";
    final private static String PORT_HEADER="请回电端口:";

    public static String buildWithPort(int port){
        return PORT_HEADER+port;
    }

    public static int parsePort(String data){
        if(StringUtils.startsWith(data,PORT_HEADER)){
            return Integer.parseInt(data.substring(PORT_HEADER.length()));
        }
        return -1;
    }

    public static String buildWithSN(String message){
        return SN_HEADER+message;
    }

    public static String parseSn(String data){
        if(StringUtils.startsWith(data,SN_HEADER)){
            return data.substring(SN_HEADER.length());
        }
        return null;
    }


}
