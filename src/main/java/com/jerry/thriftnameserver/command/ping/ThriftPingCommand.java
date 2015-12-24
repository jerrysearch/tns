package com.jerry.thriftnameserver.command.ping;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.aspects.Loggable;
import com.jerry.thriftnameserver.bean.Node;
import com.jerry.thriftnameserver.rpc.PoolAble;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class ThriftPingCommand extends HystrixCommand<Integer> {
	private static final HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory
			.asKey("ThriftPingGroup");
	private static final HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("ping");
	private static final HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory
			.asKey("T-ThriftPingGroup");

	private static final HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties
			.Setter().withExecutionTimeoutInMilliseconds(2000).withFallbackEnabled(true);
	private static final HystrixThreadPoolProperties.Setter threadPoolProperties = HystrixThreadPoolProperties
			.Setter().withCoreSize(4).withMaxQueueSize(10);
	private static final HystrixCommand.Setter setter = HystrixCommand.Setter
			.withGroupKey(groupKey).andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
			.andCommandPropertiesDefaults(commandProperties)
			.andThreadPoolPropertiesDefaults(threadPoolProperties);

	private final Node node;

	public ThriftPingCommand(Node node) {
		super(setter);
		this.node = node;
	}

	@Loggable
	public int ping(Node node) {
		return this.execute();
	}

	@Override
	protected Integer run() throws Exception {
		String host = node.getHost();
		int port = node.getPort();

		TSocket transport = new TSocket(host, port, 1000);
		transport.open();
		TProtocol protocol = new TBinaryProtocol(transport);
		PoolAble.Client client = new PoolAble.Client(protocol);
		int vNodes = client.ping();
		transport.close();
		return vNodes;
	}

	private static final Logger log = LoggerFactory.getLogger(ThriftPingCommand.class);

	@Override
	protected Integer getFallback() {
		log.error("Fallback --> {}", this.node.toAllString());
		return -1;
	}
}
