package com.jerry.thriftnameserver.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jerry.thriftnameserver.conf.Config;
import com.jerry.thriftnameserver.rpc.clusterConstants;

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

		NodeManagerMBeanServer nodeManagerMBeanServer = new NodeManagerMBeanServer();
		nodeManagerMBeanServer.start();

		CNodeManagerMBeanServer cNodeManagerMBeanServer = new CNodeManagerMBeanServer();
		cNodeManagerMBeanServer.start();

		PoolAblePingServer poolAblePingServer = new PoolAblePingServer();
		poolAblePingServer.start();

		PushServer pushServer = new PushServer();
		pushServer.start();

		log.info("--------------------------------------");
		log.info("ThriftNameServer start end");
		log.info("--------------------------------------");
	}
}
