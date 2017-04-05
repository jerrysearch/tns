package com.github.jerrysearch.tns.server.conf;

import java.lang.management.ManagementFactory;

import com.github.jerrysearch.tns.protocol.rpc.structConstants;

public class Config {
	public static final String HOSTNAME = System.getProperty("hostname", "localhost");

	public static final Long TNSID = (long) (HOSTNAME + structConstants.PORT).hashCode();

	public static final Long serviceRemoveSeconds = 60L;

	public static final String CLUSTER_ID = ManagementFactory.getRuntimeMXBean().getName();
}
