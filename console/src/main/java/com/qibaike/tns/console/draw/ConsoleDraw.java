package com.qibaike.tns.console.draw;

import java.util.Arrays;
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
				drawEvent(event);
			}
		}
		if (have) {
			// 这一秒已经有内容输出到控制台
		} else {
			drawEmpty();
		}
	}

	private void drawEvent(LogEvent logEvent) {
		long timestamp = logEvent.getTimestamp();
		String source = logEvent.getSource();
		Operation operation = logEvent.getOperation();
		List<String> attributes = logEvent.getAttributes();

		this.draw(this.formatTime(System.currentTimeMillis()), this.formatTime(timestamp), source,
				operation.name(), this.toJsonString(attributes));
	}

	private void drawEmpty() {
		this.draw(this.formatTime(System.currentTimeMillis()), "---", "---", "---", "[ ]");
	}

	private void draw(String cTime, String eTime, String source, String operation, String attributes) {
		AnsiConsole.systemInstall();
		System.out.println(Ansi.ansi().fg(Color.YELLOW).a(String.format("%-20s", cTime))
				.a(String.format("%-20s", eTime)).fg(Color.RED).a(String.format("%-20s", source))
				.fg(Color.GREEN).a(String.format("%-20s", operation)).fg(Color.BLUE).a(attributes)
				.reset());
		AnsiConsole.systemUninstall();
	}

	private String formatTime(long t) {
		return String.format("%tT", t);
	}

	private String toJsonString(List<String> attributes) {
		return Arrays.toString(attributes.toArray());
	}
}
