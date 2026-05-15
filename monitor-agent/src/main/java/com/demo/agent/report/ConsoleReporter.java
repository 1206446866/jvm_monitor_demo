package com.demo.agent.report;

import com.demo.agent.storage.MetricStore;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ConsoleReporter {

    /**
     * 打印 Top N 慢方法 JSON
     * @param topN 返回前 topN 个方法
     */
    public static void printTopSlowMethods(int topN) {
        Map<String, CopyOnWriteArrayList<Long>> durations = MetricStore.INSTANCE.getMethodDurations();

        if (durations.isEmpty()) {
            System.out.println("MetricStore 中没有数据");
            return;
        }

        // 构造方法指标
        List<Map<String, Object>> metrics = durations.entrySet().stream()
                .map(entry -> {
                    String method = entry.getKey();
                    List<Long> list = entry.getValue();
                    long total = list.stream().mapToLong(Long::longValue).sum();
                    long count = list.size();
                    double avgMs = total / 1_000_000.0 / count;

                    Map<String, Object> map = new HashMap<>();
                    map.put("method", method);
                    map.put("qps", count);           // 简单用调用次数代替 QPS
                    map.put("avgLatency", avgMs);    // 平均耗时 ms
                    map.put("errorRate", 0);         // 这里暂不统计错误，可改造
                    map.put("p95", calcPercentile(list, 95));
                    map.put("p99", calcPercentile(list, 99));
                    return map;
                })
                .sorted(Comparator.comparingDouble(m -> -((Double) m.get("avgLatency")))) // 按 avgLatency 降序
                .limit(topN)
                .collect(Collectors.toList());

        // 输出 JSON 样式
        System.out.println(metrics);
    }

    /**
     * 计算 pX 百分位耗时
     */
    private static double calcPercentile(List<Long> list, double percentile) {
        if (list.isEmpty()) return 0;
        List<Long> sorted = new ArrayList<>(list);
        Collections.sort(sorted);
        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));
        return sorted.get(index) / 1_000_000.0; // 转换为 ms
    }
}