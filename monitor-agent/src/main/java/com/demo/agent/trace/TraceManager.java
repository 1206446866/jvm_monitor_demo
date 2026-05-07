package com.demo.agent.trace;

import com.demo.agent.trace.Span;

import java.util.Stack;

/**
 * TraceManager:
 * 管理当前线程调用链
 *
 * 本质：
 * 每个线程维护一个 Span Stack
 */
public class TraceManager {

    /**
     * ThreadLocal:
     * 每个线程独立保存自己的调用栈
     */
    private static final ThreadLocal<Stack<Span>> SPAN_STACK =
            ThreadLocal.withInitial(Stack::new);

    /**
     * 方法进入
     *
     * 创建 Span
     * 并压入调用栈
     */
    public static Span enter(
            String className,
            String methodName
    ) {

        Span span =
                new Span(className, methodName);

        SPAN_STACK.get().push(span);

        return span;
    }

    /**
     * 方法退出
     *
     * 1. Span finish
     * 2. 栈弹出
     * 3. 打印耗时
     */
    public static void exit() {

        Stack<Span> stack = SPAN_STACK.get();

        if (stack.isEmpty()) {
            return;
        }

        Span span = stack.pop();

        span.finish();

        printSpan(span);

        /**
         * 如果调用链结束：
         * 清理 ThreadLocal
         *
         * 非常重要：
         * 避免线程池 ThreadLocal 泄漏
         */
        if (stack.isEmpty()) {
            SPAN_STACK.remove();
            TraceContext.clear();
        }
    }

    /**
     * 打印 Span
     */
    private static void printSpan(Span span) {

        /**
         * 当前调用深度
         */
        int depth = SPAN_STACK.get().size();

        StringBuilder indent =
                new StringBuilder();

        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }

        System.out.println(
                indent + span.toString()
        );
    }
}