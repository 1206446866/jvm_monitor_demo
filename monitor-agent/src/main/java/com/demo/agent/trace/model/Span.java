package com.demo.agent.trace.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Span:
 * 一次方法调用记录
 * <p>
 * 未来：
 * 一个 trace 会包含多个 span
 * <p>
 * 例如：
 * <p>
 * Controller.login()
 * └── Service.query()
 * └── Dao.select()
 */
public class Span {

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法开始时间
     */
    private long startTime;

    /**
     * 方法结束时间
     */
    private long endTime;

    /**
     * 父 Span
     */
    private transient Span parent;

    /**
     * 子 Span
     */
    private final List<Span> children = new CopyOnWriteArrayList<>();

    // 属于哪个请求
    private String traceId;

    // 当前 span 唯一ID
    private String spanId;

    private boolean error;
    private String errorMsg;
    private String parentSpanId;
    private volatile boolean finished = false;
    private final Map<String, String> tags = new HashMap<>();

    public Span(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;

        /**
         * 创建 Span 时记录开始时间
         */
        this.startTime = System.currentTimeMillis();
    }

//    public Span() {
//        this.startTime = System.currentTimeMillis();
//    }

    /**
     * 计算耗时
     */
    public long getCost() {
        return endTime - startTime;
    }

    public void addChild(Span child) {

        if (child == null) {
            return;
        }

        child.setParent(this);

        child.setParentSpanId(this.spanId);

        children.add(child);
    }

    public List<Span> getChildren() {
        return children;
    }

    public void setParent(Span parent) {
        this.parent = parent;
    }

    public Span getParent() {
        return parent;
    }


    @Override
    public String toString() {
        return "[traceId=" + traceId + "] " + className
                + "." + methodName + " cost=" + getCost() + "ms";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public synchronized void finish() {
        if (finished) return;
        finished = true;
        this.endTime = System.currentTimeMillis();
    }

    public void addTag(String key, String value) {
        tags.put(key, value);
    }

    public Map<String, String> getTags() {
        return tags;
    }
}