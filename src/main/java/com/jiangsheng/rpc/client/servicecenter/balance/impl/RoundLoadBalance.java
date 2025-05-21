package com.jiangsheng.rpc.client.servicecenter.balance.impl;

import com.jiangsheng.rpc.client.servicecenter.balance.LoadBalance;

import java.util.List;

public class RoundLoadBalance implements LoadBalance {
    private int choose = -1;
    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose = choose % addressList.size();
        System.out.println("使用轮询负载均衡，选择了" + choose + ": " + addressList.get(choose) + "服务器");
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
