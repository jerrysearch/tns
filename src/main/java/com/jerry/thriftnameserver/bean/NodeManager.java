package com.jerry.thriftnameserver.bean;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.aspects.Loggable;
import com.jerry.thriftnameserver.conf.Config;
import com.jerry.thriftnameserver.rpc.Cluster;
import com.jerry.thriftnameserver.rpc.SNode;
import com.jerry.thriftnameserver.rpc.clusterConstants;

public class NodeManager implements NodeManagerMBean {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final DelayQueue<DelayedNode> delayQueue = new DelayQueue<DelayedNode>();

	private final Map<String, Map<String, Node>> serviceMap = new HashMap<String, Map<String, Node>>();

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private final String id = String.valueOf(System.currentTimeMillis());

	private NodeManager() {
	}

	public Collection<Node> getServiceNodeList(String serviceName) {
		try {
			this.readLock.lock();
			if (this.serviceMap.containsKey(serviceName)) {
				return this.serviceMap.get(serviceName).values();
			} else {
				return Collections.emptyList();
			}
		} finally {
			this.readLock.unlock();
		}
	}

	public void toServiceNodeList(List<SNode> dst) {
		for (DelayedNode delayedNode : this.delayQueue) {
			Node node = delayedNode.getNode();
			SNode snode = new SNode();
			snode.setHost(node.getHost());
			snode.setPort(node.getPort());
			snode.setInstanceName(node.getInstanceName());
			snode.setVNodes(node.getvNodes());
			snode.setPingFrequency(node.getPingFrequency());
			snode.setServiceName(node.getServiceName());
			dst.add(snode);
		}
	}

	@Loggable
	public void putToDealyQueue(Node node) {
		DelayedNode delayedNode = new DelayedNode(node);
		delayQueue.put(delayedNode);
	}

	public Node take() {
		DelayedNode delayedNode = null;
		try {
			delayedNode = this.delayQueue.take();
			return delayedNode.getNode();
		} catch (InterruptedException e) {
			log.error(" operation [{}] catch InterruptedException", "take");
			return null;
		} finally {
			if (null == delayedNode) {
			} else {
				delayedNode.setPutTime(System.currentTimeMillis());
				this.delayQueue.put(delayedNode);
			}
		}
	}

	public void checkHealthOver(Node node, int vNodes) {
		if (vNodes < 1) {
			node.setHealth(false);
		} else {
			node.setHealth(true);
		}
		vNodes = Math.min(vNodes, 20); // 最大虚拟节点数20
		node.setvNodes(vNodes);
		node.setLastPingtTime(System.currentTimeMillis()); // 更新最后一次健康检查的时间
		this.updateServiceMap(node);
	}

	private void updateServiceMap(Node node) {
		try {
			this.writeLock.lock();
			String serviceName = node.getServiceName();
			if (this.serviceMap.containsKey(serviceName)) {
				Map<String, Node> map = this.serviceMap.get(serviceName);
				map.put(node.getInstanceName(), node);
			} else {
				Map<String, Node> map = new HashMap<String, Node>();
				map.put(node.getInstanceName(), node);
				this.serviceMap.put(serviceName, map);
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	@Loggable
	private boolean removeFromServiceMap(String serviceName, String instanceName) {
		try {
			this.writeLock.lock();
			if (this.serviceMap.containsKey(serviceName)) {
				Map<String, Node> map = this.serviceMap.get(serviceName);
				if (map.containsKey(instanceName)) {
					map.remove(instanceName);
					if (map.isEmpty()) {
						this.serviceMap.remove(serviceName);
					}
					return true;
				}
			}
			return false;
		} finally {
			this.writeLock.unlock();
		}
	}

	@Override
	public String onLine(String serviceName, String host, int port, long pingFrequency) {
		String instanceName = String.valueOf(System.currentTimeMillis());
		return this.onLine(serviceName, host, port, pingFrequency, instanceName);
	}

	@Override
	@Loggable
	public String onLine(String serviceName, String host, int port, long pingFrequency,
			String instanceName) {
		pingFrequency = Math.max(pingFrequency, 10); // 最小ping频率10秒
		pingFrequency = Math.min(pingFrequency, 60); // 最大ping频率1分钟

		Node node = new Node(serviceName, host, port, pingFrequency, instanceName);
		this.onLine(node);
		return node.toString();
	}

	private void onLine(Node node) {
		this.removeFromDelayQueue(node); // 加入之前，移除相同的实例
		this.putToDealyQueue(node); // 放到queue下
		this.updateServiceMap(node); // 放到serviceMap下
	}

	@Override
	@Loggable
	public String offLine(String serviceName, String instanceName) {
		Node node = new Node(serviceName, "", 0, 0, instanceName);
		boolean a = this.removeFromDelayQueue(node);
		boolean b = this.removeFromServiceMap(serviceName, instanceName);
		if (a && b) {
			return "OK !";
		} else if (a ^ b) {
			return "WARN !";
		} else {
			return "FAIL !";
		}
	}

	@Loggable
	private boolean removeFromDelayQueue(Node node) {
		DelayedNode delayedNode = new DelayedNode(node);
		return this.delayQueue.remove(delayedNode);
	}

	@Override
	public synchronized String list(String serviceName) {
		try {
			this.readLock.lock();
			if (!this.serviceMap.containsKey(serviceName)) {
				return "EMPTY !";
			}
			Map<String, Node> map = this.serviceMap.get(serviceName);
			if (map.isEmpty()) {
				return "EMPTY !";
			}
			Collection<Node> nodeList = map.values();
			StringBuilder sb = new StringBuilder();
			for (Node node : nodeList) {
				sb.append(node.toAllString()).append("\n");
			}
			return sb.toString();
		} finally {
			this.readLock.unlock();
		}
	}

	@Override
	public String listAll() {
		StringBuilder sb = new StringBuilder();
		Set<String> set = this.serviceMap.keySet();
		for (String serviceName : set) {
			sb.append(serviceName).append(" : \n");
			String content = this.list(serviceName);
			sb.append(content);
		}
		return sb.toString();
	}

	@Loggable
	@Override
	public String offlineAll() {
		this.delayQueue.clear();
		try {
			this.writeLock.lock();
			this.serviceMap.clear();
		} finally {
			this.writeLock.unlock();
		}
		return "OK !";
	}

	private static class proxy {
		private static NodeManager nodeManager = new NodeManager();
	}

	public static NodeManager getInstance() {
		return proxy.nodeManager;
	}

	@Override
	public String listDelayQueue() {
		StringBuilder sb = new StringBuilder();
		for (DelayedNode delayedNode : this.delayQueue) {
			sb.append(delayedNode.getNode().toString()).append("\n");
		}
		return sb.toString();
	}

	private final String end = "\n";
	private final String tab = "    ";

	@Override
	public String helpOnLine() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("SYNOPSIS").append(end);
		sb.append(tab)
				.append("string serviceName, string host, int port, long pingFrequency, [string instanceName]")
				.append(end);
		sb.append(end);
		sb.append("OPTIONS").append(end);
		sb.append(tab).append("serviceName : service name of this node provides").append(end);
		sb.append(tab).append("host : the dst node's host or ip").append(end);
		sb.append(tab).append("port : the dst node's port").append(end);
		sb.append(tab).append("pingFrequency : the frequency of ping (s)").append(end);
		sb.append(tab)
				.append("[instanceName] : uniquely identifies of this node , default {System.currentTimeMillis}")
				.append(end);
		return sb.toString();
	}

	@Override
	public String helpOffline() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("SYNOPSIS").append(end);
		sb.append(tab).append("string serviceName, string instanceName").append(end);
		sb.append(end);
		sb.append("OPTIONS").append(end);
		sb.append(tab).append("serviceName : service name").append(end);
		sb.append(tab)
				.append("instanceName : uniquely identifies of node , see list(String serviceName) or listAll()");
		return sb.toString();
	}

	@Override
	public String helpList() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("SYNOPSIS").append(end);
		sb.append(tab).append("string serviceName").append(end);
		sb.append(end);
		sb.append("OPTIONS").append(end);
		sb.append(tab).append("serviceName : service name");
		return sb.toString();
	}

	@Override
	public String helpListAll() {
		return "";
	}

	@Override
	public String helpListDelayQueue() {
		return "";
	}

	@Override
	public String helpOfflineAll() {
		return "";
	}

	@Override
	public String meet(String host) {
		try {
			TSocket transport = new TSocket(host, clusterConstants.PORT, 2000);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cluster.Client client = new Cluster.Client(protocol);
			transport.open();
			List<SNode> list = client.allServiceList(this.id);
			/**
			 * 清空已有node
			 */
			// this.offlineAll();

			for (SNode snode : list) {
				String serviceName = snode.getServiceName();
				String nHost = snode.getHost();
				int port = snode.getPort();
				long pingFrequency = snode.getPingFrequency();
				String instanceName = snode.getInstanceName();
				Node node = new Node(serviceName, nHost, port, pingFrequency, instanceName);
				this.onLine(node);
			}
			String myHost = Config.hostName;
			client.onLine(myHost, clusterConstants.PORT, this.id);

			return "OK !";
		} catch (Exception e) {
			return String.format("%s, Exception : %s", "FAIL", e.getMessage());
		}
	}

}
