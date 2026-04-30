<template>
  <el-container style="min-height: 100vh">
    <el-header class="header">
      <el-button text @click="goHome">返回首页</el-button>
      <div class="title">我的预约</div>
    </el-header>

    <el-main style="max-width: 1100px; margin: 0 auto; width: 100%">
      <el-card>
        <el-alert
          v-if="!store.userId"
          type="warning"
          show-icon
          :closable="false"
          title="你还没有设置用户信息，请先回到首页填写学/工号与姓名。"
        />

        <el-form :inline="true" style="margin-top: 12px">
          <el-form-item label="状态">
            <el-select v-model="status" clearable style="width: 180px">
              <el-option label="待审批" :value="0" />
              <el-option label="已通过" :value="1" />
              <el-option label="已驳回" :value="2" />
              <el-option label="已取消" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">刷新</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="rows" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
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
          <el-table-column prop="attendeeCount" label="人数" width="80" />
          <el-table-column label="状态" width="110">
            <template #default="{ row }">
              <el-tag :type="tagType(row.status)">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 0 || row.status === 1"
                type="danger"
                size="small"
                @click="cancel(row)"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { http, setUserHeaders } from '../api/http'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const store = useSessionStore()

const status = ref()
const rows = ref([])
const loading = ref(false)

function goHome() {
  router.push('/')
}

function statusText(s) {
  return ['待审批', '已通过', '已驳回', '已取消'][s] || String(s)
}
function tagType(s) {
  if (s === 1) return 'success'
  if (s === 2) return 'warning'
  if (s === 3) return 'info'
  return ''
}

async function load() {
  if (!store.userId) return
  loading.value = true
  try {
    const config = setUserHeaders({}, { userId: store.userId })
    const { data } = await http.get('/api/reservations/my', { ...config, params: { status: status.value } })
    rows.value = data
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

async function cancel(row) {
  await ElMessageBox.confirm(`确认取消预约 #${row.id} 吗？`, '提示', { type: 'warning' })
  try {
    const config = setUserHeaders({}, { userId: store.userId })
    await http.put(`/api/reservations/${row.id}/cancel`, null, config)
    ElMessage.success('已取消')
    await load()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '取消失败')
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

