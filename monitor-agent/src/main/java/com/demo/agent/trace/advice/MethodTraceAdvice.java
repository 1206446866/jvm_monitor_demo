package com.demo.agent.trace.advice;

import com.demo.agent.trace.manager.TraceManager;
import com.demo.agent.trace.model.Span;
import net.bytebuddy.asm.Advice;

public class MethodTraceAdvice {

    @Advice.OnMethodEnter
    public static Span enter(@Advice.Origin("#t.#m") String methodName) {

        return TraceManager.startSpan(methodName);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable throwable) {

        TraceManager.finishSpan(throwable);
    }
}