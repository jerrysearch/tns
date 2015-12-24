package com.jerry.thriftnameserver.conf;

public class Config {
	public static final String HOSTNAME = System.getProperty("HOSTNAME", "localhost");

	public static final Long TNSID = System.currentTimeMillis();
}
