package com.github.jerrysearch.tns.feedback.guava;

import com.google.common.reflect.Reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by jerry on 2017/7/21.
 */
public class TestReflection {
    public static void main(String[] args) {
        InvocationHandler invocationHandler = new MyInvocationHandler(new MyFoo());
        IFoo foo = Reflection.newProxy(IFoo.class, invocationHandler);
        int i = foo.doSomething(1);
        System.out.println(i);

        System.out.println(Proxy.getInvocationHandler(foo));

        Object o = new Integer(1);
        Integer j = Integer.class.cast(o);
        System.out.println(j);
    }
}
