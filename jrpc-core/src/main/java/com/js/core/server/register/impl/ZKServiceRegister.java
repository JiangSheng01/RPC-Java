package com.js.core.server.register.impl;

import com.js.api.annotation.Retryable;
import com.js.core.server.register.ServiceRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ZKServiceRegister implements ServiceRegister {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY_PATH = "CanRetry";

    public ZKServiceRegister() {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        client.start();
        log.info("zookeeper连接成功");
    }

    @Override
    public void register(Class<?> clazz, InetSocketAddress serverAddress) {
        String serviceName = clazz.getName();
        try {
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/" + serviceName);
                log.info("服务节点 {} 创建成功", "/" + serviceName);
            }

            String path = "/" + serviceName + "/" + getServiceAddress(serverAddress);
            if (client.checkExists().forPath(path) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                log.info("服务地址 {} 注册成功", path);
            } else {
                log.info("服务地址 {} 已经存在，跳过注册", path);
            }

            List<String> retryableMethods = getRetryableMethod(clazz);
            log.info("可重试的方法: {}", retryableMethods);

            CuratorFramework retryClient = client.usingNamespace(RETRY_PATH);
            for (String retryableMethod : retryableMethods) {
                retryClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path + "/" + retryableMethod);
            }
        } catch (Exception e) {
            log.error("服务注册失败，服务名：{}，错误信息：{}", serviceName, e.getMessage(), e);
        }
    }

    private List<String> getRetryableMethod(Class<?> clazz) {
        List<String> retryableMethods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Retryable.class)) {
                String methodSignature = getMethodSignature(clazz, method);
                retryableMethods.add(methodSignature);
            }
        }
        return retryableMethods;
    }

    private String getMethodSignature(Class<?> clazz, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getName()).append("#").append(method.getName()).append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            sb.append(parameterTypes[i].getName());
            if (i < parameterTypes.length - 1) {
                sb.append(",");
            } else {
                sb.append(")");
            }
        }
        return sb.toString();
    }

    private String getServiceAddress(InetSocketAddress serviceAddress)  {
        return serviceAddress.getHostString() + ":" + serviceAddress.getPort();
    }

    @Override
    public String toString() {
        return "zookeeper";
    }
}
