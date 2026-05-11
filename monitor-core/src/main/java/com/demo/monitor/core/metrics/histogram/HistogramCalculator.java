package com.demo.monitor.core.metrics.histogram;

import java.util.Collections;
import java.util.List;

public class HistogramCalculator {

    public static long p95(List<Long> data) {
        return percentile(data, 95);
    }

    public static long p99(List<Long> data) {
        return percentile(data, 99);
    }

    private static long percentile(List<Long> data, int p) {

        if (data == null || data.isEmpty()) {
            return 0;
        }

        List<Long> copy = new java.util.ArrayList<>(data);
        Collections.sort(copy);

        int index = (int) Math.ceil(p / 100.0 * copy.size()) - 1;

        return copy.get(Math.max(index, 0));
    }
}