package com.demo.agent.report;

import com.demo.agent.storage.MetricStore;
import java.lang.management.ThreadInfo;
import java.util.Arrays;

public class ConsoleReporter {

    private final MetricStore metricStore;

    public ConsoleReporter(MetricStore metricStore) {
        this.metricStore = metricStore;
    }

    public void report() {
        System.out.println("=== JVM 堆/栈监控 ===");
        System.out.printf("Heap: used=%dMB, max=%dMB%n",
                metricStore.getHeapUsed()/1024/1024,
                metricStore.getHeapMax()/1024/1024);
        System.out.printf("Non-Heap: used=%dMB, max=%dMB%n",
                metricStore.getNonHeapUsed()/1024/1024,
                metricStore.getNonHeapMax()/1024/1024);

        ThreadInfo[] infos = metricStore.getThreadInfos();
        if (infos != null) {
            System.out.println("[线程状态]");
            Arrays.stream(infos).forEach(info -> {
                if (info == null) return;
                System.out.printf("线程: %s, 状态: %s%n", info.getThreadName(), info.getThreadState());
            });
        }
        System.out.println("---------------------------");
    }
}