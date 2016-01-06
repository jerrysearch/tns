package com.qibaike.thriftnameserver.command.push;

import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.thriftnameserver.rpc.Cluster;
import com.qibaike.thriftnameserver.rpc.State;
import com.qibaike.thriftnameserver.rpc.TCNode;
import com.qibaike.thriftnameserver.rpc.TSNode;

public class ThriftPushSNodeListCommand extends BaseThriftPushCommand<State, TSNode> {

	public ThriftPushSNodeListCommand(TCNode tcnode, List<TSNode> list) {
		super(tcnode, list);
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
			client.pushServiceList(this.list);
		} finally {
			if (transport.isOpen()) {
				transport.close();
			}
		}
		return State.UP;
	}

	private static final Logger log = LoggerFactory.getLogger(ThriftPushSNodeListCommand.class);

	@Override
	protected State getFallback() {
		log.warn("Fallback --> {}", this.tcnode.toString());
		return State.DOWN;
	}

	@Override
	protected void logPush(TCNode tcnode) {
		/**
		 * just log
		 */
	}
}
