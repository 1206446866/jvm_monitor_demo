package com.demo.monitor.core.trace.worker;

import com.demo.monitor.core.trace.manager.TraceLifecycleManager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Trace 超时清理线程
 */
public class TraceTimeoutCleaner implements Runnable {

    private static final long TIMEOUT_MS = 30000;

    private static final ConcurrentHashMap<String, Long> TRACE_TIME =
            new ConcurrentHashMap<>();

    public static void record(String traceId) {
        TRACE_TIME.put(traceId, System.currentTimeMillis());
    }

    @Override
    public void run() {

        while (true) {

            try {

                long now = System.currentTimeMillis();

                Set<String> keys = TRACE_TIME.keySet();

                for (String traceId : keys) {

                    Long start = TRACE_TIME.get(traceId);

                    if (start == null) continue;

                    if (now - start > TIMEOUT_MS) {

                        TraceLifecycleManager.expireTrace(traceId);

                        TRACE_TIME.remove(traceId);
                    }
                }

                Thread.sleep(5000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}