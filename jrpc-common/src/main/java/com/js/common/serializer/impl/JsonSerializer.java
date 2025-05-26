package com.js.common.serializer.impl;

import com.js.common.message.RpcRequest;
import com.js.common.message.RpcResponse;
import com.js.common.serializer.Serializer;
import com.alibaba.fastjson.JSONObject;

public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        switch (messageType) {
            case 0:
                RpcRequest request = JSONObject.parseObject(bytes, RpcRequest.class);
                Object[] objects = new Object[request.getParams().length];
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramsType = request.getParameterTypes()[i];
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())) {
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParams()[i], paramsType);
                    } else {
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj = request;
                break;
            case 1:
                RpcResponse response = JSONObject.parseObject(bytes, RpcResponse.class);
                Class<?> dataType = response.getDataType();
                if (!dataType.isAssignableFrom(response.getData().getClass())) {
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(), dataType));
                }
                obj = response;
                break;
            default:
                System.out.println("暂不支持这种消息");
                throw new RuntimeException();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public String toString() {
        return "Json";
    }
}
