package com.demo.monitor.core.metrics;

import com.demo.monitor.core.metrics.window.SlidingWindow;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.dashboard.model.MethodMetric;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MethodSlidingWindowManager {

    private static final ConcurrentHashMap<String, SlidingWindow> WINDOWS = new ConcurrentHashMap<>();

    public static void record(Span span) {

        String method = span.getMethodName();

        SlidingWindow window = WINDOWS.computeIfAbsent(method, k -> new SlidingWindow());

        window.record(span);
    }

    public static long qps(String method) {

        SlidingWindow window = WINDOWS.get(method);

        return window == null ? 0 : window.qps();
    }

    public static double avgLatency(String method) {

        SlidingWindow window = WINDOWS.get(method);

        return window == null ? 0 : window.avgLatency();
    }

    public static double errorRate(String method) {

        SlidingWindow window = WINDOWS.get(method);

        return window == null ? 0 : window.errorRate();
    }

    public static List<MethodMetric> topSlowMethods(int n) {

        return WINDOWS.entrySet().stream().map(e -> new MethodMetric(e.getKey(), e.getValue().avgLatency(), e.getValue().errorRate(), e.getValue().totalRequests())).sorted((a, b) -> Double.compare(b.getAvgLatency(), a.getAvgLatency())).limit(n).toList();
    }
}
