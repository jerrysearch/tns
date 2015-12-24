package com.jerry.thriftnameserver.cluster;

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
	 * 埋葬自己
	 * @return
	 */
	public String tombstone();
}
