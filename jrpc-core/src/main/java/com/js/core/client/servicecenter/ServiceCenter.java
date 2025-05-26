package com.js.core.client.servicecenter;


import com.js.common.message.RpcRequest;

import java.net.InetSocketAddress;

public interface ServiceCenter {
    InetSocketAddress serviceDiscovery(RpcRequest request);
    boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature);
    void close();
}
