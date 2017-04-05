package com.github.jerrysearch.tns.client.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.jerrysearch.tns.protocol.rpc.TSNode;

public class RandomTSNodeSelector implements TNodeSelector<TSNode> {

	@Override
	public TSNode selectOne(List<TSNode> list) {
		if (list.isEmpty()) {
			return null;
		}
		int size = list.size();
		int tmp = ThreadLocalRandom.current().nextInt(size);
		return list.get(tmp);
	}
}
