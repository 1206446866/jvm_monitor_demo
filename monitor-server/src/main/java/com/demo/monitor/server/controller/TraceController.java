package com.demo.monitor.server.controller;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNode;
import com.demo.monitor.core.trace.exporter.summary.TraceSummary;
import com.demo.monitor.server.analysis.TraceAnalysisService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class TraceController {
    private final TraceAnalysisService analysis;

    public TraceController(TraceAnalysisService analysis) {
        this.analysis = analysis;
    }

    @GetMapping("/api/traces")
    public List<TraceSummary> recent(@RequestParam(name = "limit", defaultValue = "20") int limit) {
        TraceAnalysisService.validateLimit(limit);
        return analysis.recent(limit);
    }

    @GetMapping("/api/trace/{traceId}")
    public List<Span> trace(@PathVariable("traceId") String traceId) {
        return analysis.spans(traceId);
    }

    @GetMapping("/api/trace/{traceId}/tree")
    public SpanNode tree(@PathVariable("traceId") String traceId) {
        return analysis.tree(traceId);
    }

    @GetMapping("/api/trace/{traceId}/flamegraph")
    public Map<String, Object> flameGraph(@PathVariable("traceId") String traceId) {
        return analysis.flameGraph(traceId);
    }

    @GetMapping("/api/trace/{traceId}/analysis")
    public Map<String, Object> analysis(@PathVariable("traceId") String traceId) {
        return analysis.analysis(traceId);
    }
}
