package com.demo.monitor.server.service;

import com.demo.monitor.server.collector.MetricsCollector;
import com.demo.monitor.server.aggregator.DashboardAggregator;
import com.demo.monitor.server.dto.MetricsDTO;
import com.demo.monitor.server.websocket.MetricsWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

            ObjectNode payload = mapper.valueToTree(dto);
            payload.setAll((ObjectNode) mapper.valueToTree(DashboardAggregator.aggregate()));
            String json = mapper.writeValueAsString(payload);

            handler.broadcast(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
