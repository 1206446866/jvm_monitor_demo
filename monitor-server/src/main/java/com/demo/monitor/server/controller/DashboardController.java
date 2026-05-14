package com.demo.monitor.server.controller;

import com.demo.monitor.core.metrics.MethodMetrics;
import com.demo.monitor.core.test.SpanMock;
import com.demo.monitor.core.trace.dashboard.model.MethodMetric;
import com.demo.monitor.server.dto.DashboardOverview;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/overview")
    public DashboardOverview overview() {
        MethodMetrics m = new MethodMetrics();
        m.setMethod("DEBUG");
        m.record(new SpanMock(100));
        m.record(new SpanMock(200));

        List<MethodMetric> list = List.of(
                new MethodMetric("DEBUG", 2, 150, 200)
        );

        DashboardOverview vo = new DashboardOverview();
        vo.setTopSlowMethods(list);
//        return DashboardAggregator.aggregate();
        return vo;
    }
}