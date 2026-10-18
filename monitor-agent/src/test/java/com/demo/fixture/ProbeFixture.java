package com.demo.fixture;

import com.demo.monitor.core.trace.context.TraceContext;

public class ProbeFixture {
    public String traceId;

    public int outer() {
        traceId = TraceContext.current().getTraceId();
        return inner() + 1;
    }

    private int inner() {
        return 41;
    }

    public void fail(RuntimeException failure) {
        throw failure;
    }
}
