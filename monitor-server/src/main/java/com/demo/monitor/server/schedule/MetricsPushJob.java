package com.demo.monitor.server.schedule;

import com.demo.monitor.core.metrics.GlobalSlidingWindow;
import com.demo.monitor.server.export.MetricsExporter;

import java.util.HashMap;
import java.util.Map;

/** 兼容手动导出；定时广播统一由 MetricsPushService 执行。 */
public class MetricsPushJob {

    private final MetricsExporter exporter;

    public MetricsPushJob(
                          MetricsExporter exporter) {
        this.exporter = exporter;
    }

    public void push() {

        Map<String, Object> data = new HashMap<>();
        data.put("qps", GlobalSlidingWindow.qps());
        data.put("avgLatency", GlobalSlidingWindow.avgLatency());
        data.put("errorRate", GlobalSlidingWindow.errorRate());

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
