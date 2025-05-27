package com.js.core.server.netty.handler;


import com.js.common.message.RequestType;
import com.js.common.message.RpcRequest;
import com.js.common.message.RpcResponse;
import com.js.core.server.provider.ServiceProvider;
import com.js.core.server.ratelimit.RateLimit;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        if (request == null) {
            log.error("接收到非法请求，request 为空");
            return;
        }
        if (request.getType() == RequestType.HEARTBEAT) {
            log.info("接收到来自客户端的心跳包");
            return;
        }
        if (request.getType() == RequestType.NORMAL) {
            RpcResponse response = getResponse(request);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理请求时发生异常", cause);
        ctx.close();
    }

    private RpcResponse getResponse(RpcRequest rpcRequest) {
        String interfaceName = rpcRequest.getInterfaceName();

        RateLimit rateLimit = serviceProvider.getRateLimitProvider().getRateLimit(interfaceName);
        if (!rateLimit.getToken()) {
            log.warn("服务限流，接口: {}", interfaceName);
            return RpcResponse.fail("服务限流，接口 " + interfaceName + " 当前无法处理请求。请稍后再试。");
        }

        Object service = serviceProvider.getService(interfaceName);
        Method method = null;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object invoke = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.warn("服务限流，接口: {}", interfaceName);
            return RpcResponse.fail("服务限流，接口 " + interfaceName + " 当前无法处理请求。请稍后再试。");
        }
    }
}
