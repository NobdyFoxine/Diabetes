<template>
  <div class="login-container">
    <!-- 医疗元素散布背景 -->
    <div class="medical-bg">
      <span class="med-icon" style="top:8%;left:5%;font-size:36px;animation-delay:0s">🩺</span>
      <span class="med-icon" style="top:15%;left:75%;font-size:28px;animation-delay:1s">💊</span>
      <span class="med-icon" style="top:70%;left:10%;font-size:42px;animation-delay:2s">🏥</span>
      <span class="med-icon" style="top:80%;left:80%;font-size:32px;animation-delay:0.5s">💉</span>
      <span class="med-icon" style="top:25%;left:88%;font-size:24px;animation-delay:3s">📊</span>
      <span class="med-icon" style="top:55%;left:85%;font-size:38px;animation-delay:1.5s">🔬</span>
      <span class="med-icon" style="top:40%;left:5%;font-size:30px;animation-delay:2.5s">❤️</span>
      <span class="med-icon" style="top:90%;left:45%;font-size:26px;animation-delay:0.8s">🩹</span>
      <span class="med-icon" style="top:5%;left:40%;font-size:34px;animation-delay:1.8s">🫀</span>
      <span class="med-icon" style="top:60%;left:25%;font-size:22px;animation-delay:3.5s">💧</span>
      <span class="med-icon" style="top:35%;left:70%;font-size:40px;animation-delay:0.3s">🩸</span>
      <span class="med-icon" style="top:50%;left:50%;font-size:20px;animation-delay:2.8s">⚕️</span>
      <span class="med-icon" style="top:75%;left:60%;font-size:30px;animation-delay:1.2s">🧬</span>
      <span class="med-icon" style="top:20%;left:30%;font-size:26px;animation-delay:4s">📋</span>
      <span class="med-icon" style="top:85%;left:25%;font-size:34px;animation-delay:2.2s">🫁</span>
      <span class="med-icon" style="top:45%;left:92%;font-size:28px;animation-delay:3.2s">💊</span>
    </div>
    <div class="login-box">
      <div class="login-header">
        <div class="logo-icon-wrapper">
          <el-icon class="logo-icon" :size="32"><FirstAidKit/></el-icon>
        </div>
        <h2>糖尿病院外管理系统</h2>
        <h2>DiabetesHealthSystem</h2>
      </div>

      <el-form :model="loginForm" :rules="rules" ref="formRef" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" :prefix-icon="User" clearable />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password @keyup.enter="isRegister ? handleRegister() : handleLogin()" />
        </el-form-item>

        <el-form-item v-if="isRegister" prop="realName">
          <el-input v-model="loginForm.realName" placeholder="请输入真实姓名" :prefix-icon="User" />
        </el-form-item>

        <el-button type="primary" class="login-btn" :loading="loading" @click="isRegister ? handleRegister() : handleLogin()">
          {{ isRegister ? '注 册' : '登 录' }}
        </el-button>

        <div class="toggle-mode">
          {{ isRegister ? '已有账号？' : '没有账号？' }}
          <a @click="isRegister = !isRegister; loginForm.realName = ''">{{ isRegister ? '去登录' : '立即注册' }}</a>
        </div>
      </el-form>

      
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { User, Lock, FirstAidKit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const emit = defineEmits(['success'])
const formRef = ref()
const loading = ref(false)
const isRegister = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  realName: ''
})

const rules = {
  username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const { data } = await request.post('/api/v1/auth/login', { username: loginForm.username, password: loginForm.password })
    if (data.code === 200) {
      ElMessage.success(`欢迎回来, ${data.data.realName}`)
      localStorage.setItem('token', data.data.token)
      localStorage.setItem('userInfo', JSON.stringify(data.data))
      emit('success', data.data)
    } else {
      ElMessage.error(data.msg || '登录失败')
    }
  } catch (error) {
    ElMessage.error('网络异常或系统内部错误')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!loginForm.realName) { ElMessage.warning('请输入真实姓名'); return }
  await formRef.value.validate()
  loading.value = true
  try {
    const { data } = await request.post('/api/v1/auth/register', { username: loginForm.username, password: loginForm.password, realName: loginForm.realName })
    if (data.code === 200) {
      ElMessage.success('注册成功，请登录')
      isRegister.value = false
    } else {
      ElMessage.error(data.msg || '注册失败')
    }
  } catch (error) {
    ElMessage.error('网络异常')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0f2847 0%, #1a3a5c 50%, #0f2847 100%);
  position: relative;
  overflow: hidden;
}

/* ======== 医疗元素散布背景 ======== */
.medical-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.med-icon {
  position: absolute;
  display: inline-block;
  opacity: 0.12;
  filter: grayscale(50%);
  animation: medFloat 12s ease-in-out infinite;
}

.med-icon:nth-child(odd) {
  animation-duration: 14s;
  animation-direction: reverse;
}

.med-icon:nth-child(3n) {
  animation-duration: 10s;
}

.med-icon:nth-child(5n+2) {
  animation-duration: 16s;
}

@keyframes medFloat {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
    opacity: 0.08;
  }
  25% {
    transform: translate(12px, -18px) rotate(5deg);
    opacity: 0.16;
  }
  50% {
    transform: translate(-8px, -30px) rotate(-3deg);
    opacity: 0.1;
  }
  75% {
    transform: translate(6px, -10px) rotate(2deg);
    opacity: 0.14;
  }
}

/* ======== 脉搏光晕（容器层） ======== */
.login-container::before {
  content: '';
  position: absolute;
  width: 700px;
  height: 700px;
  background: radial-gradient(circle, rgba(25, 200, 83, 0.12) 0%, transparent 70%);
  border-radius: 50%;
  top: -350px;
  right: -350px;
  animation: glowPulse 8s ease-in-out infinite;
  pointer-events: none;
}

.login-container::after {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.08) 0%, transparent 70%);
  border-radius: 50%;
  bottom: -250px;
  left: -250px;
  animation: glowPulse 6s ease-in-out infinite reverse;
  pointer-events: none;
}

@keyframes glowPulse {
  0%, 100% { transform: scale(1); opacity: 0.4; }
  50% { transform: scale(1.15); opacity: 0.7; }
}

.login-box {
  width: 420px;
  max-width: 90vw;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(24px) saturate(120%);
  -webkit-backdrop-filter: blur(24px) saturate(120%);
  padding: 48px 40px 40px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.25), inset 0 1px 0 rgba(255, 255, 255, 0.2);
  z-index: 1;
  animation: slideUp 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #19C853 0%, #15a645 100%);
  border-radius: 16px;
  margin-bottom: 16px;
  box-shadow: 0 8px 24px rgba(25, 200, 83, 0.3);
}

.logo-icon {
  color: white;
}

.login-header h2 {
  margin: 0;
  color: #ffffff;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.login-header p {
  color: rgba(255, 255, 255, 0.75);
  font-size: 13px;
  margin-top: 8px;
  font-weight: 400;
}

.toggle-mode {
  text-align: center;
  margin-top: 14px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}
.toggle-mode a {
  color: #19C853;
  cursor: pointer;
  margin-left: 4px;
}
.toggle-mode a:hover { text-decoration: underline; }

.login-btn {
  width: 100%;
  margin-top: 8px;
  height: 44px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
}

.demo-tips {
  margin-top: 28px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.tips-title {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 10px;
  font-weight: 600;
}

.tips-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 6px;
  padding: 4px 0;
}

.tips-item:last-child {
  margin-bottom: 0;
}

.tips-item span {
  font-family: 'SF Mono', Consolas, Monaco, monospace;
}

/* 表单项样式优化 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.7);
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.18);
  border-color: rgba(255, 255, 255, 0.25);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.22);
  border-color: rgba(25, 200, 83, 0.5);
  box-shadow: 0 4px 20px rgba(25, 200, 83, 0.25);
}

:deep(.el-input__inner) {
  color: #ffffff;
}

:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

:deep(.el-input .el-icon) {
  color: rgba(255, 255, 255, 0.5);
}
</style>
