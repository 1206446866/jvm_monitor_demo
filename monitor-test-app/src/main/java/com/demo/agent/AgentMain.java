package com.demo.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.implementation.MethodDelegation.to;

public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {

        System.out.println("Agent Loaded");
        System.out.println("🔥 Agent Version: 2026-05-05 v2");
        new AgentBuilder.Default()
//                .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
                .with(new AgentBuilder.Listener() {
                    @Override
                    public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {

                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {

                    }

                    @Override
                    public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {

                    }

                    @Override
                    public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {

                    }

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
                        if (typeDescription.getName().startsWith("com.demo")) {
                            System.out.println("Transform: " + typeDescription.getName());
                        }
                    }


                })
                .ignore(
                        ElementMatchers.nameStartsWith("net.bytebuddy.")
                                .or(ElementMatchers.nameStartsWith("java."))
                                .or(ElementMatchers.nameStartsWith("javax."))
                                .or(ElementMatchers.nameStartsWith("sun."))
                                .or(ElementMatchers.nameStartsWith("jdk."))
                )
                .type(
                        ElementMatchers.nameStartsWith("com.demo.test")
                                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                                .and(ElementMatchers.not(ElementMatchers.isEnum()))
                                .and(ElementMatchers.not(ElementMatchers.nameContains("agent")))
                )
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                            System.out.println("Transform: " + typeDescription.getName());
                            return builder.method(
                                    ElementMatchers.nameStartsWith("query").and( // 只缓存查询方法
                                            ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class)))
                            )
//                                .intercept(to(MethodTimerInterceptor.class))
                                    .intercept(to(CacheInterceptor.class))
                                    ;
                        }
                )

                .installOn(inst);
    }
}