package com.qibike.thriftnameserver.command.push;

import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.aspects.Loggable;
import com.qibike.thriftnameserver.rpc.Cluster;
import com.qibike.thriftnameserver.rpc.STATE;
import com.qibike.thriftnameserver.rpc.TCNode;

public class ThriftPushCNodeListCommand extends BaseThriftPushCommand<STATE, TCNode> {

	public ThriftPushCNodeListCommand(TCNode tcnode, List<TCNode> list) {
		super(tcnode, list);
	}

	@Override
	protected STATE run() throws Exception {
		String host = tcnode.getHost();
		int port = tcnode.getPort();

		TSocket transport = new TSocket(host, port, 2000);
		TProtocol protocol = new TBinaryProtocol(transport);
		Cluster.Client client = new Cluster.Client(protocol);
		transport.open();
		client.pushClusterList(this.list);
		transport.close();
		return STATE.UP;
	}

	private static final Logger log = LoggerFactory.getLogger(ThriftPushCNodeListCommand.class);

	@Override
	protected STATE getFallback() {
		log.warn("Fallback --> {}", this.tcnode.toString());
		return STATE.DOWN;
	}

	@Override
	@Loggable
	protected void logPush(TCNode tcnode) {
		/**
		 * just log
		 */
	}
}
