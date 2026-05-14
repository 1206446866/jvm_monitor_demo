package com.demo.agent.probe;

import com.demo.agent.storage.MetricStore;

import java.lang.management.*;

public class MethodExecutionProbe {

    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final MetricStore metricStore;

    public MethodExecutionProbe(MetricStore metricStore) {
        this.metricStore = metricStore;
    }

    public void beforeMethod(String methodName) {
        long startTime = System.currentTimeMillis();
        metricStore.setCurrentMethodStartTime(methodName, startTime);
        sampleJvmStatus(methodName, startTime);
    }

    public void afterMethod(String methodName) {
        long endTime = System.currentTimeMillis();
        Long startTime = metricStore.getCurrentMethodStartTime(methodName);
        if (startTime != null) {
            long duration = endTime - startTime;
            metricStore.addMethodExecution(methodName, duration);
        }
    }

    private void sampleJvmStatus(String methodName, long timestamp) {
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
        long[] threadIds = threadMXBean.getAllThreadIds();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadIds, 5);

        metricStore.storeJvmSnapshot(methodName, timestamp, heap, nonHeap, threadInfos);
    }
}