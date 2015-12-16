namespace java com.jerry.thriftnameserver.rpc

service PoolAble {

	/**
	*	用于检测该服务是否有效,返回vnodes数，可以根据自己的负载动态调整vnodes数，实现动态负载均衡，返回0或<0 表示服务不可用
	*	当然nameserver不会让vnodes肆意指定，会通过Max操作，返回合理vnodes给客户端
	*/
	i32 ping()
}