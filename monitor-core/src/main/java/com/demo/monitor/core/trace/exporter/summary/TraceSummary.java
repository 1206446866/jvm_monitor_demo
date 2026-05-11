package com.demo.monitor.core.trace.exporter.summary;
public class TraceSummary {

    private String traceId;

    private long totalCost;

    private int spanCount;

    private int slowSpanCount;

    private String slowestSpan;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public int getSlowSpanCount() {
        return slowSpanCount;
    }

    public void setSlowSpanCount(int slowSpanCount) {
        this.slowSpanCount = slowSpanCount;
    }

    public String getSlowestSpan() {
        return slowestSpan;
    }

    public void setSlowestSpan(String slowestSpan) {
        this.slowestSpan = slowestSpan;
    }
}