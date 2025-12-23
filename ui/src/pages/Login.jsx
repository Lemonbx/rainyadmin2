import { useState } from 'react'
import { Form, Input, Button, Card, Typography, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import useUserStore from '../store/useUserStore'

const { Title, Text } = Typography

export default function Login() {
    const navigate = useNavigate()
    const location = useLocation()
    const login = useUserStore((state) => state.login)
    const [loading, setLoading] = useState(false)

    const onFinish = async (values) => {
        setLoading(true)
        try {
            const success = await login(values)
            if (success) {
                message.success('登录成功，欢迎回来！ (｡•̀ᴗ-)✧')
                const from = location.state?.from?.pathname || '/home'
                navigate(from, { replace: true })
            }
        } catch (error) {
            console.error(error)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div style={{ 
            height: '100vh', 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center',
            background: 'linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%)' 
        }}>
            <Card 
                style={{ 
                    width: 380, 
                    borderRadius: 16, 
                    boxShadow: '0 8px 24px rgba(0,0,0,0.12)',
                    background: 'rgba(255, 255, 255, 0.95)',
                    backdropFilter: 'blur(10px)'
                }}
                bordered={false}
            >
                <div style={{ textAlign: 'center', marginBottom: 32, marginTop: 12 }}>
                    <Title level={2} style={{ marginBottom: 4, color: '#1f2937' }}>Rainy Admin</Title>
                    <Text type="secondary" style={{ fontSize: 14 }}>
                        Hi~ (∠・ω&lt; )⌒☆ 请登录
                    </Text>
                </div>
                
                <Form
                    name="login_form"
                    onFinish={onFinish}
                    size="large"
                    layout="vertical"
                >
                    <Form.Item
                        name="username"
                        rules={[{ required: true, message: '请输入用户名喵！' }]}
                    >
                        <Input 
                            prefix={<UserOutlined />} 
                            placeholder="用户名" 
                        />
                    </Form.Item>

                    <Form.Item
                        name="password"
                        rules={[{ required: true, message: '请输入密码喵！' }]}
                    >
                        <Input.Password 
                            prefix={<LockOutlined />} 
                            placeholder="密码" 
                        />
                    </Form.Item>

                    <Form.Item style={{ marginBottom: 12 }}>
                        <Button 
                            type="primary" 
                            htmlType="submit" 
                            block 
                            loading={loading}
                        >
                            登 录
                        </Button>
                    </Form.Item>
                </Form>
            </Card>
        </div>
    )
}
