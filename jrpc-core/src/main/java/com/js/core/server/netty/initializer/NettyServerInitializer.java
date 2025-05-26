package com.js.core.server.netty.initializer;


import com.js.common.serializer.Serializer;
import com.js.common.serializer.code.MyDecoder;
import com.js.common.serializer.code.MyEncoder;
import com.js.common.serializer.impl.JsonSerializer;
import com.js.core.server.netty.handler.NettyServerHandler;
import com.js.core.server.provider.ServiceProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(3)));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyServerHandler(serviceProvider));
    }
}
