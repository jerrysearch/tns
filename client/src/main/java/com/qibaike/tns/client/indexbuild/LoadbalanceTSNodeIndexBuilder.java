package com.qibaike.tns.client.indexbuild;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.qibaike.tns.protocol.rpc.TSNode;

public class LoadbalanceTSNodeIndexBuilder implements IIndexBuilder<TSNode> {

	@Override
	public Collection<TSNode> build(List<TSNode> list) {
		List<TSNode> servcices = new LinkedList<TSNode>();
		for (TSNode tsnode : list) {
			int vNodes = tsnode.getVNodes();
			TSNode[] tSNodes = new TSNode[vNodes];
			Arrays.fill(tSNodes, tsnode);
			servcices.addAll(Arrays.asList(tSNodes));
		}
		return servcices;
	}

}
