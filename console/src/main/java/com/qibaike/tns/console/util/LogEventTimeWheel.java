package com.qibaike.tns.console.util;

import java.util.LinkedList;
import java.util.List;

import com.qibaike.tns.protocol.rpc.event.LogEvent;

public class LogEventTimeWheel {
	
	private LogEventTimeWheel(){};
	
	private final List<LogEvent> list = new LinkedList<LogEvent>();
	public synchronized void add(List<LogEvent> list){
		this.list.addAll(list);
	}
	
	public synchronized LogEvent poll(){
		if(list.isEmpty()){
			return null;
		}else{
			return list.remove(0);
		}
	}
	
	private static class proxy{
		private static LogEventTimeWheel logEventTimeWheel = new LogEventTimeWheel();
	}
	
	public static LogEventTimeWheel getInstance(){
		return proxy.logEventTimeWheel;
	}
}
