package com.jerry.thriftnameserver.bean;

public interface NodeManagerMBean {
	String onLine(String serviceName, String host, int port, long pingFrequency);

	String onLine(String serviceName, String host, int port, long pingFrequency, String instanceName);

	String offLine(String serviceName, String instanceName);
	
	String offlineAll();
	
//	String openPing(String serviceName, String instanceName);
	
//	String closePing(String serviceName, String instanceName);

	String list(String serviceName);

	String listAll();
	
	String listDelayQueue();
	
//	String listClosedMap();
	
	/**
	 * help 信息
	 */
	String helpOnLine();
	String helpOffline();
	String helpList();
	String helpListAll();
	String helpListDelayQueue();
	String helpOfflineAll();
}
