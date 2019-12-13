package com.wjy.socket.l3;


import lombok.ToString;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UDPSearcher {
    final private static int LISTEN_PORT=3000;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher Started.");
        Listen listen = listen();
        sendBroadcast();

        System.in.read();
        List<Device> devices = listen.getDevicesAndClose();
        for (Device device : devices) {
            System.out.println("Device:" + device.toString());
        }
        // 完成
        System.out.println("UDPSearcher Finished.");
    }

    private static Listen listen() throws InterruptedException {
        System.out.println("UDPSearcher start listen.");
        Listen listen=new Listen(LISTEN_PORT);
        new Thread(listen).start();
        return listen;
    }
    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast started.");
        DatagramSocket datagramSocket=new DatagramSocket();

        String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
        byte[] bytes = requestData.getBytes();
        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length);
        datagramPacket.setPort(20000);
        datagramPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
        System.out.println("UDPSearcher sendBroadcast finished.");
    }
    private static class Listen implements Runnable{
        final private int listenPort;
        private boolean done =false;
        private List<Device> devices=new ArrayList<>();
        private DatagramSocket ds;

        private Listen(int listenPort) {
            this.listenPort = listenPort;
        }

        @Override
        public void run() {
            try {
                ds=new DatagramSocket(listenPort);
                while (!done){
                    byte[] bytes=new byte[1024];
                    DatagramPacket receive=new DatagramPacket(bytes,bytes.length);
                    ds.receive(receive);
                    String ip = receive.getAddress().getHostAddress();
                    int port = receive.getPort();
                    int length = receive.getLength();
                    String data = new String(receive.getData(), 0, length);

                    System.out.println("UDPSearcher receive form ip:" + ip
                            + "\tport:" + port + "\tdata:" + data);
                    String sn = MessageCreator.parseSn(data);
                    if(sn!=null){
                        Device device=new Device(port,ip,sn);
                        devices.add(device);
                    }


                }
            } catch (Exception e) {
//                e.printStackTrace();
            }finally {
                close();
            }
            System.out.println("UDPSearcher listener finished.");
        }

        public void close(){
            if(ds!=null){
                ds.close();
                ds=null;
            }
        }
        public List<Device> getDevicesAndClose(){
            done=true;
            close();
            return devices;

        }
    }

    @ToString
    private static class Device {
        final int port;
        final String ip;
        final String sn;

        private Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }
    }
}
