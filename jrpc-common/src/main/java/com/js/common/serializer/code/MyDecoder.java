package com.js.common.serializer.code;

import com.js.common.exception.SerializeException;
import com.js.common.message.MessageType;
import com.js.common.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class MyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 6) {  // messageType + serializerType + length
            return;
        }

        short messageType = in.readShort();
        if (messageType != MessageType.REQUEST.getCode() && messageType != MessageType.RESPONSE.getCode()) {
            log.warn("暂不支持此种数据, messageType={}", messageType);
            return;
        }

        short serializerType = in.readShort();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if (serializer == null) {
            log.error("不存在对应的序列化器, serializerType={}", serializerType);
            throw new SerializeException("不存在对应的序列化器, serializerType=" + serializerType);
        }

        int length = in.readInt();
        if (in.readableBytes() < length) {
            return; // 数据不完整，等待更多数据
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        log.debug("Received bytes {}", Arrays.toString(bytes));
        Object deserialize = serializer.deserialize(bytes, messageType);
        out.add(deserialize);
    }
}
