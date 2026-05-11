package com.demo.monitor.core.trace.propagation;

import com.demo.monitor.core.trace.context.TraceContext;
import com.demo.monitor.core.model.Span;

import java.util.Deque;
import java.util.concurrent.Callable;

public class TraceCallable<V> implements Callable<V> {

    private final Callable<V> task;
    private final Deque<Span> contextSnapshot;

    public TraceCallable(Callable<V> task) {
        this.task = task;
        this.contextSnapshot = TraceContext.snapshot();
    }

    @Override
    public V call() throws Exception {

        try {
            TraceContext.restore(contextSnapshot);
            return task.call();
        } finally {
            TraceContext.clear();
        }
    }
}