package com.demo.monitor.core.analysis.link;

import com.demo.monitor.core.model.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Metrics ↔ Trace 双向索引
 */
public class MetricTraceLink {

    /**
     * method → traceId list
     */
    private static final ConcurrentHashMap<String, List<String>> METHOD_TRACE_MAP =
            new ConcurrentHashMap<>();

    /**
     * traceId → spans（用于反查）
     */
    private static final ConcurrentHashMap<String, List<Span>> TRACE_MAP =
            new ConcurrentHashMap<>();

    public static void record(Span span) {

        String method = span.getMethodName();
        String traceId = span.getTraceId();

        METHOD_TRACE_MAP
                .computeIfAbsent(method, k -> new ArrayList<>())
                .add(traceId);

        TRACE_MAP
                .computeIfAbsent(traceId, k -> new ArrayList<>())
                .add(span);
    }

}