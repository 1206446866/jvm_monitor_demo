package com.demo.agent.trace.propagation;

import com.demo.agent.trace.context.TraceContext;
import com.demo.agent.trace.model.Span;

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