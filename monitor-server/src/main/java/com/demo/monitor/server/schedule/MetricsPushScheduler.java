package com.demo.monitor.server.schedule;

import com.demo.monitor.server.websocket.MetricsWebSocketHandler;
import com.demo.monitor.server.aggregator.DashboardAggregator;

import com.fasterxml.jackson.databind.ObjectMapper;


/** 兼容手动推送；定时广播统一由 MetricsPushService 执行。 */
public class MetricsPushScheduler {

    private final MetricsWebSocketHandler handler;

    private final ObjectMapper mapper =
            new ObjectMapper();

    public MetricsPushScheduler(
            MetricsWebSocketHandler handler) {

        this.handler = handler;
    }

    /**
     * 每秒推送 dashboard 数据
     */
    public void push() {
        try {
            handler.broadcast(mapper.writeValueAsString(DashboardAggregator.aggregate()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
