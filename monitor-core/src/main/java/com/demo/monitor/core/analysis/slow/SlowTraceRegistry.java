package com.demo.monitor.core.analysis.slow;

import com.demo.monitor.core.analysis.SlowSpanDetector;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNames;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/** 每个方法保留最近 1000 个慢调用，用于指标到 Trace 的下钻。 */
public class SlowTraceRegistry {
    private static final int MAX_PER_METHOD = 1000;
    private static final ConcurrentHashMap<String, Deque<Span>> SLOW_MAP = new ConcurrentHashMap<>();

    public static void record(Span span) {
        if (!SlowSpanDetector.isSlow(span)) return;
        Deque<Span> samples = SLOW_MAP.computeIfAbsent(SpanNames.method(span), k -> new ArrayDeque<>());
        synchronized (samples) {
            samples.addFirst(span);
            while (samples.size() > MAX_PER_METHOD) samples.removeLast();
        }
    }

    public static List<Span> getSlowSpans(String method) {
        Deque<Span> samples = SLOW_MAP.get(method);
        if (samples == null) return List.of();
        synchronized (samples) { return List.copyOf(samples); }
    }
}
