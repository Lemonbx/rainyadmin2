import axios from 'axios'
import useUserStore from '../store/useUserStore'
import { message } from 'antd'

const ResultCode = {
    SUCCESS: 1,
    ERROR: 400,
    NOAUTH: 401,
    EXCEPTION: 500
}

let isReloginShowing = false

const service = axios.create({
    baseURL: '/api',
    timeout: 60000,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})
service.interceptors.request.use(
    (config) => {
        // 从 Zustand Store 获取 Token
        const token = useUserStore.getState().token
        if (token) {
            config.headers.rainytoken = token
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

service.interceptors.response.use(
    (response) => {
        const res = response.data
        if (res.code === ResultCode.SUCCESS) {
            return res.data
        }
        if (res.code === ResultCode.NOAUTH) {
            if (!isReloginShowing) {
                isReloginShowing = true
                message.warning(res.msg || '登录过期，请重新登录')
                useUserStore.getState().logout(false)
                setTimeout(() => {
                    isReloginShowing = false
                }, 3000)
            }
            return Promise.reject(new Error(res.msg || '未授权，请重新登录'))
        }
        const errorMsg = res.msg || '系统未知错误'
        message.error(errorMsg)

        return Promise.reject(new Error(errorMsg))
    },
    (error) => {
        let errorText = '网络异常，请稍后重试'
        const { response } = error

        if (response) {
            if (response.status === 401) {
                if (!isReloginShowing) {
                    isReloginShowing = true
                    useUserStore.getState().logout(false)
                    setTimeout(() => {
                        isReloginShowing = false
                    }, 3000)
                }
            }

            switch (response.status) {
                case 404: errorText = '接口不存在'; break;
                case 500: errorText = '服务器内部错误'; break;
                case 403: errorText = '拒绝访问'; break;
                default: errorText = `请求失败 (${response.status})`;
            }
        } else if (error.message.includes('timeout')) {
            errorText = '请求超时'
        }

        message.error(errorText)

        return Promise.reject(error)
    }
)

export default service