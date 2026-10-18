package com.demo.agent;

import com.demo.agent.advice.TraceAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {
        // 默认只观测示例业务；参数可指定其他业务包，避免采集监控系统自身。
        var target = agentArgs == null || agentArgs.isBlank()
                ? nameStartsWith("com.demo.monitor.server.service.")
                    .and(not(named("com.demo.monitor.server.service.MetricsPushService")))
                : nameStartsWith(agentArgs.trim());
        System.out.println("Monitor Agent started; target: " + target);

        new AgentBuilder.Default()
                .with(AgentBuilder.Listener.StreamWriting.toSystemError().withErrorsOnly())
                .ignore(nameStartsWith("com.demo.agent.")
                        .or(nameStartsWith("com.demo.monitor.core."))
                        .or(nameStartsWith("net.bytebuddy."))
                        .or(nameStartsWith("java."))
                        .or(nameStartsWith("sun."))
                        .or(nameStartsWith("com.sun.")))
                .type(target.and(not(nameContains("$$"))))
                .transform((builder, typeDescription, classLoader, module,f) ->
                        builder.visit(Advice.to(TraceAdvice.class).on(
                                isMethod().and(not(isAbstract())).and(not(isNative()))
                                        .and(not(isSynthetic()))))
                ).installOn(inst);
    }
}
