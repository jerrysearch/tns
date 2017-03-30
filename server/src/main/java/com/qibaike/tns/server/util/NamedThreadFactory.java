package com.qibaike.tns.server.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

	private final String name;
	private final boolean daemon;
	private final AtomicInteger index = new AtomicInteger();

	public NamedThreadFactory(String name, boolean daemon) {
		this.name = name;
		this.daemon = daemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName(this.name + "_" + this.index.incrementAndGet());
		t.setDaemon(this.daemon);
		return t;
	}

}