package com.demo.agent.trace;

import java.util.UUID;

public class TraceContext {

    /**
     * ThreadLocal:
     * 每个线程独立保存自己的 traceId
     * <p>
     * 一次请求通常对应一个线程，
     * 所以可以实现“调用链上下文共享”
     */
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    /**
     * 获取当前线程 traceId
     * <p>
     * 如果不存在：
     * 1. 生成新的 UUID
     * 2. 放入 ThreadLocal
     * 3. 后续同线程调用都会复用
     */
    public static String getTraceId() {

        String traceId = TRACE_ID.get();

        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            TRACE_ID.set(traceId);
        }

        return traceId;
    }

    /**
     * 请求结束后清理 ThreadLocal
     * <p>
     * 非常重要：
     * 避免线程池导致 ThreadLocal 泄漏
     */
    public static void clear() {
        TRACE_ID.remove();
    }
}