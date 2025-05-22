package com.jiangsheng.rpc.client.proxy;

import com.jiangsheng.rpc.client.RpcClient;
import com.jiangsheng.rpc.client.circuitbreaker.CircuitBreaker;
import com.jiangsheng.rpc.client.circuitbreaker.CircuitBreakerProvider;
import com.jiangsheng.rpc.client.impl.NettyRpcClient;
import com.jiangsheng.rpc.client.retry.GuavaRetry;
import com.jiangsheng.rpc.client.servicecenter.ServiceCenter;
import com.jiangsheng.rpc.client.servicecenter.ZKServiceCenter;
import com.jiangsheng.rpc.common.message.RpcRequest;
import com.jiangsheng.rpc.common.message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ClientProxy implements InvocationHandler {

    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;
    private CircuitBreakerProvider circuitBreakerProvider;

    public ClientProxy() throws InterruptedException {
        serviceCenter = new ZKServiceCenter();
        rpcClient = new NettyRpcClient(serviceCenter);
        circuitBreakerProvider = new CircuitBreakerProvider();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .parameterTypes(method.getParameterTypes()).build();

        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        if (!circuitBreaker.allowRequest()) {
            return  null;
        }
        RpcResponse response;
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            response = new GuavaRetry().sendServiceWithRetry(request, rpcClient);
        } else {
            response = rpcClient.sendRequest(request);
        }
        if (response.getCode() == 200) {
            circuitBreaker.recordSuccess();
        } else if (response.getCode() == 500) {
            circuitBreaker.recordFailure();
        }
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }
}
