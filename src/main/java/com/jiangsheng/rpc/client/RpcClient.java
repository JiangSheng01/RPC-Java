package com.jiangsheng.rpc.client;

import com.jiangsheng.rpc.common.message.RpcRequest;
import com.jiangsheng.rpc.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
