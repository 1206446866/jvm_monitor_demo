package com.demo.agent.probe;

import com.demo.agent.storage.MetricStore;


public class MethodTimer {

    private static final ThreadLocal<Long> START = new ThreadLocal<>();

    public static void before(String method) {
        START.set(System.nanoTime());
    }

    public static void after(String method) {
        Long start = START.get();
        if (start == null) return;

        long cost = System.nanoTime() - start;
        START.remove();

        MetricStore.record(method, cost);
    }
}