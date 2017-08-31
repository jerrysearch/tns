package com.github.jerrysearch.tns.server.app.server;

import com.github.jerrysearch.tns.server.app.server.AbstractServer;
import com.github.jerrysearch.tns.server.cluster.CNodeManager;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class CNodeManagerMBeanServer extends AbstractServer {

    @Override
    public void start() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        CNodeManager cNodeManagerMBean = CNodeManager.getInstance();
        try {
            mbs.registerMBean(cNodeManagerMBean, new ObjectName(
                    "CNodeManagerMBean:name=cNodeManagerMBean"));
        } catch (Exception e) {
            log.error("start fail !", e);
        }
    }

    @Override
    public void shutdown() {

    }
}
