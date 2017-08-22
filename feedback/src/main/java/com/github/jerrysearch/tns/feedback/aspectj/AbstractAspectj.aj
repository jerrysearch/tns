package com.github.jerrysearch.tns.feedback.aspectj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract aspect AbstractAspectj {
    public final Logger log = LoggerFactory.getLogger(getClass());
}
