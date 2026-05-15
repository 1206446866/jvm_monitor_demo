package com.demo.agent.storage;

import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MetricStore {

    public static final MetricStore INSTANCE = new MetricStore();
    private final Map<String, Long> methodStartTimes = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<Long>> methodDurations = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<JvmSnapshot>> methodJvmSnapshots = new ConcurrentHashMap<>();

    public void setCurrentMethodStartTime(String methodName, long startTime) {
        methodStartTimes.put(methodName, startTime);
    }

    public Long getCurrentMethodStartTime(String methodName) {
        return methodStartTimes.get(methodName);
    }

    public void addMethodExecution(String methodName, long duration) {
        methodDurations.computeIfAbsent(methodName, k -> new CopyOnWriteArrayList<>()).add(duration);
    }

    public void storeJvmSnapshot(String methodName, long timestamp, MemoryUsage heap, MemoryUsage nonHeap, ThreadInfo[] threads) {
        methodJvmSnapshots.computeIfAbsent(methodName, k -> new CopyOnWriteArrayList<>()).add(new JvmSnapshot(timestamp, heap, nonHeap, threads));
    }

    public Map<String, Long> getMethodStartTimes() {
        return methodStartTimes;
    }

    public Map<String, CopyOnWriteArrayList<Long>> getMethodDurations() {
        return methodDurations;
    }

    public Map<String, CopyOnWriteArrayList<JvmSnapshot>> getMethodJvmSnapshots() {
        return methodJvmSnapshots;
    }

    // JVM 快照对象
    public record JvmSnapshot(long timestamp, MemoryUsage heap, MemoryUsage nonHeap, ThreadInfo[] threads) {
    }
}