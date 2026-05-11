package com.demo.monitor.core.trace.context;

import com.demo.monitor.core.model.Span;

import java.util.ArrayDeque;
import java.util.Deque;

public class TraceContext {

    private static final ThreadLocal<Deque<Span>> STACK =
            ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 入栈
     */
    public static void push(Span span) {
        STACK.get().push(span);
    }

    /**
     * 出栈
     */
    public static Span pop() {
        return STACK.get().pop();
    }

    /**
     * 当前 span
     */
    public static Span current() {
        return STACK.get().peek();
    }

    /**
     * 清理（非常重要！线程池场景必须）
     */
    public static void clear() {
        STACK.remove();
    }


    public static boolean isRoot(Span span) {
        return span != null && span.getParentSpanId() == null;
    }

    public static Deque<Span> snapshot() {
        return new ArrayDeque<>(STACK.get());
    }

    public static void restore(Deque<Span> snapshot) {
        STACK.set(new ArrayDeque<>(snapshot));
    }
}