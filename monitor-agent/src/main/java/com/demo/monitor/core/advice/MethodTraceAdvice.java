package com.demo.monitor.core.advice;

import com.demo.monitor.core.trace.context.TraceContext;
import com.demo.monitor.core.trace.manager.TraceLifecycleManager;
import com.demo.monitor.core.metrics.bridge.ObservabilityBridge;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.analysis.TraceSpanStorage;
import com.demo.monitor.core.topology.TopologyRegistry;
import net.bytebuddy.asm.Advice;

public class MethodTraceAdvice {

    @Advice.OnMethodEnter
    public static Span enter(@Advice.Origin("#t") String className, @Advice.Origin("#t.#m") String methodName) {
        Span parent = TraceContext.current();

        Span span = new Span(methodName);

        if (parent != null) {

            span.setParentSpanId(parent.getSpanId());

            // ⭐ topology记录
            TopologyRegistry.record(parent, span);
        }
        return span;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable throwable) {
        span = TraceContext.pop();

        if (span == null) return;

        if (throwable != null) {
            span.setError(true);
            span.setErrorMsg(throwable.getMessage());
        }

        TraceSpanStorage.add(span);

        // ⭐ 新增：Metrics联动入口
        ObservabilityBridge.record(span);

        if (TraceContext.isRoot(span)) {
            TraceLifecycleManager.finishTrace(span);
        }
    }
}