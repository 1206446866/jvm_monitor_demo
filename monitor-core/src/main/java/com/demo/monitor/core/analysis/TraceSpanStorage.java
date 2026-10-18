package com.demo.monitor.core.analysis;

import com.demo.monitor.core.model.Span;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TraceSpanStorage {

    private static final ConcurrentHashMap<String, List<Span>> MAP = new ConcurrentHashMap<>();

    public static void add(Span span) {
        MAP.computeIfAbsent(span.getTraceId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(span);
    }

    public static List<Span> getByTraceId(String traceId) {
        List<Span> spans = MAP.get(traceId);
        if (spans == null) return List.of();
        synchronized (spans) { return List.copyOf(spans); }
    }

    public static List<List<Span>> recentTraces(int limit) {
        return MAP.keySet().stream().map(TraceSpanStorage::getByTraceId)
                .filter(spans -> spans.stream().anyMatch(s -> s.getParentSpanId() == null))
                .sorted(java.util.Comparator.<List<Span>>comparingLong(spans ->
                        spans.stream().mapToLong(Span::getStartTime).min().orElse(0)).reversed())
                .limit(limit).toList();
    }

    public static void clear(String traceId) {
        MAP.remove(traceId);
    }


}
