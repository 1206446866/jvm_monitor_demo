package com.demo.monitor.server.collector;

import com.demo.monitor.server.dto.MetricsDTO;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Component;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

@Component
public class MetricsCollector {

    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    // ================= CPU =================
    public double collectCpu() {
        double cpu = osBean.getCpuLoad();
        if (cpu < 0) return 0;
        return cpu * 100;
    }

    // ================= HEAP =================
    public long collectHeapUsed() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    public long collectHeapMax() {
        return memoryMXBean.getHeapMemoryUsage().getMax();
    }

    // ================= THREAD =================
    public int collectThreadCount() {
        return threadMXBean.getThreadCount();
    }

    // ================= GC =================
    public long collectGcCount() {
        return ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(GarbageCollectorMXBean::getCollectionCount).sum();
    }

    // ================= ASSEMBLE =================
    public MetricsDTO assemble() {

        MetricsDTO dto = new MetricsDTO();

        dto.cpu = collectCpu();
        dto.heapUsed = collectHeapUsed();
        dto.heapMax = collectHeapMax();
        dto.threadCount = collectThreadCount();
        dto.gcCount = collectGcCount();

        return dto;
    }
}