package com.demo.monitor.server.export;

import com.demo.monitor.server.websocket.MetricsWebSocketHandler;
import org.springframework.stereotype.Component;

@Component
public class MetricsExporter {

    private final MetricsWebSocketHandler webSocketHandler;

    public MetricsExporter(MetricsWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * 对外统一出口
     */
    public void export(String json) {
        webSocketHandler.broadcast(json);
    }
}