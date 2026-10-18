package com.demo.monitor.core.trace.exporter.summary;

import com.demo.monitor.core.analysis.SlowSpanDetector;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNames;
import java.util.Comparator;
import java.util.List;

public class TraceSummaryBuilder {
    public static TraceSummary build(String traceId, List<Span> spans) {
        TraceSummary summary = new TraceSummary();
        summary.setTraceId(traceId);
        summary.setSpanCount(spans.size());
        long start = spans.stream().mapToLong(Span::getStartTime).min().orElse(0);
        long end = spans.stream().mapToLong(Span::getEndTime).max().orElse(start);
        summary.setTotalCost(Math.max(0, end - start));
        summary.setSlowSpanCount(SlowSpanDetector.detect(spans).size());
        spans.stream().max(Comparator.comparingLong(Span::getCost))
                .ifPresent(s -> summary.setSlowestSpan(SpanNames.method(s)));
        return summary;
    }
}
