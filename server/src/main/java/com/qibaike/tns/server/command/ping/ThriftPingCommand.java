package com.qibaike.tns.server.command.ping;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import com.jcabi.aspects.Loggable;
import com.qibaike.tns.protocol.rpc.PoolAble;
import com.qibaike.tns.protocol.rpc.TSNode;
import com.qibaike.tns.protocol.rpc.event.LogEvent;
import com.qibaike.tns.protocol.rpc.event.Operation;
import com.qibaike.tns.server.command.BaseSysCommand;
import com.qibaike.tns.server.conf.Config;
import com.qibaike.tns.server.summary.Summary;

public class ThriftPingCommand extends BaseSysCommand<Integer> {

	private static final String CLUSTER_ID = String.valueOf(Config.TNSID);
	private final TSNode tsnode;

	public ThriftPingCommand(TSNode tsnode) {
		this.tsnode = tsnode;
	}

	@Loggable
	private int ping(TSNode tsnode) {
		return this.execute();
	}

	public int ping() {
		long start = System.nanoTime();
		int vNodes = this.ping(this.tsnode);
		long end = System.nanoTime();
		this.summaryServicePing(tsnode, vNodes, (end - start) * 0.001F);
		return vNodes;
	}

	@Override
	protected Integer run() throws Exception {
		String host = this.tsnode.getHost();
		int port = this.tsnode.getPort();

		TSocket transport = new TSocket(host, port, 1000);
		TProtocol protocol = new TBinaryProtocol(transport);
		PoolAble.Client client = new PoolAble.Client(protocol);
		transport.open();
		int vNodes = -1;
		try {
			vNodes = client.ping();
		} finally {
			if (transport.isOpen()) {
				transport.close();
			}
		}
		return vNodes;
	}

	@Override
	protected Integer getFallback() {
		return this.getFallback(this.tsnode);
	}

	@Loggable(value = Loggable.WARN)
	protected Integer getFallback(TSNode tsnode) {
		return -1;
	}

	/**
	 * 汇总service节点ping数据
	 * 
	 * @param tsnode
	 * @param timeConsuming
	 */
	private void summaryServicePing(TSNode tsnode, int vNodes, float consume) {
		LogEvent event = new LogEvent();
		event.setSource(CLUSTER_ID);
		event.setOperation(Operation.PING_SERVICE);
		List<String> attributes = new LinkedList<String>();
		attributes.add("sName=" + tsnode.getServiceName());
		attributes.add("host=" + tsnode.getHost());
		attributes.add("port=" + tsnode.getPort());
		attributes.add("vNodes=" + vNodes);
		attributes.add("consume(ms)=" + String.format("%.2f", consume));
		event.setAttributes(attributes);
		event.setTimestamp(System.currentTimeMillis());
		Summary.getInstance().appendLogEvent(event);
	}
}
