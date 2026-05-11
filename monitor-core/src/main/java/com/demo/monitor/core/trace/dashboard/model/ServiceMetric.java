package com.demo.monitor.core.trace.dashboard.model;

public class ServiceMetric {

    private String service;

    private long qps;

    private double avgLatency;

    private double errorRate;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

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
}