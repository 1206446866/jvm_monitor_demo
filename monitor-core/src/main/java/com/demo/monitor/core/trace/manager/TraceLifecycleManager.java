package com.demo.monitor.core.trace.manager;

import com.demo.monitor.core.trace.context.TraceContext;
import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.trace.queue.AsyncMetricQueue;
import com.demo.monitor.core.analysis.TraceSpanStorage;

/**
 * Trace 生命周期管理器
 *
 * <p>
 * 负责控制 trace 的完整生命周期：
 *
 * START → ACTIVE → END → EXPIRE
 *
 * <p>
 * 为什么需要它？
 *
 * 因为 tracing 本质是：
 *
 * “分布式调用链状态机”
 */
public class TraceLifecycleManager {

    /**
     * trace 正常结束
     */
    public static void finishTrace(Span root) {

        if (root == null) return;


        // 1. 进入异步队列
        AsyncMetricQueue.offer(root);

        // 2. 清理 storage
        TraceSpanStorage.clear(root.getTraceId());

        // 3. 清理 context
        TraceContext.clear();
    }

    /**
     * trace 异常结束（超时/异常）
     */
    public static void expireTrace(String traceId) {

        // 从 storage 找到残留数据
        var spans = TraceSpanStorage.getByTraceId(traceId);

        if (spans == null || spans.isEmpty()) {
            return;
        }

        // 标记最后一个 span
        Span last = spans.get(spans.size() - 1);
        last.setError(true);
        last.setErrorMsg("TRACE_EXPIRED");


        AsyncMetricQueue.offer(last);

        TraceSpanStorage.clear(traceId);
    }
}