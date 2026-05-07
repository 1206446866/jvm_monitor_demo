package com.demo.test;

import com.demo.agent.trace.TraceContext;

public class TestMain {

    public static void main(String[] args) {

        System.out.println(TraceContext.getTraceId());

        test();

        System.out.println(TraceContext.getTraceId());
    }

    public static void test() {
        System.out.println(TraceContext.getTraceId());
    }
}