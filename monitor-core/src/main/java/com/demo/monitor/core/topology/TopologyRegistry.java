package com.demo.monitor.core.topology;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNames;
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

        String from = SpanNames.service(parent);
        String to = SpanNames.service(child);

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
        return RELATIONS.values().stream()
                .sorted(java.util.Comparator.comparing(ServiceRelation::getFrom)
                        .thenComparing(ServiceRelation::getTo)).toList();
    }

}
