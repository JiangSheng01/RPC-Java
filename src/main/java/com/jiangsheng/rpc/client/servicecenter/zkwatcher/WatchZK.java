package com.jiangsheng.rpc.client.servicecenter.zkwatcher;

import com.jiangsheng.rpc.client.cache.ServiceCache;
import lombok.AllArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

@AllArgsConstructor
public class WatchZK {
    private CuratorFramework client;
    private ServiceCache cache;

    public void watchToUpdate(String path) throws InterruptedException {
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                switch (type.name()) {
                    case "NODE_CREATED":
                        String[] pathList = parsePath(childData1);
                        if (pathList.length <= 2) break;
                        else {
                            String serviceName = pathList[1];
                            String address = pathList[2];
                            cache.addServiceToCache(serviceName, address);
                        }
                        break;
                    case "NODE_CHANGED":
                        if (childData.getData() != null) {
                            System.out.println("修改前的数据" + new String(childData.getData()));
                        } else {
                            System.out.println("节点第一次赋值");
                        }
                        String[] oldPathList = parsePath(childData);
                        String[] newPathList = parsePath(childData1);
                        cache.replaceServiceAddress(oldPathList[1], oldPathList[2], newPathList[2]);
                        System.out.println("修改后的数据：" + new String(childData1.getData()));
                        break;
                    case "NODE_DELETED":
                        String[] delPathList = parsePath(childData);
                        if(delPathList.length <= 2) break;
                        else {
                            String serviceName = delPathList[1];
                            String address = delPathList[2];
                            cache.deleteServiceAddress(serviceName, address);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        curatorCache.start();
    }

    private String[] parsePath(ChildData childData) {
        return childData.getPath().split("/");
    }
}
