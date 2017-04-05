package com.github.jerrysearch.tns.console.task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.jerrysearch.tns.protocol.rpc.TCNode;

public class BaseClusterListTask {

	private static final List<TCNode> list = new ArrayList<TCNode>();

	protected static synchronized void updateAll(List<TCNode> dst) {
		list.clear();
		list.addAll(dst);
	}

	protected static synchronized TCNode selectOne() {
		int size = list.size();
		int index = ThreadLocalRandom.current().nextInt(size);
		return list.get(index);
	}

	protected static synchronized List<TCNode> getAll() {
		List<TCNode> dst = new LinkedList<TCNode>();
		dst.addAll(list);
		return dst;
	}
}
