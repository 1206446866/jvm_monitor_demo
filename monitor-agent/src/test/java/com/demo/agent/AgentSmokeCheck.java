package com.demo.agent;

import com.demo.fixture.ProbeFixture;
import com.demo.monitor.core.analysis.TraceSpanStorage;
import com.demo.monitor.core.metrics.GlobalSlidingWindow;
import com.demo.monitor.core.metrics.MethodSlidingWindowManager;
import com.demo.monitor.core.metrics.MetricsRegistry;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.context.TraceContext;

import java.util.List;

/** 使用打包后的 -javaagent 启动，验证依赖、字节码增强和异步聚合。 */
public class AgentSmokeCheck {
    public static void main(String[] args) throws Exception {
        check(Span.class.getClassLoader() == ClassLoader.getSystemClassLoader(),
                "core must be visible to the agent system class loader");
        ProbeFixture fixture = new ProbeFixture();
        check(fixture.outer() == 42, "return value must be preserved");
        check(TraceContext.current() == null, "nested call must clear context");
        List<Span> trace = TraceSpanStorage.getByTraceId(fixture.traceId);
        check(trace.size() == 2, "nested trace must contain both spans");
        Span root = trace.stream().filter(s -> s.getParentSpanId() == null).findFirst().orElseThrow();
        Span child = trace.stream().filter(s -> s.getParentSpanId() != null).findFirst().orElseThrow();
        check(child.getParentSpanId().equals(root.getSpanId()), "parent span must match");
        check(root.getCost() >= child.getCost() && child.getCost() > 0, "duration must use one clock");

        RuntimeException failure = new IllegalStateException("business failure");
        try {
            fixture.fail(failure);
            throw new AssertionError("business exception was swallowed");
        } catch (RuntimeException actual) {
            check(actual == failure, "business exception must be unchanged");
        }
        check(TraceContext.current() == null, "exception path must clear context");

        long deadline = System.nanoTime() + 5_000_000_000L;
        while (GlobalSlidingWindow.totalRequests() < 2 && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        for (String method : List.of("outer", "inner", "fail")) {
            String key = "com.demo.fixture.ProbeFixture." + method;
            check(MetricsRegistry.get(key) != null && MetricsRegistry.get(key).count() == 1,
                    "each span must be aggregated exactly once: " + key);
        }
        check(MetricsRegistry.size() == 3, "constructors and Object methods must be excluded");
        check(MethodSlidingWindowManager.errorRate("com.demo.fixture.ProbeFixture.fail") == 1,
                "failed span must reach method metrics");
        check(GlobalSlidingWindow.totalRequests() == 2, "global metrics must count root spans only");
        check(GlobalSlidingWindow.errorRate() == 0.5, "global error rate must be real");
        System.out.println("AGENT_SMOKE_OK: dependency visibility, nested calls, return values, exceptions, exact counts");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
