package com.demo.agent.trace.exporter;

import com.demo.agent.trace.model.Span;
import com.demo.agent.trace.model.SpanBuffer;
import com.demo.agent.trace.util.JsonUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TraceExporter {

    /**
     * 对外入口：导出 trace
     */
    public static String exportToJson(String traceId) {

        List<Span> allSpans = SpanBuffer.getByTraceId(traceId);

        Map<String, Object> trace = new HashMap<>();
        trace.put("traceId", traceId);
        trace.put("spans", convert(allSpans));

        return JsonUtil.toJson(trace);
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
            map.put("method", span.getMethod());
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