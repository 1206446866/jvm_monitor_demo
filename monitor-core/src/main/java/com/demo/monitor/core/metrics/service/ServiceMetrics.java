    package com.demo.monitor.core.metrics.service;

    public class ServiceMetrics {

        private long requestCount;

        private long errorCount;

        private long totalCost;

        public synchronized void record(long cost, boolean error) {

            requestCount++;

            totalCost += cost;

            if (error) {
                errorCount++;
            }
        }

        public long qps() {
            return requestCount;
        }

        public double avgLatency() {

            return requestCount == 0
                    ? 0
                    : (double) totalCost / requestCount;
        }

        public double errorRate() {

            return requestCount == 0
                    ? 0
                    : (double) errorCount / requestCount;
        }
    }