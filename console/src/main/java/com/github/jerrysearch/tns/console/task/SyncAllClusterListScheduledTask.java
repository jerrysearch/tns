package com.github.jerrysearch.tns.console.task;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerrysearch.tns.protocol.rpc.Cluster;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;

public class SyncAllClusterListScheduledTask extends BaseClusterListTask implements Runnable {

	private static final Logger log = LoggerFactory
			.getLogger(SyncAllClusterListScheduledTask.class);
	private static final String clientId = ManagementFactory.getRuntimeMXBean().getName();

	private SyncAllClusterListScheduledTask() {
	}

    @Override
	public void run() {
		TSocket transport = null;
		try {
			TCNode tcNode = selectOne();
			if (tcNode == null) {
				return;
			}
			String host = tcNode.getHost();
			int port = tcNode.getPort();
			transport = new TSocket(host, port, 1000);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cluster.Client client = new Cluster.Client(protocol);
			transport.open();
			List<TCNode> list = client.clusterList(clientId);
			log.debug(Arrays.toString(list.toArray()));
			updateAll(list);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (transport != null && transport.isOpen()) {
				transport.close();
			}
		}

	}

	private static class proxy {
		private static SyncAllClusterListScheduledTask task = new SyncAllClusterListScheduledTask();
	}

	public static SyncAllClusterListScheduledTask getInstance() {
		return proxy.task;
	}

}
