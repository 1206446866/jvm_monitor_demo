package com.demo.monitor.core.model;

import java.util.Comparator;
import java.util.List;

/** 扣除子调用区间的并集，避免并行子调用重复扣减耗时。 */
public final class SpanTimings {
    private SpanTimings() {}

    public static long selfCost(long start, long cost, List<long[]> children) {
        long end = start + cost;
        children.sort(Comparator.comparingLong(range -> range[0]));
        long covered = 0;
        long cursor = start;
        for (long[] range : children) {
            long from = Math.max(start, range[0]);
            long to = Math.min(end, range[1]);
            if (to > from) {
                covered += Math.max(0, to - Math.max(cursor, from));
                cursor = Math.max(cursor, to);
            }
        }
        return Math.max(0, cost - covered);
    }
}
