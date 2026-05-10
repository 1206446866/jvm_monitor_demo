package com.demo.agent.trace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpanBuffer {

    private static final ConcurrentHashMap<String, List<Span>> MAP =
            new ConcurrentHashMap<>();

    public static void add(Span span) {

        MAP.computeIfAbsent(span.getTraceId(),
                k -> Collections.synchronizedList(new ArrayList<>()))
                .add(span);
    }

    public static List<Span> getByTraceId(String traceId) {
        return MAP.getOrDefault(traceId, Collections.emptyList());
    }

    public static void clear(String traceId) {
        MAP.remove(traceId);
    }
}