package com.js.core.config;

import com.js.common.serializer.Serializer;
import com.js.core.client.servicecenter.balance.impl.ConsistencyHashBalance;
import com.js.core.server.register.impl.ZKServiceRegister;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class JRpcConfig {
    //名称
    private String name = "jrpc";
    //端口
    private Integer port = 9999;
    //主机名
    private String host = "localhost";
    //版本号
    private String version = "1.0.0";
    //注册中心
    private String registry = new ZKServiceRegister().toString();
    //序列化器
    private String serializer = Serializer.getSerializerByCode(3).toString();
    //负载均衡
    private String loadBalance = new ConsistencyHashBalance().toString();

}
