package com.jiangsheng.rpc.common.serializer.code;

import com.jiangsheng.rpc.common.message.MessageType;
import com.jiangsheng.rpc.common.message.RpcRequest;
import com.jiangsheng.rpc.common.message.RpcResponse;
import com.jiangsheng.rpc.common.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println(msg.getClass());
        if (msg instanceof RpcRequest) {
            out.writeShort(MessageType.REQUEST.getCode());
        } else if (msg instanceof RpcResponse) {
            out.writeShort(MessageType.RESPONSE.getCode());
        }

        out.writeShort(serializer.getType());
        byte[] serializeBytes = serializer.serialize(msg);
        out.writeInt(serializeBytes.length);
        out.writeBytes(serializeBytes);
    }
}
