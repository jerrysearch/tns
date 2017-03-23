package com.qibaike.tns.client.indexbuild;

import java.util.Collection;
import java.util.List;

/**
 * 节点索引构建器
 * 
 * @author jerry
 *
 * @param <T>
 */
public interface IIndexBuilder<T> {

	public Collection<T> build(List<T> list);
}
