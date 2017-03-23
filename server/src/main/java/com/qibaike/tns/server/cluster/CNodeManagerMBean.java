package com.qibaike.tns.server.cluster;

public interface CNodeManagerMBean {
	
	/**
	 * 同对方组成集群
	 * @param host
	 * @return
	 */
	public String meet(String host);
	/**
	 * 集群状态
	 * @return
	 */
	public String clusterStatus();
	/**
	 * 埋葬某个节点
	 * @return
	 */
	public String tombstone(long id);
}
