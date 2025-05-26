package com.js.common.serializer.code;

import com.js.common.message.MessageType;
import com.js.common.message.RpcRequest;
import com.js.common.message.RpcResponse;
import com.js.common.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        log.debug("Encoding message of type: {}", msg.getClass());

        if (msg instanceof RpcRequest) {
            out.writeShort(MessageType.REQUEST.getCode());
        } else if (msg instanceof RpcResponse) {
            out.writeShort(MessageType.RESPONSE.getCode());
        } else {
            log.error("Unknown message type: {}", msg.getClass());
            throw new IllegalArgumentException("Unknown message type: " + msg.getClass());
        }

        out.writeShort(serializer.getType());
        byte[] serializeBytes = serializer.serialize(msg);
        if (serializeBytes == null || serializeBytes.length == 0) {
            throw new IllegalArgumentException("Serialize bytes is null or empty");
        }
        out.writeInt(serializeBytes.length);
        out.writeBytes(serializeBytes);
    }
}
