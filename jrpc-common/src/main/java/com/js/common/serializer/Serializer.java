package com.js.common.serializer;
import com.js.common.serializer.impl.*;

import java.util.HashMap;
import java.util.Map;

public interface Serializer {
    byte[] serialize(Object obj);
    Object deserialize(byte[] bytes, int messageType);
    int getType();
    static final Map<Integer, Serializer> serializerMap = new HashMap<>();

    static Serializer getSerializerByCode(int code) {
        if (serializerMap.isEmpty()) {
            serializerMap.put(0, new ObjectSerializer());
            serializerMap.put(1, new JsonSerializer());
            serializerMap.put(2, new KryoSerializer());
            serializerMap.put(3, new HessianSerializer());
            serializerMap.put(4, new ProtostuffSerializer());
        }
        return serializerMap.get(code);
    }
}
