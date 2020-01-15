package testudp.server.fail;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * 消息处理类
 *
 * @author kokJuis
 * @version 1.0
 * @date 2016-9-30
 */
public class UdpChatServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //注意，UDP的通道至始至终只有一个，关了就不能接收了。
        System.out.println("UDP通道已经连接");
        UdpChatServer.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {

        System.out.println("消息来源" + packet.sender().getHostString() + ":" + packet.sender().getPort());

        // 消息处理。。。。。


        //消息发送。。。。
        DatagramPacket dp = new DatagramPacket(Unpooled.copiedBuffer("消息".getBytes()), packet.sender());
        UdpChatServer.channel.writeAndFlush(dp);

    }


}