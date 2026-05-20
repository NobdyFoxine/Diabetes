<template>
  <el-card class="chat-card" shadow="hover">
    <template #header>
      <div class="chat-header">
        <span><el-icon><ChatDotRound /></el-icon> 在线咨询</span>
        <el-select v-model="chatTargetId" placeholder="选择对话对象" size="small" style="width:180px" @change="loadMessages" filterable>
          <el-option v-for="u in contactList" :key="u.id" :label="`${u.realName} (ID:${u.id})`" :value="u.id" />
        </el-select>
      </div>
    </template>
    <div class="chat-body" ref="chatBody">
      <div v-if="!chatTargetId" class="chat-hint">请选择对话对象开始咨询</div>
      <div v-else v-for="(m, i) in messages" :key="i" :class="['msg-row', m.senderId === myUserId ? 'msg-mine' : 'msg-other']">
        <div class="msg-bubble">{{ m.content }}</div>
        <div class="msg-time">{{ formatTime(m.createTime) }}</div>
      </div>
    </div>
    <div class="chat-input" v-if="chatTargetId">
      <el-input v-model="inputText" placeholder="输入消息..." @keyup.enter="sendMessage" />
      <el-button type="primary" size="small" @click="sendMessage" :loading="sending">发送</el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const myUserId = userInfo.userId

const messages = ref<any[]>([])
const inputText = ref('')
const chatTargetId = ref<number | null>(null)
const contactList = ref<any[]>([])
const sending = ref(false)
const chatBody = ref<HTMLElement | null>(null)
let pollTimer: ReturnType<typeof setInterval> | null = null

const loadContacts = async () => {
  try {
    const isDoctor = [1, 2, 3].includes(userInfo.roleType)
    const endpoint = isDoctor ? '/api/v1/doctor/followup-plan/patients' : ''
    if (isDoctor) {
      const { data } = await request.get(endpoint)
      if (data.code === 200) contactList.value = data.data
    } else {
      // Patient: hardcode the mock doctor as contact
      contactList.value = [{ id: 20001, realName: '王医生' }]
    }
  } catch (e) { /* ignore */ }
}

const loadMessages = async () => {
  if (!chatTargetId.value) return
  try {
    const { data } = await request.get(`/api/v1/message/chat/${chatTargetId.value}`)
    if (data.code === 200) {
      messages.value = data.data
      nextTick(() => scrollToBottom())
    }
  } catch (e) { /* ignore */ }
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || !chatTargetId.value) return
  sending.value = true
  try {
    const { data } = await request.post('/api/v1/message/send', {
      receiverId: chatTargetId.value,
      content: text
    })
    if (data.code === 200) {
      inputText.value = ''
      loadMessages()
    } else {
      ElMessage.error(data.msg || '发送失败')
    }
  } catch (e) {
    ElMessage.error('网络异常')
  } finally { sending.value = false }
}

const scrollToBottom = () => {
  if (chatBody.value) chatBody.value.scrollTop = chatBody.value.scrollHeight
}

const formatTime = (iso: string) => {
  if (!iso) return ''
  return iso.substring(11, 16)
}

onMounted(() => {
  loadContacts()
  pollTimer = setInterval(() => { if (chatTargetId.value) loadMessages() }, 5000)
})
</script>

<style scoped>
.chat-card { border-radius: 24px; height: 100%; display: flex; flex-direction: column; }
.chat-card :deep(.el-card__body) { flex: 1; display: flex; flex-direction: column; padding: 12px 16px; }
.chat-header { display: flex; justify-content: space-between; align-items: center; font-size: 14px; font-weight: 700; }
.chat-body { flex: 1; overflow-y: auto; padding: 8px 0; max-height: 300px; }
.chat-hint { text-align: center; color: var(--ag-text-secondary); padding: 40px 0; font-size: 13px; }
.msg-row { margin-bottom: 10px; }
.msg-mine { text-align: right; }
.msg-bubble {
  display: inline-block; max-width: 80%; padding: 8px 12px; border-radius: 14px;
  font-size: 13px; line-height: 1.5; word-break: break-all;
}
.msg-mine .msg-bubble { background: var(--ag-primary); color: #fff; border-bottom-right-radius: 4px; }
.msg-other .msg-bubble { background: var(--ag-bg-subtle); color: var(--ag-text-primary); border-bottom-left-radius: 4px; }
.msg-time { font-size: 10px; color: var(--ag-text-secondary); margin-top: 2px; }
.chat-input { display: flex; gap: 8px; padding-top: 8px; border-top: 1px solid var(--ag-border); }
.chat-input .el-input { flex: 1; }
</style>
