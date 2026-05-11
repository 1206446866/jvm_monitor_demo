package com.demo.monitor.core.metrics;

import com.demo.monitor.core.model.Span;

public class MethodMetrics {

    /**
     * 方法名
     */
    private String method;

    /**
     * 调用次数
     */
    private long count;

    /**
     * 总耗时
     */
    private long totalCost;

    /**
     * 最大耗时
     */
    private long maxCost;

    /**
     * 错误次数
     */
    private long errorCount;

    public synchronized void record(Span span) {

        count++;

        totalCost += span.getCost();

        maxCost = Math.max(maxCost, span.getCost());

        if (span.isError()) {
            errorCount++;
        }
    }

    public double avgCost() {
        return count == 0 ? 0 : (double) totalCost / count;
    }

    public double errorRate() {
        return count == 0 ? 0 : (double) errorCount / count;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    public long getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(long maxCost) {
        this.maxCost = maxCost;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }
}
