package com.demo.monitor.server.dto;

import com.demo.monitor.core.trace.dashboard.model.MethodMetric;
import com.demo.monitor.core.trace.dashboard.model.ServiceMetric;

import java.util.List;

public class DashboardOverview {

    /**
     * 每秒请求数
     */
    private long qps;

    /**
     * 平均耗时
     */
    private double avgLatency;

    /**
     * 错误率
     */
    private double errorRate;

    /**
     * 活跃trace数量
     */
    private long activeTraceCount;

    /**
     * TopN慢方法
     */
    private List<MethodMetric> topSlowMethods;

    private List<ServiceMetric> topServices;

    /**
     * 时间戳
     */
    private long timestamp;

    public long getQps() {
        return qps;
    }

    public void setQps(long qps) {
        this.qps = qps;
    }

    public double getAvgLatency() {
        return avgLatency;
    }

    public void setAvgLatency(double avgLatency) {
        this.avgLatency = avgLatency;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public long getActiveTraceCount() {
        return activeTraceCount;
    }

    public void setActiveTraceCount(long activeTraceCount) {
        this.activeTraceCount = activeTraceCount;
    }

    public List<MethodMetric> getTopSlowMethods() {
        return topSlowMethods;
    }

    public void setTopSlowMethods(List<MethodMetric> topSlowMethods) {
        this.topSlowMethods = topSlowMethods;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}