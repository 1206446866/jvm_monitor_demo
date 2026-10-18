package com.demo.monitor.core.analysis;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNames;
import com.demo.monitor.core.model.SpanTimings;
import java.util.*;
import java.util.stream.Collectors;

public class HotspotAnalyzer {
    public record Hotspot(String method, long count, long totalCost, long selfCost, long errorCount) {}

    /** 兼容原入口，返回包含子调用的累计耗时。 */
    public static Map<String, Long> analyze(List<Span> spans) {
        return analyzeDetailed(spans).stream().collect(Collectors.toMap(Hotspot::method, Hotspot::totalCost));
    }

    /** 以自身耗时排序，父调用与子调用不会重复占用热点耗时。 */
    public static List<Hotspot> analyzeDetailed(List<Span> spans) {
        Map<String, List<Span>> children = new HashMap<>();
        for (Span span : spans) {
            if (span.getParentSpanId() != null) {
                children.computeIfAbsent(span.getParentSpanId(), k -> new ArrayList<>()).add(span);
            }
        }
        Map<String, long[]> totals = new HashMap<>();
        for (Span span : spans) {
            List<long[]> intervals = new ArrayList<>();
            for (Span child : children.getOrDefault(span.getSpanId(), List.of())) {
                intervals.add(new long[]{child.getStartTime(), child.getEndTime()});
            }
            long[] stats = totals.computeIfAbsent(SpanNames.method(span), k -> new long[4]);
            stats[0]++;
            stats[1] += span.getCost();
            stats[2] += SpanTimings.selfCost(span.getStartTime(), span.getCost(), intervals);
            if (span.isError()) stats[3]++;
        }
        return totals.entrySet().stream().map(e -> new Hotspot(e.getKey(), e.getValue()[0],
                        e.getValue()[1], e.getValue()[2], e.getValue()[3]))
                .sorted(Comparator.comparingLong(Hotspot::selfCost).reversed().thenComparing(Hotspot::method))
                .toList();
    }
}
