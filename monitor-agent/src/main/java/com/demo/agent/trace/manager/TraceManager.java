package com.demo.agent.trace.manager;

import com.demo.agent.trace.model.Span;
import com.demo.agent.trace.exporter.TraceExporter;
import com.demo.agent.trace.util.TraceUtil;

import java.util.UUID;


public class TraceManager {

    /**
     * 当前线程 Span 上下文
     */
    private static final ThreadLocal<Span> CURRENT_SPAN = new ThreadLocal<>();

    /**
     * 当前 traceId（可选，但推荐保留）
     */
    private static final ThreadLocal<String> CURRENT_TRACE_ID = new ThreadLocal<>();

    /**
     * 创建 Trace（入口 Span）
     */
    public static Span startTrace(String className, String methodName) {

        String traceId = UUID.randomUUID().toString();

        Span root = new Span(className, methodName);
        root.setTraceId(traceId);
        root.setSpanId(generateSpanId());
        root.setParentSpanId(null);

        CURRENT_TRACE_ID.set(traceId);
        CURRENT_SPAN.set(root);

        return root;
    }

    /**
     * 创建子 Span（自动挂父子关系）
     */
    public static Span startSpan(String className, String methodName) {

        Span parent = CURRENT_SPAN.get();
        if (parent == null) {
            // 没有 trace，就自动创建
            return startTrace(className, methodName);
        }

        Span child = new Span(className, methodName);

        child.setTraceId(parent.getTraceId());
        child.setSpanId(generateSpanId());
        child.setParentSpanId(parent.getSpanId());

        parent.addChild(child);

        CURRENT_SPAN.set(child);

        return child;
    }

    /**
     * 结束当前 Span
     */
    public static void finishSpan() {

        Span span = CURRENT_SPAN.get();
        if (span == null) return;

        span.finish();

        Span parent = span.getParent();

        if (parent != null) {
            CURRENT_SPAN.set(parent);
        } else {
            // root span finished → trace结束
            CURRENT_SPAN.remove();
            CURRENT_TRACE_ID.remove();

            // 👉 自动导出（关键点）
            System.out.println(TraceExporter.exportToJson(span));;
        }
    }

    /**
     * 获取当前 Span
     */
    public static Span currentSpan() {
        return CURRENT_SPAN.get();
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

    /**
     * 手动埋点：添加 tag
     */
    public static void addTag(String key, String value) {
        Span span = CURRENT_SPAN.get();
        if (span != null) {
            span.addTag(key, value);
        }
    }

    /**
     * 手动标记 error
     */
    public static void error(Throwable e) {
        Span span = CURRENT_SPAN.get();
        if (span != null) {
            span.setError(true);
            span.setErrorMsg(e.getMessage());
            span.addTag("exception", e.getClass().getName());
            span.addTag("stack", TraceUtil.getStackTrace(e));
        }
    }

}