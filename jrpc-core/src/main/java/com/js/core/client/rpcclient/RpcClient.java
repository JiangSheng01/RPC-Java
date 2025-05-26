package com.js.core.client.rpcclient;

import com.js.common.message.RpcRequest;
import com.js.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
    void close();
}
