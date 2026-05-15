package com.demo.agent.advice;

import com.demo.agent.storage.MetricStore;
import net.bytebuddy.asm.Advice;

public class TimingAdvice {

    @Advice.OnMethodEnter
    static long enter(@Advice.Origin("#t.#m") String method) {
        // 返回开始时间
        return System.nanoTime();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(@Advice.Origin("#t.#m") String method,
                     @Advice.Enter long startTime) {
        long cost = System.nanoTime() - startTime;
        MetricStore.INSTANCE.addMethodExecution(method, cost);
    }
}