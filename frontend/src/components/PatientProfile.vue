<template>
  <div class="patient-profile">
    <el-card class="profile-card" shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><User /></el-icon>
            个人健康档案
          </span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="195px" class="profile-form">
        <!-- 基础信息 -->
        <el-divider content-position="left">基础信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                placeholder="选择出生日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 临床信息 -->
        <el-divider content-position="left">临床与病史信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="糖尿病分型" prop="diabetesType">
              <el-select v-model="form.diabetesType" placeholder="请选择" style="width: 100%">
                <el-option label="1型糖尿病" :value="1" />
                <el-option label="2型糖尿病" :value="2" />
                <el-option label="妊娠期糖尿病" :value="3" />
                <el-option label="其他特殊类型" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="并发症史" prop="complications">
              <el-checkbox-group v-model="complicationsList">
                <el-checkbox label="糖尿病视网膜病变">视网膜病变</el-checkbox>
                <el-checkbox label="糖尿病肾病">糖尿病肾病</el-checkbox>
                <el-checkbox label="糖尿病足">糖尿病足</el-checkbox>
                <el-checkbox label="神经病变">神经病变</el-checkbox>
                <el-checkbox label="心血管并发症">心血管并发症</el-checkbox>
                <el-checkbox label="无">无</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 院内检验基线数据 -->
        <el-divider content-position="left">院内检验基线数据</el-divider>

        <!-- 血糖相关 -->
        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="FPG (正常 3.9-6.1 mmol/L)" prop="fpg">
              <el-input-number v-model="form.fpg" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="2hPG (正常 ≤7.8 mmol/L)" prop="twoHpg">
              <el-input-number v-model="form.twoHpg" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="GA (正常 11-17 %)" prop="ga">
              <el-input-number v-model="form.ga" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 血红蛋白 + 血脂 -->
        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="HbA1c (正常 4-6 %)" prop="hba1c">
              <el-input-number v-model="form.hba1c" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="TC (正常 3-5.2 mmol/L)" prop="cholesterol">
              <el-input-number v-model="form.cholesterol" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="TG (正常 0-1.7 mmol/L)" prop="triglyceride">
              <el-input-number v-model="form.triglyceride" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 脂蛋白 + 肾功能 -->
        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="LDL-C (正常 0-3.1 mmol/L)" prop="ldl">
              <el-input-number v-model="form.ldl" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="HDL-C (正常 0.9-2 mmol/L)" prop="hdl">
              <el-input-number v-model="form.hdl" :precision="1" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="Cr (正常 44-133 umol/L)" prop="creatinine">
              <el-input-number v-model="form.creatinine" :precision="0" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="MAU (正常 &lt;30 mg/L)" prop="urinaryMicroalbumin">
              <el-input-number v-model="form.urinaryMicroalbumin" :precision="0" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身高 (BMI 计算)" prop="height">
              <el-input-number v-model="form.height" :precision="0" :step="1" :min="50" :max="250" style="width: 100%" placeholder="cm" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-actions">
          <el-button type="primary" @click="submitForm" :loading="submitting" size="large">保存档案</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const loading = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = reactive({
  idCard: '',
  birthDate: '',
  phone: '',
  diabetesType: 2,
  complications: '',
  fpg: 0,
  twoHpg: 0,
  ga: 0,
  hba1c: 0,
  cholesterol: 0,
  triglyceride: 0,
  ldl: 0,
  hdl: 0,
  creatinine: 0,
  urinaryMicroalbumin: 0,
  height: 0
})

const complicationsList = ref<string[]>([])

// 监听并发症数组变化，同步到字符串形式
watch(complicationsList, (val) => {
  if (val.includes('无') && val.length > 1) {
    complicationsList.value = val.filter(v => v !== '无')
  }
  form.complications = complicationsList.value.join(',')
})

const rules = {
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  fpg: [{ type: 'number', min: 0.6, max: 50, message: '空腹血糖范围 0.6-50.0 mmol/L', trigger: 'blur' }],
  twoHpg: [{ type: 'number', min: 0.6, max: 60, message: '餐后血糖范围 0.6-60.0 mmol/L', trigger: 'blur' }],
  ga: [{ type: 'number', min: 1, max: 50, message: '糖化白蛋白范围 1-50%', trigger: 'blur' }],
  hba1c: [{ type: 'number', min: 2, max: 20, message: 'HbA1c 范围 2-20%', trigger: 'blur' }],
  cholesterol: [{ type: 'number', min: 0.5, max: 25, message: '总胆固醇范围 0.5-25.0 mmol/L', trigger: 'blur' }],
  triglyceride: [{ type: 'number', min: 0, max: 40, message: '甘油三酯范围 0-40.0 mmol/L', trigger: 'blur' }],
  ldl: [{ type: 'number', min: 0, max: 20, message: 'LDL-C 范围 0-20.0 mmol/L', trigger: 'blur' }],
  hdl: [{ type: 'number', min: 0, max: 6, message: 'HDL-C 范围 0-6.0 mmol/L', trigger: 'blur' }],
  creatinine: [{ type: 'number', min: 5, max: 2500, message: '肌酐范围 5-2500 umol/L', trigger: 'blur' }],
  urinaryMicroalbumin: [{ type: 'number', min: 0, max: 6000, message: 'MAU 范围 0-6000 mg/L', trigger: 'blur' }]
}

const fetchProfile = async () => {
  loading.value = true
  try {
    const { data } = await request.get('/api/v1/patient/profile/mine')
    if (data.code === 200 && data.data) {
      Object.assign(form, data.data)
      if (data.data.complications) {
        complicationsList.value = data.data.complications.split(',')
      }
    }
  } catch (error) {
    console.error('获取档案失败', error)
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const { data } = await request.post('/api/v1/patient/profile/update', form)
    if (data.code === 200) {
      ElMessage.success('档案保存成功')
    } else {
      ElMessage.error(data.msg || '保存失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.patient-profile {
  width: 100%;
}
.profile-card {
  border-radius: 24px;
}
.profile-card :deep(.el-card__body) {
  padding: 16px 24px;
}
.card-header {
  display: flex;
  align-items: center;
}
.title {
  font-size: 18px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
}
.profile-form :deep(.el-form-item) {
  margin-bottom: 14px;
}
.profile-form :deep(.el-divider) {
  margin: 12px 0;
}
.form-actions {
  text-align: center;
  margin-top: 16px;
  margin-bottom: 0;
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
</style>
