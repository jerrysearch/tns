package com.jerry.thriftnameserver.app;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jerry.thriftnameserver.bean.NodeManager;
import com.jerry.thriftnameserver.bean.NodeManagerMBean;

public class NodeManagerMBeanServer {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public void start() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		NodeManagerMBean nodeManagerMBean = NodeManager.getInstance();
		try {
			mbs.registerMBean(nodeManagerMBean, new ObjectName(
					"NodeManagerMBean:name=nodeManagerMBean"));
		} catch (Exception e) {
			log.error("registerMBean", e);
		}
	}
}
