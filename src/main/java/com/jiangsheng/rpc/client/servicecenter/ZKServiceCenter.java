package com.jiangsheng.rpc.client.servicecenter;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceCenter implements ServiceCenter{
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";

    public ZKServiceCenter() {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper连接成功");
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try{
            List<String> strings = client.getChildren().forPath("/" + serviceName);
            String string = strings.get(0);
            return parseAddress(string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private String getServiceAddress(InetSocketAddress serviceAddress)  {
        return serviceAddress.getHostString() + ":" + serviceAddress.getPort();
    }
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
