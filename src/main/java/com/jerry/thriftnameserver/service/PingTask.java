package com.jerry.thriftnameserver.service;

import java.util.concurrent.ScheduledFuture;

import com.jerry.thriftnameserver.command.ping.ThriftPingCommand;
import com.jerry.thriftnameserver.rpc.STATE;
import com.jerry.thriftnameserver.rpc.TSNode;

public class PingTask implements Runnable {

	private TSNode tsnode;
	private ScheduledFuture<?> future;

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

	public PingTask(TSNode tsnode) {
		this.tsnode = tsnode;
	}

	@Override
	public void run() {
		switch (tsnode.getState()) {
		case UP:
		case DOWN:
			ThriftPingCommand command = new ThriftPingCommand(this.tsnode);
			int vNodes = command.ping();
			if (vNodes < 1) {
				this.tsnode.setState(STATE.DOWN);
			} else {
				this.tsnode.setState(STATE.UP);
			}
			vNodes = Math.min(vNodes, 20); // 最大虚拟节点个数20,太大会增加客户端所有的成本
			this.tsnode.setVNodes(vNodes);
			this.tsnode.setTimestamp(System.currentTimeMillis());
			break;
		case Tombstone:
			this.future.cancel(true);
		}
	}

}
