package com.jerry.thriftnameserver.app;

import com.jerry.thriftnameserver.bean.Node;
import com.jerry.thriftnameserver.bean.NodeManager;
import com.jerry.thriftnameserver.ping.ThriftPingCommand;

public class PoolAblePingServer {

	private final NodeManager nodeManager = NodeManager.getInstance();

	public void start() {

		Thread poolAblePingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					Node node = nodeManager.take();
					if(null == node){
						continue;
					}
					ThriftPingCommand pingCommand = new ThriftPingCommand(node);
					int vNodes = pingCommand.execute();
					nodeManager.checkHealthOver(node, vNodes);
				}
			}
		});
		poolAblePingThread.setName("T-PoolAblePingThread");
		poolAblePingThread.start();
	}
}
