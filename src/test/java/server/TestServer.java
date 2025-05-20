package server;

import com.jiangsheng.rpc.common.service.impl.UserServiceImpl;
import com.jiangsheng.rpc.server.RpcServer;
import com.jiangsheng.rpc.server.impl.NettyRpcServer;
import com.jiangsheng.rpc.server.provider.ServiceProvider;

public class TestServer {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);
        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
