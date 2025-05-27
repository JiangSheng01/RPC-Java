package com.js.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;
    private RequestType type = RequestType.NORMAL;

    public static RpcRequest heartbeat() {
        return RpcRequest.builder().type(RequestType.HEARTBEAT).build();
    }
}
