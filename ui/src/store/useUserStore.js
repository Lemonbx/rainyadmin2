import { create } from 'zustand'
import request from '../utils/request'

const useUserStore = create((set, get) => ({

    token: localStorage.getItem('token') || null,
    userInfo: null,
    isChecking: true, // 初始化时是否正在校验 Token

    setToken: (token) => {
        localStorage.setItem('token', token)
        set({ token })
    },

    logout: async (invokeApi = true) => {
        if (invokeApi) {
            try {
                await request.get('/a/auth/logout')
            } catch (error) {
                console.error('Logout failed:', error)
            }
        }
        localStorage.removeItem('token')
        set({ token: null, userInfo: null, isChecking: false })
    },

    initAuth: async () => {
        const { token, logout } = get()

        if (!token) {
            set({ isChecking: false })
            return false
        }

        try {
            const userData = await request.get('/a/auth/userInfo')

            // 成功拿回数据 (request.js 已经解包了 data，这里直接用)
            set({ userInfo: userData, isChecking: false })
            return true

        } catch (error) {
            console.error('初始化校验失败:', error)

            logout()

            return false
        }
    },
    login: async ({ username, password }) => {
        try {
            const data = await request.post('/a/auth/login', {
                username,
                password
            })
            get().setToken(data)
            await get().initAuth()
            return true
        } catch (error) {
            return false
        }
    }
}))

export default useUserStore