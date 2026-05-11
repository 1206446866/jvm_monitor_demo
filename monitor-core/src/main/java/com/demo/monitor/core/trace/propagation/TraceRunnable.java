package com.demo.monitor.core.trace.propagation;

import com.demo.monitor.core.trace.context.TraceContext;
import com.demo.monitor.core.model.Span;

import java.util.Deque;

public class TraceRunnable implements Runnable {

    private final Runnable task;
    private final Deque<Span> contextSnapshot;

    public TraceRunnable(Runnable task) {
        this.task = task;
        this.contextSnapshot = TraceContext.snapshot();
    }

    @Override
    public void run() {

        try {
            TraceContext.restore(contextSnapshot);
            task.run();
        } finally {
            TraceContext.clear();
        }
    }
}