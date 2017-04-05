package com.github.jerrysearch.tns.console.draw;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

import com.github.jerrysearch.tns.console.util.LogEventTimeWheel;
import com.github.jerrysearch.tns.protocol.rpc.event.LogEvent;
import com.github.jerrysearch.tns.protocol.rpc.event.Operation;

public class ConsoleDraw implements Runnable {
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public void start() {
		this.drawHead();
		this.executor.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
	}

	private long lastSeconds = System.currentTimeMillis() / 1000;

	@Override
	public void run() {
		this.lastSeconds += 1; // 获取下个一秒发生的数据
		long pollSeconds = this.lastSeconds;
		boolean have = false;
		while (true) {
			LogEvent event = LogEventTimeWheel.getInstance().poll(pollSeconds);
			if (null == event) {
				if (!have) {
					// 没取到值并且这次没有输出过event
					this.drawEmpty();
				}
				return;
			} else {
				this.lastSeconds = event.getTimestamp() / 1000;
				if (have) {
					// 这一次输出的非第一行记录
					this.drawEventWithEmptyTime(event);
				} else {
					// 这一次输出的第一行记录
					this.drawEventWithCurrentTime(event);
				}
				have = true;
			}
		}
	}

	private void drawEventWithCurrentTime(LogEvent event) {
		String cTime = this.formatTime(System.currentTimeMillis());
		this.drawEvent(cTime, event);
	}

	private void drawEventWithEmptyTime(LogEvent event) {
		this.drawEvent("", event);
	}

	private void drawEvent(String cTime, LogEvent event) {
		long timestamp = event.getTimestamp();
		String source = event.getSource();
		Operation operation = event.getOperation();
		List<String> attributes = event.getAttributes();

		this.draw(cTime, this.formatTime(timestamp), source, operation.name(),
				this.toJsonString(attributes));
	}

	private void drawEmpty() {
		this.draw(this.formatTime(System.currentTimeMillis()), "*", "*", "*", "*");
	}

	private void drawHead() {
		String tmp = "";
		this.draw("cTime", "eTime", "source", "operation", "attributes");
		this.draw(tmp, tmp, tmp, tmp, tmp);
	}

	private void draw(String cTime, String eTime, String source, String operation, String attributes) {
		AnsiConsole.systemInstall();
		System.out.println(Ansi.ansi().fg(Color.RED).a(String.format("%-12s", cTime))
				.fg(Color.YELLOW).a(String.format("%-12s", eTime)).fg(Color.RED)
				.a(String.format("%-25s", source)).fg(Color.GREEN)
				.a(String.format("%-15s", operation)).fg(Color.BLUE).a(attributes).reset());
		AnsiConsole.systemUninstall();
	}

	private String formatTime(long t) {
		return String.format("%tT", t);
	}

	private String toJsonString(List<String> attributes) {
		return Arrays.toString(attributes.toArray());
	}
}
