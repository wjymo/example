package dubborpc.provider;

import dubborpc.custom.NettyClient;
import dubborpc.custom.NettyClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    //这里定义协议头
    public static final String providerName = "HelloService#hello#";
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取客户端发送的消息，并调用服务
        System.out.println("msg=" + msg);
        String param = msg.toString();
        String[] split = param.split(NettyClient.SEPARATE);
        String interfaceClazz = split[0];
        String method = split[1];



        if(StringUtils.startsWith(msg.toString(),providerName)){
            String result = new HelloServiceImpl().hello(StringUtils.substringAfter(msg.toString(), providerName));
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
    }
}
