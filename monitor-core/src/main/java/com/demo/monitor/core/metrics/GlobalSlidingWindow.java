package com.demo.monitor.core.metrics;

import com.demo.monitor.core.metrics.window.SlidingWindow;
import com.demo.monitor.core.model.Span;

public class GlobalSlidingWindow {

    /**
     * 全局唯一窗口
     */
    private static final SlidingWindow WINDOW =
            new SlidingWindow();

    /**
     * 记录一次请求
     */
    public static void record(Span span) {
        WINDOW.record(span);
    }

    /**
     * 实时QPS
     */
    public static long qps() {
        return WINDOW.qps();
    }

    /**
     * 实时平均耗时
     */
    public static double avgLatency() {
        return WINDOW.avgLatency();
    }

    /**
     * 实时错误率
     */
    public static double errorRate() {
        return WINDOW.errorRate();
    }

    /**
     * 当前窗口总请求数
     */
    public static long totalRequests() {
        return WINDOW.totalRequests();
    }
}
