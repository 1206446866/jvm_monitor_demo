package com.demo.monitor.core.analysis.slow;

import com.demo.monitor.core.model.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 慢 Trace 索引器
 */
public class SlowTraceRegistry {

    private static final ConcurrentHashMap<String, List<Span>> SLOW_MAP = new ConcurrentHashMap<>();

    /**
     * 记录慢 span
     */
    public static void record(Span span) {

        // ⚠️ 这里用 P95 / P99 阈值判断（先简单写死）
        if (span.getCost() > 100_000_000L) { // 100ms
            SLOW_MAP.computeIfAbsent(span.getMethodName(), k -> new ArrayList<>()).add(span);
        }
    }

    public static List<Span> getSlowSpans(String method) {
        return SLOW_MAP.getOrDefault(method, List.of());
    }
}