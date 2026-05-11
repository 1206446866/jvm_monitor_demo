package com.demo.monitor.core.metrics.window;

import java.util.ArrayList;
import java.util.List;

public class HistogramWindow {

    private final List<Long> latencies =
            new ArrayList<>();

    public synchronized void record(long cost) {
        latencies.add(cost);
    }
}