package com.demo.agent;

import com.demo.agent.transformer.MethodTimeTransformer;

import java.lang.instrument.Instrumentation;

public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("ASM Monitor Agent 启动...");
        inst.addTransformer(new MethodTimeTransformer(), true);
    }
}