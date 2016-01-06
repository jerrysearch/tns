package com.qibaike.thriftnameserver.cluster;

import com.qibaike.thriftnameserver.service.SNodeManager;

public class CheckAndRemoveServiceTombstoneTask implements Runnable {
	private final SNodeManager sNodeManager = SNodeManager.getInstance();

	@Override
	public void run() {
		sNodeManager.CheckAndRemoveTombstone();
	}

}
