package com.demo.monitor.core.test;

import com.demo.monitor.core.model.Span;

/**
 * 用于测试 Metrics 链路的 mock Span
 */
public class SpanMock extends Span {

    private final long cost;

    public SpanMock(long cost) {
        this.cost = cost;
    }

    @Override
    public String getClassName() {
        return "TestService";
    }

    @Override
    public String getMethodName() {
        return "testMethod";
    }

    @Override
    public long getCost() {
        return cost;
    }
}