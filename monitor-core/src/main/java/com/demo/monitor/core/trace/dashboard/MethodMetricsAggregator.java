package com.demo.monitor.core.trace.dashboard;

import com.demo.monitor.core.metrics.MethodMetrics;
import com.demo.monitor.core.metrics.MetricsRegistry;
import com.demo.monitor.core.trace.dashboard.model.MethodMetric;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MethodMetricsAggregator {

    public List<MethodMetric> topSlowMethods(int topN) {

        return MetricsRegistry
                .methodMetrics()
                .values()
                .stream()

                .sorted(Comparator.comparingDouble(
                        MethodMetrics::avgCost
                ).reversed())

                .limit(topN)

                .map(metrics -> new MethodMetric(
                        metrics.method(),
                        metrics.count(),
                        metrics.avgCost(),
                        metrics.maxCost()
                ))

                .collect(Collectors.toList());
    }
}