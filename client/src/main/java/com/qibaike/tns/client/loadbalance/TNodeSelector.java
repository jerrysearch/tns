package com.qibaike.tns.client.loadbalance;

import java.util.List;

/**
 * 节点选择器
 * 
 * @author jerry
 *
 * @param <T>
 */
public interface TNodeSelector<T> {

	public T selectOne(List<T> list);
}
