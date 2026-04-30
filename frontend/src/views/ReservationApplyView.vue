<template>
  <el-container style="min-height: 100vh">
    <el-header class="header">
      <el-button text @click="goHome">返回首页</el-button>
      <div class="title">预约申请</div>
    </el-header>

    <el-main style="max-width: 1100px; margin: 0 auto; width: 100%">
      <el-card>
        <el-form label-width="100px" style="max-width: 820px">
          <el-form-item label="日期">
            <el-date-picker v-model="date" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>

          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="开始节次">
                <el-input-number v-model="startPeriod" :min="1" :max="30" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="结束节次">
                <el-input-number v-model="endPeriod" :min="1" :max="30" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="教学楼(可选)">
            <el-select v-model="buildingId" clearable style="width: 220px">
              <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
            </el-select>
            <el-button style="margin-left: 12px" @click="loadFree">刷新空闲教室</el-button>
          </el-form-item>

          <el-form-item label="选择教室">
            <el-select v-model="classroomId" filterable style="width: 420px" placeholder="先刷新空闲教室">
              <el-option
                v-for="c in freeRooms"
                :key="c.id"
                :label="`${c.buildingName}-${c.roomNumber}（${c.capacity}座）`"
                :value="c.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="用途">
            <el-input v-model="purpose" placeholder="例如 班会/学习小组/自习" />
          </el-form-item>
          <el-form-item label="预计人数">
            <el-input-number v-model="attendeeCount" :min="1" :max="500" />
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="contact" placeholder="手机号/微信等" />
          </el-form-item>

          <el-divider />

          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="submit">提交预约</el-button>
            <el-button @click="toMy">去“我的预约”</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { http, setUserHeaders } from '../api/http'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const route = useRoute()
const store = useSessionStore()

const buildings = ref([])
const freeRooms = ref([])

const date = ref(route.query.date || new Date().toISOString().slice(0, 10))
const startPeriod = ref(Number(route.query.startPeriod || 1))
const endPeriod = ref(Number(route.query.endPeriod || 2))
const buildingId = ref(route.query.buildingId ? Number(route.query.buildingId) : undefined)
const classroomId = ref(route.query.classroomId ? Number(route.query.classroomId) : undefined)

const purpose = ref('自习')
const attendeeCount = ref(10)
const contact = ref('')
const submitting = ref(false)

function goHome() {
  router.push('/')
}

function toMy() {
  router.push('/reservation/my')
}

async function loadBuildings() {
  const { data } = await http.get('/api/buildings')
  buildings.value = data
}

async function loadFree() {
  if (startPeriod.value > endPeriod.value) {
    ElMessage.error('开始节次不能大于结束节次')
    return
  }
  try {
    const { data } = await http.get('/api/classrooms/free', {
      params: {
        date: date.value,
        startPeriod: startPeriod.value,
        endPeriod: endPeriod.value,
        buildingId: buildingId.value,
      },
    })
    freeRooms.value = data
    if (classroomId.value && !freeRooms.value.find((c) => c.id === classroomId.value)) {
      classroomId.value = undefined
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '获取空闲教室失败')
  }
}

watch(
  () => buildingId.value,
  async () => {
    classroomId.value = undefined
    await loadFree()
  },
)

async function submit() {
  if (!store.userId || !store.userName) {
    ElMessage.error('请先在首页保存用户学/工号与姓名')
    return
  }
  if (!classroomId.value) {
    ElMessage.error('请选择教室')
    return
  }
  if (startPeriod.value > endPeriod.value) {
    ElMessage.error('开始节次不能大于结束节次')
    return
  }
  submitting.value = true
  try {
    const config = setUserHeaders({}, { userId: store.userId })
    const { data } = await http.post(
      '/api/reservations',
      {
        userId: store.userId,
        userName: store.userName,
        classroomId: classroomId.value,
        purpose: purpose.value,
        reservationDate: date.value,
        startPeriod: startPeriod.value,
        endPeriod: endPeriod.value,
        attendeeCount: attendeeCount.value,
        contact: contact.value,
      },
      config,
    )
    ElMessage.success(`提交成功，预约ID=${data.id}（待审批）`)
    router.push('/reservation/my')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadBuildings()
  await loadFree()
})
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

