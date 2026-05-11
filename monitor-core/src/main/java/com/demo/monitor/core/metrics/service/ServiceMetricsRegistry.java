package com.demo.monitor.core.metrics.service;

import com.demo.monitor.core.model.Span;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务级指标注册中心
 */
public class ServiceMetricsRegistry {

    private static final ConcurrentHashMap<String, ServiceMetrics> MAP =
            new ConcurrentHashMap<>();

    public static void record(Span span) {

        String service = extractService(span);

        ServiceMetrics metrics =
                MAP.computeIfAbsent(service, k -> new ServiceMetrics());

        metrics.record(
                span.getCost(),
                span.isError()
        );
    }

    public static ServiceMetrics get(String service) {
        return MAP.get(service);
    }

    private static String extractService(Span span) {

        String method = span.getMethodName();

        int idx = method.indexOf(".");

        if (idx == -1) {
            return "unknown";
        }

        return method.substring(0, idx);
    }

}