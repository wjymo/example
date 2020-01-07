package dubborpc.custom;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {
    public static final String SEPARATE="@@";
    //创建线程池
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static NettyClientHandler nettyClientHandler;
    private int count=0;

    public Object getBean(Class<?> serviceClass,String providerName){
        Object o = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                , new Class<?>[]{serviceClass}
                , (proxy, method, args) -> {
                    System.out.println("method:"+method);
                    System.out.println("(proxy, method, args) 进入...." + (++count) + " 次");
                    if(nettyClientHandler==null){
                        initClient();
                    }
                    String name = method.getName();
                    String s = method.toString();
                    Class<?> declaringClass = method.getDeclaringClass();

                    //设置要发给服务器端的信息
                    //providerName 协议头 args[0] 就是客户端调用api hello(???), 参数
                    nettyClientHandler.setParam(declaringClass.toString()+SEPARATE+name+SEPARATE+providerName + args[0]);
                    Object result = executor.submit(nettyClientHandler).get();
                    return result;
                });
        return o;
    }

    //初始化客户端
    private static void initClient() {
        nettyClientHandler=new NettyClientHandler();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap=new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(nettyClientHandler);
                    }
                })
                .option(ChannelOption.TCP_NODELAY,true);

        try {
            bootstrap.connect("127.0.0.1",7000).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
