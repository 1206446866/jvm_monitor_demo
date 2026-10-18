package com.demo.monitor.core.analysis.link;

import com.demo.monitor.core.analysis.TraceSpanStorage;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNames;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/** 方法到最近 Trace 的有界索引；反向查询复用调用链存储。 */
public class MetricTraceLink {
    private static final int MAX_PER_METHOD = 1000;
    private static final ConcurrentHashMap<String, LinkedHashSet<String>> METHOD_TRACES = new ConcurrentHashMap<>();

    public static void record(Span span) {
        LinkedHashSet<String> traces = METHOD_TRACES.computeIfAbsent(SpanNames.method(span), k -> new LinkedHashSet<>());
        synchronized (traces) {
            traces.remove(span.getTraceId());
            traces.add(span.getTraceId());
            while (traces.size() > MAX_PER_METHOD) traces.remove(traces.iterator().next());
        }
    }

    public static List<String> traceIds(String method, int limit) {
        LinkedHashSet<String> traces = METHOD_TRACES.get(method);
        if (traces == null) return List.of();
        synchronized (traces) {
            var snapshot = new java.util.ArrayList<>(traces);
            java.util.Collections.reverse(snapshot);
            return snapshot.stream().limit(limit).toList();
        }
    }

    public static List<Span> spans(String traceId) {
        return TraceSpanStorage.getByTraceId(traceId);
    }
}
