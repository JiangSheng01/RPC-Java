package com.jiangsheng.rpc.client.proxy;

import com.jiangsheng.rpc.client.RpcClient;
import com.jiangsheng.rpc.client.impl.NettyRpcClient;
import com.jiangsheng.rpc.common.message.RpcRequest;
import com.jiangsheng.rpc.common.message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ClientProxy implements InvocationHandler {

    private RpcClient rpcClient;

    public ClientProxy() throws InterruptedException {
        rpcClient = new NettyRpcClient();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .parameterTypes(method.getParameterTypes()).build();
        RpcResponse response = rpcClient.sendRequest(request);
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }
}
