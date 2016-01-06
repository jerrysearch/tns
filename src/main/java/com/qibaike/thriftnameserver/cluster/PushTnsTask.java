package com.qibaike.thriftnameserver.cluster;

import java.util.LinkedList;
import java.util.List;

import com.qibaike.thriftnameserver.command.push.ThriftPushCNodeListCommand;
import com.qibaike.thriftnameserver.rpc.STATE;
import com.qibaike.thriftnameserver.rpc.TCNode;

public class PushTnsTask implements Runnable {
	private final CNodeManager cNodeManager = CNodeManager.getInstance();

	@Override
	public void run() {
		TCNode tcnode = cNodeManager.getOne();
		if (null == tcnode) {
			return;
		}
		List<TCNode> list = new LinkedList<TCNode>();
		cNodeManager.toAllClusterNodeList(list);
		ThriftPushCNodeListCommand command = new ThriftPushCNodeListCommand(tcnode, list);
		STATE state = command.push();
		/**
		 * 更新节点tcnode状态
		 */
		tcnode.setState(state);
		tcnode.setTimestamp(System.currentTimeMillis());
	}

}
