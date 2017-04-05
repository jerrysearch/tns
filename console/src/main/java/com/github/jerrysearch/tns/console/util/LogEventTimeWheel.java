package com.github.jerrysearch.tns.console.util;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerrysearch.tns.protocol.rpc.event.LogEvent;

public class LogEventTimeWheel {

	private LogEventTimeWheel() {
	};

	private final Logger log = LoggerFactory.getLogger(LogEventTimeWheel.class);
	private final PriorityQueue<LogEvent> queue = new PriorityQueue<LogEvent>(1000,
			new LogEventComparator());

	public synchronized void add(List<LogEvent> list) {
		for (LogEvent event : list) {
			boolean ok = this.queue.offer(event);
			if (!ok) {
				log.warn("offer {} return false, please slow down", event.toString());
			}
		}
	}

	public synchronized LogEvent poll(long seconds) {
		LogEvent event = this.queue.peek();
		if (null == event) {
			return null;
		}
		long thisSeconds = event.getTimestamp() / 1000;
		if (thisSeconds <= seconds) {
			return this.queue.poll();
		} else {
			// 还没到时间，不允许输出
			return null;
		}
	}

	private static class proxy {
		private static LogEventTimeWheel logEventTimeWheel = new LogEventTimeWheel();
	}

	public static LogEventTimeWheel getInstance() {
		return proxy.logEventTimeWheel;
	}

	private class LogEventComparator implements Comparator<LogEvent> {

		@Override
		public int compare(LogEvent o1, LogEvent o2) {
			return Long.signum(o1.getTimestamp() - o2.getTimestamp());
		}
	}
}
