package com.demo.monitor.core.metrics;

import com.demo.monitor.core.model.Span;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsRegistry {

    /**
     * method -> metrics
     */
    private static final ConcurrentHashMap<String, MethodMetrics> METRICS
            = new ConcurrentHashMap<>();

    public static void record(Span span) {

        String method =
                span.getClassName() + "." + span.getMethodName();

        METRICS.computeIfAbsent(
                method,
                k -> {
                    MethodMetrics m = new MethodMetrics();
                    m.setMethod(method);
                    return m;
                }
        ).record(span);
    }

    public static Collection<MethodMetrics> all() {
        return METRICS.values();
    }
}