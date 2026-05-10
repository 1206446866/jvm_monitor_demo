package com.demo.agent;

import com.demo.agent.trace.exporter.TraceExporter;
import com.demo.agent.trace.model.Span;
import com.demo.agent.trace.queue.AsyncMetricQueue;
import com.demo.agent.transformer.MonitorTransformer;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {

//        MonitorTransformer.install(inst);

        ExecutorService exporter = Executors.newSingleThreadExecutor();

        exporter.submit(() -> {

            while (true) {
                try {
                    Span span = AsyncMetricQueue.take();

                    TraceExporter.exportToJson(span.getTraceId());

                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }

}