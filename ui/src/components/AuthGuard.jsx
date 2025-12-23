import { useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import useUserStore from '../store/useUserStore'

const AuthGuard = ({ children }) => {
    const navigate = useNavigate()
    const location = useLocation()
    const { token, isChecking, initAuth } = useUserStore()

    useEffect(() => {
        initAuth()
    }, [initAuth])

    useEffect(() => {
        if (!isChecking && !token) {
            navigate('/login', { state: { from: location }, replace: true })
        }
    }, [isChecking, token, navigate, location])

    if (isChecking) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                Loading...
            </div>
        )
    }

    if (!token) {
        return null
    }

    return children
}

export default AuthGuard
