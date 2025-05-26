package com.js.consumer;


import com.js.common.util.ConfigUtil;
import com.js.core.config.JRpcConfig;

public class ConsumerTestConfig {
    public static void main(String[] args) {
        JRpcConfig rpc = ConfigUtil.loadConfig(JRpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}