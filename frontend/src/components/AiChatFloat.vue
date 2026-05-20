<template>
  <div class="ai-float-container">
    <transition name="fade-scale">
      <div v-if="isOpen" class="ai-panel glass-panel">
        <div class="ai-panel-header">
          <span><el-icon><ChatDotRound /></el-icon> AI 糖尿病管理助手</span>
          <el-icon class="ai-close" @click="isOpen = false"><Close /></el-icon>
        </div>
        <div class="ai-messages" ref="msgBox">
          <div class="ai-msg ai-msg-bot">
            <div class="ai-bubble">您好，我是糖尿病管理助手 🤖<br>可以问我血糖、饮食、运动、用药等方面的问题。</div>
          </div>
          <div v-for="(m, i) in chatHistory" :key="i" :class="['ai-msg', m.role === 'user' ? 'ai-msg-user' : 'ai-msg-bot']">
            <div class="ai-bubble">{{ m.text }}</div>
          </div>
        </div>
        <div class="ai-quick-asks">
          <el-tag v-for="q in quickQuestions" :key="q" size="small" @click="askQuestion(q)" class="ai-tag">{{ q }}</el-tag>
        </div>
        <div class="ai-input-row">
          <el-input v-model="inputText" placeholder="输入您的问题..." @keyup.enter="sendMessage" size="small" />
          <el-button type="primary" size="small" @click="sendMessage" circle><el-icon><Promotion /></el-icon></el-button>
        </div>
      </div>
    </transition>
    <div class="ai-fab" @click="isOpen = !isOpen" :class="{ active: isOpen }">
      <el-icon :size="24"><ChatDotRound v-if="!isOpen" /><Close v-else /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ChatDotRound, Close, Promotion } from '@element-plus/icons-vue'

const isOpen = ref(false)
const inputText = ref('')
const chatHistory = ref<{ role: string; text: string }[]>([])
const msgBox = ref<HTMLElement | null>(null)

const quickQuestions = ['血糖多少算正常？', '怎么降空腹血糖？', '糖尿病能吃什么？', '运动多久合适？']

const askQuestion = (q: string) => {
  inputText.value = q
  sendMessage()
}

import request from '../utils/request'

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text) return
  chatHistory.value.push({ role: 'user', text })
  inputText.value = ''
  nextTick(() => scrollBottom())

  const msgPayload = chatHistory.value.map(m => ({ role: m.role, content: m.text }))
  try {
    const { data } = await request.post('/api/v1/ai/chat', { messages: msgPayload })
    if (data.code === 200) {
      chatHistory.value.push({ role: 'bot', text: data.data.reply })
    } else {
      chatHistory.value.push({ role: 'bot', text: localReply(text) + '\n\n（大模型暂不可用，以上为本地知识库回复）' })
    }
  } catch (e) {
    chatHistory.value.push({ role: 'bot', text: localReply(text) + '\n\n（网络异常，以上为本地知识库回复）' })
  }
  nextTick(() => scrollBottom())
}

const localReply = (q: string): string => {
  const kw = q.toLowerCase()
  if (kw.includes('正常') || kw.includes('标准') || kw.includes('范围')) return '📊 正常参考：\n空腹血糖 3.9-6.1 | 餐后2h ≤7.8 | HbA1c 4-6% | 血压 90-140/60-90 mmHg | BMI 18.5-23.9'
  if (kw.includes('降') && kw.includes('血糖')) return '💡 降糖建议：控制晚餐碳水 | 餐后散步30分钟 | 遵医嘱用药 | 晨起记录空腹血糖趋势'
  if (kw.includes('吃') || kw.includes('饮食')) return '🥗 饮食：主食定量粗细搭配 | 绿叶菜500g+/天 | 优质蛋白(鱼/禽/豆) | 少食多餐 | 推荐燕麦/荞麦/苦瓜'
  if (kw.includes('运动')) return '🏃 每周≥150分钟中强度运动 | 快走/游泳/骑行 | 30-45分钟/次 | 避免空腹运动防低血糖'
  if (kw.includes('低血糖')) return '⚠️ 低血糖急救(15-15法则)：立即15g速效碳水(半杯果汁/3块方糖) → 15分钟后复测 → 仍低则重复 → 严重时立即就医'
  if (kw.includes('高血糖')) return '🚨 高血糖：>16.7查尿酮体 | 多饮水 | 按时用药 | 酮体阳性+呕吐立即就医'
  if (kw.includes('药')) return '💊 按时按量服药 | 二甲双胍餐中服 | 胰岛素未开封冷藏(2-8℃) | 记录用药+血糖反应'
  if (kw.includes('血压')) return '❤️ 血压目标<130/80 | 低盐<5g/天 | 规律运动控体重 | 早晚自测记录'
  return '我目前可回答血糖、饮食、运动、用药、血压、低血糖/高血糖处理等问题。如需专业建议，请通过在线咨询联系医生。'
}

const scrollBottom = () => {
  if (msgBox.value) msgBox.value.scrollTop = msgBox.value.scrollHeight
}
</script>

<style scoped>
.ai-float-container { position: fixed; bottom: 24px; left: 24px; z-index: 1000; }
.ai-fab {
  width: 52px; height: 52px; border-radius: 50%;
  background: linear-gradient(135deg, #19C853, #15a645);
  color: #fff; display: flex; align-items: center; justify-content: center;
  cursor: pointer; box-shadow: 0 4px 16px rgba(25,200,83,0.35);
  transition: all 0.3s cubic-bezier(0.34,1.56,0.64,1);
}
.ai-fab:hover { transform: scale(1.1); box-shadow: 0 6px 24px rgba(25,200,83,0.45); }
.ai-fab.active { background: #fff; color: #19C853; box-shadow: 0 4px 16px rgba(0,0,0,0.15); }

.ai-panel {
  position: absolute; bottom: 64px; left: 0;
  width: 380px; max-height: 480px;
  background: var(--ag-surface);
  border-radius: 20px;
  border: 1px solid var(--ag-border);
  box-shadow: 0 12px 40px rgba(0,0,0,0.12);
  display: flex; flex-direction: column; overflow: hidden;
}
.ai-panel-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 14px 16px; font-weight: 700; font-size: 15px;
  border-bottom: 1px solid var(--ag-border);
  flex-shrink: 0;
}
.ai-close { cursor: pointer; color: var(--ag-text-secondary); }
.ai-close:hover { color: var(--ag-text-primary); }

.ai-messages { flex: 1; padding: 12px 14px; overflow-y: auto; max-height: 260px; }
.ai-msg { margin-bottom: 10px; }
.ai-msg-bot .ai-bubble {
  background: var(--ag-bg-subtle); color: var(--ag-text-primary);
  border-radius: 14px 14px 14px 4px; padding: 10px 14px;
  font-size: 13px; line-height: 1.6; display: inline-block; max-width: 90%;
  white-space: pre-line;
}
.ai-msg-user { text-align: right; }
.ai-msg-user .ai-bubble {
  background: var(--ag-primary); color: #fff;
  border-radius: 14px 14px 4px 14px; padding: 10px 14px;
  font-size: 13px; line-height: 1.6; display: inline-block; max-width: 85%;
}

.ai-quick-asks { padding: 6px 14px; display: flex; flex-wrap: wrap; gap: 6px; flex-shrink: 0; }
.ai-tag { cursor: pointer; }
.ai-tag:hover { background: var(--ag-primary); color: #fff; border-color: var(--ag-primary); }

.ai-input-row { display: flex; gap: 8px; padding: 10px 14px; border-top: 1px solid var(--ag-border); flex-shrink: 0; }
.ai-input-row .el-input { flex: 1; }

.fade-scale-enter-active, .fade-scale-leave-active { transition: all 0.3s cubic-bezier(0.34,1.56,0.64,1); }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: translateY(10px) scale(0.95); }

.glass-panel { backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); }
</style>
