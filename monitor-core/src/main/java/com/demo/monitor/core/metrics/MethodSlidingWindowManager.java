package com.demo.monitor.core.metrics;

import com.demo.monitor.core.metrics.window.SlidingWindow;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.dashboard.model.MethodMetric;
import com.demo.monitor.core.metrics.histogram.HistogramRegistry;
import com.demo.monitor.core.metrics.histogram.HistogramCalculator;
import com.demo.monitor.core.analysis.link.MetricTraceLink;
import com.demo.monitor.core.analysis.slow.SlowTraceRegistry;
import com.demo.monitor.core.model.SpanNames;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MethodSlidingWindowManager {

    private static final ConcurrentHashMap<String, SlidingWindow> WINDOWS = new ConcurrentHashMap<>();

    public static void record(Span span) {

        String method = SpanNames.method(span);

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

        return WINDOWS.entrySet().stream()
                .filter(e -> e.getValue().totalRequests() > 0)
                .sorted((a, b) -> Double.compare(b.getValue().avgLatency(), a.getValue().avgLatency()))
                .limit(n).map(e -> snapshot(e.getKey())).toList();
    }

    public static MethodMetric snapshot(String method) {
        SlidingWindow window = WINDOWS.get(method);
        if (window == null) return null;
        MethodMetric metric = new MethodMetric(method, window.avgLatency(),
                window.errorRate(), window.totalRequests());
        var histogram = HistogramRegistry.get(method);
        List<Long> samples = histogram == null ? List.of() : histogram.snapshot();
        metric.setP95(HistogramCalculator.p95(samples));
        metric.setP99(HistogramCalculator.p99(samples));
        metric.setTraceIds(MetricTraceLink.traceIds(method, 20));
        metric.setSlowTraceIds(SlowTraceRegistry.getSlowSpans(method).stream()
                .map(Span::getTraceId).distinct().limit(20).toList());
        return metric;
    }
}
