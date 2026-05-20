<template>
  <div class="plan-manager">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span><el-icon><Calendar /></el-icon> 随访计划制定与管理</span>
          <el-button type="primary" @click="openDialog">
            <el-icon><Plus /></el-icon> 新建计划
          </el-button>
        </div>
      </template>

      <el-table :data="plans" style="width: 100%" v-loading="loading">
        <el-table-column prop="patientId" label="患者ID" width="120" />
        <el-table-column prop="planDate" label="计划执行日" width="150" />
        <el-table-column prop="content" label="随访与干预内容" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              type="success" 
              v-if="scope.row.status === 0"
              @click="updateStatus(scope.row.id, 1)"
            >完成</el-button>
            <el-button 
              size="small" 
              type="danger" 
              v-if="scope.row.status === 0"
              @click="updateStatus(scope.row.id, 2)"
            >取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建计划弹窗 -->
    <el-dialog v-model="dialogVisible" title="新建随访计划" width="560px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="使用模板">
          <el-select v-model="selectedTemplateId" placeholder="可选：从模板快速创建" style="width: 100%" clearable @change="applyTemplate">
            <el-option v-for="t in templates" :key="t.id" :label="`${t.name}（每${t.cycleDays}天）`" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标患者" prop="patientId">
          <el-select 
            v-model="form.patientId" 
            filterable 
            placeholder="请输入或选择患者" 
            style="width: 100%"
          >
            <el-option
              v-for="p in patientsList"
              :key="p.id"
              :label="`${p.realName} (ID: ${p.id})`"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划日期" prop="planDate">
          <el-date-picker
            v-model="form.planDate"
            type="date"
            placeholder="选择随访日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="随访内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            rows="4"
            placeholder="请输入健康教育、用药调整或检查提醒等随访内容..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPlan" :loading="submitting">
            确认下达
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Calendar, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const emit = defineEmits(['stats-update'])

const plans = ref<any[]>([])
const patientsList = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()

const selectedTemplateId = ref<number | null>(null)
const templates = ref<any[]>([])

const fetchTemplates = async () => {
  try {
    const { data } = await request.get('/api/v1/doctor/followup-plan/templates')
    if (data.code === 200) templates.value = data.data
  } catch (e) { /* ignore */ }
}

const applyTemplate = (templateId: number | null) => {
  if (!templateId) return
  const t = templates.value.find((t: any) => t.id === templateId)
  if (t) {
    form.content = (t.defaultContent || '').replace(/\\n/g, '\n')
    form.planDate = new Date(Date.now() + (t.cycleDays || 30) * 86400000).toISOString().slice(0, 10)
  }
}

const form = reactive({
  patientId: null,
  planDate: '',
  content: ''
})

const rules = {
  patientId: [{ required: true, message: '请输入患者ID', trigger: 'blur' }],
  planDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }],
  content: [{ required: true, message: '请填写随访内容', trigger: 'blur' }]
}

const fetchPlans = async () => {
  loading.value = true
  try {
    const { data } = await request.get('/api/v1/doctor/followup-plan/list')
    if (data.code === 200) {
      plans.value = data.data
      // 向父组件报告统计
      const total = data.data.length
      const pending = data.data.filter((p: any) => p.status === 0).length
      const completed = data.data.filter((p: any) => p.status === 1).length
      emit('stats-update', { total, pending, completed })
    }
  } catch (error) {
    ElMessage.error('获取随访计划失败')
  } finally {
    loading.value = false
  }
}

const submitPlan = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const { data } = await request.post('/api/v1/doctor/followup-plan/create', form)
    if (data.code === 200) {
      ElMessage.success('随访计划创建成功')
      dialogVisible.value = false
      form.content = '' // 重置内容
      fetchPlans()
    } else {
      ElMessage.error(data.msg || '创建失败')
    }
  } catch (error) {
    ElMessage.error('请求异常')
  } finally {
    submitting.value = false
  }
}

const updateStatus = async (id: number, status: number) => {
  try {
    const { data } = await request.put(`/api/v1/doctor/followup-plan/${id}/status/${status}`)
    if (data.code === 200) {
      ElMessage.success('状态更新成功')
      fetchPlans()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const getStatusType = (status: number) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  if (status === 2) return 'info'
  return ''
}

const getStatusText = (status: number) => {
  if (status === 0) return '待执行'
  if (status === 1) return '已完成'
  if (status === 2) return '已取消'
  return '未知'
}

const formatTime = (isoStr: string) => {
  if (!isoStr) return ''
  return isoStr.replace('T', ' ').substring(0, 16)
}

const fetchPatients = async () => {
  try {
    const { data } = await request.get('/api/v1/doctor/followup-plan/patients')
    if (data.code === 200) {
      patientsList.value = data.data
    }
  } catch (error) {
    console.error('获取患者列表失败', error)
  }
}

const openDialog = () => {
  dialogVisible.value = true
  if (patientsList.value.length === 0) { fetchPatients() }
  if (templates.value.length === 0) { fetchTemplates() }
}

onMounted(() => {
  fetchPlans()
  fetchTemplates()
})
</script>

<style scoped>
.plan-manager {
  width: 100%;
}
.box-card {
  border-radius: 24px;
}
.box-card :deep(.el-card__body) {
  padding: 16px 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
}
</style>
