import { useState,createElement,useEffect } from 'react'
import { Layout, Menu, Avatar, Dropdown, Space, theme, Typography,Modal, Form, Input, message } from 'antd'
import { 
    MenuFoldOutlined, 
    MenuUnfoldOutlined, 
    UserOutlined,
    LogoutOutlined,
    ExclamationCircleOutlined,
    LockOutlined
} from '@ant-design/icons'
import { useNavigate, useLocation, Outlet } from 'react-router-dom'
import useUserStore from '../store/useUserStore'
import request from '../utils/request'


const { Header, Sider, Content } = Layout
const { Text } = Typography

const AdminLayout = ({ children }) => {
    const [collapsed, setCollapsed] = useState(false)
    const { token: { colorBgContainer, borderRadiusLG } } = theme.useToken()
    const navigate = useNavigate()
    const location = useLocation()
    const { userInfo, logout } = useUserStore()
    const [openKeys, setOpenKeys] = useState([])

    // 修改密码相关状态
    const [pwdModalVisible, setPwdModalVisible] = useState(false)
    const [pwdForm] = Form.useForm()
    const [pwdLoading, setPwdLoading] = useState(false)

    // 查找当前路径的所有父级菜单 key
    const getOpenKeys = () => {
        if (!userInfo?.menus) return []
        const menus = userInfo.menus
        const current = menus.find(item => item.path === location.pathname)
        if (!current) return []
        
        const keys = []
        let parentId = current.parentId
        while (parentId) {
            const parent = menus.find(item => item.id === parentId)
            if (parent) {
                keys.push(parent.path || String(parent.id))
                parentId = parent.parentId
            } else {
                break
            }
        }
        return keys
    }

    // 监听路由或用户信息变化，自动展开菜单
    useEffect(() => {
        const keys = getOpenKeys()
        if (keys.length > 0) {
            setOpenKeys(keys)
        }
    }, [location.pathname, userInfo])

    const getMenuItems = (menus) => {
        if (!menus) return []
        const buildTree = (items, parentId = null) => {
            return items
                .filter(item => item.parentId === parentId)
                .map(item => {
                    const children = buildTree(items, item.id)
                    return {
                        key: item.path || String(item.id),
                        label: item.name || '未命名',
                        icon: null,
                        children: children.length > 0 ? children : null,
                        onClick: () => {
                            if (item.path && !children.length) {
                                navigate(item.path)
                            }
                        }
                    }
                })
        }

        // 注意：这里假设根节点的 parentId 是 null
        return buildTree(userInfo.menus || [], null)
    }

    const menuItems = getMenuItems(userInfo?.menus)

    const handleLogout = () => {
        Modal.confirm({
            title: '确认退出',
            icon: <ExclamationCircleOutlined />,
            content: '确定要退出登录吗？',
            okText: '确认',
            cancelText: '取消',
            onOk: () => logout(),
        })
    }

    const handleUpdatePwd = async () => {
        try {
            const values = await pwdForm.validateFields()
            setPwdLoading(true)
            await request.post('/a/auth/resetPwd', values)
            message.success('密码修改成功')
            setPwdModalVisible(false)
        } catch (error) {
            console.error('Update password failed:', error)
        } finally {
            setPwdLoading(false)
        }
    }

    const userMenu = {
        items: [
            {
                key: 'password',
                icon: <LockOutlined />,
                label: '修改密码',
                onClick: () => {
                    pwdForm.resetFields()
                    setPwdModalVisible(true)
                }
            },
            {
                key: 'logout',
                icon: <LogoutOutlined />,
                label: '退出登录',
                onClick: handleLogout
            }
        ]
    }

    return (
        <Layout style={{ height: '100vh', overflow: 'hidden' }}>
            <Sider trigger={null} collapsible collapsed={collapsed} theme="light" style={{
                boxShadow: '2px 0 8px 0 rgba(29,35,41,.05)',
                zIndex: 10,
                overflowY: 'auto'
            }}>
                <div style={{ 
                    height: 64, 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center',
                    borderBottom: '1px solid #f0f0f0'
                }}>
                    <Text strong style={{ fontSize: 18, color: '#1890ff', whiteSpace: 'nowrap', overflow: 'hidden' }}>
                        {collapsed ? 'RA' : 'Rainy Admin'}
                    </Text>
                </div>
                <Menu
                    theme="light"
                    mode="inline"
                    defaultSelectedKeys={[location.pathname]}
                    selectedKeys={[location.pathname]}
                    openKeys={openKeys}
                    onOpenChange={setOpenKeys}
                    items={menuItems}
                    style={{ borderRight: 0 }}
                />
            </Sider>
            <Layout>
                <Header style={{ 
                    padding: '0 24px', 
                    background: colorBgContainer,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    boxShadow: '0 1px 4px rgba(0,21,41,.08)',
                    zIndex: 9
                }}>
                    {createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
                        className: 'trigger',
                        onClick: () => setCollapsed(!collapsed),
                        style: { fontSize: '18px', cursor: 'pointer', transition: 'color 0.3s' }
                    })}
                    
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                        <Dropdown menu={userMenu} placement="bottomRight">
                            <Space style={{ cursor: 'pointer' }}>
                                <Avatar icon={<UserOutlined />} src={userInfo?.avatar} style={{ backgroundColor: '#8ec5fc' }} />
                                <Text>{userInfo?.nickname || userInfo?.username || 'Admin'}</Text>
                            </Space>
                        </Dropdown>
                    </div>
                </Header>
                <Content
                    style={{
                        padding: 24,
                        minHeight: 280,
                        background: colorBgContainer,
                        borderRadius: borderRadiusLG,
                        overflow: 'auto'
                    }}
                >
                    {children}
                </Content>
            </Layout>

            <Modal
                title="修改密码"
                open={pwdModalVisible}
                onOk={handleUpdatePwd}
                onCancel={() => setPwdModalVisible(false)}
                confirmLoading={pwdLoading}
            >
                <Form
                    form={pwdForm}
                    layout="vertical"
                    name="pwdForm"
                >
                    <Form.Item
                        name="oldPwd"
                        label="旧密码"
                        rules={[{ required: true, message: '请输入旧密码' }]}
                    >
                        <Input.Password placeholder="请输入旧密码" />
                    </Form.Item>
                    <Form.Item
                        name="newPwd"
                        label="新密码"
                        rules={[
                            { required: true, message: '请输入新密码' },
                            { min: 6, message: '密码长度不能少于6位' }
                        ]}
                    >
                        <Input.Password placeholder="请输入新密码" />
                    </Form.Item>
                    <Form.Item
                        name="confirmPwd"
                        label="确认新密码"
                        dependencies={['newPwd']}
                        rules={[
                            { required: true, message: '请确认新密码' },
                            ({ getFieldValue }) => ({
                                validator(_, value) {
                                    if (!value || getFieldValue('newPwd') === value) {
                                        return Promise.resolve()
                                    }
                                    return Promise.reject(new Error('两次输入的密码不一致'))
                                },
                            }),
                        ]}
                    >
                        <Input.Password placeholder="请再次输入新密码" />
                    </Form.Item>
                </Form>
            </Modal>
        </Layout>
    )
}


export default AdminLayout
