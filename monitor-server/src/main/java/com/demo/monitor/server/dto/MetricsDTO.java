package com.demo.monitor.server.dto;

public class MetricsDTO {

    public double cpu;
    public long heapUsed;
    public long heapMax;
    public int threadCount;
    public long gcCount;

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public long getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(long heapMax) {
        this.heapMax = heapMax;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public long getGcCount() {
        return gcCount;
    }

    public void setGcCount(long gcCount) {
        this.gcCount = gcCount;
    }
}