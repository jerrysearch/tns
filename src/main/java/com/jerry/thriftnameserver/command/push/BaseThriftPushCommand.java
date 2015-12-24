package com.jerry.thriftnameserver.command.push;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public abstract class BaseThriftPushCommand<T> extends HystrixCommand<T> {
	protected BaseThriftPushCommand(com.netflix.hystrix.HystrixCommand.Setter setter) {
		super(setter);
	}
	private static final HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory
			.asKey("PushGroup");
	private static final HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("node");
	private static final HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory
			.asKey("T-ThriftPushGroup");

	private static final HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties
			.Setter().withExecutionTimeoutInMilliseconds(2000).withFallbackEnabled(true);
	private static final HystrixThreadPoolProperties.Setter threadPoolProperties = HystrixThreadPoolProperties
			.Setter().withCoreSize(4).withMaxQueueSize(10);
	public static final HystrixCommand.Setter setter = HystrixCommand.Setter
			.withGroupKey(groupKey).andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
			.andCommandPropertiesDefaults(commandProperties)
			.andThreadPoolPropertiesDefaults(threadPoolProperties);
}
