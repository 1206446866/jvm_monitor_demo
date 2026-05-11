package com.demo.monitor.core.metrics.bridge;

import com.demo.monitor.core.analysis.link.MetricTraceLink;
import com.demo.monitor.core.analysis.slow.SlowTraceRegistry;
import com.demo.monitor.core.metrics.MethodSlidingWindowManager;
import com.demo.monitor.core.metrics.histogram.HistogramRegistry;
import com.demo.monitor.core.metrics.service.ServiceMetricsRegistry;
import com.demo.monitor.core.model.Span;

public class ObservabilityBridge {

    /**
     * Span → Metrics 转换入口
     */
    public static void record(Span span) {

        String method = span.getMethodName();

        // ✔ 方法级统计
        MethodSlidingWindowManager.record(span);

        // ✔ Histogram
        HistogramRegistry.record(method, span.getCost());

        // ✔ service metrics（新增）
        ServiceMetricsRegistry.record(span);

        // ✔ ⭐慢请求索引（新增关键能力）
        SlowTraceRegistry.record(span);

        // ✔ ⭐新增：双向索引
        MetricTraceLink.record(span);

    }


}