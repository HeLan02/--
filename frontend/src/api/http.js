import axios from 'axios'

export const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000,
})

export function setUserHeaders(config, { userId, userName }) {
  config.headers = config.headers || {}
  if (userId) config.headers['X-User-Id'] = userId
  return config
}

export function setAdminBasicAuth(config, { username, password }) {
  if (!username || !password) return config
  config.headers = config.headers || {}
  const token = btoa(`${username}:${password}`)
  config.headers.Authorization = `Basic ${token}`
  return config
}

