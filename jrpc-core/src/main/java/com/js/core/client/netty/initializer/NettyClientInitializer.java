package com.js.core.client.netty.initializer;

import com.js.common.serializer.Serializer;
import com.js.core.client.netty.handler.NettyClientHandler;
import com.js.common.serializer.code.MyDecoder;
import com.js.common.serializer.code.MyEncoder;
import com.js.core.client.netty.handler.HeartbeatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        try {
            pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(3)));
            pipeline.addLast(new IdleStateHandler(0, 8, 0, TimeUnit.SECONDS));
            pipeline.addLast(new HeartbeatHandler());
            pipeline.addLast(new MyDecoder());
            pipeline.addLast(new NettyClientHandler());
            log.info("Netty client pipeline initialized with serializer type: {}",Serializer.getSerializerByCode(3).toString());
        } catch (Exception e) {
            log.error("Error initializing Netty client pipeline", e);
            throw e;  // 重新抛出异常，确保管道初始化失败时处理正确
        }

    }
}
