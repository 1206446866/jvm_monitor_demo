package com.demo.monitor.server.schedule;

import com.demo.monitor.server.aggregator.DashboardAggregator;
import com.demo.monitor.server.dto.DashboardOverview;
import com.demo.monitor.server.websocket.MetricsWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
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
    @Scheduled(fixedRate = 1000)
    public void push() {

        try {

            DashboardOverview overview =
                    DashboardAggregator.aggregate();

            String json =
                    mapper.writeValueAsString(overview);

            handler.broadcast(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}