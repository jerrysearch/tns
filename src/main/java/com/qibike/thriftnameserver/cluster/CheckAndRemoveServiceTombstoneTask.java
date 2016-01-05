package com.qibike.thriftnameserver.cluster;

import com.qibike.thriftnameserver.service.SNodeManager;

public class CheckAndRemoveServiceTombstoneTask implements Runnable {
	private final SNodeManager sNodeManager = SNodeManager.getInstance();

	@Override
	public void run() {
		sNodeManager.CheckAndRemoveTombstone();
	}

}
