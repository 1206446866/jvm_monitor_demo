package com.demo.monitor.core;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AgentMain {

    public static void premain(String agentArgs, Instrumentation inst) {

//        MonitorTransformer.install(inst);

        ExecutorService exporter = Executors.newSingleThreadExecutor();

//        exporter.submit(() -> {
//
//            while (true) {
//                try {
//                    Span span = AsyncMetricQueue.take();
//
//                    TraceExporter.exportToJson(span.getTraceId());
//
//                } catch (InterruptedException e) {
//                    break;
//                }
//            }
//        });
    }

}