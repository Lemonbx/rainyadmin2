import React from 'react'
import useUserStore from '../store/useUserStore'

/**
 * 权限控制组件
 * @param {string | string[]} authority - 需要的权限，可以是单个字符串或字符串数组（满足其一即可）
 * @param {React.ReactNode} children - 拥有权限时显示的内容
 * @param {React.ReactNode} fallback - 无权限时显示的内容（可选）
 */
const Permission = ({ authority, children, fallback = null }) => {
    const { userInfo } = useUserStore()
    const permissions = userInfo?.permissions || []

    // 如果没有传入权限要求，直接显示
    if (!authority) {
        return children
    }

    const hasPermission = () => {
        if (Array.isArray(authority)) {
            // 只要包含其中任意一个权限即可
            return authority.some(p => permissions.includes(p))
        }
        return permissions.includes(authority)
    }

    return hasPermission() ? children : fallback
}

export default Permission
