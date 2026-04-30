<template>
  <el-container style="min-height: 100vh">
    <el-header class="header">
      <el-button text @click="goHome">返回首页</el-button>
      <div class="title">审批管理（管理员）</div>
    </el-header>

    <el-main style="max-width: 1100px; margin: 0 auto; width: 100%">
      <el-card>
        <el-alert
          type="info"
          show-icon
          :closable="false"
          title="提示：使用 BasicAuth 管理员账号访问后端。默认 admin/admin，可在首页修改并保存。"
        />

        <div style="margin-top: 12px">
          <el-button type="primary" @click="load">刷新待审批</el-button>
        </div>

        <el-table :data="rows" style="width: 100%; margin-top: 12px" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="申请人" width="160">
            <template #default="{ row }">{{ row.userName }}（{{ row.userId }}）</template>
          </el-table-column>
          <el-table-column label="教室" min-width="160">
            <template #default="{ row }">
              {{ row.classroom.buildingName }}-{{ row.classroom.roomNumber }}
            </template>
          </el-table-column>
          <el-table-column prop="reservationDate" label="日期" width="120" />
          <el-table-column label="节次" width="120">
            <template #default="{ row }">{{ row.startPeriod }}-{{ row.endPeriod }}</template>
          </el-table-column>
          <el-table-column prop="purpose" label="用途" min-width="160" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="approve(row)">通过</el-button>
              <el-button size="small" type="danger" @click="openReject(row)">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>

    <el-dialog v-model="rejectDialog" title="驳回原因" width="520px">
      <el-input v-model="rejectComment" type="textarea" :rows="4" placeholder="请输入审批意见" />
      <template #footer>
        <el-button @click="rejectDialog = false">取消</el-button>
        <el-button type="danger" @click="reject">确认驳回</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { http, setAdminBasicAuth } from '../api/http'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const store = useSessionStore()

const rows = ref([])
const loading = ref(false)

const rejectDialog = ref(false)
const rejectComment = ref('')
const current = ref()

function goHome() {
  router.push('/')
}

function adminConfig() {
  return setAdminBasicAuth({}, { username: store.adminUser, password: store.adminPass })
}

async function load() {
  loading.value = true
  try {
    const { data } = await http.get('/api/reservations/pending', adminConfig())
    rows.value = data
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '加载失败（请检查管理员账号密码）')
  } finally {
    loading.value = false
  }
}

async function approve(row) {
  try {
    await http.put(`/api/reservations/${row.id}/approve`, null, adminConfig())
    ElMessage.success('已通过')
    await load()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '审批失败')
  }
}

function openReject(row) {
  current.value = row
  rejectComment.value = ''
  rejectDialog.value = true
}

async function reject() {
  if (!rejectComment.value.trim()) {
    ElMessage.error('请输入驳回原因')
    return
  }
  try {
    await http.put(
      `/api/reservations/${current.value.id}/reject`,
      { comment: rejectComment.value },
      adminConfig(),
    )
    ElMessage.success('已驳回')
    rejectDialog.value = false
    await load()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '驳回失败')
  }
}

onMounted(load)
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  gap: 12px;
}
.title {
  font-weight: 700;
}
</style>

