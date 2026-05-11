package com.demo.monitor.core.metrics.window;

import com.demo.monitor.core.model.Span;

public class WindowBucket {

    /**
     * bucket时间
     */
    private long timestamp;

    /**
     * 请求数
     */
    private long count;

    /**
     * 总耗时
     */
    private long totalCost;

    /**
     * 错误数
     */
    private long errorCount;



    public synchronized void record(Span span) {

        count++;

        totalCost += span.getCost();

        if (span.isError()) {
            errorCount++;
        }
    }

    public void reset(long timestamp) {
        this.timestamp = timestamp;
        this.count = 0;
        this.totalCost = 0;
        this.errorCount = 0;
    }

    public double avgCost() {
        return count == 0 ? 0 : (double) totalCost / count;
    }

    public double errorRate() {
        return count == 0 ? 0 : (double) errorCount / count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCount() {
        return count;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }
}
