package com.jiangsheng.rpc.common.serializer;

import com.jiangsheng.rpc.common.serializer.impl.ObjectSerializer;
import com.jiangsheng.rpc.common.serializer.impl.JsonSerializer;

public interface Serializer {
    byte[] serialize(Object obj);
    Object deserialize(byte[] bytes, int messageType);
    int getType();
    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
