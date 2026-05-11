package com.demo.monitor.core.trace.worker;

import com.demo.monitor.core.metrics.MetricsRegistry;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.queue.AsyncMetricQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.demo.monitor.core.trace.exporter.TraceExporter.exportBatch;

/**
 * Trace 异步批量消费线程
 *
 * <p>
 * 为什么需要 Batch？
 * <p>
 * tracing/export 属于慢操作：
 * <p>
 * 1. JSON 序列化
 * 2. 文件 IO
 * 3. HTTP 上报
 * 4. Kafka 发送
 * <p>
 * 如果每个 trace 都立即 export：
 * <p>
 * trace -> export -> IO
 * <p>
 * 会导致：
 * <p>
 * - IO 频繁
 * - flush 频繁
 * - CPU 上下文切换增加
 * - 吞吐量下降
 * <p>
 * 因此工业级 tracing 系统都会采用：
 * <p>
 * Batch Export（批量导出）
 * <p>
 * 即：
 * <p>
 * Queue -> Batch -> Export
 * <p>
 * OpenTelemetry / SkyWalking / Kafka Producer
 * 都是这种模式。
 */
public class BatchTraceWorker implements Runnable {

    /**
     * 单次最大批量大小
     * <p>
     * 例如：
     * 一次最多处理100个 trace
     */
    private static final int MAX_BATCH_SIZE = 100;

    @Override
    public void run() {

        /**
         * 为什么 while(true)？
         *
         * Worker 是后台常驻线程。
         *
         * 它不是执行一次任务后退出，
         * 而是持续消费 Queue。
         *
         * 本质上这是：
         *
         * Consumer Event Loop
         */
        while (true) {

            try {

                /**
                 * 当前批次
                 */
                List<Span> batch = new ArrayList<>();

                /**
                 * take()：
                 *
                 * 阻塞等待至少一个 trace。
                 *
                 * Queue为空时：
                 * worker线程休眠，不占CPU。
                 */
                Span first = AsyncMetricQueue.poll(500, TimeUnit.MILLISECONDS);
                if (first != null) {
                    batch.add(first);
                    AsyncMetricQueue.drainTo(batch, MAX_BATCH_SIZE - 1);
                }

                for (Span span : batch) {
                    MetricsRegistry.record(span);
                }

                /**
                 * 批量 export
                 */
                String json = exportBatch(batch);
                System.out.println(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}