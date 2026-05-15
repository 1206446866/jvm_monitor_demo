package com.demo.agent.report;

import com.demo.agent.storage.MetricStore;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TopSlowMethodReporter {

    /**
     * 返回 Top N 慢方法
     * @param limit 前 N 个
     * @return List<Map.Entry<方法名, 平均耗时(ms)>>
     */
    public static List<Map.Entry<String, Double>> top(int limit) {
        Map<String, CopyOnWriteArrayList<Long>> durations = MetricStore.INSTANCE.getMethodDurations();

        if (durations.isEmpty()) {
            return Collections.emptyList();
        }

        // 构造 平均耗时 Map
        Map<String, Double> avgMap = new HashMap<>();
        durations.forEach((method, list) -> {
            long total = list.stream().mapToLong(Long::longValue).sum();
            long count = list.size();
            double avgMs = total / 1_000_000.0 / count;
            avgMap.put(method, avgMs);
        });

        // 按平均耗时降序排序，取前 limit 个
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(avgMap.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        if (limit > 0 && sorted.size() > limit) {
            return sorted.subList(0, limit);
        }
        return sorted;
    }
}