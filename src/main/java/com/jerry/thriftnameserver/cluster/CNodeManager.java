package com.jerry.thriftnameserver.cluster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import com.jcabi.aspects.Loggable;
import com.jerry.thriftnameserver.conf.Config;
import com.jerry.thriftnameserver.rpc.Cluster;
import com.jerry.thriftnameserver.rpc.STATE;
import com.jerry.thriftnameserver.rpc.TCNode;
import com.jerry.thriftnameserver.rpc.clusterConstants;

public class CNodeManager implements CNodeManagerMBean {

	private final TreeMap<Long, TCNode> cMap = new TreeMap<Long, TCNode>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private final long clearMills = 60000; // 1分钟

	private final TCNode me = new TCNode();

	private CNodeManager() {
		this.me.setHost(Config.HOSTNAME);
		this.me.setPort(clusterConstants.PORT);
		this.me.setId(Config.TNSID);
		this.me.setState(STATE.UP);
		this.me.setTimestamp(System.currentTimeMillis());
		/**
		 * 将自己放到列表中
		 */
		this.cMap.put(this.me.getId(), this.me);
	}

	/**
	 * 选择一个其它节点进行同步 如果没有其它节点，返回null 1 -> 2 -> 3 -> 1
	 * 
	 * @return
	 */
	public TCNode getOne() {
		try {
			this.readLock.lock();
			return this.getOne(this.me.getId());
		} finally {
			this.readLock.unlock();
		}

	}

	public List<TCNode> getList() {
		return new LinkedList<TCNode>(this.cMap.values());
	}

	@Loggable
	private TCNode getOne(Long id) {
		Long key = this.cMap.higherKey(id);
		if (null == key) {
			key = this.cMap.firstKey();
		}
		/**
		 * 选择到自己，终止
		 */
		if (key.longValue() == this.me.getId()) {
			return null;
		}
		TCNode tcnode = this.cMap.get(key);
		/**
		 * 跳过不健康的节点 并对墓碑进行清除
		 */
		switch (tcnode.getState()) {
		case UP:
			return tcnode;
		case Tombstone:
			long time = System.currentTimeMillis() - tcnode.getTimestamp();
			if (time > this.clearMills) {
				this.cMap.remove(key);
			}
			return this.getOne(key);
		case DOWN:
			return this.getOne(key);
		default:
			return null;
		}
	}

	@Override
	public String meet(String host) {
		TSocket transport = new TSocket(host, clusterConstants.PORT, 2000);
		TProtocol protocol = new TBinaryProtocol(transport);
		Cluster.Client client = new Cluster.Client(protocol);
		try {
			transport.open();
			client.up(this.me);
			return "OK !";
		} catch (Exception e) {
			return String.format("%s, Exception : %s", "FAIL", e.getMessage());
		} finally {
			if (transport.isOpen()) {
				transport.close();
			}
		}
	}

	/**
	 * 某个节点上线了
	 * 
	 * @param tcnode
	 */
	public void up(TCNode tcnode) {
		Long key = tcnode.getId();
		this.cMap.put(key, tcnode);
	}

	private final String format = "    %-20s%-20s%-20s%-20s\n";
	private final String headLine = String.format(format, "STATE", "HOST", "ID", "TIMESTAMP");

	@Override
	public String clusterStatus() {
		try {
			StringBuilder sb = new StringBuilder(500);
			sb.append(headLine).append("\n");
			this.readLock.lock();
			Collection<TCNode> collection = this.cMap.values();
			for (TCNode tcnode : collection) {
				String s = String.format(format, tcnode.getState().toString(), tcnode.getHost(),
						tcnode.getId(), tcnode.getTimestamp());
				sb.append(s);
			}
			return sb.toString();
		} finally {
			this.readLock.unlock();
		}
	}

	public void pushClusterList(List<TCNode> list) {
		try {
			this.writeLock.lock();
			for (TCNode tcnode : list) {
				long id = tcnode.getId();
				if (this.cMap.containsKey(id)) {
					TCNode tmp = this.cMap.get(id);
					if (tmp.getState() == STATE.Tombstone) { // 墓碑是不可恢复的，一个完整周期后，墓碑会传播到所有节点
						continue;
					}
					switch (tcnode.getState()) {
					case UP:
					case DOWN:
						if (tcnode.getTimestamp() > tmp.getTimestamp()) {
							this.cMap.put(id, tcnode); // 更新
						}
						break;
					case Tombstone:
						this.cMap.put(id, tcnode); // 更新
						break;
					}
				} else {
					this.cMap.put(id, tcnode);
				}
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	@Override
	public String tombstone(long id) {
		try {
			this.writeLock.lock();
			if (this.cMap.containsKey(id)) {
				TCNode tcnode = this.cMap.get(id);
				tcnode.setState(STATE.Tombstone);
				tcnode.setTimestamp(System.currentTimeMillis());
				return "OK !";
			} else {
				return "FAIL !";
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	private static class proxy {
		private static final CNodeManager instance = new CNodeManager();
	}

	public static CNodeManager getInstance() {
		return proxy.instance;
	}
}
