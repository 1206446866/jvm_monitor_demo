package com.demo.monitor.core.metrics.histogram;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法级延迟分布窗口
 */
public class HistogramWindow {

    private final List<Long> latencies = new ArrayList<>();

    public synchronized void record(long cost) {
        latencies.add(cost);
    }

    public List<Long> snapshot() {
        return new ArrayList<>(latencies);
    }
}