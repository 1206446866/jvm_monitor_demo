package com.demo.agent.trace.exporter;

import com.demo.agent.trace.model.Span;

import java.util.*;

public class TraceExporter {

    /**
     * 对外入口：导出 trace
     */
    public static String exportToJson(Span rootSpan) {

        List<Span> allSpans = new ArrayList<>();
        collect(rootSpan, allSpans);

        Map<String, Object> trace = new HashMap<>();
        trace.put("traceId", rootSpan.getTraceId());
        trace.put("spans", convert(allSpans));

        return JsonUtil.toJson(trace);
    }

    /**
     * 递归收集 Span
     */
    private static void collect(Span span, List<Span> list) {
        if (span == null) return;

        list.add(span);

        if (span.getChildren() != null) {
            for (Span child : span.getChildren()) {
                collect(child, list);
            }
        }
    }

    /**
     * Span → Map
     */
    private static List<Map<String, Object>> convert(List<Span> spans) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Span span : spans) {
            Map<String, Object> map = new HashMap<>();

            map.put("spanId", span.getSpanId());
            map.put("parentSpanId", span.getParentSpanId());
            map.put("className", span.getClassName());
            map.put("methodName", span.getMethodName());
            map.put("startTime", span.getStartTime());
            map.put("endTime", span.getEndTime());
            map.put("cost", span.getCost());
            map.put("error", span.isError());
            map.put("errorMsg", span.getErrorMsg());
            map.put("tags", span.getTags());

            result.add(map);
        }

        return result;
    }

}