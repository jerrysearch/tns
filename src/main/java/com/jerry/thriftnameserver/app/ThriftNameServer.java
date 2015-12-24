package com.jerry.thriftnameserver.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftNameServer {
	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger(ThriftNameServer.class);
		log.info("--------------------------------------");
		log.info("ThriftNameServer start begin");
		log.info("--------------------------------------");
		String host = System.getProperty("HOSTNAME", "127.0.0.3");
		int port = 8700;
		ThriftPoolServer thriftPoolServer = new ThriftPoolServer();
		thriftPoolServer.start(host, port);

		NodeManagerMBeanServer nodeManagerMBeanServer = new NodeManagerMBeanServer();
		nodeManagerMBeanServer.start();

		PoolAblePingServer poolAblePingServer = new PoolAblePingServer();
		poolAblePingServer.start();

		log.info("--------------------------------------");
		log.info("ThriftNameServer start end");
		log.info("--------------------------------------");
	}
}
