package com.qibaike.tns.client;

import java.lang.management.ManagementFactory;

public class TestJVMName {

	public static void main(String[] args) {
		String name = ManagementFactory.getRuntimeMXBean().getSpecName();
		System.out.println(name);
	}

}
