<template>
  <el-card class="article-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span><el-icon><Document /></el-icon> 健康教育文章库</span>
        <el-button type="primary" size="small" @click="fetchArticles" :loading="loading">刷新</el-button>
      </div>
    </template>
    <div class="article-grid" v-loading="loading">
      <div v-for="a in articles" :key="a.id" class="article-item">
        <div class="article-cat">
          <el-tag size="small" type="info">{{ a.category }}</el-tag>
        </div>
        <div class="article-title">{{ a.title }}</div>
        <div class="article-summary">{{ a.summary }}</div>
        <div class="article-footer">
          <span class="article-tags">{{ a.tags }}</span>
          <el-button size="small" type="success" plain @click="pushArticle(a)">推送给患者</el-button>
        </div>
      </div>
      <el-empty v-if="!loading && articles.length === 0" description="暂无健康文章" :image-size="60" />
    </div>

    <el-dialog v-model="pushVisible" title="推送给患者" width="400px">
      <el-form label-width="80px">
        <el-form-item label="选择患者">
          <el-select v-model="pushPatientId" filterable placeholder="选择患者" style="width: 100%">
            <el-option v-for="p in patientList" :key="p.id" :label="`${p.realName} (ID: ${p.id})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="推送内容">
          <el-input v-model="pushContent" type="textarea" rows="3" readonly />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pushVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPush">确认推送</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const articles = ref<any[]>([])
const patientList = ref<any[]>([])
const loading = ref(false)
const pushVisible = ref(false)
const pushPatientId = ref<number | null>(null)
const pushContent = ref('')

const fetchArticles = async () => {
  loading.value = true
  try {
    const { data } = await request.get('/api/v1/doctor/followup-plan/articles')
    if (data.code === 200) articles.value = data.data
  } catch (e) { /* ignore */ } finally { loading.value = false }
}

const pushArticle = (article: any) => {
  pushContent.value = `【${article.title}】\n${article.summary}\n\n—— 来自你的主管医生`
  pushVisible.value = true
  if (patientList.value.length === 0) fetchPatients()
}

const fetchPatients = async () => {
  try {
    const { data } = await request.get('/api/v1/doctor/followup-plan/patients')
    if (data.code === 200) patientList.value = data.data
  } catch (e) { /* ignore */ }
}

const confirmPush = async () => {
  if (!pushPatientId.value) { ElMessage.warning('请选择患者'); return }
  try {
    const { data } = await request.post('/api/v1/doctor/followup-plan/create', {
      patientId: pushPatientId.value,
      planDate: new Date().toISOString().slice(0, 10),
      content: pushContent.value
    })
    if (data.code === 200) {
      ElMessage.success('健康文章已推送至患者')
      pushVisible.value = false
    } else {
      ElMessage.error(data.msg || '推送失败')
    }
  } catch (e) {
    ElMessage.error('网络异常')
  }
}

onMounted(() => fetchArticles())
</script>

<style scoped>
.article-card { border-radius: 24px; }
.card-header { display: flex; justify-content: space-between; align-items: center; font-size: 18px; font-weight: 700; }
.article-grid { max-height: 320px; overflow-y: auto; padding-right: 2px; }
.article-item {
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 16px;
  background: var(--ag-bg-subtle);
  border: 1px solid var(--ag-border);
  transition: all 0.25s ease;
}
.article-item:hover { transform: translateY(-2px); box-shadow: var(--ag-shadow); }
.article-cat { margin-bottom: 4px; }
.article-title { font-size: 14px; font-weight: 600; color: var(--ag-text-primary); margin-bottom: 4px; }
.article-summary { font-size: 12px; color: var(--ag-text-secondary); line-height: 1.5; margin-bottom: 8px; }
.article-footer { display: flex; justify-content: space-between; align-items: center; }
.article-tags { font-size: 11px; color: var(--ag-text-secondary); }
.article-card :deep(.el-card__body) { padding: 16px 20px; }
</style>
