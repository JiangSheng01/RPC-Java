package com.jiangsheng.rpc.client.servicecenter;

import com.jiangsheng.rpc.client.cache.ServiceCache;
import com.jiangsheng.rpc.client.servicecenter.balance.impl.ConsistencyHashBalance;
import com.jiangsheng.rpc.client.servicecenter.zkwatcher.WatchZK;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceCenter implements ServiceCenter{
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY_PATH = "CanRetry";
    private ServiceCache cache;

    public ZKServiceCenter() throws InterruptedException{
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper连接成功");
        this.cache = new ServiceCache();
        WatchZK watcher = new WatchZK(client, cache);
        watcher.watchToUpdate(ROOT_PATH);
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try{
            List<String> serviceList = cache.getServiceFromCache(serviceName);
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/" + serviceName);
            }
            String address = new ConsistencyHashBalance().balance(serviceList);
            return parseAddress(address);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serviceList = client.getChildren().forPath("/" + RETRY_PATH);
            for (String s : serviceList) {
                if (s.equals(serviceName)) {
                    System.out.println("服务" + serviceName + "在白名单上，可进行重试");
                    canRetry = true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return canRetry;
    }

    private String getServiceAddress(InetSocketAddress serviceAddress)  {
        return serviceAddress.getHostString() + ":" + serviceAddress.getPort();
    }
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
