package com.demo.agent.trace.propagation;

import java.util.concurrent.Callable;

public class TraceExecutor {

    public static Runnable wrap(Runnable task) {
        return new TraceRunnable(task);
    }

    public static <V> Callable<V> wrap(Callable<V> task) {
        return new TraceCallable<>(task);
    }
}