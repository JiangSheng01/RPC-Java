package com.js.core.client.rpcclient.impl;

import com.js.common.message.RpcRequest;
import com.js.common.message.RpcResponse;
import com.js.core.client.netty.initializer.NettyClientInitializer;
import com.js.core.client.rpcclient.RpcClient;
import com.js.core.client.servicecenter.ServiceCenter;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClient implements RpcClient {

    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private InetSocketAddress address;

    static {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    public NettyRpcClient(InetSocketAddress inetSocketAddress) {
        address = inetSocketAddress;
    }
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        if (address == null) {
            log.error("服务发现失败，返回的地址为 null");
            return RpcResponse.fail("服务发现失败，地址为 null");
        }

        String host = address.getHostName();
        int port = address.getPort();
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();

            if (response == null) {
                log.error("服务响应为空，可能是请求失败或超时");
                return RpcResponse.fail("服务响应为空");
            }
            log.info("收到响应: {}", response);
            return response;
        } catch (InterruptedException e) {
            log.error("请求被中断，发送请求失败: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("发送请求时发生异常: {}", e.getMessage(), e);
        }
        return RpcResponse.fail("请求失败");
    }

    @Override
    public void close() {
        try {
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully().sync();
            }
        } catch (InterruptedException e) {
            log.error("关闭 Netty 资源时发生异常: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
