import { PageContainer } from '@ant-design/pro-layout';
import { useRef, useState, useEffect } from 'react';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Button, Modal, Form, Input, message, Popconfirm, Space, Select, DatePicker } from 'antd';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { list3 as listTags, add2 as addTag, edit2 as updateTag, remove4 as deleteTag } from '@/services/test-swagger/tagController';
import { list5 as listSchools } from '@/services/test-swagger/schoolController';
import { useModel } from '@umijs/max';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

const TagList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [editingTag, setEditingTag] = useState<API.Tags>();
  const [selectedRows, setSelectedRows] = useState<API.Tags[]>([]);
  const [form] = Form.useForm();

  // 查询条件状态
  const [searchText, setSearchText] = useState<string>('');
  const [selectedSchool, setSelectedSchool] = useState<number>();
  const [schoolOptions, setSchoolOptions] = useState<{ label: string; value: number; }[]>([]);
  const [serviceType, setServiceType] = useState<number>();
  const [timeRange, setTimeRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>();
  const [updateTimeRange, setUpdateTimeRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>();

  // 获取当前用户信息
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;
  const isSchoolAgent = currentUser?.user?.userType === 1;

  // 获取校区列表
  const fetchSchoolList = async () => {
    try {
      const res = await listSchools({
        pageQuery: {
          pageSize: 999,
          pageNum: 1,
        },
        school: {}
      });
      if (res.code === 200) {
        const options = (res.rows || []).map((item) => ({
          label: item.name,
          value: item.id,
        }));
        setSchoolOptions(options);
      } else {
        message.error(res.msg || '获取校区列表失败');
      }
    } catch (error) {
      message.error('获取校区列表失败');
    }
  };

  useEffect(() => {
    if (!isSchoolAgent) {
      fetchSchoolList();
    }
  }, []);

  // 获取当前代理校区名称
  const agentSchoolName = currentUser?.user?.userPc?.agentSchool;

  // 打开新增/编辑弹窗
  const handleModalOpen = (record?: API.Tags) => {
    setEditingTag(record);
    form.setFieldsValue(record || {});
    setModalVisible(true);
  };

  // 关闭弹窗
  const handleModalClose = () => {
    setModalVisible(false);
    setEditingTag(undefined);
    form.resetFields();
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingTag) {
        const response = await updateTag({
          ...values,
          id: editingTag.id,
        });
        if (response.code === 200) {
          message.success('更新成功');
          handleModalClose();
          actionRef.current?.reload();
        } else {
          message.error(response.msg || '更新失败');
        }
      } else {
        const response = await addTag(values);
        if (response.code === 200) {
          message.success('添加成功');
          handleModalClose();
          actionRef.current?.reload();
        } else {
          message.error(response.msg || '添加失败');
        }
      }
    } catch (error) {
      console.error('表单错误:', error);
    }
  };

  // 删除标签
  const handleDelete = async (id: number) => {
    try {
      const response = await deleteTag({ id });
      if (response.code === 200) {
        message.success('删除成功');
        actionRef.current?.reload();
      } else {
        message.error(response.msg || '删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 批量删除
  const handleBatchDelete = async () => {
    Modal.confirm({
      title: '批量删除',
      content: `确定要删除选中的 ${selectedRows.length} 个标签吗？`,
      onOk: async () => {
        try {
          const results = await Promise.all(
            selectedRows.map((tag) => deleteTag({ id: tag.id }))
          );
          const hasError = results.some((res) => res.code !== 200);
          if (hasError) {
            message.warning('部分标签删除失败');
          } else {
            message.success('删除成功');
          }
          setSelectedRows([]);
          actionRef.current?.reload();
          actionRef.current?.clearSelected?.();
        } catch (error) {
          message.error('删除失败');
        }
      },
    });
  };

  // 重置查询条件
  const handleReset = () => {
    setSearchText('');
    setSelectedSchool(undefined);
    setServiceType(undefined);
    setTimeRange(undefined);
    setUpdateTimeRange(undefined);
    actionRef.current?.reload();
  };

  // 添加搜索函数
  const handleSearch = () => {
    actionRef.current?.reload();
  };

  const columns: ProColumns<API.Tags>[] = [
    {
      title: '标签名称',
      dataIndex: 'name',
      ellipsis: true,
      width: 120,
    },
    {
      title: '所属校区',
      dataIndex: 'schoolId',
      hideInTable: true,
      hideInSearch: isSchoolAgent,
      width: 120,
      valueType: 'select',
      ellipsis: true,
      valueEnum: Object.fromEntries(
        schoolOptions.map(({ label, value }) => [value, { text: label }])
      ),
    },
    {
      title: '服务类型',
      dataIndex: 'serviceType',
      width: 80,
      valueType: 'select',
      valueEnum: {
        0: { text: '帮取送', status: 'Processing' },
        1: { text: '代买', status: 'Success' },
        2: { text: '万能服务', status: 'Warning' },
      },
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 150,
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 150,
      search: false,
    },
    {
      title: '修改时间',
      dataIndex: 'updateTime',
      width: 150,
      search: false,
    },
    {
      title: '操作',
      width: 180,
      key: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a key="edit" onClick={() => handleModalOpen(record)}>
          编辑
        </a>,
        <Popconfirm
          key="delete"
          title="确定要删除这个标签吗？"
          onConfirm={() => handleDelete(record.id)}
        >
          <a style={{ color: 'red' }}>删除</a>
        </Popconfirm>,
      ],
    },
  ];

  return (
    <PageContainer>
      {isSchoolAgent && agentSchoolName && (
        <div 
          style={{ 
            marginBottom: 16,
            padding: '12px 24px',
            background: '#f5f5f5',
            borderRadius: '4px',
            fontSize: '14px'
          }}
        >
          当前校区：<span style={{ fontWeight: 'bold', color: '#1890ff' }}>{agentSchoolName}</span>
        </div>
      )}
      <div style={{ marginBottom: 16, display: 'flex', gap: 16, flexWrap: 'wrap', alignItems: 'center' }}>
        {currentUser?.user?.userType !== 1 && (
          <Select
            allowClear
            showSearch
            style={{ width: 200 }}
            placeholder="请选择校区"
            optionFilterProp="label"
            options={schoolOptions}
            value={selectedSchool}
            onChange={(value) => setSelectedSchool(value)}
            filterOption={(input, option) =>
              (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
            }
          />
        )}
        <Select
          allowClear
          style={{ width: 150 }}
          placeholder="服务类型"
          value={serviceType}
          onChange={(value) => setServiceType(value)}
          options={[
            { label: '帮取送', value: 0 },
            { label: '代买', value: 1 },
            { label: '万能服务', value: 2 },
          ]}
        />
        <Input
          placeholder="搜索标签名称"
          prefix={<SearchOutlined />}
          style={{ width: 200 }}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
        />
        <RangePicker
          showTime
          style={{ width: 380 }}
          value={timeRange}
          placeholder={['创建开始时间', '创建结束时间']}
          onChange={(dates) => setTimeRange(dates as [dayjs.Dayjs, dayjs.Dayjs])}
        />
        <RangePicker
          showTime
          style={{ width: 380 }}
          value={updateTimeRange}
          placeholder={['修改开始时间', '修改结束时间']}
          onChange={(dates) => setUpdateTimeRange(dates as [dayjs.Dayjs, dayjs.Dayjs])}
        />
        <Button type="primary" onClick={handleSearch}>
          搜索
        </Button>
        <Button onClick={handleReset}>重置</Button>
      </div>

      <ProTable<API.Tags>
        rowKey="id"
        actionRef={actionRef}
        columns={columns}
        search={false}
        options={{
          density: true,
          fullScreen: true,
          reload: true,
          setting: true,
        }}
        rowSelection={{
          onChange: (_, rows) => {
            setSelectedRows(rows);
          },
        }}
        tableAlertRender={({ selectedRowKeys, onCleanSelected }) => (
          <Space size={24}>
            <span>
              已选 {selectedRowKeys.length} 项
              <a style={{ marginLeft: 8 }} onClick={onCleanSelected}>
                取消选择
              </a>
            </span>
          </Space>
        )}
        tableAlertOptionRender={() => (
          <Space size={16}>
            <Button danger onClick={handleBatchDelete}>
              批量删除
            </Button>
          </Space>
        )}
        toolBarRender={() => [
          <Button
            key="add"
            type="primary"
            onClick={() => handleModalOpen()}
            icon={<PlusOutlined />}
          >
            新增标签
          </Button>,
        ]}
        request={async (params) => {
          try {
            const queryParams = {
              pageQuery: {
                pageSize: params.pageSize,
                pageNum: params.current,
              },
              tags: {
                name: searchText,
                schoolId: selectedSchool,
                serviceType,
                remark: params.remark,
              },
            };

            // 添加创建时间范围参数
            if (timeRange?.[0]) {
              queryParams.tags[`params['createTimeBegin']`] = timeRange[0].format('YYYY-MM-DD HH:mm:ss');
              queryParams.tags[`params['createTimeEnd']`] = timeRange[1].format('YYYY-MM-DD HH:mm:ss');
            }

            // 添加修改时间范围参数
            if (updateTimeRange?.[0]) {
              queryParams.tags[`params['updateTimeBegin']`] = updateTimeRange[0].format('YYYY-MM-DD HH:mm:ss');
              queryParams.tags[`params['updateTimeEnd']`] = updateTimeRange[1].format('YYYY-MM-DD HH:mm:ss');
            }

            // 移除所有空值
            const cleanParams = JSON.parse(JSON.stringify(queryParams));

            // 移除 tags 中的空值
            Object.keys(cleanParams.tags).forEach(key => {
              if (cleanParams.tags[key] === undefined) {
                delete cleanParams.tags[key];
              }
            });

            const response = await listTags(cleanParams);
            if (response.code === 200) {
              return {
                data: response.rows || [],
                success: true,
                total: response.total || 0,
              };
            } else {
              message.error(response.msg || '获取数据失败');
              return {
                data: [],
                success: false,
                total: 0,
              };
            }
          } catch (error) {
            message.error('获取数据失败');
            return {
              data: [],
              success: false,
              total: 0,
            };
          }
        }}
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
        }}
      />

      <Modal
        title={editingTag ? '编辑标签' : '新增标签'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={handleModalClose}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          {/* 只有非校区管理员才显示校区选择 */}
          {currentUser?.user?.userType !== 1 && (
            <Form.Item
              name="schoolId"
              label="所属校区"
              rules={[{ required: true, message: '请选择校区' }]}
            >
              <Select
                placeholder="请选择校区"
                options={schoolOptions}
                showSearch
                optionFilterProp="label"
              />
            </Form.Item>
          )}
          <Form.Item
            name="name"
            label="标签名称"
            rules={[{ required: true, message: '请输入标签名称' }]}
          >
            <Input placeholder="请输入标签名称" />
          </Form.Item>
          <Form.Item
            name="serviceType"
            label="服务类型"
            rules={[{ required: true, message: '请选择服务类型' }]}
          >
            <Select
              placeholder="请选择服务类型"
              options={[
                { label: '帮取送', value: 0 },
                { label: '代买', value: 1 },
                { label: '万能服务', value: 2 },
              ]}
            />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea placeholder="请输入备注" rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default TagList;
