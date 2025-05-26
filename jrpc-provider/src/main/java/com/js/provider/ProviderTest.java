package com.js.provider;

import com.js.api.service.UserService;

import com.js.core.JRpcApplication;
import com.js.core.server.provider.ServiceProvider;
import com.js.core.server.rpcserver.RpcServer;
import com.js.core.server.rpcserver.impl.NettyRpcServer;
import com.js.provider.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProviderTest {

    public static void main(String[] args) throws InterruptedException {
        JRpcApplication.initialize();
        String ip= JRpcApplication.getRpcConfig().getHost();
        int port=JRpcApplication.getRpcConfig().getPort();
        // 创建 UserService 实例
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider(ip, port);
        // 发布服务接口到 ServiceProvider
        serviceProvider.provideServiceInterface(userService);  // 可以设置是否支持重试

        // 启动 RPC 服务器并监听端口
        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(port);  // 启动 Netty RPC 服务，监听 port 端口
        log.info("RPC 服务端启动，监听端口" + port);
    }

}