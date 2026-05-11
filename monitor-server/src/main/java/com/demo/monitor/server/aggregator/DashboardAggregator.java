package com.demo.monitor.server.aggregator;

import com.demo.monitor.core.metrics.GlobalSlidingWindow;
import com.demo.monitor.core.metrics.MethodSlidingWindowManager;
import com.demo.monitor.server.dto.DashboardOverview;

public class DashboardAggregator {

    public static DashboardOverview aggregate() {

        DashboardOverview dto = new DashboardOverview();

        // QPS
        dto.setQps(GlobalSlidingWindow.qps());

        // latency
        dto.setAvgLatency(GlobalSlidingWindow.avgLatency());

        // error
        dto.setErrorRate(GlobalSlidingWindow.errorRate());

        // slow methods
        dto.setTopSlowMethods(
                MethodSlidingWindowManager.topSlowMethods(5)
        );

        return dto;
    }
}