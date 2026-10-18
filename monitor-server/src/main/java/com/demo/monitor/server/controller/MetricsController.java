package com.demo.monitor.server.controller;

import com.demo.monitor.core.analysis.link.MetricTraceLink;
import com.demo.monitor.core.analysis.slow.SlowTraceRegistry;
import com.demo.monitor.core.metrics.MethodSlidingWindowManager;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.dashboard.model.MethodMetric;
import com.demo.monitor.core.trace.exporter.summary.TraceSummary;
import com.demo.monitor.core.trace.exporter.summary.TraceSummaryBuilder;
import com.demo.monitor.server.analysis.TraceAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
public class MetricsController {
    @GetMapping("/api/metrics/slow-methods")
    public List<MethodMetric> slowMethods(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        TraceAnalysisService.validateLimit(limit);
        return MethodSlidingWindowManager.topSlowMethods(limit);
    }

    @GetMapping("/api/metrics/method")
    public MethodMetric method(@RequestParam("method") String method) {
        validateMethod(method);
        MethodMetric metric = MethodSlidingWindowManager.snapshot(method);
        if (metric == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Method not found");
        return metric;
    }

    @GetMapping("/api/metrics/traces")
    public List<TraceSummary> traces(@RequestParam("method") String method,
                                    @RequestParam(name = "slowOnly", defaultValue = "false") boolean slowOnly,
                                    @RequestParam(name = "limit", defaultValue = "20") int limit) {
        validateMethod(method);
        TraceAnalysisService.validateLimit(limit);
        List<String> ids = slowOnly ? SlowTraceRegistry.getSlowSpans(method).stream()
                .map(Span::getTraceId).distinct().limit(limit).toList()
                : MetricTraceLink.traceIds(method, limit);
        return ids.stream().map(MetricTraceLink::spans).filter(spans -> !spans.isEmpty())
                .map(spans -> TraceSummaryBuilder.build(spans.get(0).getTraceId(), spans)).toList();
    }

    private static void validateMethod(String method) {
        if (method.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "method must not be blank");
    }
}
