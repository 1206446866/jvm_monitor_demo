package com.demo.agent.trace.queue;

import com.demo.agent.trace.model.Span;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncMetricQueue {

    /**
     * 生产队列（trace 数据出口）
     */
    private static final BlockingQueue<String> QUEUE =
            new LinkedBlockingQueue<>(10000);

    /**
     * 写入队列（业务线程）
     */
    public static void offer(String traceId) {
        QUEUE.offer(traceId);
    }

    /**
     * 获取 span（消费线程）
     */
    public static String take() throws InterruptedException {
        return QUEUE.take();
    }
}