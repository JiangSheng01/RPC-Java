package com.jiangsheng.rpc.server.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress serverAddress, boolean canRetry);
}
