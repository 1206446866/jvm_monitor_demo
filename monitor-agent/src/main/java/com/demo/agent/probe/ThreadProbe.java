package com.demo.agent.probe;

import com.demo.agent.storage.MetricStore;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadProbe {

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final MetricStore metricStore;

    public ThreadProbe(MetricStore metricStore) {
        this.metricStore = metricStore;
    }

    public void sample() {
        long[] threadIds = threadMXBean.getAllThreadIds();
        ThreadInfo[] infos = threadMXBean.getThreadInfo(threadIds, 5); // 栈顶 5 层

        metricStore.setThreadInfos(infos);
    }
}