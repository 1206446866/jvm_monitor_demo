package com.demo.monitor.core.trace.manager;

import com.demo.monitor.core.trace.context.TraceContext;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.analysis.TraceSpanStorage;
import com.demo.monitor.core.trace.queue.AsyncMetricQueue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

@Deprecated
public class TraceManager {

    /**
     * 当前线程 Span 上下文
     */
    private static final ThreadLocal<Deque<Span>> STACK = ThreadLocal.withInitial(ArrayDeque::new);
    /**
     * 当前 traceId（可选，但推荐保留）
     */
    private static final ThreadLocal<String> CURRENT_TRACE_ID = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> IN_TRACE = new ThreadLocal<>();

    public static Span startSpan(String method) {

        Span parent = TraceContext.current();

        Span span = new Span(method);

        span.setSpanId(generateSpanId());

        if (parent == null) {
            span.setTraceId(UUID.randomUUID().toString());
        } else {
            span.setTraceId(parent.getTraceId());
            span.setParentSpanId(parent.getSpanId());
        }

        TraceContext.push(span);

        return span;
    }

    /**
     * 结束当前 Span
     */
    public static void finishSpan(Throwable error) {
        //Context 是“当前正在执行什么”
        Span span = TraceContext.pop();

        if (span == null) return;

        span.setEndTime(System.nanoTime());

        if (error != null) {
            span.setError(true);
            span.setErrorMsg(error.getMessage());
        }

        // ⭐只做收集 Buffer 是 已经发生了什么
        TraceSpanStorage.add(span);

        // ⭐root span 才触发 export
        if (TraceContext.isRoot(span)) {

            AsyncMetricQueue.offer(span); // 异步执行

            TraceContext.clear();
        }
    }

    /**
     * 生成 SpanId
     */
    private static String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "");
    }


}