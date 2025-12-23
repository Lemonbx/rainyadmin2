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
    Tree
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
import { handleTree } from '../../utils/index'
import Permission from '../../components/Permission'

const SysRole = () => {
    const queryClient = useQueryClient()
    const [searchForm] = Form.useForm()
    const [form] = Form.useForm()
    
    // 分页状态
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10
    })

    // 搜索参数状态
    const [searchParams, setSearchParams] = useState({})

    // 弹窗状态
    const [modalVisible, setModalVisible] = useState(false)
    const [modalTitle, setModalTitle] = useState('新增角色')
    const [editingId, setEditingId] = useState(null)
    
    // 菜单选择状态
    const [checkedKeys, setCheckedKeys] = useState([]) // UI 显示用（仅叶子节点）
    const [submitMenuIds, setSubmitMenuIds] = useState([]) // 提交用（所有节点）

    // --- Queries ---

    // 获取所有菜单列表
    const { data: menuTree } = useQuery({
        queryKey: ['sysMenusAll'],
        queryFn: async () => {
            const res = await request.get('/a/sysmenu/')
            return handleTree(res || [])
        },
    })

    // 获取角色详情
    const { data: roleDetail, isLoading: isDetailLoading } = useQuery({
        queryKey: ['sysRole', editingId],
        queryFn: async () => {
            const res = await request.get(`/a/sysrole/${editingId}`)
            return res
        },
        enabled: !!editingId && modalVisible,
    })

    // 获取角色列表
    const { data, isLoading, isFetching } = useQuery({
        queryKey: ['sysRoles', pagination.current, pagination.pageSize, searchParams],
        queryFn: async () => {
            const res = await request.get('/a/sysrole/', {
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

    // 删除角色
    const deleteMutation = useMutation({
        mutationFn: (id) => request.delete(`/a/sysrole/${id}`),
        onSuccess: () => {
            message.success('删除成功')
            queryClient.invalidateQueries({ queryKey: ['sysRoles'] })
        },
        onError: (error) => {
            console.error('Delete failed:', error)
        }
    })

    // 新增角色
    const createMutation = useMutation({
        mutationFn: (values) => request.post('/a/sysrole/', values),
        onSuccess: () => {
            message.success('创建成功')
            setModalVisible(false)
            queryClient.invalidateQueries({ queryKey: ['sysRoles'] })
        },
        onError: (error) => {
            console.error('Create failed:', error)
        }
    })

    // 更新角色
    const updateMutation = useMutation({
        mutationFn: ({ id, values }) => request.put('/a/sysrole/', { id, ...values }),
        onSuccess: () => {
            message.success('更新成功')
            setModalVisible(false)
            queryClient.invalidateQueries({ queryKey: ['sysRoles'] })
        },
        onError: (error) => {
            console.error('Update failed:', error)
        }
    })

    // --- Event Handlers ---

    const handleSearch = (values) => {
        setPagination(prev => ({ ...prev, current: 1 }))
        setSearchParams(values)
    }

    const handleReset = () => {
        searchForm.resetFields()
        setPagination(prev => ({ ...prev, current: 1 }))
        setSearchParams({})
    }

    const handleTableChange = (newPagination) => {
        setPagination({
            current: newPagination.current,
            pageSize: newPagination.pageSize
        })
    }

    const handleAdd = () => {
        setModalTitle('新增角色')
        setEditingId(null)
        setModalVisible(true)
        setCheckedKeys([])
        setSubmitMenuIds([])
    }

    const handleEdit = (record) => {
        setModalTitle('编辑角色')
        setEditingId(record.id)
        setModalVisible(true)
    }

    // 辅助函数：筛选出所有的叶子节点 ID
    const filterLeafIds = (ids, treeData) => {
        const leafIds = []
        const idSet = new Set(ids)
        
        const traverse = (nodes) => {
            nodes.forEach(node => {
                if (!node.children || node.children.length === 0) {
                    if (idSet.has(node.id)) {
                        leafIds.push(node.id)
                    }
                } else {
                    traverse(node.children)
                }
            })
        }
        traverse(treeData || [])
        return leafIds
    }

    // 处理菜单选中
    const onCheck = (checkedKeysValue, info) => {
        setCheckedKeys(checkedKeysValue)
        setSubmitMenuIds([...checkedKeysValue, ...info.halfCheckedKeys])
    }

    useEffect(() => {
        if (modalVisible) {
            form.resetFields()
            if (roleDetail) {
                form.setFieldsValue({
                    ...roleDetail
                })
                const fullIds = roleDetail.menu || []
                setSubmitMenuIds(fullIds)
                const leafIds = filterLeafIds(fullIds, menuTree)
                setCheckedKeys(leafIds)
            } else {
                // 新增模式，清空
                setCheckedKeys([])
                setSubmitMenuIds([])
            }
        }
    }, [modalVisible, roleDetail, form, menuTree])

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields()
            // 合并菜单ID
            const payload = { ...values, menu: submitMenuIds }

            if (editingId) {
                updateMutation.mutate({ id: editingId, values: payload })
            } else {
                createMutation.mutate(payload)
            }
        } catch (error) {
            console.error('Validate failed:', error)
        }
    }

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },
        {
            title: '角色名称',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: '角色标识',
            dataIndex: 'rolekey',
            key: 'rolekey',
        },
        {
            title: '操作',
            key: 'action',
            width: 200,
            render: (_, record) => (
                <Space size="middle">
                    <Permission authority="sys:role:save">
                        <Button 
                            type="link" 
                            icon={<EditOutlined />} 
                            onClick={() => handleEdit(record)}
                        >
                            编辑
                        </Button>
                    </Permission>
                    <Permission authority="sys:role:delete">
                        <Popconfirm
                            title="确定删除该角色吗？"
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
            <div style={{ marginBottom: 24 }}>
                <Form name="searchForm" form={searchForm} onFinish={handleSearch} layout="inline">
                    <Form.Item name="name" label="角色名称">
                        <Input placeholder="请输入角色名称" allowClear style={{ width: 200 }} />
                    </Form.Item>
                    <Form.Item name="rolekey" label="角色标识">
                        <Input placeholder="请输入角色标识" allowClear style={{ width: 200 }} />
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

            <div style={{ marginBottom: 16 }}>
                <Permission authority="sys:role:save">
                    <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                        新增角色
                    </Button>
                </Permission>
            </div>

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

            <Modal
                title={modalTitle}
                open={modalVisible}
                onOk={handleSubmit}
                onCancel={() => setModalVisible(false)}
                confirmLoading={createMutation.isPending || updateMutation.isPending}
            >
                <Spin spinning={isDetailLoading && !!editingId}>
                    <Form
                        name="roleForm"
                        form={form}
                        layout="vertical"
                        preserve={false}
                    >
                        <Form.Item
                            name="name"
                            label="角色名称"
                            rules={[{ required: true, message: '请输入角色名称' }]}
                        >
                            <Input placeholder="请输入角色名称"/>
                        </Form.Item>
                        
                        <Form.Item
                            name="rolekey"
                            label="角色标识"
                            rules={[{ required: true, message: '请输入角色标识' }]}
                        >
                            <Input placeholder="请输入角色标识" />
                        </Form.Item>

                        <Form.Item label="菜单权限">
                            <Tree
                                checkable
                                checkedKeys={checkedKeys}
                                onCheck={onCheck}
                                treeData={menuTree}
                                fieldNames={{ title: 'name', key: 'id', children: 'children' }}
                                height={400}
                                style={{ border: '1px solid #d9d9d9', borderRadius: 4, padding: 8 }}
                            />
                        </Form.Item>
                    </Form>
                </Spin>
            </Modal>
        </div>
    )
}

export default SysRole
