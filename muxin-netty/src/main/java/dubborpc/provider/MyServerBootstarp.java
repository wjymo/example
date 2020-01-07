package dubborpc.provider;

public class MyServerBootstarp {
    public static void main(String[] args) {
        NettyServer.startServer("127.0.0.1",7000);
    }
}
