package com.demo.monitor.core.analysis;

import com.demo.monitor.core.trace.dashboard.MetricsSnapshot;
import com.demo.monitor.core.trace.exporter.MetricsExporter;

import java.util.List;

public class TopNMetricsAnalyzer {

    public static List<MetricsSnapshot> topSlowMethods(int n) {

        return MetricsExporter.export()
                .stream()
                .sorted((a, b) ->
                        Double.compare(
                                b.getAvgCost(),
                                a.getAvgCost()
                        )
                )
                .limit(n)
                .toList();
    }
}