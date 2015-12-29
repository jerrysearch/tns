package com.qibike.thriftnameserver.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibike.thriftnameserver.conf.Config;
import com.qibike.thriftnameserver.rpc.clusterConstants;

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

		SNodeManagerMBeanServer nodeManagerMBeanServer = new SNodeManagerMBeanServer();
		nodeManagerMBeanServer.start();

		CNodeManagerMBeanServer cNodeManagerMBeanServer = new CNodeManagerMBeanServer();
		cNodeManagerMBeanServer.start();

		PushServer pushServer = new PushServer();
		pushServer.start();

		log.info("--------------------------------------");
		log.info("ThriftNameServer start end");
		log.info("--------------------------------------");
	}
}
