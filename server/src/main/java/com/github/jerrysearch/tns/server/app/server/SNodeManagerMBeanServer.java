package com.github.jerrysearch.tns.server.app.server;

import com.github.jerrysearch.tns.server.app.server.AbstractServer;
import com.github.jerrysearch.tns.server.service.SNodeManager;
import com.github.jerrysearch.tns.server.service.SNodeManagerMBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class SNodeManagerMBeanServer extends AbstractServer {

    @Override
    public void start() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        SNodeManagerMBean sNodeManagerMBean = SNodeManager.getInstance();
        try {
            mbs.registerMBean(sNodeManagerMBean, new ObjectName(
                    "SNodeManagerMBean:name=sNodeManagerMBean"));
        } catch (Exception e) {
            log.error("start fail !", e);
        }
    }

    @Override
    public void shutdown() {

    }
}
