package com.demo.agent.trace.exporter;

import com.demo.agent.trace.model.Span;

public interface SpanExporter {
    /**
     * 导出 root span
     */
    void export(Span rootSpan);
}
