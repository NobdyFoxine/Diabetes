<template>
  <div class="health-upload-container">
    <div class="push-cards" v-if="pendingPlans.length > 0">
      <div v-for="plan in pendingPlans" :key="plan.id" class="push-card">
        <div class="push-header">
          <el-icon class="push-icon"><Bell /></el-icon>
          <span class="push-title">主治医生通知 · {{ plan.planDate }}</span>
        </div>
        <div class="push-content">{{ plan.content }}</div>
      </div>
    </div>

    <el-card class="health-upload-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>今日体征上报</span>
        </div>
      </template>
      
      <el-form :model="formData" :rules="rules" ref="formRef" label-position="top">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="血糖值 (3.9-10.0 mmol/L)" prop="glucoseValue">
              <el-input-number v-model="formData.glucoseValue" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="测量时段" prop="glucosePeriod">
              <el-select v-model="formData.glucosePeriod" placeholder="请选择" style="width: 100%">
                <el-option label="空腹" :value="1" />
                <el-option label="早餐后" :value="2" />
                <el-option label="午餐前" :value="3" />
                <el-option label="午餐后" :value="4" />
                <el-option label="晚餐前" :value="5" />
                <el-option label="晚餐后" :value="6" />
                <el-option label="睡前" :value="7" />
                <el-option label="凌晨" :value="8" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="收缩压 (正常 90-140 mmHg)" prop="systolicBp">
              <el-input-number v-model="formData.systolicBp" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压 (正常 60-90 mmHg)" prop="diastolicBp">
              <el-input-number v-model="formData.diastolicBp" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="心率 (正常 60-100 bpm)" prop="heartRate">
              <el-input-number v-model="formData.heartRate" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重 (BMI 18.5-23.9)" prop="weight">
              <el-input-number v-model="formData.weight" :precision="1" :step="0.5" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">生活方式与用药（选填）</el-divider>

        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="运动步数（步）">
              <el-input-number v-model="formData.exerciseSteps" :step="100" :min="0" :max="100000" style="width: 100%" placeholder="今日步数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="饮食热量（kcal）">
              <el-input-number v-model="formData.dietCalories" :precision="0" :step="50" :min="0" :max="10000" style="width: 100%" placeholder="估算今日摄入" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="用药名称">
              <el-input v-model="formData.medicationName" placeholder="如：二甲双胍" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="用药剂量">
              <el-input v-model="formData.medicationDose" placeholder="如：500mg" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="用药时间">
              <el-select v-model="formData.medicationTime" placeholder="请选择" style="width: 100%" clearable>
                <el-option label="早餐前" value="早餐前" />
                <el-option label="早餐后" value="早餐后" />
                <el-option label="午餐前" value="午餐前" />
                <el-option label="午餐后" value="午餐后" />
                <el-option label="晚餐前" value="晚餐前" />
                <el-option label="晚餐后" value="晚餐后" />
                <el-option label="睡前" value="睡前" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-button type="primary" :loading="isSubmitting" @click="submitData" class="submit-btn">
          一键同步至主治医生
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const formRef = ref()
const isSubmitting = ref(false)
const pendingPlans = ref<any[]>([])

const userInfoStr = localStorage.getItem('userInfo')
const userInfo = userInfoStr ? JSON.parse(userInfoStr) : { userId: 10001 }

const formData = reactive({
  patientId: userInfo.userId,
  glucoseValue: 5.6,
  glucosePeriod: 1,
  systolicBp: 120,
  diastolicBp: 80,
  heartRate: 75,
  weight: 65.5,
  exerciseSteps: undefined as number | undefined,
  dietCalories: undefined as number | undefined,
  medicationName: '',
  medicationDose: '',
  medicationTime: ''
})

const rules = {
  glucoseValue: [
    { required: true, message: '请录入血糖值', trigger: 'blur' },
    { type: 'number', min: 0.6, max: 50, message: '血糖范围 0.6-50.0 mmol/L', trigger: 'blur' }
  ],
  glucosePeriod: [{ required: true, message: '请选择时段', trigger: 'change' }],
  systolicBp: [{ type: 'number', min: 50, max: 300, message: '收缩压范围 50-300 mmHg', trigger: 'blur' }],
  diastolicBp: [{ type: 'number', min: 30, max: 200, message: '舒张压范围 30-200 mmHg', trigger: 'blur' }],
  heartRate: [{ type: 'number', min: 30, max: 300, message: '心率范围 30-300 bpm', trigger: 'blur' }],
  weight: [{ type: 'number', min: 20, max: 300, message: '体重范围 20-300 kg', trigger: 'blur' }]
}

const submitData = async () => {
  await formRef.value.validate()
  isSubmitting.value = true
  try {
    const { data } = await request.post('/api/v1/patient/health-data/upload', formData)
    if (data.code === 200) {
      ElMessage.success('体征数据记录成功！系统已进行智能评估。')
    } else {
      ElMessage.error(data.msg || '上报失败')
    }
  } catch (error) {
    ElMessage.error('上报请求异常，请重试')
  } finally {
    isSubmitting.value = false
  }
}

const fetchPlans = async () => {
  try {
    const { data } = await request.get('/api/v1/patient/followup-plan/list')
    if (data.code === 200) {
      // 过滤出状态为 0 (待执行) 的计划
      pendingPlans.value = data.data.filter((p: any) => p.status === 0)
    }
  } catch (error) {
    console.error('获取随访任务失败', error)
  }
}

onMounted(() => {
  fetchPlans()
})
</script>

<style scoped>
.health-upload-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.health-upload-card {
  width: 100%;
  flex: 1;
  border-radius: 24px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.health-upload-card :deep(.el-card__body) {
  padding: 16px 20px;
}
.push-cards {
  width: 100%;
  margin-bottom: 16px;
}
.push-card {
  padding: 12px 16px;
  margin-bottom: 10px;
  border-radius: 16px;
  background: rgba(230, 162, 60, 0.06);
  border-left: 3px solid #E6A23C;
  box-shadow: var(--ag-shadow);
  transition: all 0.25s ease;
}
.push-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(12, 13, 13, 0.08);
}
.push-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}
.push-icon {
  color: #E6A23C;
  font-size: 16px;
}
.push-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--ag-text-primary);
}
.push-content {
  font-size: 13px;
  color: var(--ag-text-secondary);
  line-height: 1.6;
  white-space: pre-line;
}

.health-upload-card :deep(.el-form-item) {
  margin-bottom: 12px;
}
.health-upload-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--ag-shadow-hover);
}
.ref-range {
  display: inline-block;
  font-size: 11px;
  font-weight: 400;
  color: var(--ag-text-secondary);
  background: var(--ag-bg-subtle);
  padding: 0 5px;
  border-radius: 4px;
  margin-left: 2px;
  vertical-align: middle;
}
.submit-btn {
  width: 100%;
  border-radius: 8px;
  font-weight: bold;
  margin-top: 8px;
}
.card-header {
  font-size: 18px;
  font-weight: 700;
}
</style>
