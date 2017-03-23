package com.qibaike.tns.client.indexbuild;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import com.qibaike.tns.protocol.rpc.TSNode;

/**
 * 实现master slave的索引构建器 配合 MasterSlaveTSNodeSelector 才有效果
 * 
 * @author jerry
 *
 */
public class MaterSlaveTSNodeIndexBuilder implements IIndexBuilder<TSNode> {

	private static final TSNodeComparator tsnodeComparator = new TSNodeComparator();

	@Override
	public Collection<TSNode> build(List<TSNode> list) {
		TreeSet<TSNode> treeSet = new TreeSet<TSNode>(tsnodeComparator);
		treeSet.addAll(list);
		return treeSet;
	}

}
