package com.github.jerrysearch.tns.client.conf;

import java.lang.management.ManagementFactory;

/**
 * 客户端配置
 *
 * @author jerry
 */
public class ClientConfig {
    private final String clientId = ManagementFactory.getRuntimeMXBean().getName();

    public String getClientId() {
        return clientId;
    }
}
