package com.demo.monitor.core.metrics.histogram;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法级 histogram 注册中心
 */
public class HistogramRegistry {

    private static final ConcurrentHashMap<String, HistogramWindow> MAP =
            new ConcurrentHashMap<>();

    public static void record(String method, long cost) {

        HistogramWindow window =
                MAP.computeIfAbsent(method, k -> new HistogramWindow());

        window.record(cost);
    }

    public static HistogramWindow get(String method) {
        return MAP.get(method);
    }
}