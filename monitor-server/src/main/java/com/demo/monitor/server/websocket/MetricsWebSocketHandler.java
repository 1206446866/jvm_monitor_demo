package com.demo.monitor.server.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MetricsWebSocketHandler extends TextWebSocketHandler {

    /**
     * 所有在线 websocket session
     */
    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        sessions.add(session);

        System.out.println("WebSocket connected: " + session.getId());

        // 🔥 延迟 200ms 再发（关键修复）
        new Thread(() -> {
            try {
                Thread.sleep(200);

                session.sendMessage(
                        new TextMessage("""
                    {
                      "message": "hello websocket"
                    }
                    """)
                );

                System.out.println("message sent");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("🔥 CLIENT MESSAGE RECEIVED: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) {

        sessions.remove(session);

        System.out.println("WebSocket closed: "
                + session.getId());
    }

    /**
     * 广播消息
     */
    public void broadcast(String message) {

        sessions.forEach(session -> {

            try {

                if (session.isOpen()) {

                    session.sendMessage(
                            new TextMessage(message)
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}