import { defineStore } from 'pinia'

export const useSessionStore = defineStore('session', {
  state: () => ({
    userId: localStorage.getItem('cfs_userId') || '',
    userName: localStorage.getItem('cfs_userName') || '',
    adminUser: localStorage.getItem('cfs_adminUser') || 'admin',
    adminPass: localStorage.getItem('cfs_adminPass') || '123456',
  }),
  actions: {
    setUser(userId, userName) {
      this.userId = userId || ''
      this.userName = userName || ''
      localStorage.setItem('cfs_userId', this.userId)
      localStorage.setItem('cfs_userName', this.userName)
    },
    setAdmin(adminUser, adminPass) {
      this.adminUser = adminUser || ''
      this.adminPass = adminPass || ''
      localStorage.setItem('cfs_adminUser', this.adminUser)
      localStorage.setItem('cfs_adminPass', this.adminPass)
    },
  },
})

