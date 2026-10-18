package com.demo.monitor.core.trace.worker;

import com.demo.monitor.core.metrics.GlobalSlidingWindow;
import com.demo.monitor.core.analysis.TraceSpanStorage;
import com.demo.monitor.core.metrics.MetricsRegistry;
import com.demo.monitor.core.metrics.bridge.ObservabilityBridge;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.context.TraceContext;
import com.demo.monitor.core.trace.queue.AsyncMetricQueue;
import com.demo.monitor.core.topology.TopologyRegistry;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.demo.monitor.core.trace.exporter.TraceExporter.exportBatch;

/** 消费所有已完成的 span，统一维护指标和调用链导出。 */
public class BatchTraceWorker implements Runnable {
    private static final int MAX_BATCH_SIZE = 100;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Span first = AsyncMetricQueue.poll(500, TimeUnit.MILLISECONDS);
                if (first == null) {
                    continue;
                }
                List<Span> batch = new ArrayList<>();
                batch.add(first);
                AsyncMetricQueue.drainTo(batch, MAX_BATCH_SIZE - 1);
                List<Span> completed = new ArrayList<>();
                for (Span root : batch) {
                    List<Span> trace = TraceSpanStorage.getByTraceId(root.getTraceId());
                    // 兼容显式入队而没有写 storage 的调用者。
                    List<Span> spans = trace.isEmpty() ? List.of(root) : List.copyOf(trace);
                    Map<String, Span> byId = new HashMap<>();
                    for (Span span : spans) byId.put(span.getSpanId(), span);
                    for (Span span : spans) {
                        MetricsRegistry.record(span);
                        ObservabilityBridge.record(span);
                        if (span.getParentSpanId() != null) {
                            TopologyRegistry.record(byId.get(span.getParentSpanId()), span);
                        }
                        if (TraceContext.isRoot(span)) {
                            GlobalSlidingWindow.record(span);
                        }
                    }
                    completed.addAll(spans);
                }
                System.out.println(exportBatch(completed));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("Monitor trace aggregation failed");
                e.printStackTrace();
            }
        }
    }
}
