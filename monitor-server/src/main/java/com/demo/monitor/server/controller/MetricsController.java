package com.demo.monitor.server.controller;

import com.demo.monitor.core.metrics.MethodSlidingWindowManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

    @GetMapping("/api/metrics/slow-methods")
    public Object slowMethods() {
        return MethodSlidingWindowManager.topSlowMethods(10);
    }
}