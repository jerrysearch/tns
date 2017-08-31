package com.github.jerrysearch.tns.server.app;

import com.github.jerrysearch.tns.protocol.rpc.structConstants;
import com.github.jerrysearch.tns.server.app.server.*;
import com.github.jerrysearch.tns.server.conf.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftNameServer {
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(ThriftNameServer.class);
        log.info("--------------------------------------");
        log.info("ThriftNameServer start begin");
        log.info("--------------------------------------");
        String host = Config.HOSTNAME;
        /*
         * cluster rpc
		 */
        ClusterRpcServer clusterRpcServer = new ClusterRpcServer(host, structConstants.PORT);
        clusterRpcServer.start();
        log.info("ClusterRpcServer start Ok! host : {}, port : {}", host, structConstants.PORT);

		/*
		 * tns rpc
		 */
        TNSRpcServer tnsRpcServer = new TNSRpcServer(host, structConstants.PORT + 1);
        tnsRpcServer.start();
        log.info("TNSRpcServer start Ok! host : {}, port : {}", host, structConstants.PORT + 1);

		/*
		 * sNodeManagerMBeanServer
		 */
        SNodeManagerMBeanServer sNodeManagerMBeanServer = new SNodeManagerMBeanServer();
        sNodeManagerMBeanServer.start();
        log.info("SNodeManagerMBeanServer start Ok!");

		/*
		 * cNodeManagerMBeanServer
		 */
        CNodeManagerMBeanServer cNodeManagerMBeanServer = new CNodeManagerMBeanServer();
        cNodeManagerMBeanServer.start();
        log.info("CNodeManagerMBeanServer start Ok!");

		/*
		  clusterScheduleServer
		 */
        ClusterScheduleServer clusterScheduleServer = new ClusterScheduleServer();
        clusterScheduleServer.start();
        log.info("ClusterScheduleServer start Ok!");

        log.info("--------------------------------------");
        log.info("ThriftNameServer start end");
        log.info("--------------------------------------");
    }
}
