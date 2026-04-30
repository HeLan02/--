<template>
  <el-container style="min-height: 100vh">
    <el-header class="header">
      <el-button text @click="goHome">返回首页</el-button>
      <div class="title">自习室推荐</div>
    </el-header>

    <el-main style="max-width: 1100px; margin: 0 auto; width: 100%">
      <el-card>
        <el-form :inline="true" label-width="90px">
          <el-form-item label="日期">
            <el-date-picker v-model="date" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item label="开始节次">
            <el-input-number v-model="startPeriod" :min="1" :max="30" />
          </el-form-item>
          <el-form-item label="结束节次">
            <el-input-number v-model="endPeriod" :min="1" :max="30" />
          </el-form-item>
          <el-form-item label="教学楼">
            <el-select v-model="buildingId" clearable style="width: 160px">
              <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="最小座位">
            <el-input-number v-model="minCapacity" :min="1" :max="500" />
          </el-form-item>
          <el-form-item label="周次">
            <el-input-number v-model="week" :min="1" :max="30" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="query">推荐</el-button>
          </el-form-item>
        </el-form>

        <el-divider />

        <el-table :data="rows" style="width: 100%" v-loading="loading">
          <el-table-column prop="classroom.buildingName" label="教学楼" width="120" />
          <el-table-column prop="classroom.roomNumber" label="教室" width="120" />
          <el-table-column prop="classroom.capacity" label="座位数" width="100" />
          <el-table-column prop="score" label="推荐分" width="100" />
          <el-table-column label="设备">
            <template #default="{ row }">
              <el-tag v-if="row.classroom.hasMultimedia" type="success" style="margin-right: 6px">多媒体</el-tag>
              <el-tag v-if="row.classroom.hasAc" type="info">空调</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="toApply(row.classroom)">立即预约</el-button>
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
import { ElMessage } from 'element-plus'
import { http } from '../api/http'

const router = useRouter()

const buildings = ref([])
const rows = ref([])
const loading = ref(false)

const date = ref(new Date().toISOString().slice(0, 10))
const startPeriod = ref(1)
const endPeriod = ref(2)
const buildingId = ref()
const minCapacity = ref(40)
const week = ref()

function goHome() {
  router.push('/')
}

async function loadBuildings() {
  const { data } = await http.get('/api/buildings')
  buildings.value = data
}

async function query() {
  if (startPeriod.value > endPeriod.value) {
    ElMessage.error('开始节次不能大于结束节次')
    return
  }
  loading.value = true
  try {
    const { data } = await http.get('/api/classrooms/recommend', {
      params: {
        date: date.value,
        startPeriod: startPeriod.value,
        endPeriod: endPeriod.value,
        buildingId: buildingId.value,
        minCapacity: minCapacity.value,
        week: week.value,
      },
    })
    rows.value = data
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '推荐失败')
  } finally {
    loading.value = false
  }
}

function toApply(classroom) {
  router.push({
    path: '/reservation/apply',
    query: {
      classroomId: classroom.id,
      buildingId: buildingId.value ?? classroom.buildingId,
      date: date.value,
      startPeriod: startPeriod.value,
      endPeriod: endPeriod.value,
    },
  })
}

onMounted(async () => {
  await loadBuildings()
  await query()
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

