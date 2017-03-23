package com.qibaike.tns.server.command.push;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import com.jcabi.aspects.Loggable;
import com.qibaike.tns.protocol.rpc.Cluster;
import com.qibaike.tns.protocol.rpc.State;
import com.qibaike.tns.protocol.rpc.TCNode;
import com.qibaike.tns.protocol.rpc.TSNode;
import com.qibaike.tns.protocol.rpc.event.LogEvent;
import com.qibaike.tns.protocol.rpc.event.Operation;
import com.qibaike.tns.server.command.BaseSysCommand;
import com.qibaike.tns.server.conf.Config;
import com.qibaike.tns.server.summary.Summary;

public class ThriftPushCNodeAndSNodeListCommand extends BaseSysCommand<State> {

	private static final String CLUSTER_ID = String.valueOf(Config.TNSID);
	protected final TCNode tcnode;
	protected final List<TCNode> cList;
	protected final List<TSNode> sList;

	public ThriftPushCNodeAndSNodeListCommand(TCNode tcnode, List<TCNode> cList, List<TSNode> sList) {
		this.tcnode = tcnode;
		this.cList = cList;
		this.sList = sList;
	}

	@Override
	protected State run() throws Exception {
		String host = tcnode.getHost();
		int port = tcnode.getPort();

		TSocket transport = new TSocket(host, port, 1000);
		TProtocol protocol = new TBinaryProtocol(transport);
		Cluster.Client client = new Cluster.Client(protocol);
		transport.open();
		try {
			client.pushClusterAndServiceList(cList, sList);
		} finally {
			if (transport.isOpen()) {
				transport.close();
			}
		}
		return State.UP;
	}

	@Override
	protected State getFallback() {
		return this.getFallback(this.tcnode);
	}

	@Loggable(value = Loggable.WARN)
	protected State getFallback(TCNode tcnode) {
		switch (tcnode.getState()) {
		case DOWN_1:
			return State.DOWN_2;
		case DOWN_2:
			return State.DOWN;
		default:
			return State.DOWN_1;
		}
	}

	public State push() {
		long start = System.nanoTime();
		State state = this.push(tcnode);
		long end = System.nanoTime();
		this.summaryClusterPush(tcnode, state, (end - start) * 0.001F);
		return state;
	}

	@Loggable
	private State push(TCNode tcnode) {
		return this.execute();
	}

	private void summaryClusterPush(TCNode tcnode, State state, float consume) {
		LogEvent event = new LogEvent();
		event.setSource(CLUSTER_ID);
		event.setOperation(Operation.SYNC_CAS);
		List<String> attributes = new LinkedList<String>();
		attributes.add("toCluster=" + tcnode.getHost());
		attributes.add("state=" + state.toString());
		attributes.add("consume(ms)=" + consume);
		event.setAttributes(attributes);
		event.setTimestamp(System.currentTimeMillis());
		Summary.getInstance().appendLogEvent(event);
	}
}
