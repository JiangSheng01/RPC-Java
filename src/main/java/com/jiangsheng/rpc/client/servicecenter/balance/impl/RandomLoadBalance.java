package com.jiangsheng.rpc.client.servicecenter.balance.impl;

import com.jiangsheng.rpc.client.servicecenter.balance.LoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        Random random = new Random();
        int choose = random.nextInt(addressList.size());
        System.out.println("使用随机负载均衡，选择了" + choose + ": " + addressList.get(choose) + "服务器");
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
