package com.jiangsheng.rpc.client.netty.initializer;

import com.jiangsheng.rpc.client.netty.handler.NettyClientHandler;
import com.jiangsheng.rpc.common.serializer.code.MyDecoder;
import com.jiangsheng.rpc.common.serializer.code.MyEncoder;
import com.jiangsheng.rpc.common.serializer.impl.JsonSerializer;
import com.jiangsheng.rpc.common.serializer.impl.ObjectSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyClientHandler());
    }
}
