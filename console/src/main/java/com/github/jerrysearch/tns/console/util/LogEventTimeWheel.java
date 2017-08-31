package com.github.jerrysearch.tns.console.util;

import com.github.jerrysearch.tns.protocol.rpc.event.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class LogEventTimeWheel {

    private final Logger log = LoggerFactory.getLogger(LogEventTimeWheel.class);
    private final PriorityQueue<LogEvent> queue = new PriorityQueue<LogEvent>(1000,
            new LogEventComparator());
    private LogEventTimeWheel() {
    }

    public static LogEventTimeWheel getInstance() {
        return proxy.logEventTimeWheel;
    }

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

    private class LogEventComparator implements Comparator<LogEvent> {

        @Override
        public int compare(LogEvent o1, LogEvent o2) {
            return Long.signum(o1.getTimestamp() - o2.getTimestamp());
        }
    }
}
