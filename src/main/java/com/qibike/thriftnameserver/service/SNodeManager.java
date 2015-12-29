package com.qibike.thriftnameserver.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.jcabi.aspects.Loggable;
import com.qibike.thriftnameserver.rpc.STATE;
import com.qibike.thriftnameserver.rpc.TSNode;

public class SNodeManager implements SNodeManagerMBean {
	private final Map<String, Map<Long, TSNode>> serviceMap = new HashMap<String, Map<Long, TSNode>>();
	private final PingTaskManager pingTaskManager = PingTaskManager.getInstance();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private SNodeManager() {
	}

	/**
	 * 返回该service下，存活的节点列表
	 * 
	 * @param serviceName
	 * @param list
	 */
	public void toUpServiceNodeList(String serviceName, List<TSNode> list) {
		try {
			this.readLock.lock();
			if (this.serviceMap.containsKey(serviceName)) {
				Map<Long, TSNode> map = this.serviceMap.get(serviceName);
				for (Map.Entry<Long, TSNode> entry : map.entrySet()) {
					TSNode tsnode = entry.getValue();
					if (tsnode.getState() == STATE.UP) {
						list.add(tsnode);
					}
				}
			}
		} finally {
			this.readLock.unlock();
		}
	}

	/**
	 * 返回所有节点列表，不管节点状态
	 * 
	 * @param list
	 */
	public void toAllServiceNodeList(List<TSNode> list) {
		try {
			this.readLock.lock();
			for (Map<Long, TSNode> map : this.serviceMap.values()) {
				list.addAll(map.values());
			}
		} finally {
			this.readLock.unlock();
		}
	}

	@Override
	public String onLine(String serviceName, String host, int port, int pingFrequency) {
		long id = System.currentTimeMillis();
		return this.onLine(serviceName, host, port, pingFrequency, id);
	}

	@Loggable
	public String onLine(String serviceName, String host, int port, int pingFrequency, long id) {
		pingFrequency = Math.max(pingFrequency, 10); // 最小ping频率10秒
		pingFrequency = Math.min(pingFrequency, 60); // 最大ping频率1分钟

		TSNode tsnode = new TSNode();
		tsnode.setServiceName(serviceName);
		tsnode.setHost(host);
		tsnode.setPort(port);
		tsnode.setPingFrequency(pingFrequency);
		tsnode.setId(id);
		tsnode.setState(STATE.DOWN);
		tsnode.setTimestamp(System.currentTimeMillis());
		this.addOrTombstone(tsnode);
		return tsnode.toString();
	}

	/**
	 * 新增一个节点
	 * 
	 * @param tsnode
	 */
	private void putToServiceMap(TSNode tsnode) {
		String serviceName = tsnode.getServiceName();
		long id = tsnode.getId();
		if (this.serviceMap.containsKey(serviceName)) {
			Map<Long, TSNode> map = this.serviceMap.get(serviceName);
			map.put(id, tsnode);
		} else {
			Map<Long, TSNode> map = new HashMap<Long, TSNode>();
			map.put(id, tsnode);
			this.serviceMap.put(serviceName, map);
		}
	}

	/**
	 * 删除一个节点
	 * 
	 * @param tsnode
	 */
	private void tombstoneToServiceMap(String serviceName, long id, long timestamp) {
		TSNode tsnode = this.serviceMap.get(serviceName).get(id);
		tsnode.setState(STATE.Tombstone);
		tsnode.setTimestamp(timestamp);
	}

	/**
	 * 新增、删除更新
	 * 
	 * @param tsnodes
	 */
	private void addOrTombstone(TSNode... tsnodes) {
		try {
			this.writeLock.lock();
			for (TSNode tsnode : tsnodes) {
				if (this.isNew(tsnode)) { // add
					this.putToServiceMap(tsnode);
					this.pingTaskManager.submit(tsnode);
				} else {
					if (tsnode.getState() == STATE.Tombstone) { // delete
						String serviceName = tsnode.getServiceName();
						long id = tsnode.getId();
						long timestamp = tsnode.getTimestamp();
						this.tombstoneToServiceMap(serviceName, id, timestamp);
					}
				}
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	private boolean isNew(TSNode tsnode) {
		String serviceName = tsnode.getServiceName();
		long id = tsnode.getId();
		return !(this.serviceMap.containsKey(serviceName) && this.serviceMap.get(serviceName)
				.containsKey(id));
	}

	public void pushServiceList(List<TSNode> list) {
		this.addOrTombstone(list.toArray(new TSNode[list.size()]));
	}

	private final String format = "%-15s%-15s%-16s%-15s%-15s%-15s%-15s%-15s\n";
	private final String headLine = String.format(format, "STATE", "SERVICENAME", "HOST", "PORT",
			"ID", "VNODES", "PINGFREQUENCY", "TIMESTAMP");

	@Override
	public String serviceStatus() {
		try {
			this.readLock.lock();
			StringBuilder sb = new StringBuilder();
			sb.append(this.headLine);
			Set<String> set = this.serviceMap.keySet();
			for (String serviceName : set) {
				sb.append("\n");
				String content = this.statusService(serviceName);
				sb.append(content);
			}
			return sb.toString();
		} finally {
			this.readLock.unlock();
		}
	}

	private String statusService(String serviceName) {
		Map<Long, TSNode> map = this.serviceMap.get(serviceName);
		if (map.isEmpty()) {
			return serviceName + " EMPTY !\n";
		}
		Collection<TSNode> tsnodeList = map.values();
		StringBuilder sb = new StringBuilder();
		for (TSNode tsnode : tsnodeList) {
			String s = String.format(this.format, tsnode.getState(), tsnode.getServiceName(),
					tsnode.getHost(), tsnode.getPort(), tsnode.getId(), tsnode.getVNodes(),
					tsnode.getPingFrequency(), tsnode.getTimestamp());
			sb.append(s);
		}
		return sb.toString();
	}

	@Loggable
	@Override
	public String offLine(String serviceName, long id) {
		try {
			this.writeLock.lock();
			if (this.serviceMap.containsKey(serviceName)) {
				Map<Long, TSNode> map = this.serviceMap.get(serviceName);
				if (map.containsKey(id)) {
					this.tombstoneToServiceMap(serviceName, id, System.currentTimeMillis());
					return "OK !";
				}
			}
			return "FAIL !";
		} finally {
			this.writeLock.unlock();
		}
	}

	private static class proxy {
		private static SNodeManager nodeManager = new SNodeManager();
	}

	public static SNodeManager getInstance() {
		return proxy.nodeManager;
	}

	private final String end = "\n";
	private final String tab = "    ";

	@Override
	public String helpOnLine() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("SYNOPSIS").append(end);
		sb.append(tab).append("string serviceName, string host, int port, long pingFrequency")
				.append(end);
		sb.append(end);
		sb.append("OPTIONS").append(end);
		sb.append(tab).append("serviceName : service name of this node provides").append(end);
		sb.append(tab).append("host : the dst node's host or ip").append(end);
		sb.append(tab).append("port : the dst node's port").append(end);
		sb.append(tab).append("pingFrequency : the frequency of ping (s)").append(end);
		return sb.toString();
	}

	@Override
	public String helpOffline() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("SYNOPSIS").append(end);
		sb.append(tab).append("string serviceName, long id").append(end);
		sb.append(end);
		sb.append("OPTIONS").append(end);
		sb.append(tab).append("serviceName : service name").append(end);
		sb.append(tab).append("id : uniquely identifies of node , see serviceStatus");
		return sb.toString();
	}
}
