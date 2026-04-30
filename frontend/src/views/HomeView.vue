<template>
  <el-container style="min-height: 100vh">
    <el-header class="header">
      <div class="title">教室空闲状态查询系统</div>
      <el-menu mode="horizontal" :default-active="active" @select="onSelect">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/free">空闲查询</el-menu-item>
        <el-menu-item index="/recommend">自习室推荐</el-menu-item>
        <el-menu-item index="/reservation/apply">预约申请</el-menu-item>
        <el-menu-item index="/reservation/my">我的预约</el-menu-item>
        <el-menu-item index="/admin/pending">审批管理</el-menu-item>
      </el-menu>
    </el-header>

    <el-main style="max-width: 1100px; margin: 0 auto; width: 100%">
      <el-card>
        <template #header>快速开始</template>
        <el-alert
          type="info"
          show-icon
          :closable="false"
          title="提示：请选择学生登录或管理员登录。"
        />

        <el-divider />

        <div style="display: flex; justify-content: center; margin-bottom: 16px">
          <el-segmented v-model="mode" :options="modeOptions" />
        </div>

        <el-form v-if="mode === 'student'" label-width="110px" style="max-width: 680px">
          <el-form-item label="学/工号">
            <el-input v-model="userId" placeholder="例如 2023123456" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="userName" placeholder="例如 张三" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveUser">学生登录</el-button>
          </el-form-item>
        </el-form>

        <el-form v-else label-width="110px" style="max-width: 680px">
          <el-form-item label="管理员账号">
            <el-input v-model="adminUser" />
          </el-form-item>
          <el-form-item label="管理员密码">
            <el-input v-model="adminPass" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveAdmin">管理员登录</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { http, setAdminBasicAuth } from '../api/http'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const route = useRoute()
const store = useSessionStore()

const active = computed(() => route.path)

const mode = ref('student')
const modeOptions = [
  { label: '学生登录', value: 'student' },
  { label: '管理员登录', value: 'admin' },
]

const userId = ref(store.userId)
const userName = ref(store.userName)
const adminUser = ref(store.adminUser)
const adminPass = ref(store.adminPass)

function onSelect(path) {
  router.push(path)
}

function saveUser() {
  store.setUser(userId.value, userName.value)
  ElMessage.success('学生登录信息已保存')
}

async function saveAdmin() {
  const config = setAdminBasicAuth({}, { username: adminUser.value, password: adminPass.value })
  try {
    await http.get('/api/reservations/pending', config)
    store.setAdmin(adminUser.value, adminPass.value)
    ElMessage.success('管理员登录成功')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '管理员账号或密码错误')
  }
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  gap: 16px;
}
.title {
  font-weight: 700;
  white-space: nowrap;
}
</style>

