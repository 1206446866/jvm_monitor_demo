package com.demo.agent.trace.exporter;

import com.demo.agent.trace.builder.TraceTreeBuilder;
import com.demo.agent.trace.model.Span;
import com.demo.agent.trace.model.SpanBuffer;
import com.demo.agent.trace.model.SpanNode;
import com.demo.agent.trace.util.JsonUtil;

import java.util.*;

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

    public static String exportTree(String traceId) {

        List<Span> spans = SpanBuffer.getByTraceId(traceId);

        SpanNode root = TraceTreeBuilder.build(spans);

        Map<String, Object> result = new HashMap<>();
        result.put("traceId", traceId);
        result.put("root", toMap(root));

        return JsonUtil.toJson(result);
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

    private static Map<String, Object> toMap(SpanNode node) {

        Map<String, Object> map = new HashMap<>();

        map.put("spanId", node.getSpanId());
        map.put("method", node.getMethod());
        map.put("cost", node.getCost());

        List<Map<String, Object>> children = new ArrayList<>();

        for (SpanNode child : node.children) {
            children.add(toMap(child));
        }

        map.put("children", children);

        return map;
    }
}