import React, { useState, useEffect } from 'react'
import { 
    Table, 
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
    Radio,
    InputNumber,
    TreeSelect,
    Tag
} from 'antd'
import { 
    SearchOutlined, 
    ReloadOutlined, 
    PlusOutlined, 
    EditOutlined, 
    DeleteOutlined,
    FolderOutlined,
    FileOutlined,
    AppstoreOutlined
} from '@ant-design/icons'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import request from '../../utils/request'
import { handleTree } from '../../utils/index'
import Permission from '../../components/Permission'

const SysMenu = () => {
    const queryClient = useQueryClient()
    const [searchForm] = Form.useForm()
    const [form] = Form.useForm()
    
    // 搜索参数状态
    const [searchParams, setSearchParams] = useState({})

    // 弹窗状态
    const [modalVisible, setModalVisible] = useState(false)
    const [modalTitle, setModalTitle] = useState('新增菜单')
    const [editingId, setEditingId] = useState(null)
    const [parentId, setParentId] = useState(null) // 用于“新增子菜单”时预填

    // --- Queries ---

    // 获取菜单列表 (树形)
    const { data: menuTree, isLoading, isFetching } = useQuery({
        queryKey: ['sysMenus', searchParams],
        queryFn: async () => {
            const res = await request.get('/a/sysmenu/', {
                params: {
                    ...searchParams
                }
            })
            console.log('API Response:', res)
            const tree = handleTree(res || [])
            console.log('Handled Tree:', tree)
            return tree
        },
    })

    // 获取菜单详情
    const { data: menuDetail, isLoading: isDetailLoading } = useQuery({
        queryKey: ['sysMenu', editingId],
        queryFn: async () => {
            const res = await request.get(`/a/sysmenu/${editingId}`)
            return res
        },
        enabled: !!editingId && modalVisible,
    })

    // --- Mutations ---

    const deleteMutation = useMutation({
        mutationFn: (id) => request.delete(`/a/sysmenu/${id}`),
        onSuccess: () => {
            message.success('删除成功')
            queryClient.invalidateQueries({ queryKey: ['sysMenus'] })
        },
        onError: (error) => {
            console.error('Delete failed:', error)
        }
    })

    const createMutation = useMutation({
        mutationFn: (values) => {
            return request.post('/a/sysmenu/', values)
        },
        onSuccess: () => {
            message.success('创建成功')
            setModalVisible(false)
            queryClient.invalidateQueries({ queryKey: ['sysMenus'] })
        },
        onError: (error) => {
            console.error('Create failed:', error)
        }
    })

    const updateMutation = useMutation({
        mutationFn: ({ id, values }) => {
            const payload = { id, ...values }
            return request.put('/a/sysmenu/', payload)
        },
        onSuccess: () => {
            message.success('更新成功')
            setModalVisible(false)
            queryClient.invalidateQueries({ queryKey: ['sysMenus'] })
        },
        onError: (error) => {
            console.error('Update failed:', error)
        }
    })

    // --- Event Handlers ---

    const handleSearch = (values) => {
        setSearchParams(values)
    }

    const handleReset = () => {
        searchForm.resetFields()
        setSearchParams({})
    }

    const handleAdd = (pid = null) => {
        setModalTitle('新增菜单')
        setEditingId(null)
        setParentId(pid)
        setModalVisible(true)
    }

    const handleEdit = (record) => {
        setModalTitle('编辑菜单')
        setEditingId(record.id)
        setParentId(null)
        setModalVisible(true)
    }

    const handleDelete = (id) => {
        deleteMutation.mutate(id)
    }

    // 处理表单回显
    useEffect(() => {
        if (modalVisible) {
            form.resetFields()
            if (menuDetail) {
                form.setFieldsValue({
                    ...menuDetail
                })
            } else {
                // 新增模式，如果有预设父ID
                if (parentId) {
                    form.setFieldsValue({ parentId: parentId, type: 2, sort: 1 }) // 默认为菜单类型
                } else {
                    form.setFieldsValue({ type: 1, sort: 1 }) // 默认为目录类型
                }
            }
        }
    }, [modalVisible, menuDetail, form, parentId])

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

    const columns = [
        {
            title: '名称',
            dataIndex: 'name',
            key: 'name',
            width: 250,
        },
        {
            title: '图标',
            dataIndex: 'logo',
            key: 'logo',
            width: 80,
            render: (text) => text ? <i className={text} /> : '-'
        },
        {
            title: '排序',
            dataIndex: 'sort',
            key: 'sort',
            width: 80,
        },
        {
            title: '权限标识',
            dataIndex: 'perms',
            key: 'perms',
            ellipsis: true,
        },
        {
            title: '组件路径',
            dataIndex: 'component',
            key: 'component',
            ellipsis: true,
        },
        {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            width: 100,
            render: (type) => {
                const map = {
                    1: <Tag color="blue">目录</Tag>,
                    2: <Tag color="green">菜单</Tag>,
                    3: <Tag color="orange">按钮</Tag>
                }
                return map[type] || type
            }
        },
        {
            title: '操作',
            key: 'action',
            width: 250,
            render: (_, record) => (
                <Space size="small">
                     <Permission authority="sys:menu:save">
                        <Button 
                            type="link" 
                            size="small"
                            icon={<EditOutlined />} 
                            onClick={() => handleEdit(record)}
                        >
                            编辑
                        </Button>
                    </Permission>
                    {record.type != 3 &&<Permission authority="sys:menu:save">
                        <Button 
                            type="link" 
                            size="small"
                            icon={<PlusOutlined />} 
                            onClick={() => handleAdd(record.id)}
                        >
                            新增
                        </Button>
                    </Permission>}
                    {!record.children && <Permission authority="sys:menu:delete">
                        <Popconfirm
                            title="确定删除该菜单吗？"
                            onConfirm={() => handleDelete(record.id)}
                            okText="确定"
                            cancelText="取消"
                        >
                            <Button type="link" danger size="small" icon={<DeleteOutlined />}>
                                删除
                            </Button>
                        </Popconfirm>
                    </Permission>}
                </Space>
            ),
        },
    ]

    // 递归处理 TreeSelect 数据，禁用自身及子级作为父级（防止死循环，仅在编辑时）
    const processTreeData = (data, disabledId) => {
        return data.map(item => ({
            title: item.name,
            value: item.id,
            key: item.id,
            disabled: item.id === disabledId,
            children: item.children ? processTreeData(item.children, disabledId) : []
        }))
    }

    return (
        <div style={{ padding: 24 }}>
            <div style={{ marginBottom: 24 }}>
                <Form name="searchForm" form={searchForm} onFinish={handleSearch} layout="inline">
                    <Form.Item name="name" label="菜单名称">
                        <Input placeholder="请输入菜单名称" allowClear style={{ width: 200 }} />
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
                <Permission authority="sys:menu:save">
                    <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd()}>
                        新增菜单
                    </Button>
                </Permission>
            </div>

            <Table
                columns={columns}
                dataSource={menuTree || []}
                rowKey="id"
                pagination={false}
                loading={isLoading || isFetching}
                expandable={{
                    defaultExpandAllRows: false, // 默认不全展开，数据量大时卡顿
                }}
            />

            <Modal
                title={modalTitle}
                open={modalVisible}
                onOk={handleSubmit}
                onCancel={() => setModalVisible(false)}
                confirmLoading={createMutation.isPending || updateMutation.isPending}
                width={600}
            >
                <Spin spinning={isDetailLoading && !!editingId}>
                    <Form
                        name="menuForm"
                        form={form}
                        layout="vertical"
                        preserve={false}
                        initialValues={{ type: 1, sort: 1 }}
                    >
                         <Form.Item label="上级菜单" name="parentId">
                            <TreeSelect
                                style={{ width: '100%' }}
                                dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                                treeData={processTreeData(menuTree || [], editingId)}
                                placeholder="请选择上级菜单（留空为顶级）"
                                treeDefaultExpandAll
                                allowClear
                            />
                        </Form.Item>

                        <Form.Item label="菜单类型" name="type" rules={[{ required: true }]}>
                            <Radio.Group>
                                <Radio value={1}>目录</Radio>
                                <Radio value={2}>菜单</Radio>
                                <Radio value={3}>按钮</Radio>
                            </Radio.Group>
                        </Form.Item>

                        <Row gutter={16}>
                             <Col span={12}>
                                <Form.Item
                                    name="name"
                                    label="菜单名称"
                                    rules={[{ required: true, message: '请输入菜单名称' }]}
                                >
                                    <Input placeholder="请输入菜单名称"/>
                                </Form.Item>
                             </Col>
                             <Col span={12}>
                                <Form.Item
                                    name="sort"
                                    label="显示排序"
                                    rules={[{ required: true, message: '请输入排序' }]}
                                >
                                    <InputNumber style={{ width: '100%' }} min={0} />
                                </Form.Item>
                             </Col>
                        </Row>

                        <Form.Item
                            noStyle
                            shouldUpdate={(prev, current) => prev.type !== current.type}
                        >
                            {({ getFieldValue }) => {
                                const type = getFieldValue('type')
                                return type !== 3 ? (
                                    <>
                                        <Row gutter={16}>
                                            <Col span={12}>
                                                <Form.Item name="logo" label="菜单图标">
                                                    <Input placeholder="请输入图标类名" />
                                                </Form.Item>
                                            </Col>
                                            <Col span={12}>
                                                <Form.Item name="path" label="路由地址">
                                                    <Input placeholder="请输入路由地址" />
                                                </Form.Item>
                                            </Col>
                                        </Row>
                                        {type === 2 && (
                                            <Form.Item name="component" label="组件路径">
                                                <Input placeholder="请输入组件路径 (e.g. sys/User)" />
                                            </Form.Item>
                                        )}
                                    </>
                                ) : null
                            }}
                        </Form.Item>

                        <Form.Item
                            name="perms"
                            label="权限标识"
                            extra="控制器中定义的权限标识，如：sys:user:query"
                        >
                            <Input placeholder="请输入权限标识" />
                        </Form.Item>

                    </Form>
                </Spin>
            </Modal>
        </div>
    )
}

export default SysMenu
