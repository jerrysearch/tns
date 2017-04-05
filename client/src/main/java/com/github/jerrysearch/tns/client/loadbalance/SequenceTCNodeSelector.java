package com.github.jerrysearch.tns.client.loadbalance;

import java.util.List;

import com.github.jerrysearch.tns.protocol.rpc.TCNode;

public class SequenceTCNodeSelector implements TNodeSelector<TCNode> {

	private int index = 0;

	@Override
	public TCNode selectOne(List<TCNode> list) {
		if (list.isEmpty()) {
			return null;
		}
		index = index & Integer.MAX_VALUE;
		int size = list.size();
		int tmp = index % size;
		index++;
		return list.get(tmp);
	}
}
