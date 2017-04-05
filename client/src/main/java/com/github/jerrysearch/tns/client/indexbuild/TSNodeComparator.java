package com.github.jerrysearch.tns.client.indexbuild;

import java.util.Comparator;

import com.github.jerrysearch.tns.protocol.rpc.TSNode;

/**
 * 反自然顺序排序
 * 
 * @author Jerry
 *
 */
public class TSNodeComparator implements Comparator<TSNode> {

	@Override
	public int compare(TSNode t1, TSNode t2) {

		int t1Vnodes = t1.getVNodes();
		int t2Vnodes = t2.getVNodes();

		if (t1Vnodes < t2Vnodes) {
			return 1;
		} else if (t1Vnodes > t2Vnodes) {
			return -1;
		} else {
			long t1Id = t1.getId();
			long t2Id = t2.getId();
			return Long.signum(t1Id - t2Id);
		}
	}

}
