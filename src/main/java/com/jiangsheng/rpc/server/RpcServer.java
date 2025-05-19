package com.jiangsheng.rpc.server;

public interface RpcServer {
    void start(int port);
    void stop();
}
