package com.jerry.thriftnameserver.command.push;

import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.aspects.Loggable;
import com.jerry.thriftnameserver.rpc.Cluster;
import com.jerry.thriftnameserver.rpc.STATE;
import com.jerry.thriftnameserver.rpc.TCNode;
import com.jerry.thriftnameserver.rpc.TSNode;

public class ThriftPushSNodeListCommand extends BaseThriftPushCommand<STATE, TSNode> {

	public ThriftPushSNodeListCommand(TCNode tcnode, List<TSNode> list) {
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
		client.pushServiceList(this.list);
		transport.close();
		return STATE.UP;
	}

	private static final Logger log = LoggerFactory.getLogger(ThriftPushSNodeListCommand.class);

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
