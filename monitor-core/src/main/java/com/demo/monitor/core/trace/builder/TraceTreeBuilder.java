package com.demo.monitor.core.trace.builder;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNode;
import java.util.List;

/** 兼容旧入口，统一使用可处理乱序和断链的实现。 */
public class TraceTreeBuilder {
    public static SpanNode build(List<Span> spans) {
        return com.demo.monitor.core.trace.exporter.tree.TraceTreeBuilder.build(spans);
    }
}
