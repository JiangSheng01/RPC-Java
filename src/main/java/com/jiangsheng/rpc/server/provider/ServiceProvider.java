package com.jiangsheng.rpc.server.provider;

import com.jiangsheng.rpc.server.register.ServiceRegister;
import com.jiangsheng.rpc.server.register.impl.ZKServiceRegister;


import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private String host;
    private int port;
    private Map<String, Object> interfaceProvider;
    private ServiceRegister serviceRegister;
    public ServiceProvider(String host, int port) {
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
    }

    public void provideServiceInterface(Object service) {
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        for (Class<?> clazz : interfaceName) {
            interfaceProvider.put(clazz.getName(), service);
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port));
        }
    }
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
