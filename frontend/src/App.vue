<template>
  <el-config-provider>
    <!-- 未登录时显示登录页 -->
    <Login v-if="!isLoggedIn" @success="handleLoginSuccess" />
    
    <!-- 已登录显示主界面 -->
    <el-container v-else class="layout-container">
    <!-- AI 助手悬浮球（患者端） -->
    <AiChatFloat v-if="userInfo?.roleType === 4" />
      <!-- 侧边栏 -->
      <el-aside width="240px" class="aside">
        <div class="logo">
          <el-icon class="logo-icon" :size="24"><FirstAidKit /></el-icon>
          <span>DiabetesHealthSystem</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          @select="handleSelect"
          background-color="transparent"
          text-color="var(--ag-text-secondary)"
          active-text-color="var(--ag-primary)"
        >
          <el-menu-item index="patient" v-if="userInfo?.roleType === 4">
            <el-icon><Monitor /></el-icon>
            <span>患者端 - 体征监控大屏</span>
          </el-menu-item>
          <el-menu-item index="profile" v-if="userInfo?.roleType === 4">
            <el-icon><User /></el-icon>
            <span>患者端 - 健康档案</span>
          </el-menu-item>
          <el-menu-item index="doctor" v-if="userInfo?.roleType === 2">
            <el-icon><Monitor /></el-icon>
            <span>医生端 - 监控工作台</span>
          </el-menu-item>
          <el-menu-item index="plan" v-if="userInfo?.roleType === 2">
            <el-icon><Calendar /></el-icon>
            <span>医生端 - 随访计划</span>
          </el-menu-item>
          <el-menu-item index="admin" v-if="userInfo?.roleType === 1">
            <el-icon><Setting /></el-icon>
            <span>管理员 - 系统总览</span>
          </el-menu-item>
          <el-menu-item index="nurse" v-if="userInfo?.roleType === 3">
            <el-icon><Monitor /></el-icon>
            <span>护士 - 随访工作台</span>
          </el-menu-item>
          <el-menu-item index="log" v-if="userInfo?.roleType === 1">
            <el-icon><Setting /></el-icon>
            <span>系统管理 - 操作日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主体内容 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item>DiabetesHealthSystem</el-breadcrumb-item>
              <el-breadcrumb-item>{{ menuTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-switch
              v-model="isDark"
              :active-icon="Moon"
              :inactive-icon="Sunny"
              inline-prompt
              class="theme-switch"
              @change="toggleTheme"
            />
            <el-dropdown>
              <span class="el-dropdown-link user-profile">
                <el-avatar size="small" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                <span class="username">{{ userInfo?.realName || 'User' }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>角色: {{ roleName }}</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容渲染区 -->
        <el-main class="main">
          <div class="view-wrapper">
            <!-- 模块化大标题 -->
            <div class="page-header">
              <h2 class="page-title">{{ pageTitle }}</h2>
            </div>

            <transition name="fade-transform" mode="out-in">
              <!-- 患者视角：模块化健康大屏 -->
              <div v-if="activeMenu === 'patient'" class="view-content patient-view">
                <!-- Row 1: 患者统计概览 -->
                <el-row :gutter="16" class="stat-row">
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">最近血糖</div>
                      <div class="stat-value">{{ patientStats.lastGlucose }}</div>
                      <div class="stat-sub">mmol/L</div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">今日上报</div>
                      <div class="stat-value" :class="patientStats.todayCount > 0 ? 'stat-success' : 'stat-danger'">{{ patientStats.todayCount }}</div>
                      <div class="stat-sub">{{ patientStats.todayCount > 0 ? '已完成' : '待上报' }}</div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">本周平均</div>
                      <div class="stat-value">{{ patientStats.weekAvg }}</div>
                      <div class="stat-sub">mmol/L</div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">达标率 (TIR)</div>
                      <div class="stat-value stat-success">{{ patientStats.tirRate }}%</div>
                      <div class="stat-sub">目标 ≥ 70%</div>
                    </el-card>
                  </el-col>
                </el-row>

                <!-- Row 2: 表单 + 图表 -->
                <el-row :gutter="16" class="main-row">
                  <el-col :span="9">
                    <HealthDataUpload />
                  </el-col>
                  <el-col :span="15">
                    <HealthTrendChart :patientId="userInfo?.userId || 10001" />
                  </el-col>
                </el-row>
                <!-- Row 3: 在线咨询 -->
                <div style="margin-top: 16px;">
                  <MessageChat />
                </div>
              </div>

              <!-- 患者视角：个人档案 -->
              <div v-else-if="activeMenu === 'profile'" class="view-content">
                <PatientProfile />
              </div>
              
              <!-- 医生视角：模块化仪表盘 -->
              <div v-else-if="activeMenu === 'doctor'" class="view-content doctor-view">
                <!-- 患者选择 -->
                <div class="patient-select-bar">
                  <span class="select-label">当前查看患者：</span>
                  <el-select v-model="selectedPatientId" filterable placeholder="选择患者" size="default" style="width:240px" @change="onPatientChange">
                    <el-option v-for="p in doctorPatientList" :key="p.id" :label="`${p.realName} (ID:${p.id})`" :value="p.id" />
                  </el-select>
                </div>
                <!-- Row 1: 顶部统计概览卡片 -->
                <el-row :gutter="16" class="stat-row">
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">今日上报</div>
                      <div class="stat-value">{{ stats.todayUploads }}</div>
                      <div class="stat-sub">较昨日 <span class="stat-up">+{{ stats.uploadDelta }}</span></div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">待处理预警</div>
                      <div class="stat-value stat-danger">{{ stats.unhandledEvents }}</div>
                      <div class="stat-sub">需立即响应</div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">已处理事件</div>
                      <div class="stat-value stat-success">{{ stats.handledEvents }}</div>
                      <div class="stat-sub">处理完毕归档</div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">达标率 (TIR)</div>
                      <div class="stat-value stat-success">{{ stats.tirRate }}%</div>
                      <div class="stat-sub">目标 ≥ 70%</div>
                    </el-card>
                  </el-col>
                </el-row>

                <!-- Row 2: 趋势图 + 预警控制台 -->
                <el-row :gutter="16" class="main-row">
                  <el-col :span="15">
                    <HealthTrendChart :patientId="selectedPatientId" />
                  </el-col>
                  <el-col :span="9">
                    <DoctorAlert />
                  </el-col>
                </el-row>

                <!-- Row 3: 事件时间轴（全宽） -->
                <div class="timeline-row">
                  <HealthEventTimeline :patientId="selectedPatientId" />
                </div>
                <!-- Row 4: 健康教育文章库 + 在线咨询 -->
                <el-row :gutter="16" style="margin-top: 16px;">
                  <el-col :span="12"><HealthArticleCenter /></el-col>
                  <el-col :span="12"><MessageChat /></el-col>
                </el-row>
              </div>

              <!-- 随访计划视角：模块化调度中心 -->
              <div v-else-if="activeMenu === 'plan'" class="view-content plan-view">
                <!-- Row 1: 随访统计概览 -->
                <el-row :gutter="16" class="stat-row">
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">总计划数</div>
                      <div class="stat-value">{{ planStats.total }}</div>
                      <div class="stat-sub">累计创建</div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">待执行</div>
                      <div class="stat-value stat-warning">{{ planStats.pending }}</div>
                      <div class="stat-sub">需跟进处理</div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">已完成</div>
                      <div class="stat-value stat-success">{{ planStats.completed }}</div>
                      <div class="stat-sub">执行完毕</div>
                    </el-card>
                  </el-col>
                  <el-col :span="6">
                    <el-card class="stat-card" shadow="never">
                      <div class="stat-label">完成率</div>
                      <div class="stat-value stat-success">{{ planStats.rate }}%</div>
                      <div class="stat-sub">目标 ≥ 85%</div>
                    </el-card>
                  </el-col>
                </el-row>

                <!-- Row 2: 计划表格 -->
                <FollowupPlanManager @stats-update="updatePlanStats" />
              </div>

              <!-- 管理员视角：系统总览 -->
              <div v-else-if="activeMenu === 'admin'" class="view-content admin-view">
                <el-row :gutter="16" class="stat-row">
                  <el-col :span="8"><el-card class="stat-card" shadow="never"><div class="stat-label">系统用户</div><div class="stat-value">{{ adminUsers.length }}</div><div class="stat-sub">含医生/护士/患者</div></el-card></el-col>
                  <el-col :span="8"><el-card class="stat-card" shadow="never"><div class="stat-label">今日上报</div><div class="stat-value">{{ stats.todayUploads }}</div><div class="stat-sub">体征数据采集量</div></el-card></el-col>
                  <el-col :span="8"><el-card class="stat-card" shadow="never"><div class="stat-label">待处理预警</div><div class="stat-value stat-danger">{{ stats.unhandledEvents }}</div><div class="stat-sub">需医生及时响应</div></el-card></el-col>
                </el-row>
                <el-card class="admin-table-card" shadow="hover">
                  <template #header><span style="font-weight:700">用户管理与角色分配</span></template>
                  <el-table :data="adminUsers" style="width:100%" v-loading="adminLoading">
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column prop="username" label="账号" width="140" />
                    <el-table-column prop="realName" label="姓名" width="140" />
                    <el-table-column prop="roleType" label="当前角色" width="140">
                      <template #default="{ row }"><el-tag :type="roleTagType(row.roleType)" size="small">{{ roleText(row.roleType) }}</el-tag></template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="注册时间" width="180">
                      <template #default="{ row }">{{ row.createTime?.substring(0,16) }}</template>
                    </el-table-column>
                    <el-table-column label="操作">
                      <template #default="{ row }">
                        <el-select v-model="row.newRole" placeholder="更改角色" size="small" style="width:110px" @change="(val:number) => changeUserRole(row, val)">
                          <el-option label="管理员" :value="1" />
                          <el-option label="医生" :value="2" />
                          <el-option label="护士" :value="3" />
                          <el-option label="患者" :value="4" />
                        </el-select>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-card>
              </div>

              <!-- 护士视角：随访工作台 -->
              <div v-else-if="activeMenu === 'nurse'" class="view-content nurse-view">
                <el-row :gutter="16" class="stat-row">
                  <el-col :span="8"><el-card class="stat-card" shadow="never"><div class="stat-label">今日待随访</div><div class="stat-value stat-warning">{{ planStats.pending }}</div><div class="stat-sub">需执行随访任务</div></el-card></el-col>
                  <el-col :span="8"><el-card class="stat-card" shadow="never"><div class="stat-label">今日已完成</div><div class="stat-value stat-success">{{ planStats.completed }}</div><div class="stat-sub">随访执行进度</div></el-card></el-col>
                  <el-col :span="8"><el-card class="stat-card" shadow="never"><div class="stat-label">待处理预警</div><div class="stat-value stat-danger">{{ stats.unhandledEvents }}</div><div class="stat-sub">需通知主管医生</div></el-card></el-col>
                </el-row>
                <FollowupPlanManager @stats-update="updatePlanStats" />
              </div>

              <!-- 系统管理视角：操作日志 -->
              <div v-else-if="activeMenu === 'log'" class="view-content">
                <OperationLog />
              </div>
            </transition>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </el-config-provider>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { FirstAidKit, User, Monitor, Setting, ArrowDown, Calendar, Moon, Sunny } from '@element-plus/icons-vue'
import Login from './components/Login.vue'
import HealthDataUpload from './components/HealthDataUpload.vue'
import DoctorAlert from './components/DoctorAlert.vue'
import HealthEventTimeline from './components/HealthEventTimeline.vue'
import FollowupPlanManager from './components/FollowupPlanManager.vue'
import HealthTrendChart from './components/HealthTrendChart.vue'
import PatientProfile from './components/PatientProfile.vue'
import OperationLog from './components/OperationLog.vue'
import HealthArticleCenter from './components/HealthArticleCenter.vue'
import MessageChat from './components/MessageChat.vue'
import AiChatFloat from './components/AiChatFloat.vue'
import request from './utils/request'

const isLoggedIn = ref(false)
const userInfo = ref<any>(null)
const activeMenu = ref('doctor')
const isDark = ref(false)

const roleName = computed(() => {
  const map: Record<number, string> = { 1: '管理员', 2: '医生', 3: '护士', 4: '患者' }
  return map[userInfo.value?.roleType] || '用户'
})

const menuTitle = computed(() => {
  const titles: Record<string, string> = {
    patient: '体征监控大屏', profile: '个人健康档案',
    doctor: '患者健康监控台', plan: '随访计划调度中心',
    admin: '系统管理总览', nurse: '随访任务工作台', log: '系统安全审计日志'
  }
  return titles[activeMenu.value] || '控制台'
})

const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    patient: '体征监控大屏', profile: '个人健康档案管理',
    doctor: '患者健康监控台', plan: '随访计划调度中心',
    admin: '系统管理总览', nurse: '随访任务工作台', log: '系统安全审计日志'
  }
  return titles[activeMenu.value] || '控制台'
})

// 是否已被用户手动切换（手动切换后不再跟随系统）
const themeExplicitlySet = ref(false)

// 系统主题媒体查询
let systemDarkQuery: MediaQueryList | null = null

const applyTheme = (dark: boolean) => {
  document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light')
  localStorage.setItem('theme', dark ? 'dark' : 'light')
}

const toggleTheme = (val: boolean) => {
  themeExplicitlySet.value = true
  applyTheme(val)
}

// 监听系统主题变化（仅当用户未手动切换时生效）
const handleSystemThemeChange = (e: MediaQueryListEvent) => {
  if (!themeExplicitlySet.value) {
    isDark.value = e.matches
    applyTheme(e.matches)
  }
}

// 医生端全局统计数据
const stats = reactive({
  todayUploads: 0,
  uploadDelta: 0,
  unhandledEvents: 0,
  handledEvents: 0,
  tirRate: 72
})

// 患者端统计数据
const patientStats = reactive({
  lastGlucose: '--',
  todayCount: 0,
  weekAvg: '--',
  tirRate: 0
})

// 随访计划统计
const planStats = reactive({
  total: 0,
  pending: 0,
  completed: 0,
  rate: 0
})

const loadStats = async () => {
  try {
    // 紧急事件统计
    const statsRes = await request.get('/api/v1/patient/health-events/alerts/stats')
    if (statsRes.data.code === 200 && statsRes.data.data) {
      stats.unhandledEvents = statsRes.data.data.unhandled || 0
      stats.handledEvents = statsRes.data.data.handled || 0
    }
    // 体征统计
    const histRes = await request.get(`/api/v1/patient/health-data/history/${selectedPatientId.value}`)
    if (histRes.data.code === 200 && histRes.data.data) {
      const today = new Date().toISOString().slice(0, 10)
      const todayData = histRes.data.data.filter((d: any) => d.createTime?.startsWith(today))
      stats.todayUploads = todayData.length
      stats.uploadDelta = Math.max(0, todayData.length - 2)
      const allData = histRes.data.data
      if (allData.length > 0) {
        const inRange = allData.filter((d: any) => d.glucoseValue >= 3.9 && d.glucoseValue <= 10.0).length
        stats.tirRate = Math.round((inRange / allData.length) * 100)
      }
    }
  } catch (e) {
    console.error('统计数据加载失败', e)
  }
}

const loadPatientStats = async (pid: number) => {
  try {
    const { data } = await request.get(`/api/v1/patient/health-data/history/${pid}`)
    if (data.code === 200 && data.data && data.data.length > 0) {
      const all = data.data
      patientStats.lastGlucose = all[all.length - 1].glucoseValue?.toFixed(1) || '--'
      const today = new Date().toISOString().slice(0, 10)
      patientStats.todayCount = all.filter((d: any) => d.createTime?.startsWith(today)).length
      const sum = all.reduce((s: number, d: any) => s + (d.glucoseValue || 0), 0)
      patientStats.weekAvg = (sum / all.length).toFixed(1)
      const inRange = all.filter((d: any) => d.glucoseValue >= 3.9 && d.glucoseValue <= 10.0).length
      patientStats.tirRate = Math.round((inRange / all.length) * 100)
    }
  } catch (e) {
    console.error('患者统计加载失败', e)
  }
}

const updatePlanStats = (data: { total: number; pending: number; completed: number }) => {
  planStats.total = data.total
  planStats.pending = data.pending
  planStats.completed = data.completed
  planStats.rate = data.total > 0 ? Math.round((data.completed / data.total) * 100) : 0
}

let statsTimer: ReturnType<typeof setInterval> | null = null

const adminUsers = ref<any[]>([])
const selectedPatientId = ref(10001)
const doctorPatientList = ref<any[]>([])

const fetchDoctorPatients = async () => {
  try {
    const { data } = await request.get('/api/v1/doctor/followup-plan/patients')
    if (data.code === 200) doctorPatientList.value = data.data
  } catch (e) { /* ignore */ }
}

const onPatientChange = () => {
  loadStats()
}
const adminLoading = ref(false)

const roleText = (rt: number) => ({ 1: '管理员', 2: '医生', 3: '护士', 4: '患者' } as Record<number,string>)[rt] || ''
const roleTagType = (rt: number) => ({ 1: 'danger', 2: 'primary', 3: 'warning', 4: 'success' } as Record<number,string>)[rt] || 'info'

const loadAdminUsers = async () => {
  adminLoading.value = true
  try {
    const { data } = await request.get('/api/v1/admin/users')
    if (data.code === 200) adminUsers.value = data.data.map((u: any) => ({ ...u, newRole: u.roleType }))
  } catch (e) { /* ignore */ }
  finally { adminLoading.value = false }
}

const changeUserRole = async (user: any, newRole: number) => {
  try {
    const { data } = await request.put(`/api/v1/admin/user/${user.id}/role`, { roleType: newRole })
    if (data.code === 200) {
      ElMessage.success(`用户 ${user.username} 角色已更新`)
      user.roleType = newRole
    } else {
      ElMessage.error(data.msg || '更改失败')
      user.newRole = user.roleType
    }
  } catch (e) {
    ElMessage.error('操作失败')
    user.newRole = user.roleType
  }
}

const initPageData = (roleType: number, userId?: number) => {
  if (statsTimer) { clearInterval(statsTimer); statsTimer = null }
  if (roleType === 1) {
    loadStats()
    loadAdminUsers()
    statsTimer = setInterval(loadStats, 10000)
  } else if (roleType === 3) {
    loadStats()
    statsTimer = setInterval(loadStats, 10000)
  } else if (roleType === 2) {
    loadStats()
    fetchDoctorPatients()
    statsTimer = setInterval(loadStats, 10000)
  } else if (roleType === 4) {
    loadPatientStats(userId || 10001)
  }
}

onMounted(() => {
  // 初始化主题：用户保存的偏好 > 系统主题
  systemDarkQuery = window.matchMedia('(prefers-color-scheme: dark)')
  const saved = localStorage.getItem('theme')
  if (saved) {
    // 用户曾手动切换过
    themeExplicitlySet.value = true
    isDark.value = saved === 'dark'
  } else {
    // 跟随系统主题
    isDark.value = systemDarkQuery.matches
  }
  applyTheme(isDark.value)
  // 监听系统主题变化
  systemDarkQuery.addEventListener('change', handleSystemThemeChange)
  // 检查登录状态
  const token = localStorage.getItem('token')
  const storedUser = localStorage.getItem('userInfo')
  if (token && storedUser) {
    isLoggedIn.value = true
    userInfo.value = JSON.parse(storedUser)
    activeMenu.value = getDefaultMenu(userInfo.value.roleType)
    initPageData(userInfo.value.roleType, userInfo.value.userId)
  }
})

onUnmounted(() => {
  if (systemDarkQuery) {
    systemDarkQuery.removeEventListener('change', handleSystemThemeChange)
  }
})

const handleLoginSuccess = (user: any) => {
  isLoggedIn.value = true
  userInfo.value = user
  activeMenu.value = getDefaultMenu(user.roleType)
  initPageData(user.roleType, user.userId)
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  isLoggedIn.value = false
  userInfo.value = null
}

const getDefaultMenu = (roleType: number) => {
  const map: Record<number, string> = { 1: 'admin', 2: 'doctor', 3: 'nurse', 4: 'patient' }
  return map[roleType] || 'doctor'
}

const handleSelect = (key: string) => {
  activeMenu.value = key
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&display=swap');

:root,
[data-theme='light'] {
  --ag-primary: #19C853;
  --ag-primary-hover: #15a645;
  --ag-bg-subtle: rgba(25, 200, 83, 0.1);
  --ag-surface: #FFFFFF;
  --ag-background: #EDECF1;
  --ag-sidebar: #FFFFFF;
  --ag-text-primary: #0C0D0D;
  --ag-text-secondary: #7e828a;
  --ag-text-tertiary: #909399;
  --ag-border-radius: 24px;
  --ag-border-light: rgba(12, 13, 13, 0.03);
  --ag-border-medium: rgba(12, 13, 13, 0.06);
  --ag-border-strong: rgba(12, 13, 13, 0.1);
  --ag-shadow-sm: 0 2px 8px rgba(12, 13, 13, 0.03);
  --ag-shadow: 0 6px 24px rgba(12, 13, 13, 0.04);
  --ag-shadow-hover: 0 12px 32px rgba(12, 13, 13, 0.08);
  --ag-input-bg: #FFFFFF;
  --ag-input-border: #DCDFE6;
  --ag-table-stripe: #FAFAFA;
  --ag-hover-bg: rgba(0, 0, 0, 0.04);
  --ag-code-bg: rgba(25, 200, 83, 0.05);
  --ag-event-info-bg: #f0f9ff;
  --ag-event-warn-bg: #fdf6ec;
  --ag-event-danger-bg: #fef0f0;
  --ag-button-text: #FFFFFF;
  --ag-menu-hover-bg: rgba(0, 0, 0, 0.03);
  --ag-breadcrumb-color: #606266;
  --ag-glass-bg: rgba(255, 255, 255, 0.6);
  --ag-glass-border: rgba(255, 255, 255, 0.8);
}

[data-theme='dark'] {
  --ag-primary: #2dd468;
  --ag-primary-hover: #3fe07a;
  --ag-bg-subtle: rgba(45, 212, 104, 0.12);
  --ag-surface: #1a1d2e;
  --ag-background: #12141f;
  --ag-sidebar: #161824;
  --ag-text-primary: #e8eaf0;
  --ag-text-secondary: #8b8fa5;
  --ag-text-tertiary: #6b6f82;
  --ag-border-radius: 24px;
  --ag-border-light: rgba(255, 255, 255, 0.04);
  --ag-border-medium: rgba(255, 255, 255, 0.08);
  --ag-border-strong: rgba(255, 255, 255, 0.12);
  --ag-shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.2);
  --ag-shadow: 0 6px 24px rgba(0, 0, 0, 0.25);
  --ag-shadow-hover: 0 12px 32px rgba(0, 0, 0, 0.35);
  --ag-input-bg: #232636;
  --ag-input-border: #2e3144;
  --ag-table-stripe: #1a1d2e;
  --ag-hover-bg: rgba(255, 255, 255, 0.04);
  --ag-code-bg: rgba(45, 212, 104, 0.08);
  --ag-event-info-bg: rgba(64, 158, 255, 0.1);
  --ag-event-warn-bg: rgba(230, 162, 60, 0.1);
  --ag-event-danger-bg: rgba(245, 108, 108, 0.1);
  --ag-button-text: #0C0D0D;
  --ag-menu-hover-bg: rgba(255, 255, 255, 0.06);
  --ag-breadcrumb-color: #8b8fa5;
  --ag-glass-bg: rgba(255, 255, 255, 0.06);
  --ag-glass-border: rgba(255, 255, 255, 0.1);
}

body {
  margin: 0;
  font-family: 'Outfit', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  color: var(--ag-text-primary);
  background-color: var(--ag-background);
  -webkit-font-smoothing: antialiased;
}

.layout-container {
  height: 100vh;
}

/* 毛玻璃核心类 */
.glass-panel {
  background: var(--ag-surface);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: var(--ag-border);
}

.aside {
  background: var(--ag-sidebar);
  border-right: 1px solid var(--ag-border-light);
  z-index: 10;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  color: var(--ag-text-primary);
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.5px;
  position: relative;
  overflow: hidden;
}

.logo::after {
  content: '';
  position: absolute;
  bottom: 0;
  width: 80%;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--ag-border-medium), transparent);
}

.logo span {
  color: var(--ag-text-primary);
}

.logo-icon {
  margin-right: 10px;
  color: var(--ag-primary);
  transition: transform 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.logo:hover .logo-icon {
  transform: rotate(15deg) scale(1.1);
}

.el-menu-vertical {
  border-right: none;
  flex: 1;
  background: transparent;
  padding-top: 8px;
}

.el-menu-vertical .el-menu-item {
  height: 44px;
  line-height: 44px;
  margin: 4px 12px;
  border-radius: 16px;
  color: var(--ag-text-secondary);
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid transparent;
}

.el-menu-vertical .el-menu-item:hover {
  background-color: var(--ag-menu-hover-bg);
  color: var(--ag-primary);
  transform: translateX(6px);
}

.el-menu-item.is-active {
  background: var(--ag-primary) !important;
  color: var(--ag-button-text) !important;
  font-weight: 600;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(25, 200, 83, 0.3);
  transform: translateX(4px);
}

.header {
  background: var(--ag-surface);
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  box-shadow: var(--ag-shadow-sm);
  border-bottom: 1px solid var(--ag-border-light);
  z-index: 9;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.theme-switch {
  --el-switch-on-color: #2dd468;
  --el-switch-off-color: #DCDFE6;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: var(--ag-text-secondary);
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 24px;
  background: var(--ag-hover-bg);
  border: 1px solid var(--ag-border-light);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.user-profile:hover {
  background: var(--ag-hover-bg);
  transform: translateY(-2px);
  box-shadow: var(--ag-shadow-hover);
  color: var(--ag-primary);
}

.username {
  margin: 0 8px;
}

.main {
  background: transparent;
  padding: 20px 28px;
  overflow-x: hidden;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--ag-text-primary);
  margin: 0;
  letter-spacing: -0.5px;
}

.view-wrapper {
  min-height: calc(100vh - 108px);
  width: 100%;
}

.spacer {
  height: 24px;
}

.view-content {
  width: 100%;
}

/* 覆盖 Element Plus 卡片为毛玻璃态并增加呼吸交互 */
.el-card {
  border-radius: var(--ag-border-radius) !important;
  background: var(--ag-surface) !important;
  border: var(--ag-border) !important;
  box-shadow: var(--ag-shadow) !important;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
}

.el-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--ag-shadow-hover) !important;
}

/* 页面切换动画（弹性滑动） */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.98);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-30px) scale(0.98);
}

/* 所有的按钮加上点击涟漪和悬浮微调 */
.el-button {
  border-radius: 8px !important;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}
.el-button--primary {
  background-color: var(--ag-primary) !important;
  border-color: var(--ag-primary) !important;
}
.el-button--primary:hover {
  background-color: var(--ag-primary-hover) !important;
  border-color: var(--ag-primary-hover) !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(25, 200, 83, 0.3) !important;
}
/* 模块化仪表盘布局 */
.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
  padding: 4px 0;
  border-radius: 16px !important;
  border: 1px solid var(--ag-border-medium) !important;
}

.stat-card:hover {
  transform: translateY(-2px) !important;
}

.stat-label {
  font-size: 12px;
  color: var(--ag-text-secondary);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--ag-text-primary);
  line-height: 1.2;
}

.stat-value.stat-danger {
  color: #F56C6C;
}

.stat-value.stat-success {
  color: var(--ag-primary);
}

.stat-value.stat-warning {
  color: #E6A23C;
}

.stat-sub {
  font-size: 12px;
  color: var(--ag-text-secondary);
  margin-top: 4px;
}

.stat-up {
  color: var(--ag-primary);
  font-weight: 600;
}

.main-row {
  margin-bottom: 16px;
  display: flex !important;
  align-items: stretch !important;
}

.main-row > .el-col {
  display: flex !important;
}

.main-row > .el-col > * {
  flex: 1;
  width: 100%;
}

.timeline-row {
  margin-top: 0;
}

.patient-select-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}
.select-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--ag-text-secondary);
}

.admin-table-card {
  border-radius: 24px;
}
.admin-table-card :deep(.el-card__body) {
  padding: 16px 20px;
}

/* ======== Element Plus 深色覆盖 ======== */
[data-theme='dark'] .el-table {
  --el-table-bg-color: var(--ag-surface);
  --el-table-tr-bg-color: var(--ag-surface);
  --el-table-header-bg-color: var(--ag-sidebar);
  --el-table-row-hover-bg-color: var(--ag-hover-bg);
  --el-table-border-color: var(--ag-border-medium);
  --el-table-text-color: var(--ag-text-primary);
  --el-table-header-text-color: var(--ag-text-secondary);
}

[data-theme='dark'] .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: var(--ag-table-stripe);
}

[data-theme='dark'] .el-breadcrumb__inner {
  color: var(--ag-breadcrumb-color);
}

[data-theme='dark'] .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: var(--ag-text-primary);
}

[data-theme='dark'] .el-dialog {
  --el-dialog-bg-color: var(--ag-surface);
  --el-dialog-title-font-size: 18px;
}

[data-theme='dark'] .el-pagination .btn-prev,
[data-theme='dark'] .el-pagination .btn-next {
  background-color: var(--ag-surface);
  color: var(--ag-text-primary);
}

[data-theme='dark'] .el-input__wrapper {
  background-color: var(--ag-input-bg);
  box-shadow: 0 0 0 1px var(--ag-input-border);
}

[data-theme='dark'] .el-select .el-input__wrapper {
  background-color: var(--ag-input-bg);
}

[data-theme='dark'] .el-select-dropdown {
  background-color: var(--ag-surface);
  border: 1px solid var(--ag-border-medium);
}

[data-theme='dark'] .el-select-dropdown__item {
  color: var(--ag-text-primary);
}

[data-theme='dark'] .el-select-dropdown__item.hover,
[data-theme='dark'] .el-select-dropdown__item:hover {
  background-color: var(--ag-hover-bg);
}

[data-theme='dark'] .el-popper.is-light {
  background: var(--ag-surface);
  border: 1px solid var(--ag-border-medium);
  color: var(--ag-text-primary);
}

[data-theme='dark'] .el-dropdown-menu {
  background-color: var(--ag-surface);
  border: 1px solid var(--ag-border-medium);
}

[data-theme='dark'] .el-dropdown-menu__item {
  color: var(--ag-text-primary);
}

[data-theme='dark'] .el-dropdown-menu__item:hover {
  background-color: var(--ag-hover-bg);
}

[data-theme='dark'] .el-empty__description p {
  color: var(--ag-text-secondary);
}

[data-theme='dark'] .el-divider__text {
  background-color: var(--ag-surface);
  color: var(--ag-text-secondary);
}

[data-theme='dark'] .el-check-tag,
[data-theme='dark'] .el-checkbox__label {
  color: var(--ag-text-primary);
}

[data-theme='dark'] .el-date-editor {
  --el-date-editor-bg-color: var(--ag-input-bg);
}

[data-theme='dark'] .el-tag--default {
  --el-tag-bg-color: var(--ag-hover-bg);
  --el-tag-border-color: var(--ag-border-medium);
  --el-tag-text-color: var(--ag-text-secondary);
}

[data-theme='dark'] .el-tag--plain {
  --el-tag-bg-color: var(--ag-hover-bg);
}

[data-theme='dark'] .el-drawer {
  --el-drawer-bg-color: var(--ag-surface);
}

[data-theme='dark'] .el-tabs__item {
  color: var(--ag-text-secondary);
}

[data-theme='dark'] .el-tabs__item.is-active {
  color: var(--ag-primary);
}
</style>
