<template>
  <el-card class="timeline-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span><el-icon><Calendar /></el-icon> 随访与健康事件时间轴</span>
        <el-button type="primary" size="small" @click="fetchEvents" :loading="loading">
          刷新轴线
        </el-button>
      </div>
    </template>

    <!-- 顶部 ECharts 数据看板概览 -->
    <div ref="chartRef" class="event-chart"></div>

    <el-divider border-style="dashed" />

    <!-- 垂直时间轴 -->
    <div class="timeline-container" v-loading="loading">
      <el-empty v-if="events.length === 0" description="暂无健康事件记录" />
      
      <el-timeline v-else>
        <el-timeline-item
          v-for="event in events"
          :key="event.id"
          :timestamp="formatDate(event.eventTime)"
          :type="getAlertType(event.alertLevel)"
          :color="getAlertColor(event.alertLevel)"
          :size="event.alertLevel === 2 ? 'large' : 'normal'"
          placement="top"
        >
          <el-card shadow="never" :class="['event-detail', `alert-level-${event.alertLevel}`]">
            <h4>{{ getEventTypeName(event.eventType) }}</h4>
            <p>{{ event.description }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '../utils/request'
import { Calendar } from '@element-plus/icons-vue'

const props = defineProps<{ patientId: number }>()
const events = ref<any[]>([])
const loading = ref(false)
const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const fetchEvents = async () => {
  loading.value = true
  try {
    const { data } = await request.get(`/api/v1/patient/health-events/${props.patientId}`)
    if (data.code === 200) {
      events.value = data.data
      updateChart()
    }
  } catch (e) {
    console.error('获取时间轴失败', e)
  } finally {
    loading.value = false
  }
}

const getEventTypeName = (type: number) => {
  const map: Record<number, string> = {
    1: '🏥 门诊/住院',
    2: '🚨 异常生理报警',
    3: '💊 用药方案变更',
    4: '📝 随访记录完成'
  }
  return map[type] || '其他事件'
}

const getAlertType = (level: number) => {
  if (level === 2) return 'danger'
  if (level === 1) return 'warning'
  return 'primary'
}

const getAlertColor = (level: number) => {
  if (level === 2) return '#F56C6C'
  if (level === 1) return '#E6A23C'
  return '#409EFF'
}

const formatDate = (isoString: string) => {
  if (!isoString) return ''
  return isoString.replace('T', ' ').substring(0, 16)
}

const updateChart = () => {
  if (!chartInstance) return
  
  // 简单统计各类事件的数量用于 ECharts 饼图展示
  const typeCounts: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0 }
  events.value.forEach(e => {
    if (typeCounts[e.eventType] !== undefined) {
      typeCounts[e.eventType]++
    }
  })

  const option = {
    title: {
      text: '事件分布洞察',
      left: 'center',
      textStyle: { fontSize: 14, color: '#606266' }
    },
    tooltip: { trigger: 'item' },
    legend: { bottom: '0', icon: 'circle' },
    series: [
      {
        name: '事件分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: [
          { value: typeCounts[1], name: '就诊记录', itemStyle: { color: '#409EFF' } },
          { value: typeCounts[2], name: '异常预警', itemStyle: { color: '#F56C6C' } },
          { value: typeCounts[3], name: '用药变更', itemStyle: { color: '#67C23A' } },
          { value: typeCounts[4], name: '随访记录', itemStyle: { color: '#E6A23C' } }
        ]
      }
    ]
  }
  chartInstance.setOption(option)
}

onMounted(() => {
  nextTick(() => {
    if (chartRef.value) {
      chartInstance = echarts.init(chartRef.value)
    }
    fetchEvents()
  })
})

watch(() => props.patientId, () => { fetchEvents() })
</script>

<style scoped>
.timeline-card {
  border-radius: 24px;
  margin: 0 auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
}
.event-chart {
  height: 200px;
  width: 100%;
}
.timeline-container {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 15px;
}
.event-detail {
  border-left: 4px solid #409EFF;
  border-radius: 12px;
  transition: transform 0.2s;
}
.event-detail:hover {
  transform: translateX(5px);
}
.alert-level-2 {
  border-left-color: #F56C6C;
  background-color: var(--ag-event-danger-bg);
}
.alert-level-1 {
  border-left-color: #E6A23C;
  background-color: var(--ag-event-warn-bg);
}
.event-detail h4 {
  margin: 0 0 10px 0;
  font-size: 15px;
  color: var(--ag-text-primary);
}
.event-detail p {
  margin: 0;
  font-size: 13px;
  color: var(--ag-text-secondary);
  line-height: 1.5;
}
</style>
