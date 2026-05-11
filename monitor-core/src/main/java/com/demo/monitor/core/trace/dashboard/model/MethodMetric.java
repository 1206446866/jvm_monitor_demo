package com.demo.monitor.core.trace.dashboard.model;

import java.util.List;

public class MethodMetric {

    private String method;
    private double avgLatency;
    private double errorRate;
    private long qps;
    private long p95;
    private long p99;
    private List<String> slowTraceIds;
    private List<String> traceIds;

    public MethodMetric(String method,
                        double avgLatency,
                        double errorRate,
                        long qps) {
        this.method = method;
        this.avgLatency = avgLatency;
        this.errorRate = errorRate;
        this.qps = qps;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    public long getQps() {
        return qps;
    }

    public void setQps(long qps) {
        this.qps = qps;
    }

    public long getP95() {
        return p95;
    }

    public void setP95(long p95) {
        this.p95 = p95;
    }

    public long getP99() {
        return p99;
    }

    public void setP99(long p99) {
        this.p99 = p99;
    }
}
