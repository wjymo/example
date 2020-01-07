package dubborpc.custom;

import dubborpc.publicinterface.HelloService;

public class ClientBootstrap {
    //这里定义协议头
    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) throws InterruptedException {
        NettyClient custom=new NettyClient();
        HelloService helloService = (HelloService) custom.getBean(HelloService.class, providerName);

        while (true){
            Thread.sleep(3 * 1000);
            String res = helloService.hello("我是胡尧");
            System.out.println("调用的结果 res= " + res);
        }
    }
}
