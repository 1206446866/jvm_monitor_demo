package com.demo.agent.trace.queue;

import com.demo.agent.trace.model.Span;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncMetricQueue {

    /**
     * 生产队列（trace 数据出口）
     */
    private static final BlockingQueue<Span> QUEUE =
            new LinkedBlockingQueue<>(10000);

    /**
     * 写入队列（业务线程）
     */
    public static void offer(Span span) {
        QUEUE.offer(span);
    }

    /**
     * 获取 span（消费线程）
     */
    public static Span take() throws InterruptedException {
        return QUEUE.take();
    }
}