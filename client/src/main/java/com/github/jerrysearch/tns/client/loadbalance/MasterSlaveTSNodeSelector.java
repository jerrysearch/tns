package com.github.jerrysearch.tns.client.loadbalance;

import com.github.jerrysearch.tns.protocol.rpc.TSNode;

import java.util.List;

/**
 * 实现master slave的node选择器 配合 MaterSlaveTSNodeIndexBuilder 才有效果
 *
 * @author jerry
 */
public class MasterSlaveTSNodeSelector implements TNodeSelector<TSNode> {

    @Override
    public TSNode selectOne(List<TSNode> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
