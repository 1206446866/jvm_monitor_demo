package com.demo.monitor.core.metrics.histogram;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.LongSupplier;

/** 与方法指标一致，保留最近 60 秒的延迟样本，单位纳秒。 */
public class HistogramWindow {
    private static final long WINDOW_MILLIS = 60_000;
    private record Sample(long timestamp, long cost) {}
    private final Deque<Sample> samples = new ArrayDeque<>();
    private final LongSupplier clock;

    public HistogramWindow() { this(System::currentTimeMillis); }
    public HistogramWindow(LongSupplier clock) { this.clock = clock; }

    public synchronized void record(long cost) {
        long now = clock.getAsLong();
        evict(now);
        samples.addLast(new Sample(now, cost));
    }

    public synchronized List<Long> snapshot() {
        evict(clock.getAsLong());
        return samples.stream().map(Sample::cost).toList();
    }

    private void evict(long now) {
        // 与 SlidingWindow 的按秒桶边界保持一致。
        long oldestSecond = now / 1000 - WINDOW_MILLIS / 1000;
        while (!samples.isEmpty() && samples.peekFirst().timestamp() / 1000 <= oldestSecond) {
            samples.removeFirst();
        }
    }
}
