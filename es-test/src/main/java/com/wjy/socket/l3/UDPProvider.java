package com.wjy.socket.l3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

public class UDPProvider {
    public static void main(String[] args) throws IOException {
        String sn= UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        new Thread(provider).start();

        System.in.read();
        provider.exit();
    }

    private static class Provider implements Runnable{
        final private String snMessage;
        private boolean done=false;
        private DatagramSocket ds=null;

        public Provider(String snMessage) {
            this.snMessage = snMessage;
        }

        @Override
        public void run() {
            System.out.println("UDPProvider start");
            try {
                ds=new DatagramSocket(20000);
                while (!done){
                    final byte[] buf=new byte[1024];
                    DatagramPacket datagramPacket=new DatagramPacket(buf,buf.length);
                    ds.receive(datagramPacket);
                    String ip = datagramPacket.getAddress().getHostAddress();
                    int port = datagramPacket.getPort();
                    int length = datagramPacket.getLength();
                    String data = new String(datagramPacket.getData(), 0, length);
                    System.out.println("UDPProvider receive form ip:" + ip
                            + "\tport:" + port + "\tdata:" + data);

                    int parsePort = MessageCreator.parsePort(data);
                    if(parsePort!=-1){
                        String responseData=MessageCreator.buildWithSN(snMessage);
                        byte[] bytes = responseData.getBytes();
                        DatagramPacket response=new DatagramPacket(bytes,bytes.length,datagramPacket.getAddress(),parsePort);
                        ds.send(response);
                    }
                }
            }catch (Exception ignored) {
//                ignored.printStackTrace();
            } finally {
                close();
            }
            System.out.println("UDPProvider Finished.");
        }

        public void exit(){
            done=true;
            close();
        }
        public void close(){
            if(ds!=null){
                ds.close();
                ds=null;
            }
        }
    }
}
