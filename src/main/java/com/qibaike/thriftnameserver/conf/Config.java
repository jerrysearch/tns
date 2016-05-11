package com.qibaike.thriftnameserver.conf;

import com.qibaike.thriftnameserver.rpc.structConstants;

public class Config {
	public static final String HOSTNAME = System.getProperty("hostname", "localhost");

	public static final Long TNSID = (long) (HOSTNAME + structConstants.PORT).hashCode();

	public static final Long serviceRemoveSeconds = 60L;
}
