package com.demo.agent.transformer;

import com.demo.agent.trace.interceptor.TracingInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

/**
 * JVM 字节码增强入口
 */
public class MonitorTransformer {

    public static void install(Instrumentation inst) {

        new AgentBuilder.Default()
                /**
                 * 匹配需要增强的类
                 */
                .type(
                        ElementMatchers.nameStartsWith(
                                "com.demo.app"
                        )
                )

                /**
                 * 转换类
                 */
                .transform((builder,
                            typeDescription,
                            classLoader,
                            module,
                            protectionDomain) ->
                        builder.method(
                                ElementMatchers.isAnnotatedWith(
                                        ElementMatchers.isPublic()
                                )
                        )

                                .intercept(
                                        MethodDelegation.to(
                                                TracingInterceptor.class
                                        )
                                )
                )

                .installOn(inst);
    }
}