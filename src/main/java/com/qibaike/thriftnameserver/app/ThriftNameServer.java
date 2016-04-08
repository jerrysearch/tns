package com.qibaike.thriftnameserver.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.thriftnameserver.conf.Config;
import com.qibaike.thriftnameserver.rpc.clusterConstants;

public class ThriftNameServer {
	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger(ThriftNameServer.class);
		log.info("--------------------------------------");
		log.info("ThriftNameServer start begin");
		log.info("--------------------------------------");
		String host = Config.HOSTNAME;
		int port = clusterConstants.PORT;
		TNSRpcServer tnsRpcServer = new TNSRpcServer();
		tnsRpcServer.start(host, port);

		SNodeManagerMBeanServer sNodeManagerMBeanServer = new SNodeManagerMBeanServer();
		sNodeManagerMBeanServer.start();

		CNodeManagerMBeanServer cNodeManagerMBeanServer = new CNodeManagerMBeanServer();
		cNodeManagerMBeanServer.start();

		ClusterServer pushServer = new ClusterServer();
		pushServer.start();

		log.info("--------------------------------------");
		log.info("ThriftNameServer start end");
		log.info("--------------------------------------");
	}
}
