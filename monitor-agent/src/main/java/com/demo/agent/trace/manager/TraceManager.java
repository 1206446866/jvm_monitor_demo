package com.demo.agent.trace.manager;

import com.demo.agent.trace.context.TraceContext;
import com.demo.agent.trace.exporter.TraceExporter;
import com.demo.agent.trace.model.Span;
import com.demo.agent.trace.model.SpanBuffer;
import com.demo.agent.trace.queue.AsyncMetricQueue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;


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

        Span span = TraceContext.pop();

        if (span == null) return;

        span.setEndTime(System.nanoTime());

        if (error != null) {
            span.setError(true);
            span.setErrorMsg(error.getMessage());
        }

        // ⭐只做收集
        SpanBuffer.add(span);

        // ⭐root span 才触发 export
        if (TraceContext.isRootFinished()) {

            String traceId = span.getTraceId();

            String json = TraceExporter.exportTree(traceId);

            System.out.println(json);

            AsyncMetricQueue.offer(traceId); // 或 trigger event

            TraceContext.clear();
        }
    }


    /**
     * 获取当前 TraceId
     */
    public static String currentTraceId() {
        return CURRENT_TRACE_ID.get();
    }

    /**
     * 生成 SpanId
     */
    private static String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public static void push(Span span) {
        STACK.get().push(span);
    }

    public static Span pop() {
        return STACK.get().pop();
    }

    public static Span current() {
        return STACK.get().peek();
    }


}