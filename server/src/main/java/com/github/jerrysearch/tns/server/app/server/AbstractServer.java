package com.github.jerrysearch.tns.server.app.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractServer {
    protected Logger log = LoggerFactory.getLogger(getClass().getName());

    public abstract void start();

    public abstract void shutdown();
}
