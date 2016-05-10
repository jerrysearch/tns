package com.qibaike.thriftnameserver.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.thriftnameserver.conf.Config;
import com.qibaike.thriftnameserver.rpc.clusterConstants;
import com.qibaike.thriftnameserver.rpc.tnsrpcConstants;

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
		clusterRpcServer.start(host, clusterConstants.PORT);
		
		/**
		 * tns rpc
		 */
		TNSRpcServer tnsRpcServer = new TNSRpcServer();
		tnsRpcServer.start(host, tnsrpcConstants.PORT);
		
		
		SNodeManagerMBeanServer sNodeManagerMBeanServer = new SNodeManagerMBeanServer();
		sNodeManagerMBeanServer.start();

		CNodeManagerMBeanServer cNodeManagerMBeanServer = new CNodeManagerMBeanServer();
		cNodeManagerMBeanServer.start();

		ClusterScheduleServer pushServer = new ClusterScheduleServer();
		pushServer.start();

		log.info("--------------------------------------");
		log.info("ThriftNameServer start end");
		log.info("--------------------------------------");
	}
}
