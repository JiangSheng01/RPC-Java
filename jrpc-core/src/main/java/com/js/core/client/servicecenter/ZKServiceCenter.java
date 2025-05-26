package com.js.core.client.servicecenter;


import com.js.common.message.RpcRequest;
import com.js.core.client.cache.ServiceCache;
import com.js.core.client.servicecenter.balance.LoadBalance;
import com.js.core.client.servicecenter.balance.impl.ConsistencyHashBalance;
import com.js.core.client.servicecenter.zkwatcher.WatchZK;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class ZKServiceCenter implements ServiceCenter{
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY_PATH = "CanRetry";
    private ServiceCache cache;
    private final LoadBalance loadBalance = new ConsistencyHashBalance();
    //保证线程安全使用CopyOnWriteArraySet
    private Set<String> retryServiceCache = new CopyOnWriteArraySet<>();

    public ZKServiceCenter() throws InterruptedException{
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        log.info("zookeeper连接成功");
        this.cache = new ServiceCache();
        WatchZK watcher = new WatchZK(client, cache);
        watcher.watchToUpdate(ROOT_PATH);
    }

    @Override
    public InetSocketAddress serviceDiscovery(RpcRequest request) {
        String serviceName = request.getInterfaceName();
        try{
            List<String> serviceList = cache.getServiceListFromCache(serviceName);
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/" + serviceName);

                List<String> cachedAddresses = cache.getServiceListFromCache(serviceName);
                if (cachedAddresses == null || cachedAddresses.isEmpty()) {
                    // 假设 addServiceToCache 方法可以处理单个地址
                    for (String address : serviceList) {
                        cache.addServiceToCache(serviceName, address);
                    }
                }
            }
            if (serviceList.isEmpty()) {
                log.warn("未找到服务：{}", serviceName);
                return null;
            }
            String address = loadBalance.balance(serviceList);
            return parseAddress(address);
        } catch (Exception e) {
            log.error("服务发现失败，服务名：{}", serviceName, e);
        }
        return null;
    }

    @Override
    public boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature) {
        if (retryServiceCache.isEmpty()) {
            try {
                CuratorFramework rootClient = client.usingNamespace(RETRY_PATH);
                List<String> retryableMethods = rootClient.getChildren().forPath("/" + getServiceAddress(serviceAddress));
                retryServiceCache.addAll(retryableMethods);
            } catch (Exception e) {
                log.error("检查重试失败，方法签名：{}", methodSignature, e);
            }
        }
        return retryServiceCache.contains(methodSignature);
    }

    @Override
    public void close() {
        client.close();
    }

    private String getServiceAddress(InetSocketAddress serviceAddress)  {
        return serviceAddress.getHostString() + ":" + serviceAddress.getPort();
    }
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
