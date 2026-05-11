package com.demo.monitor.core.topology;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.topology.model.ServiceRelation;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务拓扑注册中心
 */
public class TopologyRegistry {

    /**
     * from->to
     */
    private static final ConcurrentHashMap<String, ServiceRelation> RELATIONS =
            new ConcurrentHashMap<>();

    public static void record(Span parent, Span child) {

        if (parent == null || child == null) {
            return;
        }

        String from = extractService(parent);
        String to = extractService(child);

        // 自调用可忽略
        if (from.equals(to)) {
            return;
        }

        String key = from + "->" + to;

        ServiceRelation relation =
                RELATIONS.computeIfAbsent(
                        key,
                        k -> new ServiceRelation(from, to)
                );

        relation.increment();
    }

    public static Collection<ServiceRelation> relations() {
        return RELATIONS.values();
    }

    /**
     * 提取服务名
     */
    private static String extractService(Span span) {

        String method = span.getMethodName();

        int idx = method.indexOf(".");

        if (idx == -1) {
            return "unknown";
        }

        return method.substring(0, idx);
    }
}