package com.demo.agent.probe;

import com.demo.agent.storage.MetricStore;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryProbe {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final MetricStore metricStore;

    public MemoryProbe(MetricStore metricStore) {
        this.metricStore = metricStore;
    }

    public void sample() {
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();

//        metricStore.setHeapUsed(heap.getUsed());
//        metricStore.setHeapMax(heap.getMax());
//        metricStore.setNonHeapUsed(nonHeap.getUsed());
//        metricStore.setNonHeapMax(nonHeap.getMax());
    }
}