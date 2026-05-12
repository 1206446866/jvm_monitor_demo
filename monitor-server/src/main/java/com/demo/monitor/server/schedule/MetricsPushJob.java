package com.demo.monitor.server.schedule;

import com.demo.monitor.core.metrics.window.SlidingWindow;
import com.demo.monitor.server.export.MetricsExporter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MetricsPushJob {

    private final SlidingWindow slidingWindow = new SlidingWindow();
    private final MetricsExporter exporter;

    public MetricsPushJob(
                          MetricsExporter exporter) {
        this.exporter = exporter;
    }

    @Scheduled(fixedRate = 1000)
    public void push() {

        Map<String, Object> data = new HashMap<>();
        data.put("qps", slidingWindow.qps());
        data.put("avgLatency", slidingWindow.avgLatency());
        data.put("errorRate", slidingWindow.errorRate());

        String json = toJson(data);

        exporter.export(json);
    }

    private String toJson(Map<String, Object> map) {

        StringBuilder sb = new StringBuilder("{");

        map.forEach((k, v) ->
                sb.append("\"").append(k).append("\":")
                        .append(v).append(",")
        );

        if (sb.length() > 1) sb.deleteCharAt(sb.length() - 1);

        sb.append("}");

        return sb.toString();
    }
}