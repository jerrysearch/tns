package com.qibaike.tns.server.summary;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.qibaike.tns.protocol.rpc.event.LogEvent;

/**
 * 各种数据汇总
 * 
 * @author jerry
 *
 */
public class Summary {

	private final ConcurrentLinkedQueue<LogEvent> queue = new ConcurrentLinkedQueue<LogEvent>();

	public void appendLogEvent(LogEvent event) {
		this.queue.add(event);
	}

	public List<LogEvent> takeAllLogEvent() {
		List<LogEvent> list = new LinkedList<LogEvent>();
		while (true) {
			LogEvent event = this.queue.poll();
			if (event == null) {
				break;
			}
			list.add(event);
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
