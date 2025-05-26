package com.js.core.server.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void register(Class<?> clazz, InetSocketAddress serverAddress);
}
