package com.github.jerrysearch.tns.feedback.guava;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by jerry on 2017/7/21.
 */
public class MyInvocationHandler implements InvocationHandler {

    private final Object t;

    public MyInvocationHandler(Object t) {
        this.t = t;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println(proxy.getClass().getName());

        System.out.println("start");
        System.out.println(Arrays.toString(args));
        Object object = method.invoke(t, args);
        System.out.println("end");
        return object;
    }
}
