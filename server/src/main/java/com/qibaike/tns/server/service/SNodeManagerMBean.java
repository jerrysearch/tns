package com.qibaike.tns.server.service;

public interface SNodeManagerMBean {
	String onLine(String serviceName, String host, int port, int pingFrequency);

	String offLine(String serviceName, long id);

	String serviceStatus();

	/**
	 * help 信息
	 */
	String helpOnLine();

	String helpOffline();
}
