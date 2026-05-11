package com.demo.monitor.core.trace.exporter.summary;

import com.demo.monitor.core.model.Span;

import java.util.List;

public class TraceSummaryBuilder {

    public static TraceSummary build(String traceId, List<Span> spans) {

        TraceSummary summary = new TraceSummary();

        summary.setTraceId(traceId);

        summary.setSpanCount(spans.size());

        long total = 0;
        long max = 0;
        String slowest = null;
        int slowCount = 0;

        for (Span span : spans) {

            total = Math.max(total, span.getEndTime());

            if (span.getCost() > max) {
                max = span.getCost();
                slowest = span.getMethodName();
            }

            if (span.getCost() > 50) {
                slowCount++;
            }
        }

        summary.setTotalCost(total);
        summary.setSlowSpanCount(slowCount);
        summary.setSlowestSpan(slowest);

        return summary;
    }
}
