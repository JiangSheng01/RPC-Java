package com.js.core;

import com.js.common.util.ConfigUtil;
import com.js.core.config.JRpcConfig;
import com.js.core.config.RpcConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JRpcApplication {
    private static volatile JRpcConfig rpcConfigInstance;

    public static void initialize(JRpcConfig customRpcConfig) {
        rpcConfigInstance = customRpcConfig;
        log.info("RPC 框架初始化，配置 = {}", customRpcConfig);
    }

    public static void initialize() {
        JRpcConfig customRpcConfig;
        try {
            customRpcConfig = ConfigUtil.loadConfig(JRpcConfig.class, RpcConstant.CONFIG_FILE_PREFIX);
            log.info("成功加载配置文件，配置文件名称 = {}", RpcConstant.CONFIG_FILE_PREFIX); // 添加成功加载的日志
        } catch (Exception e) {
            // 配置加载失败，使用默认配置
            customRpcConfig = new JRpcConfig();
            log.warn("配置加载失败，使用默认配置");
        }
        initialize(customRpcConfig);
    }

    public static JRpcConfig getRpcConfig() {
        if (rpcConfigInstance == null) {
            synchronized (JRpcApplication.class) {
                if (rpcConfigInstance == null) {
                    initialize();  // 确保在第一次调用时初始化
                }
            }
        }
        return rpcConfigInstance;
    }
}