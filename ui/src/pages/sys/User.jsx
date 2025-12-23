import React, { useState, useEffect } from 'react'
import { 
    Table, 
    Card, 
    Form, 
    Input, 
    Button, 
    Space, 
    Row, 
    Col, 
    Modal, 
    message,
    Popconfirm,
    Spin,
    Select
} from 'antd'
import { 
    SearchOutlined, 
    ReloadOutlined, 
    PlusOutlined, 
    EditOutlined, 
    DeleteOutlined 
} from '@ant-design/icons'
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query'
import request from '../../utils/request'
import Permission from '../../components/Permission'

const SysUser = () => {
    const queryClient = useQueryClient()
    const [searchForm] = Form.useForm()
    const [form] = Form.useForm()
    
    // 分页状态
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10
    })

    // 搜索参数状态（用于触发 query 更新）
    const [searchParams, setSearchParams] = useState({})

    // 弹窗状态
    const [modalVisible, setModalVisible] = useState(false)
    const [modalTitle, setModalTitle] = useState('新增用户')
    const [editingId, setEditingId] = useState(null)

    // --- Queries ---

    // 获取用户详情
    const { data: userDetail, isLoading: isDetailLoading } = useQuery({
        queryKey: ['sysUser', editingId],
        queryFn: async () => {
            const res = await request.get(`/a/sysusers/${editingId}`)
            return res
        },
        enabled: !!editingId && modalVisible,
    })

    // 获取所有角色列表（用于下拉选择）
    const { data: roleList, isLoading: isRoleLoading } = useQuery({
        queryKey: ['sysRoles'],
        queryFn: async () => {
            const res = await request.get('/a/sysrole/', {
                params: {
                    page: 1,
                    size: 9999 // 获取所有角色
                }
            })
            return res
        },
        enabled: modalVisible, // 弹窗打开时才加载
    })

    // 获取用户列表
    const { data, isLoading, isFetching } = useQuery({
        queryKey: ['sysUsers', pagination.current, pagination.pageSize, searchParams],
        queryFn: async () => {
            const res = await request.get('/a/sysusers/', {
                params: {
                    page: pagination.current,
                    size: pagination.pageSize,
                    ...searchParams
                }
            })
            return res
        },
        placeholderData: keepPreviousData,
    })

    // --- Mutations ---

    // 删除用户
    const deleteMutation = useMutation({
        mutationFn: (id) => request.delete(`/a/sysusers/${id}`),
        onSuccess: () => {
            message.success('删除成功')
            queryClient.invalidateQueries({ queryKey: ['sysUsers'] })
        },
        onError: (error) => {
            console.error('Delete failed:', error)
        }
    })

    // 新增用户
    const createMutation = useMutation({
        mutationFn: (values) => request.post('/a/sysusers/', values),
        onSuccess: () => {
            message.success('创建成功')
            setModalVisible(false)
            queryClient.invalidateQueries({ queryKey: ['sysUsers'] })
        },
        onError: (error) => {
            console.error('Create failed:', error)
        }
    })

    // 更新用户
    const updateMutation = useMutation({
        mutationFn: ({ id, values }) => request.put('/a/sysusers/', { id, ...values }),
        onSuccess: () => {
            message.success('更新成功')
            setModalVisible(false)
            queryClient.invalidateQueries({ queryKey: ['sysUsers'] })
        },
        onError: (error) => {
            console.error('Update failed:', error)
        }
    })

    // --- Event Handlers ---

    // 处理搜索
    const handleSearch = (values) => {
        setPagination(prev => ({ ...prev, current: 1 }))
        setSearchParams(values)
    }

    // 处理重置
    const handleReset = () => {
        searchForm.resetFields()
        setPagination(prev => ({ ...prev, current: 1 }))
        setSearchParams({})
    }

    // 处理表格变动
    const handleTableChange = (newPagination) => {
        setPagination({
            current: newPagination.current,
            pageSize: newPagination.pageSize
        })
    }

    // 打开新增弹窗
    const handleAdd = () => {
        setModalTitle('新增用户')
        setEditingId(null)
        setModalVisible(true)
    }

    // 打开编辑弹窗
    const handleEdit = (record) => {
        setModalTitle('编辑用户')
        setEditingId(record.id)
        setModalVisible(true)
    }

    // 监听弹窗显示状态和详情数据，处理表单回显
    useEffect(() => {
        if (modalVisible) {
            form.resetFields()
            if (userDetail) {
                form.setFieldsValue({
                    ...userDetail
                })
            }
        }
    }, [modalVisible, userDetail, form])

    // 处理删除
    const handleDelete = (id) => {
        deleteMutation.mutate(id)
    }

    // 处理提交
    const handleSubmit = async () => {
        try {
            const values = await form.validateFields()
            if (editingId) {
                updateMutation.mutate({ id: editingId, values })
            } else {
                createMutation.mutate(values)
            }
        } catch (error) {
            console.error('Validate failed:', error)
        }
    }

    // 表格列定义
    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },
        {
            title: '用户名',
            dataIndex: 'username',
            key: 'username',
        },
        {
            title: '昵称',
            dataIndex: 'nickname',
            key: 'nickname',
        },
        {
            title: '操作',
            key: 'action',
            width: 200,
            render: (_, record) => (
                <Space size="middle">
                    <Permission authority="sys:user:save">
                        <Button 
                            type="link" 
                            icon={<EditOutlined />} 
                            onClick={() => handleEdit(record)}
                        >
                            编辑
                        </Button>
                    </Permission>
                    <Permission authority="sys:user:delete">
                        <Popconfirm
                            title="确定删除该用户吗？"
                            onConfirm={() => handleDelete(record.id)}
                            okText="确定"
                            cancelText="取消"
                        >
                            <Button type="link" danger icon={<DeleteOutlined />}>
                                删除
                            </Button>
                        </Popconfirm>
                    </Permission>
                </Space>
            ),
        },
    ]

    return (
        <div style={{ padding: 24 }}>
            {/* 搜索区域 */}
            <div style={{ marginBottom: 24 }}>
                <Form name="searchForm" form={searchForm} onFinish={handleSearch} layout="inline">
                    <Form.Item name="username" label="用户名">
                        <Input placeholder="请输入用户名" allowClear style={{ width: 200 }} />
                    </Form.Item>
                    <Form.Item name="nickname" label="昵称">
                        <Input placeholder="请输入昵称" allowClear style={{ width: 200 }} />
                    </Form.Item>
                    <Form.Item>
                        <Space>
                            <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                                查询
                            </Button>
                            <Button onClick={handleReset} icon={<ReloadOutlined />}>
                                重置
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </div>

            {/* 操作按钮区 */}
            <div style={{ marginBottom: 16 }}>
                <Permission authority="sys:user:save">
                    <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                        新增用户
                    </Button>
                </Permission>
            </div>

            {/* 表格区域 */}
            <Table
                columns={columns}
                dataSource={data?.data || []}
                rowKey="id"
                pagination={{
                    current: pagination.current,
                    pageSize: pagination.pageSize,
                    total: data?.total || 0,
                    showSizeChanger: true,
                    showQuickJumper: true,
                    showTotal: (total) => `共 ${total} 条`
                }}
                loading={isLoading || isFetching}
                onChange={handleTableChange}
            />

            {/* 新增/编辑弹窗 */}
            <Modal
                title={modalTitle}
                open={modalVisible}
                onOk={handleSubmit}
                onCancel={() => setModalVisible(false)}
                confirmLoading={createMutation.isPending || updateMutation.isPending}
            >
                <Spin spinning={isDetailLoading && !!editingId}>
                    <Form
                        name="userForm"
                        form={form}
                        layout="vertical"
                        preserve={false}
                    >
                        <Form.Item
                            name="username"
                            label="用户名"
                            rules={[{ required: true, message: '请输入用户名' }]}
                        >
                            <Input placeholder="请输入用户名"/>
                        </Form.Item>
                        
                        <Form.Item
                            name="nickname"
                            label="昵称"
                            rules={[{ required: true, message: '请输入昵称' }]}
                        >
                            <Input placeholder="请输入昵称" />
                        </Form.Item>

                        <Form.Item
                            name="role"
                            label="角色"
                        >
                            <Select
                                mode="multiple"
                                placeholder="请选择角色"
                                loading={isRoleLoading}
                                optionFilterProp="label"
                                options={roleList?.data?.map(role => ({
                                    label: `${role.name} (${role.rolekey})`,
                                    value: role.id
                                })) || []}
                            />
                        </Form.Item>

                        <Form.Item
                            name="password"
                            label={editingId ? "密码 (留空则不修改)" : "密码"}
                            rules={[{ required: !editingId, message: '请输入密码' }]}
                        >
                            <Input.Password placeholder="请输入密码" />
                        </Form.Item>
                    </Form>
                </Spin>
            </Modal>
        </div>
    )
}

export default SysUser