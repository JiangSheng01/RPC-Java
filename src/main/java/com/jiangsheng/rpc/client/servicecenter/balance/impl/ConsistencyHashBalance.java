package com.jiangsheng.rpc.client.servicecenter.balance.impl;

import com.jiangsheng.rpc.client.servicecenter.balance.LoadBalance;

import java.util.*;

public class ConsistencyHashBalance implements LoadBalance {
    private static final int VIRTUAL_NUM = 5;
    private SortedMap<Integer, String> shards = new TreeMap<Integer, String>();
    private List<String> realNodes = new LinkedList<String>();
    private String[] servers;

    private void init(List<String> servers) {
        for (String server : servers) {
            realNodes.add(server);
            System.out.println("真实节点 [" + server + "] 被添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点 [" + virtualNode + "] hash:" + hash + ", 被添加");
            }
        }
    }

    public String getServer(String node, List<String> serviceList) {
        init(serviceList);
        int hash = getHash(node);
        Integer key = null;
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            key = shards.lastKey();
        } else {
            key = subMap.firstKey();
        }
        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    @Override
    public String balance(List<String> addressList) {
        String random = UUID.randomUUID().toString();
        return getServer(random, addressList);
    }

    @Override
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            System.out.println("真实节点 [" + node + "] 上线添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点 [" + virtualNode + "] hash:" + hash + ", 被添加");
            }
        }
    }

    @Override
    public void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            System.out.println("真实节点 [" + node + "] 下线移除");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                System.out.println("虚拟节点 [" + virtualNode + "] hash:" + hash + ", 被移除");
            }
        }
    }

    private static int getHash(String str) {
        final int prime = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * prime;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }
}
