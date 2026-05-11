package com.demo.monitor.core.trace.queue;

import com.demo.monitor.core.model.Span;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Trace 异步队列
 *
 * <p>
 * 作用：
 * <p>
 * 将：
 * <p>
 * tracing runtime（业务线程）
 * <p>
 * 与：
 * <p>
 * export pipeline（后台线程）
 * <p>
 * 解耦。
 * <p>
 * 业务线程：
 * <p>
 * finishSpan -> offer()
 * <p>
 * 后台线程：
 * <p>
 * take() -> export()
 * <p>
 * 这样可以避免：
 * <p>
 * export IO 阻塞业务线程。
 */
public class AsyncMetricQueue {

    /**
     * Trace 导出队列
     * <p>
     * Queue中的元素：
     * <p>
     * root span（代表完整trace）
     */
    private static final BlockingQueue<Span> QUEUE =
            new LinkedBlockingQueue<>(10000);

    /**
     * 业务线程写入
     */
    public static void offer(Span span) {
        QUEUE.offer(span);
    }

    /**
     * Worker线程消费
     * <p>
     * take()：
     * <p>
     * Queue为空时会阻塞等待。
     */
    public static Span take() throws InterruptedException {
        return QUEUE.take();
    }

    /**
     * 批量拉取 trace
     * <p>
     * drainTo 的优势：
     * <p>
     * 一次性获取多个任务，
     * 减少锁竞争与线程切换。
     */
    public static int drainTo(Collection<? super Span> list, int maxElements) {
        return QUEUE.drainTo(list, maxElements);
    }

    /**
     * ⭐新增：带超时等待
     *
     * @return null 表示超时
     */
    public static Span poll(long timeout, TimeUnit unit) throws InterruptedException {
        return QUEUE.poll(timeout, unit);
    }
}