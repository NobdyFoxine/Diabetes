<template>
  <div class="doctor-dashboard">
    <el-card class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>医生控制中心</span>
          <div class="status-tags">
            <el-button type="primary" size="small" plain @click="openThresholdDialog">阈值配置</el-button>
            <el-tag :type="isConnected ? 'success' : 'danger'" size="small" style="margin-left: 6px;">
              {{ isConnected ? '在线' : '离线' }}
            </el-tag>
            <el-tag type="warning" size="small" style="margin-left: 6px;">
              待处理 {{ stats.unhandled }}
            </el-tag>
            <el-tag type="success" size="small" style="margin-left: 4px;">
              已处理 {{ stats.handled }}
            </el-tag>
          </div>
        </div>
      </template>

      <div class="empty-state" v-if="alerts.length === 0">
        <el-empty description="当前监控正常，无异常体征预警" :image-size="80" />
      </div>

      <div class="alert-list" v-else>
        <div
          v-for="(alert, index) in alerts"
          :key="alert.id || index"
          :class="['event-card', alert.isHandled ? 'event-handled' : '', alert.alertLevel === 2 ? 'event-danger' : 'event-warn']"
        >
          <div class="event-body">
            <div class="event-header">
              <span class="event-badge" :class="alert.alertLevel === 2 ? 'badge-danger' : 'badge-warn'">
                {{ alert.alertLevel === 2 ? '危急' : '关注' }}
              </span>
              <span class="event-id">患者 {{ alert.patientId }}</span>
              <span v-if="alert.isHandled" class="event-done">已处理</span>
              <span v-else class="event-action" @click="handleEvent(alert)">标记处理</span>
            </div>
            <div class="event-desc">{{ alert.description || alert.msg }}</div>
          </div>
        </div>
      </div>

    </el-card>

    <!-- 阈值配置弹窗（放在卡片外，避免 overflow:hidden 影响） -->
    <el-dialog v-model="thresholdDialogVisible" title="患者个性化预警阈值配置" width="560px">
      <el-form :model="thresholdForm" label-width="145px">
        <el-form-item label="目标患者">
          <el-select v-model="thresholdForm.patientId" filterable placeholder="选择患者" style="width: 100%" @change="loadThreshold">
            <el-option v-for="p in patientList" :key="p.id" :label="`${p.realName || '患者'} (ID: ${p.id})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-divider content-position="left">血糖阈值</el-divider>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="低血糖危急"><el-input-number v-model="thresholdForm.glucoseLow" :precision="1" :step="0.1" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="高血糖危急"><el-input-number v-model="thresholdForm.glucoseHigh" :precision="1" :step="0.1" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="血糖偏低关注"><el-input-number v-model="thresholdForm.glucoseWarnLow" :precision="1" :step="0.1" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="血糖偏高关注"><el-input-number v-model="thresholdForm.glucoseWarnHigh" :precision="1" :step="0.1" style="width: 100%" /></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">血压阈值</el-divider>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="收缩压关注"><el-input-number v-model="thresholdForm.systolicWarn" :step="1" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="收缩压危急"><el-input-number v-model="thresholdForm.systolicMax" :step="1" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="舒张压关注"><el-input-number v-model="thresholdForm.diastolicWarn" :step="1" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="舒张压危急"><el-input-number v-model="thresholdForm.diastolicMax" :step="1" style="width: 100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="thresholdForm.notes" placeholder="例：高龄患者降糖方案，放宽控制目标" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="thresholdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveThreshold">保存配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElNotification, ElMessage } from 'element-plus'
import request from '../utils/request'

const isConnected = ref(false)
const alerts = ref<any[]>([])
const thresholdDialogVisible = ref(false)
const patientList = ref<any[]>([])
const stats = reactive({ unhandled: 0, handled: 0 })

const thresholdForm = reactive({
  patientId: null as number | null,
  glucoseLow: 3.9,
  glucoseWarnLow: 4.5,
  glucoseWarnHigh: 10.0,
  glucoseHigh: 16.7,
  systolicMax: 140,
  diastolicMax: 90,
  systolicWarn: 130,
  diastolicWarn: 85,
  notes: ''
})

let ws: WebSocket | null = null
let pollTimer: ReturnType<typeof setInterval> | null = null
let knownIds = new Set<number>()

const fetchPatientList = async () => {
  try {
    const { data } = await request.get('/api/v1/doctor/followup-plan/patients')
    if (data.code === 200) patientList.value = data.data
  } catch (e) { /* ignore */ }
}

const loadThreshold = async (pid: number) => {
  try {
    const { data } = await request.get(`/api/v1/doctor/alert-threshold/${pid}`)
    if (data.code === 200 && data.data) {
      Object.assign(thresholdForm, data.data)
      thresholdForm.patientId = pid
    }
  } catch (e) { /* ignore */ }
}

const openThresholdDialog = () => {
  thresholdDialogVisible.value = true
  if (patientList.value.length === 0) fetchPatientList()
}

const saveThreshold = async () => {
  try {
    const { data } = await request.post('/api/v1/doctor/alert-threshold/save', thresholdForm)
    if (data.code === 200) {
      ElMessage.success('阈值配置已保存，即时生效')
      thresholdDialogVisible.value = false
    } else {
      ElMessage.error(data.msg || '保存失败')
    }
  } catch (e) {
    ElMessage.error('网络异常')
  }
}

const doctorId = 20001

// ======= 方式1: WebSocket 实时推送 =======
const connectWebSocket = () => {
  try {
    ws = new WebSocket(`ws://localhost:8081/ws/doctor/${doctorId}`)

    ws.onopen = () => {
      isConnected.value = true
      console.log('[DoctorAlert] WebSocket 连接成功')
    }

    ws.onmessage = (event) => {
      console.log('[DoctorAlert] 收到WebSocket消息:', event.data)
      try {
        const data = JSON.parse(event.data)
        if (data.alertLevel > 0) {
          alerts.value.unshift(data)
          ElNotification({
            title: data.alertLevel === 2 ? '🚨 紧急体征预警' : '⚠️ 体征波动提醒',
            message: data.msg || data.description,
            type: data.alertLevel === 2 ? 'error' : 'warning',
            duration: data.alertLevel === 2 ? 0 : 5000,
            position: 'top-right'
          })
        }
      } catch (e) {
        console.error('[DoctorAlert] 消息解析失败', e)
      }
    }

    ws.onclose = () => {
      isConnected.value = false
      console.log('[DoctorAlert] WebSocket 连接断开，5秒后重连...')
      setTimeout(connectWebSocket, 5000)
    }

    ws.onerror = (error) => {
      console.error('[DoctorAlert] WebSocket 异常', error)
    }
  } catch (e) {
    console.error('[DoctorAlert] WebSocket 创建失败', e)
  }
}

// ======= 方式2: HTTP 轮询兜底（每10秒拉取一次数据库中的预警记录）=======
const pollAlerts = async () => {
  try {
    const { data } = await request.get('/api/v1/patient/health-events/alerts/recent')
    if (data.code === 200 && data.data) {
      const newAlerts: any[] = []
      for (const a of data.data) {
        if (!knownIds.has(a.id)) {
          knownIds.add(a.id)
          newAlerts.push(a)
        }
      }
      if (newAlerts.length > 0 && alerts.value.length === 0) {
        // 首次加载：显示全部历史预警
        alerts.value = data.data
        data.data.forEach((a: any) => knownIds.add(a.id))
      } else if (newAlerts.length > 0) {
        // 后续轮询：仅推入新增的
        for (const a of newAlerts) {
          alerts.value.unshift(a)
          ElNotification({
            title: a.alertLevel === 2 ? '🚨 紧急体征预警' : '⚠️ 体征波动提醒',
            message: a.description,
            type: a.alertLevel === 2 ? 'error' : 'warning',
            duration: a.alertLevel === 2 ? 0 : 5000,
            position: 'top-right'
          })
        }
      }
    }
  } catch (e) {
    console.error('[DoctorAlert] 轮询预警失败', e)
  }
}

const removeAlert = (index: number) => {
  alerts.value.splice(index, 1)
}

const handleEvent = async (alert: any) => {
  try {
    const { data } = await request.put(`/api/v1/patient/health-events/${alert.id}/handle`)
    if (data.code === 200) {
      ElMessage.success('事件已标记为已处理')
      alert.isHandled = 1
      stats.unhandled = Math.max(0, stats.unhandled - 1)
      stats.handled += 1
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const fetchStats = async () => {
  try {
    const { data } = await request.get('/api/v1/patient/health-events/alerts/stats')
    if (data.code === 200 && data.data) {
      stats.unhandled = data.data.unhandled || 0
      stats.handled = data.data.handled || 0
    }
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  connectWebSocket()
  pollAlerts()
  fetchStats()
  pollTimer = setInterval(() => { pollAlerts(); fetchStats() }, 10000)
})

onUnmounted(() => {
  if (ws) ws.close()
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped>
.doctor-dashboard {
  width: 100%;
}
.dashboard-card {
  height: 420px;
  border-radius: 24px;
}
.dashboard-card :deep(.el-card__body) {
  height: calc(420px - 70px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
}
.status-tags {
  display: flex;
  align-items: center;
}
.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.alert-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 2px;
}

.event-card {
  padding: 10px 12px;
  border-radius: 12px;
  margin-bottom: 8px;
  cursor: default;
  transition: all 0.25s ease;
  animation: slideIn 0.3s ease-out;
}
.event-card:hover {
  transform: translateX(3px);
}

.event-danger {
  background: rgba(245, 108, 108, 0.08);
  border-left: 3px solid #F56C6C;
}
.event-warn {
  background: rgba(230, 162, 60, 0.08);
  border-left: 3px solid #E6A23C;
}
.event-handled {
  opacity: 0.55;
  background: rgba(25, 200, 83, 0.04);
  border-left-color: #19C853;
}

.event-body {
  min-width: 0;
}

.event-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.event-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 1px 7px;
  border-radius: 4px;
  line-height: 1.6;
  color: #fff;
  flex-shrink: 0;
}
.badge-danger { background: #F56C6C; }
.badge-warn { background: #E6A23C; }

.event-id {
  font-size: 13px;
  font-weight: 500;
  color: var(--ag-text-primary);
}

.event-done {
  margin-left: auto;
  font-size: 11px;
  color: #19C853;
  font-weight: 500;
}

.event-action {
  margin-left: auto;
  font-size: 12px;
  color: #19C853;
  cursor: pointer;
  font-weight: 500;
  padding: 2px 6px;
  border-radius: 4px;
  transition: all 0.2s;
}
.event-action:hover {
  background: rgba(25, 200, 83, 0.12);
}

.event-desc {
  font-size: 12px;
  color: var(--ag-text-secondary);
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
