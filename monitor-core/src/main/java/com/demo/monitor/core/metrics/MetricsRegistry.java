package com.demo.monitor.core.metrics;

import com.demo.monitor.core.model.Span;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MetricsRegistry {


    /**
     * method -> metrics
     */
    private static final ConcurrentHashMap<String, MethodMetrics> METRICS = new ConcurrentHashMap<>();

    /**
     * 记录 Span
     */
    public static void record(Span span) {

        String method = span.getClassName() + "." + span.getMethodName();

        METRICS.computeIfAbsent(method, k -> {
            MethodMetrics m = new MethodMetrics();

            m.setMethod(method);

            return m;
        }).record(span);
    }

    /**
     * 所有指标
     */
    public static Collection<MethodMetrics> all() {
        return METRICS.values();
    }

    /**
     * 返回原始 map
     */
    public static Map<String, MethodMetrics> methodMetrics() {

        return METRICS;
    }

    /**
     * 获取单方法指标
     */
    public static MethodMetrics get(String method) {

        return METRICS.get(method);
    }

    /**
     * Top N 慢方法
     */
    public static List<MethodMetrics> topSlowMethods(int topN) {

        return METRICS.values().stream()

                .sorted(Comparator.comparingDouble(MethodMetrics::avgCost).reversed())

                .limit(topN)

                .collect(Collectors.toList());
    }

    /**
     * Top N 最大耗时
     */
    public static List<MethodMetrics> topMaxCostMethods(int topN) {

        return METRICS.values().stream()

                .sorted(Comparator.comparingLong(MethodMetrics::maxCost).reversed())

                .limit(topN)

                .collect(Collectors.toList());
    }

    /**
     * 清空指标
     */
    public static void clear() {

        METRICS.clear();
    }

    /**
     * 方法数量
     */
    public static int size() {

        return METRICS.size();
    }


}