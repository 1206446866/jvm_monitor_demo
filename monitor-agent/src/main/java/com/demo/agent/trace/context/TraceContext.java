package com.demo.agent.trace.context;

import com.demo.agent.trace.model.Span;

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

    /**
     * 是否 trace 结束
     */
    public static boolean isRootFinished() {
        return STACK.get().isEmpty();
    }

    public static Deque<Span> snapshot() {
        return new ArrayDeque<>(STACK.get());
    }

    public static void restore(Deque<Span> snapshot) {
        STACK.set(new ArrayDeque<>(snapshot));
    }
}