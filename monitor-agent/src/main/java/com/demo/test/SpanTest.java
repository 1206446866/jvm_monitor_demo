package com.demo.test;


import com.demo.agent.trace.model.Span;

public class SpanTest {

    public static void main(String[] args)
            throws Exception {

        Span span =
                new Span(
                        "UserService",
                        "queryUser"
                );

        /**
         * 模拟业务耗时
         */
        Thread.sleep(120);

        span.finish();

        System.out.println(span);
    }
}