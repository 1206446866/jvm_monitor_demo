<template>
  <div style="padding: 20px">

    <h1>JVM Monitor Dashboard</h1>

    <MetricsChart title="QPS" :data="metrics.qps" />
    <MetricsChart title="Latency" :data="metrics.latency" />
    <MetricsChart title="Error Rate" :data="metrics.errorRate" />

  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import socket from "../websocket/metricsSocket";
import MetricsChart from "../components/MetricsChart.vue";

const metrics = reactive({
  qps: [],
  latency: [],
  errorRate: []
});

onMounted(() => {
  socket.onmessage = (event) => {
    const data = JSON.parse(event.data);

    const now = Date.now();

    metrics.qps.push({ time: now, value: data.qps });
    metrics.latency.push({ time: now, value: data.avgLatency });
    metrics.errorRate.push({ time: now, value: data.errorRate });

    if (metrics.qps.length > 60) metrics.qps.shift();
    if (metrics.latency.length > 60) metrics.latency.shift();
    if (metrics.errorRate.length > 60) metrics.errorRate.shift();
  };
});
</script>