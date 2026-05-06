package com.demo.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.implementation.MethodDelegation.to;

public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {

        System.out.println("Agent Loaded");

        new AgentBuilder.Default()
//                .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
                .type(ElementMatchers.nameStartsWith("com.demo.test"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {

                    System.out.println("Transforming: " + typeDescription.getName());

                    return builder
                            .method(ElementMatchers.named("login"))
                            .intercept(to(MethodTimerInterceptor.class));
                })

                .installOn(inst);
    }
}