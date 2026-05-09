package com.demo.agent;

import com.demo.agent.transformer.MonitorTransformer;

import java.lang.instrument.Instrumentation;


public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {

        MonitorTransformer.install(inst);
    }

}