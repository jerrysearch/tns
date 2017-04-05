package com.github.jerrysearch.tns.server.cluster;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerrysearch.tns.protocol.rpc.State;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;
import com.github.jerrysearch.tns.protocol.rpc.TSNode;
import com.github.jerrysearch.tns.server.command.push.ThriftPushCNodeAndSNodeListCommand;
import com.github.jerrysearch.tns.server.service.SNodeManager;

public class PushTnsAndServiceTask implements Runnable {
	private final CNodeManager cNodeManager = CNodeManager.getInstance();
	private final SNodeManager sNodeManager = SNodeManager.getInstance();
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void run() {
		TCNode tcnode = cNodeManager.getNext();
		if (null == tcnode) {
			log.warn("can't get next node, and do nothing");
			return;
		}
		List<TCNode> cList = new LinkedList<TCNode>();
		cNodeManager.toAllClusterNodeList(cList);

		List<TSNode> sList = new LinkedList<TSNode>();
		sNodeManager.toAllServiceNodeList(sList);
		ThriftPushCNodeAndSNodeListCommand command = new ThriftPushCNodeAndSNodeListCommand(tcnode,
				cList, sList);
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
