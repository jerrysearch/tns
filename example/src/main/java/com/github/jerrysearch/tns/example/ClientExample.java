package com.github.jerrysearch.tns.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerrysearch.tns.client.client.ServicePool;
import com.github.jerrysearch.tns.client.cluster.ClusterPool;
import com.github.jerrysearch.tns.protocol.rpc.TSNode;

public class ClientExample {

	private static final Logger log = LoggerFactory.getLogger(ClientExample.class);
	public static void main(String[] args) throws InterruptedException {
		
		if(null == args || args.length != 2){
			log.info("please input server's hostname and service name");
			return;
		}
		
		ClusterPool clusterPool = new ClusterPool(args[0]);
		
		ServicePool servicePool = new ServicePool(clusterPool, args[1], 10);
		while(true){
			TSNode node = servicePool.getOne();
			
			log.info(node.toString());
			Thread.sleep(1000L);
		}
	}

}
