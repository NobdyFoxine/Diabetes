<template>
  <div class="operation-log-container">
    <el-card class="log-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><Document /></el-icon>
            系统操作日志（审计）
          </span>
          <el-button type="primary" size="small" @click="fetchLogs" plain>刷新</el-button>
        </div>
      </template>
      
      <el-table :data="logs" style="width: 100%" height="calc(100vh - 280px)" stripe>
        <el-table-column prop="createTime" label="操作时间" width="160">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="140">
          <template #default="{ row }">
            <div class="operator-info">
              <el-tag size="small" :type="getRoleTagType(row.roleType)">
                {{ getRoleName(row.roleType) }}
              </el-tag>
              <span class="operator-name">{{ row.operatorName || '未知用户' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作内容" width="180">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="info">{{ row.action }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetData" label="请求详情 / 参数" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <code class="data-code">{{ row.targetData }}</code>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const logs = ref<any[]>([])
const loading = ref(false)

const getRoleName = (roleType: number) => {
  switch(roleType) {
    case 1: return '管理员'
    case 2: return '医生'
    case 3: return '护士'
    case 4: return '患者'
    default: return '系统'
  }
}

const getRoleTagType = (roleType: number) => {
  switch(roleType) {
    case 1: return 'danger'
    case 2: return 'primary'
    case 3: return 'warning'
    case 4: return 'success'
    default: return 'info'
  }
}

const fetchLogs = async () => {
  loading.value = true
  try {
    const { data } = await request.get('/api/v1/system/log/recent')
    if (data.code === 200) {
      logs.value = data.data
    } else {
      ElMessage.error(data.msg || '获取日志失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，无法获取日志')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.operation-log-container {
  padding: 0;
  height: 100%;
}

.log-card {
  border-radius: 24px;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-weight: 700;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-text {
  color: var(--ag-text-secondary);
  font-size: 13px;
}

.operator-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.operator-name {
  font-weight: 500;
}

.data-code {
  background-color: var(--ag-code-bg);
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  color: var(--ag-text-secondary);
  font-family: 'SF Mono', Consolas, Monaco, monospace;
}
</style>
