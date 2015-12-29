package com.qibike.thriftnameserver.app;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibike.thriftnameserver.cluster.CNodeManager;

public class CNodeManagerMBeanServer {
	private final Logger log = LoggerFactory.getLogger(getClass());

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
}
