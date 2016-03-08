package com.qibaike.thriftnameserver.cluster;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.thriftnameserver.command.push.ThriftPushCNodeListCommand;
import com.qibaike.thriftnameserver.rpc.State;
import com.qibaike.thriftnameserver.rpc.TCNode;

public class PushTnsTask implements Runnable {
	private final CNodeManager cNodeManager = CNodeManager.getInstance();
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void run() {
		TCNode tcnode = cNodeManager.getOne();
		if (null == tcnode) {
			return;
		}
		List<TCNode> list = new LinkedList<TCNode>();
		cNodeManager.toAllClusterNodeList(list);
		ThriftPushCNodeListCommand command = new ThriftPushCNodeListCommand(tcnode, list);
		State state = command.push();
		/**
		 * 更新节点tcnode状态
		 */
		if (state == State.DOWN) {
			log.error("node [{}] state changed to DOWN !", tcnode.toString());
		}
		tcnode.setState(state);
		long version = tcnode.getVersion();
		tcnode.setVersion(version + 1);
		tcnode.setTimestamp(System.currentTimeMillis());
	}

}
