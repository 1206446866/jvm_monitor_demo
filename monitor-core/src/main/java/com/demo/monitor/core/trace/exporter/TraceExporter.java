package com.demo.monitor.core.trace.exporter;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.util.JsonUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TraceExporter {

    public static String exportBatch(List<Span> spans) {

        Map<String, List<Span>> grouped = spans.stream()
                .collect(Collectors.groupingBy(Span::getTraceId));

        List<Map<String, Object>> traces = new ArrayList<>();

        for (Map.Entry<String, List<Span>> entry : grouped.entrySet()) {

            entry.getValue().sort(Comparator.comparing(Span::getStartTime));

            Map<String, Object> trace = new HashMap<>();

            trace.put("traceId", entry.getKey());

            trace.put("spans", entry.getValue().stream()
                    .map(TraceExporter::convertSpan)
                    .collect(Collectors.toList())
            );

            traces.add(trace);
        }

        return JsonUtil.toJson(traces);
    }

    private static Map<String, Object> convertSpan(Span span) {

        Map<String, Object> map = new HashMap<>();

        map.put("spanId", span.getSpanId());
        map.put("parentSpanId", span.getParentSpanId());
        map.put("method", span.getMethodName());
        map.put("startTime", span.getStartTime());
        map.put("endTime", span.getEndTime());
        map.put("cost", span.getCost());
        map.put("error", span.isError());
        map.put("errorMsg", span.getErrorMsg());
        map.put("tags", span.getTags());

        return map;
    }
}