package com.demo.monitor.server.service;

import com.demo.monitor.server.collector.MetricsCollector;
import com.demo.monitor.server.dto.MetricsDTO;
import com.demo.monitor.server.websocket.MetricsWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MetricsPushService {

    private final MetricsCollector collector;
    private final MetricsWebSocketHandler handler;
    private final ObjectMapper mapper = new ObjectMapper();

    public MetricsPushService(MetricsCollector collector, MetricsWebSocketHandler handler) {
        this.collector = collector;
        this.handler = handler;
    }

    @Scheduled(fixedRate = 1000)
    public void pushMetrics() {

        try {
            MetricsDTO dto = collector.assemble();

            String json = mapper.writeValueAsString(dto);

            handler.broadcast(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}