package com.demo.agent;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class MethodTimerInterceptor {

    // 慢方法阈值（毫秒）
    private static final long THRESHOLD_MS = 50;

    public static Object intercept(@SuperCall Callable<?> callable, @Origin Method method) throws Exception {

        TraceContext.enter();

        int depth = TraceContext.getDepth();
        String threadName = Thread.currentThread().getName();

        try {
            return callable.call();
        } finally {

            long costNs = TraceContext.exit();
            long costMs = costNs / 1_000_000;

            // 👉 只打印慢方法
            if (costMs >= THRESHOLD_MS) {

                printIndent(depth);

                System.out.println(
                        "[" + threadName + "] "
                                + method.getDeclaringClass().getSimpleName()
                                + "." + method.getName()
                                + " cost " + costMs + " ms"
                );
            }
        }
    }

    private static void printIndent(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("    ");
        }
    }
}