package com.qibaike.tns.server.rpc.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;

import com.jcabi.aspects.Loggable;
import com.qibaike.tns.protocol.rpc.TCNode;
import com.qibaike.tns.protocol.rpc.TNSRpc.Iface;
import com.qibaike.tns.protocol.rpc.TSNode;
import com.qibaike.tns.protocol.rpc.event.LogEvent;
import com.qibaike.tns.protocol.rpc.event.Operation;
import com.qibaike.tns.server.cluster.CNodeManager;
import com.qibaike.tns.server.conf.Config;
import com.qibaike.tns.server.service.SNodeManager;
import com.qibaike.tns.server.summary.Summary;

public class TNSRpcImpl implements Iface {

	private final SNodeManager sNodeManager = SNodeManager.getInstance();
	private final CNodeManager cNodeManager = CNodeManager.getInstance();
	private final String clusterId = String.valueOf(Config.TNSID);

	/**
	 * 请求service列表
	 */
	@Override
	@Loggable(skipResult = true)
	public List<TSNode> serviceList(String clientId, String serviceName) throws TException {
		this.serviceListLogEvent(clientId, serviceName);
		List<TSNode> list = new LinkedList<TSNode>();
		this.sNodeManager.toUpServiceNodeList(serviceName, list);
		return list;
	}

	/**
	 * 上报LogEvent 用于统计
	 * 
	 * @param clientId
	 * @param serviceName
	 * @see LogEvent
	 */
	private void serviceListLogEvent(String clientId, String serviceName) {
		LogEvent event = new LogEvent();
		event.setSource(clientId);
		event.setOperation(Operation.SYNC_SERVICE);
		List<String> attributes = new LinkedList<String>();
		attributes.add("fromCluster=" + this.clusterId);
		attributes.add("sName=" + serviceName);
		event.setAttributes(attributes);
		event.setTimestamp(System.currentTimeMillis());
		Summary.getInstance().appendLogEvent(event);
	}

	/**
	 * 请求cluster列表
	 */
	@Override
	@Loggable(skipResult = true)
	public List<TCNode> clusterList(String clientId) throws TException {
		this.clusterListLogEvent(clientId);
		List<TCNode> list = new LinkedList<TCNode>();
		this.cNodeManager.toUpClusterNodeList(list);
		return list;
	}

	/**
	 * 上报LogEvent 用于统计
	 * 
	 * @param clientId
	 * @see LogEvent
	 */
	private void clusterListLogEvent(String clientId) {
		LogEvent event = new LogEvent();
		event.setSource(clientId);
		event.setOperation(Operation.SYNC_CLUSTER);
		List<String> attributes = new LinkedList<String>();
		attributes.add("fromCluster=" + this.clusterId);
		event.setAttributes(attributes);
		event.setTimestamp(System.currentTimeMillis());
		Summary.getInstance().appendLogEvent(event);
	}
}
