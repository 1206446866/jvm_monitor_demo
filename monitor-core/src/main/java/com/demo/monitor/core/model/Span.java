package com.demo.monitor.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Span:
 * 一次方法调用的最小观测单元（immutable mindset）
 *
 * ⚠ 设计原则：
 * - 不维护调用树结构（children / parent object 不在这里）
 * - 不控制生命周期（finish 在 TraceManager）
 * - 只负责“记录事实”
 */
public class Span {

    /**
     * 属于哪个 trace
     */
    private String traceId;

    /**
     * 当前 span 唯一 ID
     */
    private String spanId;

    /**
     * 父 spanId（用于还原调用关系）
     */
    private String parentSpanId;

    /**
     * 方法全名（推荐：com.demo.Service.method）
     */
    private String methodName;

    /**
     * 开始时间（纳秒级更适合 tracing）
     */
    private long startTime;

    /**
     * 结束时间
     */
    private long endTime;

    /**
     * 是否异常
     */
    private boolean error;

    /**
     * 异常信息
     */
    private String errorMsg;

    private String className;
    /**
     * 扩展标签（业务 / SQL / HTTP / 自定义）
     */
    private Map<String, String> tags = new HashMap<>();

    // -----------------------------
    // 构造
    // -----------------------------

    public Span() {
        this.startTime = System.nanoTime();
    }

    public Span(String methodName) {
        this.methodName = methodName;
        this.startTime = System.nanoTime();
    }

    public Span(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
        this.startTime = System.currentTimeMillis();
    }

    // -----------------------------
    // 业务方法（轻量）
    // -----------------------------

    public void addTag(String key, String value) {
        tags.put(key, value);
    }

    public long getCost() {
        return endTime - startTime;
    }

    // -----------------------------
    // getter / setter
    // -----------------------------

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "Span{" +
                "traceId='" + traceId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", parentSpanId='" + parentSpanId + '\'' +
                ", method='" + methodName + '\'' +
                ", cost=" + getCost() +
                ", error=" + error +
                '}';
    }
}