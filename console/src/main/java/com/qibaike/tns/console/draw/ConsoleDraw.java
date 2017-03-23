package com.qibaike.tns.console.draw;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

import com.qibaike.tns.console.util.LogEventTimeWheel;
import com.qibaike.tns.protocol.rpc.event.LogEvent;
import com.qibaike.tns.protocol.rpc.event.Operation;

public class ConsoleDraw implements Runnable {
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public void start() {
		this.executor.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		boolean have = false;
		while (true) {
			LogEvent event = LogEventTimeWheel.getInstance().poll();
			if (null == event) {
				break;
			} else {
				have = true;
				draw(event);
			}
		}
		if (have) {
			// 这一秒已经有内容输出到控制台
		} else {
			LogEvent empty = new LogEvent();
			empty.setSource("");
			empty.setTimestamp(System.currentTimeMillis());
			empty.setOperation(Operation.TAKE_LEVENT);
			List<String> attributes = Collections.emptyList();
			empty.setAttributes(attributes);
			draw(empty);
		}
	}

	private void draw(LogEvent logEvent) {
		AnsiConsole.systemInstall();
		long timestamp = logEvent.getTimestamp();
		String source = logEvent.getSource();
		Operation operation = logEvent.getOperation();
		List<String> attributes = logEvent.getAttributes();

		System.out.println(Ansi.ansi().fg(Color.YELLOW)
				.a(this.formatTime(System.currentTimeMillis())).fg(Color.YELLOW)
				.a(this.formatTime(timestamp)).fg(Color.RED).a(this.format20String(source))
				.fg(Color.GREEN).a(this.format20String(operation.name())).fg(Color.BLUE)
				.a(this.toJsonString(attributes)).reset());
		AnsiConsole.systemUninstall();
	}

	private String formatTime(long t) {
		return String.format("%tT", t);
	}

	@SuppressWarnings("unused")
	private String formatFloat(float f) {
		return String.format("%.2f", f);
	}

	private String format20String(String s) {
		return String.format("%-20s", s);
	}

	private String toJsonString(List<String> attributes) {
		return String.format("%-20s", Arrays.toString(attributes.toArray()));
	}
}
