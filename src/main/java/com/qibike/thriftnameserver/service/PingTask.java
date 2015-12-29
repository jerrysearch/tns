package com.qibike.thriftnameserver.service;

import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibike.thriftnameserver.command.ping.ThriftPingCommand;
import com.qibike.thriftnameserver.rpc.STATE;
import com.qibike.thriftnameserver.rpc.TSNode;

public class PingTask implements Runnable {

	private TSNode tsnode;
	private ScheduledFuture<?> future;
	private static final Logger log = LoggerFactory.getLogger(PingTask.class);

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
		case DOWN: // down的节点仍执行ping，万一恢复了呢
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
		case Tombstone: // 死亡节点，本实例不会再ping，直接移除
			boolean isCancel = this.future.cancel(true);
			log.warn("calcel -> [{}] : {}", this.tsnode.toString(), Boolean.valueOf(isCancel)
					.toString());
		}
	}
}
