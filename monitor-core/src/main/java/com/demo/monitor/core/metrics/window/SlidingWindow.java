package com.demo.monitor.core.metrics.window;

import com.demo.monitor.core.model.Span;

public class SlidingWindow {

    /**
     * 60秒窗口
     */
    private static final int WINDOW_SIZE = 60;

    private final WindowBucket[] buckets =
            new WindowBucket[WINDOW_SIZE];

    public SlidingWindow() {
        for (int i = 0; i < WINDOW_SIZE; i++) {
            buckets[i] = new WindowBucket();
        }
    }

    public void record(Span span) {

        long now = System.currentTimeMillis() / 1000;

        int index = (int) (now % WINDOW_SIZE);

        WindowBucket bucket = buckets[index];

        synchronized (bucket) {

            if (bucket.getTimestamp() != now) {
                bucket.reset(now);
            }

            bucket.record(span);
        }
    }

    public long qps() {
        return totalRequests() / WINDOW_SIZE;
    }

    public long totalRequests() {

        long sum = 0;
        long now = System.currentTimeMillis() / 1000;

        for (WindowBucket b : buckets) {

            if (b.getTimestamp() == now ||
                    now - b.getTimestamp() < WINDOW_SIZE) {

                sum += b.getCount();
            }
        }

        return sum;
    }

    public double avgLatency() {

        long total = 0;
        long count = 0;
        long now = System.currentTimeMillis() / 1000;

        for (WindowBucket b : buckets) {

            if (b.getTimestamp() == now ||
                    now - b.getTimestamp() < WINDOW_SIZE) {

                total += b.getTotalCost();
                count += b.getCount();
            }
        }

        return count == 0 ? 0 : (double) total / count;
    }

    public double errorRate() {

        long error = 0;
        long total = 0;
        long now = System.currentTimeMillis() / 1000;

        for (WindowBucket b : buckets) {

            if (b.getTimestamp() == now ||
                    now - b.getTimestamp() < WINDOW_SIZE) {

                error += b.getErrorCount();
                total += b.getCount();
            }
        }

        return total == 0 ? 0 : (double) error / total;
    }

}
