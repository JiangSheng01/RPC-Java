package com.js.core.client.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ServiceCache {
    private static Map<String, List<String>> cache = new HashMap<>();

    public void addServiceToCache(String serviceName, String address) {
        if (cache.containsKey(serviceName)) {
            List<String> addressList = cache.get(serviceName);
            addressList.add(address);
            log.info("有服务名情况，将name为" + serviceName + "的服务的地址" + address + "添加到本地缓存中");
        } else {
            List<String> addressList = new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName, addressList);
            log.info("无服务名情况，将name为" + serviceName + "和地址为" + address + "的服务添加到本地缓存中");
        }
    }

    public void replaceServiceAddress(String serviceName, String oldAddress, String newAddress) {
        if (cache.containsKey(serviceName)) {
            List<String> addressList = cache.get(serviceName);
            addressList.remove(oldAddress);
            addressList.add(newAddress);
            log.info("将服务{}的地址{}替换为{}", serviceName, oldAddress, newAddress);
        } else {
            log.error("不存在该服务{}", serviceName);
        }
    }

    public List<String> getServiceListFromCache(String serviceName) {
        if (!cache.containsKey(serviceName)) {
            log.warn("服务{}未找到", serviceName);
            return Collections.emptyList();
        }
        return cache.get(serviceName);
    }

    public void deleteServiceAddress(String serviceName, String address) {
        List<String> addressList = cache.get(serviceName);
        if (addressList != null && addressList.contains(address)) {
            addressList.remove(address);
            log.info("将name为{}中地址为{}的服务从本地缓存中删除", serviceName, address);
            if (addressList.isEmpty()) {
                cache.remove(serviceName);
                log.info("服务{}的地址列表为空，已从缓存中清除", serviceName);
            }
        } else {
            log.warn("删除失败，地址{}不在服务{}的地址列表中", address, serviceName);
        }
    }
}
