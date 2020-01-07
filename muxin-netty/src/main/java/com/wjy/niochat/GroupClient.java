package com.wjy.niochat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GroupClient {
    private final String HOST = "127.0.0.1"; // 服务器的ip
    private SocketChannel socketChannel;
    private Selector selector;
    private final int PORT=6667;
    private String username;

    public GroupClient() {
        try {
            selector=Selector.open();
            socketChannel=socketChannel.open(new InetSocketAddress(HOST,PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            username=socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(username+"is ok..");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取从服务器端回复的消息
    public void readInfo() {
        try {
            int count = selector.select(1000);
            if (count > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()) {
                        //得到相关的通道
                        SocketChannel sc = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        sc.read(buffer);
                        //把读到的缓冲区的数据转成字符串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupClient groupClient = new GroupClient();
        new Thread(()->{
           while (true){
               groupClient.readInfo();
               try {
                   TimeUnit.SECONDS.sleep(2);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String next = scanner.next();
            groupClient.sendInfo(next);
        }
    }
    //向服务器发送消息
    public void sendInfo(String info) {

        info = username + " 说：" + info;

        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
