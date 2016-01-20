package com.qibaike.thriftnameserver.service;

import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.aspects.Loggable;
import com.qibaike.thriftnameserver.command.ping.ThriftPingCommand;
import com.qibaike.thriftnameserver.rpc.State;
import com.qibaike.thriftnameserver.rpc.TSNode;

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
		case Joining:
		case DOWN_1:
		case DOWN_2:
		case DOWN: // down的节点仍执行ping，万一恢复了呢
			ThriftPingCommand command = new ThriftPingCommand(this.tsnode);
			int vNodes = command.ping();
			if (vNodes < 1) {
				if (tsnode.getState() != State.DOWN) {
					log.error("node [{}] state changed to DOWN ! ", tsnode.toString());
				}
				this.tsnode.setState(State.DOWN);
			} else {
				if (tsnode.getState() != State.UP) {
					log.warn("node [{}] state changed to UP ! ", tsnode.toString());
				}
				this.tsnode.setState(State.UP);
			}
			vNodes = Math.min(vNodes, 20); // 最大虚拟节点个数20,太大会增加客户端索引的成本
			this.tsnode.setVNodes(vNodes);
			this.tsnode.setTimestamp(System.currentTimeMillis());
			break;
		case Leaving:
		case Tombstone: // 死亡节点，本实例不会再ping，直接移除
			this.cancelTask(this.tsnode);
		}
	}

	@Loggable(value = Loggable.WARN)
	private boolean cancelTask(TSNode tsnode) {
		return this.future.cancel(true);
	}
}
