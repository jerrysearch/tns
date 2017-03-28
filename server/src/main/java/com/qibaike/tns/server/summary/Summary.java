package com.qibaike.tns.server.summary;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.qibaike.tns.protocol.rpc.event.LogEvent;

/**
 * 各种数据汇总
 * 
 * @author jerry
 *
 */
public class Summary {

	/**
	 * 初始容量100
	 */
	private final LinkedBlockingQueue<LogEvent> queue = new LinkedBlockingQueue<LogEvent>(100);

	public void appendLogEvent(LogEvent event) {
		boolean sucess = this.queue.offer(event);
		if (sucess) {
			return;
		} else {
			this.queue.clear(); // 暴力清除
			this.queue.offer(event);
		}
	}

	public List<LogEvent> takeAllLogEvent() {
		List<LogEvent> list = new LinkedList<LogEvent>();
		while (true) {
			LogEvent event = this.queue.poll();
			if (null == event) {
				break;
			} else {
				list.add(event);
			}
		}
		return list;
	}

	private Summary() {
	};

	private static class proxy {
		private static Summary summary = new Summary();
	}

	public static Summary getInstance() {
		return proxy.summary;
	}
}
