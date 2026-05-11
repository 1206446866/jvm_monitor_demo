package com.demo.monitor.server.controller;

import com.demo.monitor.server.aggregator.DashboardAggregator;
import com.demo.monitor.server.dto.DashboardOverview;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @GetMapping("/api/dashboard/overview")
    public DashboardOverview overview() {
        return DashboardAggregator.aggregate();
    }
}