package com.github.jerrysearch.tns.feedback;

import com.github.jerrysearch.tns.feedback.aspectj.Feedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hello {

	private Logger log = LoggerFactory.getLogger(Hello.class);

	public static void main(String[] args) throws Exception {

		Hello hello = new Hello();
		int i = hello.sayHello(System.currentTimeMillis(), 10);
		hello.log.info("return is = {}", i);
	}

	/**
	 * @param time 时间
	 * @param tmp	变量
	 * @return 变量
	 *
	 */
	@Feedback
	private int sayHello(long time, int tmp) {
		log.info("time = {}, tmp = {}", time, tmp);
		return 0;
	}
}
