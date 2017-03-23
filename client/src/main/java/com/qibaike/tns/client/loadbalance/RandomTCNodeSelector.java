package com.qibaike.tns.client.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.qibaike.tns.protocol.rpc.TCNode;

public class RandomTCNodeSelector implements TNodeSelector<TCNode> {

	@Override
	public TCNode selectOne(List<TCNode> list) {
		if (list.isEmpty()) {
			return null;
		}
		int size = list.size();
		int tmp = ThreadLocalRandom.current().nextInt(size);
		return list.get(tmp);
	}
}
