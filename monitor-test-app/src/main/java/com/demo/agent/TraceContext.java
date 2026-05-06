package com.demo.agent;

import java.util.ArrayDeque;
import java.util.Deque;

public class TraceContext {

    // 每个线程一个调用栈（存方法开始时间）
    private static final ThreadLocal<Deque<Long>> TIME_STACK =
            ThreadLocal.withInitial(ArrayDeque::new);

    // 每个线程的调用深度（用于缩进）
    private static final ThreadLocal<Integer> DEPTH =
            ThreadLocal.withInitial(() -> 0);

    // 进入方法
    public static void enter() {
        TIME_STACK.get().push(System.nanoTime());
        DEPTH.set(DEPTH.get() + 1);
    }

    // 退出方法，返回耗时
    public static long exit() {
        long start = TIME_STACK.get().pop();
        DEPTH.set(DEPTH.get() - 1);
        return System.nanoTime() - start;
    }

    // 获取当前调用深度
    public static int getDepth() {
        return DEPTH.get();
    }
}