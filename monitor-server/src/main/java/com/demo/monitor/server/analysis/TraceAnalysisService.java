package com.demo.monitor.server.analysis;

import com.demo.monitor.core.analysis.HotspotAnalyzer;
import com.demo.monitor.core.analysis.SlowSpanDetector;
import com.demo.monitor.core.analysis.TraceSpanStorage;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNode;
import com.demo.monitor.core.trace.exporter.FlameGraphExporter;
import com.demo.monitor.core.trace.exporter.flame.FlameGraphBuilder;
import com.demo.monitor.core.trace.exporter.summary.TraceSummary;
import com.demo.monitor.core.trace.exporter.summary.TraceSummaryBuilder;
import com.demo.monitor.core.trace.exporter.tree.TraceTreeBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TraceAnalysisService {
    public static void validateLimit(int limit) {
        if (limit < 1 || limit > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be between 1 and 100");
        }
    }
    public List<Span> spans(String traceId) {
        List<Span> spans = TraceSpanStorage.getByTraceId(traceId);
        if (spans.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trace not found");
        }
        return spans;
    }

    public List<TraceSummary> recent(int limit) {
        return TraceSpanStorage.recentTraces(limit).stream()
                .map(spans -> TraceSummaryBuilder.build(spans.get(0).getTraceId(), spans)).toList();
    }

    public SpanNode tree(String traceId) {
        return TraceTreeBuilder.build(spans(traceId));
    }

    public Map<String, Object> flameGraph(String traceId) {
        return FlameGraphExporter.export(traceId, FlameGraphBuilder.build(tree(traceId)));
    }

    public Map<String, Object> analysis(String traceId) {
        List<Span> spans = spans(traceId);
        SpanNode tree = TraceTreeBuilder.build(spans);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("traceId", traceId);
        result.put("timeUnit", "ns");
        result.put("slowThresholdNanos", SlowSpanDetector.THRESHOLD_NANOS);
        result.put("summary", TraceSummaryBuilder.build(traceId, spans));
        result.put("tree", tree);
        result.put("flameGraph", FlameGraphExporter.export(traceId, FlameGraphBuilder.build(tree)));
        result.put("hotspots", HotspotAnalyzer.analyzeDetailed(spans));
        result.put("slowSpans", SlowSpanDetector.detect(spans));
        return result;
    }
}
