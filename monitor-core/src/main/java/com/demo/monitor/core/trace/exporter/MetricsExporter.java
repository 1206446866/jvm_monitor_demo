package com.demo.monitor.core.trace.exporter;

import com.demo.monitor.core.metrics.MethodMetrics;
import com.demo.monitor.core.metrics.MetricsRegistry;
import com.demo.monitor.core.trace.dashboard.MetricsSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MetricsExporter {

    public static List<MetricsSnapshot> export() {

        List<MetricsSnapshot> result = new ArrayList<>();

        for (MethodMetrics metrics : MetricsRegistry.all()) {

            MetricsSnapshot snapshot = new MetricsSnapshot();
//TODO
            snapshot.setMethod(metrics.getMethod());
            snapshot.setCount(metrics.getCount());
            snapshot.setAvgCost(metrics.avgCost());
            snapshot.setMaxCost(metrics.getMaxCost());
//            snapshot.setErrorRate(metrics.errorRate());

            result.add(snapshot);
        }

        return result;
    }
}