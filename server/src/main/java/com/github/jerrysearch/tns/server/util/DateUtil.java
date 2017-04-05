package com.github.jerrysearch.tns.server.util;

import java.text.SimpleDateFormat;

public final class DateUtil {
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss");
	
	public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
			"HH");
}
