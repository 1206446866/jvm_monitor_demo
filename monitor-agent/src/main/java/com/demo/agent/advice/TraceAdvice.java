package com.demo.agent.advice;

import com.demo.agent.storage.MetricStore;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.manager.TraceManager;
import net.bytebuddy.asm.Advice;

public class TraceAdvice {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    static Span enter(@Advice.Origin("#t.#m") String method) {
        return TraceManager.startSpan(method);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    static void exit(@Advice.Enter Span span, @Advice.Thrown Throwable throwable) {
        if (span == null) {
            return;
        }
        TraceManager.finishSpan(throwable);
        MetricStore.INSTANCE.addMethodExecution(span.getMethodName(), span.getCost());
    }
}
