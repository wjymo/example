package com.wjy.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;


//@Component
public class WSServer {

    private static class SingletionWSServer {
        static final WSServer instance = new WSServer();
    }

    public static WSServer getInstance() {
        return SingletionWSServer.instance;
    }

    private EventLoopGroup mainGroup;
    private EventLoopGroup subGroup;
    private ServerBootstrap server;
    private ChannelFuture future;

    public WSServer() {
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
//                .childHandler(new WSServerInitialzer())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        // 通过SocketChannel去获得对应的管道
                        ChannelPipeline pipeline = channel.pipeline();
                        // 通过管道，添加handler
                        // HttpServerCodec是由netty自己提供的助手类，可以理解为拦截器
                        // 当请求到服务端，我们需要做解码，响应到客户端做编码
                        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpObjectAggregator(1024*64));
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        // 添加自定义的助手类，返回 "hello netty~"
//                        pipeline.addLast("customHandler", new CustomHandler2());
                        pipeline.addLast("chatHandler", new ChatHandler());
                    }
                })
        ;
    }

    public void start() {
        this.future = server.bind(8088);
        System.err.println("netty websocket server 启动完毕...");
    }
}
