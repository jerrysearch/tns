package com.github.jerrysearch.tns.server.cluster;

import com.github.jerrysearch.tns.server.service.SNodeManager;

public class CheckAndRemoveServiceTombstoneTask implements Runnable {
    private final SNodeManager sNodeManager = SNodeManager.getInstance();

    @Override
    public void run() {
        sNodeManager.CheckAndRemoveTombstone();
    }

}
