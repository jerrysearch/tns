package com.github.jerrysearch.tns.client.task;

import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerrysearch.tns.client.cluster.ClusterPool;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;
import com.github.jerrysearch.tns.protocol.rpc.TNSRpc;

public class SysClusterListTask implements Runnable {

	private final ClusterPool clusterPool;
	private final String clientId;

	private static final Logger log = LoggerFactory.getLogger(SysClusterListTask.class);

	public SysClusterListTask(ClusterPool clusterPool, String clientId) {
		this.clusterPool = clusterPool;
		this.clientId = clientId;
	}

	@Override
	public void run() {
		TCNode tcnode = this.clusterPool.getOne();
		if (null == tcnode) {
			return;
		}
		TSocket transport = null;
		try {
			transport = new TSocket(tcnode.getHost(), tcnode.getPort() + 1, 3000); // 默认超时3秒
			TProtocol protocol = new TBinaryProtocol(transport);
			TNSRpc.Client client = new TNSRpc.Client(protocol);
			transport.open();
			List<TCNode> list = client.clusterList(this.clientId);
			this.clusterPool.rebuildIndex(list);
		} catch (Exception e) {
			log.warn("[{}] has one exception -> {}", tcnode.toString(), e.getMessage());
		} finally {
			if (null != transport && transport.isOpen()) {
				transport.close();
			}
		}
	}

	@Override
	public String toString() {
		return "SysClusterListTask [clientId=" + clientId + "]";
	}
}
