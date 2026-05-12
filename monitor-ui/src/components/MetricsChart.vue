<template>
  <div ref="chartRef" style="width: 100%; height: 300px;"></div>
</template>

<script setup>
import * as echarts from "echarts";
import { onMounted, watch, ref } from "vue";

const props = defineProps({
  data: Array,
  title: String
});

const chartRef = ref();

let chart;

onMounted(() => {
  chart = echarts.init(chartRef.value);
  render();
});

watch(() => props.data, () => {
  render();
}, { deep: true });

function render() {
  if (!chart) return;

  chart.setOption({
    title: { text: props.title },
    xAxis: {
      type: "category",
      data: props.data.map(i => new Date(i.time).toLocaleTimeString())
    },
    yAxis: { type: "value" },
    series: [{
      type: "line",
      data: props.data.map(i => i.value)
    }]
  });
}
</script>