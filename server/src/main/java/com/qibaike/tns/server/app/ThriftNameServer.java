package com.qibaike.tns.server.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.tns.protocol.rpc.structConstants;
import com.qibaike.tns.server.conf.Config;

public class ThriftNameServer {
	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger(ThriftNameServer.class);
		log.info("--------------------------------------");
		log.info("ThriftNameServer start begin");
		log.info("--------------------------------------");
		String host = Config.HOSTNAME;
		/**
		 * cluster rpc
		 */
		ClusterRpcServer clusterRpcServer = new ClusterRpcServer();
		clusterRpcServer.start(host, structConstants.PORT);
		log.info("ClusterRpcServer start Ok! host : {}, port : {}", host, structConstants.PORT);

		/**
		 * tns rpc
		 */
		TNSRpcServer tnsRpcServer = new TNSRpcServer();
		tnsRpcServer.start(host, structConstants.PORT + 1);
		log.info("TNSRpcServer start Ok! host : {}, port : {}", host, structConstants.PORT + 1);

		/**
		 * sNodeManagerMBeanServer
		 */
		SNodeManagerMBeanServer sNodeManagerMBeanServer = new SNodeManagerMBeanServer();
		sNodeManagerMBeanServer.start();
		log.info("SNodeManagerMBeanServer start Ok!");

		/**
		 * cNodeManagerMBeanServer
		 */
		CNodeManagerMBeanServer cNodeManagerMBeanServer = new CNodeManagerMBeanServer();
		cNodeManagerMBeanServer.start();
		log.info("CNodeManagerMBeanServer start Ok!");

		/**
		 * clusterScheduleServer
		 */
		ClusterScheduleServer pushServer = new ClusterScheduleServer();
		pushServer.start();
		log.info("ClusterScheduleServer start Ok!");

		log.info("--------------------------------------");
		log.info("ThriftNameServer start end");
		log.info("--------------------------------------");
	}
}
