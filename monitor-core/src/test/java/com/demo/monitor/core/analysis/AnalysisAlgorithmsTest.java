package com.demo.monitor.core.analysis;

import com.demo.monitor.core.analysis.link.MetricTraceLink;
import com.demo.monitor.core.metrics.histogram.HistogramCalculator;
import com.demo.monitor.core.metrics.histogram.HistogramWindow;
import com.demo.monitor.core.model.*;
import com.demo.monitor.core.trace.exporter.FlameGraphExporter;
import com.demo.monitor.core.trace.exporter.flame.FlameGraphBuilder;
import com.demo.monitor.core.trace.exporter.flame.FlameNode;
import com.demo.monitor.core.trace.exporter.summary.TraceSummaryBuilder;
import com.demo.monitor.core.trace.exporter.tree.TraceTreeBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisAlgorithmsTest {
    @Test
    void buildsOutOfOrderNestedCallsAndUsesOneFlameOrigin() {
        Span root = span("r", null, "demo.User.outer", 100, 1000);
        Span child = span("c", "r", "demo.User.inner", 200, 700);
        Span grandchild = span("g", "c", "demo.Db.query", 300, 500);
        SpanNode tree = TraceTreeBuilder.build(List.of(grandchild, child, root));
        assertEquals("r", tree.getSpanId());
        assertEquals(2, tree.getChildren().get(0).getChildren().get(0).getDepth());
        FlameNode flame = FlameGraphBuilder.build(tree);
        assertEquals(100, flame.getChildren().get(0).getOffset());
        assertEquals(200, flame.getChildren().get(0).getChildren().get(0).getOffset());
        assertEquals(100, SpanNode.from(root).getStartTime());
    }

    @Test
    void keepsOrphansAndMultipleRootsWithoutLosingCalls() {
        Span orphan = span("orphan", "missing", "demo.Db.query", 50, 100);
        Span root = span("root", null, "demo.User.login", 100, 300);
        SpanNode tree = TraceTreeBuilder.build(List.of(orphan, root));
        assertEquals("virtual-root", tree.getSpanId());
        assertEquals(2, tree.getChildren().size());
        assertEquals(250, tree.getCost());
        assertNull(TraceTreeBuilder.build(List.of()));
        assertNull(TraceTreeBuilder.build(null));
    }

    @Test
    void rejectsCyclicEdgesRatherThanRecursingForever() {
        SpanNode tree = TraceTreeBuilder.build(List.of(
                span("a", "b", "demo.A.run", 0, 100),
                span("b", "a", "demo.B.run", 10, 80)));
        assertEquals(2, tree.getChildren().size());
        assertTrue(tree.getChildren().stream().allMatch(node -> node.getChildren().isEmpty()));
    }

    @Test
    @SuppressWarnings("unchecked")
    void overlappingChildrenAreSubtractedOnceForHotspotsAndFlames() {
        List<Span> spans = List.of(span("r", null, "demo.Root.run", 0, 100),
                span("a", "r", "demo.A.run", 10, 70),
                span("b", "r", "demo.B.run", 50, 90));
        SpanNode root = TraceTreeBuilder.build(spans);
        assertEquals(20, root.getSelfCost());
        var hotspots = HotspotAnalyzer.analyzeDetailed(spans);
        assertEquals(20, hotspots.stream().filter(h -> h.method().equals("demo.Root.run"))
                .findFirst().orElseThrow().selfCost());
        Map<String, Object> flame = FlameGraphExporter.export("trace", FlameGraphBuilder.build(root));
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) flame.get("nodes");
        assertEquals(20L, nodes.get(0).get("selfCost"));
        assertEquals("demo.A.run", hotspots.get(0).method());
    }

    @Test
    void summaryUsesDurationAndOneHundredMillisecondThreshold() {
        long ms = 1_000_000;
        List<Span> spans = List.of(span("r", null, "demo.User.run", 100 * ms, 500 * ms),
                span("a", "r", "demo.Db.atThreshold", 200 * ms, 300 * ms),
                span("b", "r", "demo.Db.overThreshold", 300 * ms, 401 * ms));
        var summary = TraceSummaryBuilder.build("trace", spans);
        assertEquals(400 * ms, summary.getTotalCost());
        assertEquals(2, summary.getSlowSpanCount());
        assertEquals(2, SlowSpanDetector.detect(spans).size());
        assertEquals("demo.User.run", summary.getSlowestSpan());
    }

    @Test
    void percentilesExpireAtTheSameSecondBoundaryAsMethodMetrics() {
        AtomicLong clock = new AtomicLong(100_500);
        HistogramWindow window = new HistogramWindow(clock::get);
        for (int i = 1; i <= 100; i++) window.record(i);
        assertEquals(95, HistogramCalculator.p95(window.snapshot()));
        assertEquals(99, HistogramCalculator.p99(window.snapshot()));
        clock.set(159_999);
        assertEquals(100, window.snapshot().size());
        clock.set(160_000);
        assertTrue(window.snapshot().isEmpty());
        assertEquals(0, HistogramCalculator.p95(window.snapshot()));
    }

    @Test
    void methodTraceIndexDeduplicatesAndBoundsHistory() {
        for (int i = 0; i < 1005; i++) {
            Span span = span("s" + i, null, "demo.Index.run", 0, 100);
            span.setTraceId("trace-" + i);
            MetricTraceLink.record(span);
            MetricTraceLink.record(span);
        }
        List<String> ids = MetricTraceLink.traceIds("demo.Index.run", 2000);
        assertEquals(1000, ids.size());
        assertEquals("trace-1004", ids.get(0));
        assertEquals("trace-5", ids.get(999));
    }

    @Test
    void spanClockAndServiceIdentityAreConsistent() {
        long before = System.nanoTime();
        Span split = new Span("demo.UserService", "query");
        assertTrue(split.getStartTime() >= before && split.getStartTime() <= System.nanoTime());
        assertEquals("demo.UserService.query", SpanNames.method(split));
        assertEquals("demo.UserService", SpanNames.service(split));
        assertEquals("demo.DbService", SpanNames.service(new Span("demo.DbService.query")));
    }

    private static Span span(String id, String parent, String method, long start, long end) {
        Span span = new Span(method) {
            @Override public long getStartTime() { return start; }
            @Override public long getEndTime() { return end; }
            @Override public long getCost() { return end - start; }
        };
        span.setSpanId(id);
        span.setParentSpanId(parent);
        span.setTraceId("trace");
        return span;
    }
}
