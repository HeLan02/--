import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import FreeQueryView from '../views/FreeQueryView.vue'
import RecommendView from '../views/RecommendView.vue'
import ReservationApplyView from '../views/ReservationApplyView.vue'
import MyReservationsView from '../views/MyReservationsView.vue'
import AdminPendingView from '../views/AdminPendingView.vue'

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/free', name: 'free', component: FreeQueryView },
  { path: '/recommend', name: 'recommend', component: RecommendView },
  { path: '/reservation/apply', name: 'reservation-apply', component: ReservationApplyView },
  { path: '/reservation/my', name: 'my-reservations', component: MyReservationsView },
  { path: '/admin/pending', name: 'admin-pending', component: AdminPendingView },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})

