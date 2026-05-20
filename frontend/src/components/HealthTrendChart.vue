<template>
  <el-card class="chart-card" shadow="hover" v-loading="loading">
    <template #header>
      <div class="card-header">
        <span><el-icon><TrendCharts /></el-icon> 血糖趋势与 TIR 达标率</span>
      </div>
    </template>
    <div ref="glucoseChartRef" class="chart glucose-chart"></div>
    <div class="chart-divider"></div>
    <div class="chart-label">血压 / BMI 复合趋势</div>
    <div ref="bpBmiChartRef" class="chart bp-chart"></div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, nextTick, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { TrendCharts } from '@element-plus/icons-vue'
import request from '../utils/request'

const props = defineProps<{ patientId: number }>()

const loading = ref(false)
const glucoseChartRef = ref<HTMLElement | null>(null)
const bpBmiChartRef = ref<HTMLElement | null>(null)
let glucoseChart: echarts.ECharts | null = null
let bpBmiChart: echarts.ECharts | null = null

const fetchData = async () => {
  loading.value = true
  try {
    const [histRes, profileRes] = await Promise.all([
      request.get(`/api/v1/patient/health-data/history/${props.patientId}`),
      request.get(`/api/v1/patient/profile/by-user/${props.patientId}`)
    ])
    if (histRes.data.code === 200 && histRes.data.data?.length > 0) {
      const profile = profileRes.data.code === 200 ? profileRes.data.data : null
      renderCharts(histRes.data.data, profile)
    }
  } catch (e) {
    console.error('图表数据加载失败', e)
  } finally {
    loading.value = false
  }
}

const renderCharts = (historyData: any[], profile: any) => {
  if (!glucoseChart || !bpBmiChart) return

  const sorted = [...historyData].sort((a, b) =>
    new Date(a.recordTime).getTime() - new Date(b.recordTime).getTime())

  const xData = sorted.map((item: any) => {
    const d = new Date(item.recordTime)
    return `${d.getMonth() + 1}-${d.getDate()}`
  })

  const glucose = sorted.map((i: any) => i.glucoseValue)
  const sys = sorted.map((i: any) => i.systolicBp)
  const dia = sorted.map((i: any) => i.diastolicBp)

  const h = profile?.height ? profile.height / 100 : null
  const bmi = sorted.map((i: any) => h && i.weight ? +(i.weight / (h * h)).toFixed(1) : null)
  const hasBmi = bmi.some((v: any) => v != null)

  // Chart 1 — Glucose
  const marks: any[] = []
  if (profile?.fpg) {
    marks.push({ yAxis: +profile.fpg, lineStyle: { color: '#19C853', type: 'dashed', width: 1.5 }, label: { formatter: `FPG ${profile.fpg}`, fontSize: 10 } })
  }
  glucoseChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['血糖'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '16%', top: '4%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: xData },
    yAxis: { type: 'value', name: 'mmol/L', min: 0, axisLine: { lineStyle: { color: '#F56C6C' } } },
    series: [{
      name: '血糖', type: 'line', data: glucose, smooth: true, symbol: 'none',
      itemStyle: { color: '#F56C6C' },
      markArea: { silent: true, itemStyle: { color: 'rgba(103,194,58,0.08)' }, data: [[{ yAxis: 3.9 }, { yAxis: 10.0 }]] },
      markLine: { symbol: 'none', data: marks }
    }]
  })

  // Chart 2 — BP + BMI
  const series: any[] = [
    { name: '收缩压', type: 'line', data: sys, smooth: true, symbol: 'none', itemStyle: { color: '#409EFF' } },
    { name: '舒张压', type: 'line', data: dia, smooth: true, symbol: 'none', itemStyle: { color: '#E6A23C' } }
  ]
  const yAxes: any[] = [{ type: 'value', name: 'mmHg', min: 0, axisLine: { lineStyle: { color: '#409EFF' } }, splitLine: { lineStyle: { type: 'dashed' } } }]
  if (hasBmi) {
    series.push({ name: 'BMI', type: 'line', yAxisIndex: 1, data: bmi, smooth: true, symbol: 'none', itemStyle: { color: '#9C27B0' }, markArea: { silent: true, itemStyle: { color: 'rgba(156,39,176,0.05)' }, data: [[{ yAxis: 18.5 }, { yAxis: 23.9 }]] } })
    yAxes.push({ type: 'value', name: 'kg/m²', axisLine: { lineStyle: { color: '#9C27B0' } }, splitLine: { show: false } })
  }
  bpBmiChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: series.map((s: any) => s.name), bottom: 0 },
    grid: { left: '3%', right: hasBmi ? '6%' : '4%', bottom: '16%', top: '4%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: xData },
    yAxis: yAxes,
    series
  })
}

onMounted(() => {
  nextTick(() => {
    if (glucoseChartRef.value) glucoseChart = echarts.init(glucoseChartRef.value)
    if (bpBmiChartRef.value) bpBmiChart = echarts.init(bpBmiChartRef.value)
    fetchData()
  })
})

watch(() => props.patientId, () => { fetchData() })

onUnmounted(() => {
  glucoseChart?.dispose()
  bpBmiChart?.dispose()
})
</script>

<style scoped>
.chart-card {
  border-radius: 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.chart-card :deep(.el-card__body) {
  flex: 1;
  padding: 8px 20px 10px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
}
.chart { width: 100%; flex-shrink: 0; }
.glucose-chart { flex: 1; min-height: 140px; }
.bp-chart { flex: 1; min-height: 100px; }
.chart-divider { height: 1px; background: var(--ag-border); margin: 4px 0; flex-shrink: 0; }
.chart-label { font-size: 12px; font-weight: 600; color: var(--ag-text-secondary); padding: 0 0 2px 4px; flex-shrink: 0; }
</style>
