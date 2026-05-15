package com.demo.agent;

import com.demo.agent.advice.TraceAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Byte Buddy Monitor Agent 启动...");

        new AgentBuilder.Default()
                .ignore(nameStartsWith("java.")
                        .or(nameStartsWith("sun."))
                        .or(nameStartsWith("com.sun.")))
                .type(nameContains("UserService"))  // 匹配原类和CGLIB代理// 只插 UserService
                .transform((builder, typeDescription, classLoader, module,f) ->
                        builder.method(any()) // 插入所有方法
                                .intercept(Advice.to(TraceAdvice.class))
                ).installOn(inst);
    }
}