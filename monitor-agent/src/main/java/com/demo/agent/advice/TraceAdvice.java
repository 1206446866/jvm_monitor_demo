package com.demo.agent.advice;

import com.demo.agent.storage.MetricStore;
import com.demo.monitor.core.metrics.MethodSlidingWindowManager;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.manager.TraceManager;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class TraceAdvice {

    /**
     * 方法进入
     */
    @Advice.OnMethodEnter
    static Span enter(@Advice.Origin Method method) {
        String methodName = method.getDeclaringClass().getName() + "#" + method.getName();

        // 1️开始 Span
        Span span = TraceManager.startSpan(methodName);

        // 2️可选：打印进入
        System.out.println("Enter method: " + methodName);

        return span; // 通过 @Advice.Enter 传递给 exit
    }

    /**
     * 方法退出
     */
    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(@Advice.Enter Span span,
                     @Advice.Origin Method method,
                     @Advice.Thrown Throwable throwable) {

        // 1️捕获异常
        if (throwable != null) {
            span.setError(true);
            span.setErrorMsg(throwable.getMessage());
        }

        // 2️结束 Span
        TraceManager.finishSpan(throwable);

        // 3️写入 MetricStore
        long cost = span.getCost();
        String fullMethodName = method.getDeclaringClass().getName() + "#" + method.getName();
        MetricStore.INSTANCE.addMethodExecution(fullMethodName, cost);

        // 4️写入 SlidingWindowManager
        MethodSlidingWindowManager.record(span);

        // 5️控制台打印耗时
        System.out.println("Exit method: " + fullMethodName
                + " | cost: " + cost / 1_000_000.0 + " ms"
                + " | error: " + span.isError());
    }
}