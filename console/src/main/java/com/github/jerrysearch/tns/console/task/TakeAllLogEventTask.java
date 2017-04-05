package com.github.jerrysearch.tns.console.task;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerrysearch.tns.console.util.LogEventTimeWheel;
import com.github.jerrysearch.tns.protocol.rpc.Cluster;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;
import com.github.jerrysearch.tns.protocol.rpc.event.LogEvent;

public class TakeAllLogEventTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(TakeAllLogEventTask.class);
	private static final String clientId = ManagementFactory.getRuntimeMXBean().getName();
	private final TCNode tcNode;

	public TakeAllLogEventTask(TCNode tcNode) {
		this.tcNode = tcNode;
	}

	@Override
	public void run() {
		TSocket transport = null;
		try {
			String host = this.tcNode.getHost();
			int port = this.tcNode.getPort();

			transport = new TSocket(host, port, 1000);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cluster.Client client = new Cluster.Client(protocol);
			transport.open();
			List<LogEvent> list = client.takeAllLogEvent(clientId);
			LogEventTimeWheel.getInstance().add(list);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (transport != null && transport.isOpen()) {
				transport.close();
			}
		}

	}

}
