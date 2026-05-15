package com.demo.monitor.core.metrics;

import com.demo.monitor.core.model.Span;

import java.util.concurrent.atomic.LongAdder;

public class MethodMetrics {

    /**
     * 方法名
     */
    private String method;

    /**
     * 调用次数
     */
    private final LongAdder count =
            new LongAdder();

    /**
     * 总耗时
     */
    private final LongAdder totalCost =
            new LongAdder();

    /**
     * 最大耗时
     */
    private volatile long maxCost;

    /**
     * 记录 span
     */
    public void record(Span span) {

        long cost = span.getCost();

        count.increment();

        totalCost.add(cost);

        if (cost > maxCost) {
            maxCost = cost;
        }
    }

    /**
     * 平均耗时
     */
    public double avgCost() {

        long c = count.sum();

        if (c == 0) {
            return 0;
        }

        return totalCost.sum() * 1.0 / c;
    }

    public long maxCost() {
        return maxCost;
    }

    public long count() {
        return count.sum();
    }

    // getter setter

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String method() {
        return method;
    }

    public long getCount() {
        return count.sum();
    }

    public LongAdder getTotalCost() {
        return totalCost;
    }

    public long getMaxCost() {
        return maxCost;
    }
}