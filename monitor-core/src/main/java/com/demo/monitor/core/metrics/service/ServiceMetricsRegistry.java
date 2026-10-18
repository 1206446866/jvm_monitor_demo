package com.demo.monitor.core.metrics.service;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNames;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务级指标注册中心
 */
public class ServiceMetricsRegistry {

    private static final ConcurrentHashMap<String, ServiceMetrics> MAP =
            new ConcurrentHashMap<>();

    public static void record(Span span) {

        String service = SpanNames.service(span);

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


}
