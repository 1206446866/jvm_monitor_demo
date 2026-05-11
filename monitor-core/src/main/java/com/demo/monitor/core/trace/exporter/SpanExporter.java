package com.demo.monitor.core.trace.exporter;

import com.demo.monitor.core.model.Span;

public interface SpanExporter {
    /**
     * 导出 root span
     */
    void export(Span rootSpan);
}
