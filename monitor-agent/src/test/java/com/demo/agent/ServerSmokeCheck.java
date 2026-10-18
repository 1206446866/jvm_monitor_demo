package com.demo.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/** 对启动后的 Spring Boot 可执行 JAR 验证 HTTP 和 WebSocket 链路。 */
public class ServerSmokeCheck {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        String base = args.length == 0 ? "http://127.0.0.1:18080" : args[0];
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>();
        WebSocket socket = client.newWebSocketBuilder().buildAsync(
                URI.create(base.replace("http://", "ws://") + "/ws/metrics"),
                new WebSocket.Listener() {
                    private final StringBuilder buffer = new StringBuilder();

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        webSocket.request(1);
                    }

                    @Override
                    public java.util.concurrent.CompletionStage<?> onText(
                            WebSocket webSocket, CharSequence data, boolean last) {
                        buffer.append(data);
                        if (last) {
                            messages.add(buffer.toString());
                            buffer.setLength(0);
                        }
                        webSocket.request(1);
                        return null;
                    }
                }).get(5, TimeUnit.SECONDS);
        try {
            for (int i = 0; i < 2; i++) {
                get(client, base + "/api/test-agent");
            }
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            JsonNode methods;
            do {
                methods = MAPPER.readTree(get(client, base + "/api/metrics/slow-methods"));
                if (methods.size() == 4 && countsMatch(methods) && analysisReady(methods)) {
                    break;
                }
                Thread.sleep(20);
            } while (System.nanoTime() < deadline);
            check(methods.size() == 4 && countsMatch(methods), "HTTP method counts must be 2/2/4/4");
            check(analysisReady(methods), "percentiles and trace drill-down must be populated");
            JsonNode dashboard = MAPPER.readTree(get(client, base + "/api/dashboard/overview"));
            check(dashboard.path("topSlowMethods").size() == 4, "dashboard must contain real methods");
            check(dashboard.path("avgLatency").asDouble() > 0, "global latency must be populated");
            check(dashboard.path("timestamp").asLong() > 0, "snapshot must be timestamped");
            verifyAnalysis(client, base);
            boolean received = false;
            while (System.nanoTime() < deadline) {
                String message = messages.poll(1, TimeUnit.SECONDS);
                if (message == null) continue;
                JsonNode payload = MAPPER.readTree(message);
                check(payload.has("cpu") && payload.has("heapUsed") && payload.has("topSlowMethods"),
                        "every WebSocket payload must combine JVM and dashboard metrics");
                if (countsMatch(payload.path("topSlowMethods")) && analysisReady(payload.path("topSlowMethods"))) {
                    received = true;
                    break;
                }
            }
            check(received, "WebSocket must receive the same method metrics");
            System.out.println("SERVER_SMOKE_OK: counts 2/2/4/4, P95/P99, trace links, tree, flamegraph, hotspots, slow calls, topology, HTTP errors, WebSocket");
        } finally {
            socket.abort();
        }
    }

    private static boolean countsMatch(JsonNode methods) {
        Map<String, Long> counts = new HashMap<>();
        for (JsonNode method : methods) {
            String name = method.path("method").asText();
            counts.put(name.substring(name.lastIndexOf('.') + 1), method.path("qps").asLong());
        }
        return Long.valueOf(2).equals(counts.get("login"))
                && Long.valueOf(2).equals(counts.get("queryUser"))
                && Long.valueOf(4).equals(counts.get("queryDb"))
                && Long.valueOf(4).equals(counts.get("query"));
    }

    private static boolean analysisReady(JsonNode methods) {
        for (JsonNode method : methods) {
            if (method.path("p95").asLong() <= 0 || method.path("p99").asLong() < method.path("p95").asLong()
                    || method.path("traceIds").isEmpty()) return false;
        }
        return !methods.isEmpty();
    }

    private static void verifyAnalysis(HttpClient client, String base) throws Exception {
        String method = URLEncoder.encode("com.demo.monitor.server.service.UserService.queryUser", StandardCharsets.UTF_8);
        JsonNode metric = MAPPER.readTree(get(client, base + "/api/metrics/method?method=" + method));
        check(metric.path("slowTraceIds").size() == 2, "both queryUser calls must be indexed as slow");
        check(metric.path("traceIds").size() == 2, "method trace IDs must be deduplicated");
        JsonNode traces = MAPPER.readTree(get(client, base + "/api/metrics/traces?method=" + method + "&slowOnly=true"));
        check(traces.size() == 2, "slow trace lookup must return two summaries");
        check(traces.get(0).path("spanCount").asInt() == 3, "summary must contain all three nested spans");
        JsonNode allTraces = MAPPER.readTree(get(client, base + "/api/traces?limit=20"));
        check(allTraces.size() == 6, "only six root traces should exist after two test requests");
        String traceId = traces.get(0).path("traceId").asText();
        String traceUrl = base + "/api/trace/" + traceId;
        JsonNode spans = MAPPER.readTree(get(client, traceUrl));
        JsonNode tree = MAPPER.readTree(get(client, traceUrl + "/tree"));
        check(tree.path("method").asText().endsWith("UserService.queryUser"), "tree root must be queryUser");
        check(tree.path("children").get(0).path("children").get(0).path("method")
                .asText().endsWith("DatabaseService.query"), "tree must include the database service");
        JsonNode analysis = MAPPER.readTree(get(client, traceUrl + "/analysis"));
        check(analysis.path("timeUnit").asText().equals("ns"), "analysis units must be explicit");
        check(analysis.path("summary").path("totalCost").asLong() == tree.path("cost").asLong(),
                "summary duration must match the root span");
        check(analysis.path("hotspots").size() == 3, "hotspots must include all three methods");
        for (JsonNode hotspot : analysis.path("hotspots")) {
            check(hotspot.path("selfCost").asLong() >= 0
                    && hotspot.path("selfCost").asLong() <= hotspot.path("totalCost").asLong(),
                    "self cost must be a valid exclusive duration");
        }
        check(!analysis.path("slowSpans").isEmpty(), "slow span analysis must be populated");
        JsonNode flame = MAPPER.readTree(get(client, traceUrl + "/flamegraph"));
        check(flame.path("nodes").size() == 3, "flamegraph must contain three nodes");
        JsonNode grandchild = flame.path("nodes").get(2);
        JsonNode dbSpan = null;
        for (JsonNode span : spans) {
            if (span.path("methodName").asText().endsWith("DatabaseService.query")) dbSpan = span;
        }
        check(dbSpan != null && grandchild.path("x").asLong()
                        == dbSpan.path("startTime").asLong() - tree.path("startTime").asLong(),
                "third-level flame offset must be relative to the root");
        JsonNode topology = MAPPER.readTree(get(client, base + "/api/topology"));
        check(topology.size() == 1 && topology.get(0).path("count").asLong() == 4,
                "topology must count four cross-class calls and skip self calls");
        check(topology.get(0).path("from").asText().endsWith("UserService")
                        && topology.get(0).path("to").asText().endsWith("DatabaseService"),
                "topology nodes must be business classes");
        checkStatus(client, base + "/api/trace/missing/analysis", 404);
        checkStatus(client, base + "/api/trace/missing/tree", 404);
        checkStatus(client, base + "/api/trace/missing/flamegraph", 404);
        checkStatus(client, base + "/api/metrics/method?method=missing", 404);
        checkStatus(client, base + "/api/metrics/method?method=%20", 400);
        checkStatus(client, base + "/api/traces?limit=0", 400);
        checkStatus(client, base + "/api/metrics/slow-methods?limit=101", 400);
    }

    private static void checkStatus(HttpClient client, String url, int expected) throws Exception {
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(5)).GET().build(), HttpResponse.BodyHandlers.ofString());
        check(response.statusCode() == expected, "unexpected status for " + url + ": " + response.statusCode());
    }

    private static String get(HttpClient client, String url) throws Exception {
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(5)).GET().build(), HttpResponse.BodyHandlers.ofString());
        check(response.statusCode() == 200, "HTTP request failed: " + url + ": " + response.body());
        return response.body();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
