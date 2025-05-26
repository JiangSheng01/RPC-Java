package com.js.core.server.provider;

import com.js.core.server.ratelimit.provider.RateLimitProvider;
import com.js.core.server.register.ServiceRegister;
import com.js.core.server.register.impl.ZKServiceRegister;
import lombok.Getter;


import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private String host;
    private int port;
    private Map<String, Object> interfaceProvider;
    private ServiceRegister serviceRegister;
    @Getter
    private RateLimitProvider rateLimitProvider;
    public ServiceProvider(String host, int port) {
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
        rateLimitProvider = new RateLimitProvider();
    }

    public void provideServiceInterface(Object service) {
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        for (Class<?> clazz : interfaceName) {
            interfaceProvider.put(clazz.getName(), service);
            serviceRegister.register(clazz, new InetSocketAddress(host, port));
        }
    }
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
