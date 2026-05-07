package com.demo.agent.trace;

/**
 * Span:
 * 一次方法调用记录
 *
 * 未来：
 * 一个 trace 会包含多个 span
 *
 * 例如：
 *
 * Controller.login()
 *   └── Service.query()
 *         └── Dao.select()
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

    public Span(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;

        /**
         * 创建 Span 时记录开始时间
         */
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 方法结束
     */
    public void finish() {
        this.endTime = System.currentTimeMillis();
    }

    /**
     * 计算耗时
     */
    public long cost() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return "[traceId=" + TraceContext.getTraceId() + "] "
                + className
                + "."
                + methodName
                + " cost="
                + cost()
                + "ms";
    }
}