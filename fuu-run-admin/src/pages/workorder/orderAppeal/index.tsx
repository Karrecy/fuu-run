import { PageContainer } from '@ant-design/pro-layout';
import { useRef, useState, useEffect } from 'react';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Button, Modal, Form, Input, message, Space, Select, Tag, Descriptions, Image } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { edit3 as editAppeal, getAppeal } from '@/services/test-swagger/orderAppealController';
import { useModel, useAccess, Access } from '@umijs/max';
import { list5 as listSchools } from '@/services/test-swagger/schoolController';
import { list5 } from '@/services/test-swagger/orderAppealController';

const OrderAppealList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [currentRow, setCurrentRow] = useState<API.OrderAppealVO>();
  const { initialState } = useModel('@@initialState');
  const [rejectModalVisible, setRejectModalVisible] = useState(false);
  const [rejectForm] = Form.useForm();
  const access = useAccess();

  // 查询条件状态
  const [searchOrderId, setSearchOrderId] = useState<string>('');
  const [selectedStatus, setSelectedStatus] = useState<number>();
  const [selectedSchool, setSelectedSchool] = useState<number>();
  const [schoolOptions, setSchoolOptions] = useState<{ label: string; value: number; }[]>([]);

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
        message.error(res.msg);
      }
    } catch (error) {
      message.error('获取校区列表失败，请重试');
    }
  };

  useEffect(() => {
    fetchSchoolList();
  }, []);

  // 每当查询条件改变时触发查询
  useEffect(() => {
    actionRef.current?.reload();
  }, [searchOrderId, selectedStatus, selectedSchool]);

  const getStatusTag = (status?: number) => {
    const statusMap = {
      0: { color: 'error', text: '不通过' },
      1: { color: 'success', text: '通过' },
      2: { color: 'processing', text: '申诉中' },
    };
    const { color, text } = statusMap[status as keyof typeof statusMap] || { color: 'default', text: '未知' };
    return <Tag color={color}>{text}</Tag>;
  };

  // 处理申诉
  const handleEdit = async (fields: API.OrderAppeal) => {
    const hide = message.loading('正在处理...');
    try {
      const res = await editAppeal({
        ...fields,
        updateId: initialState?.currentUser?.user?.uid,
        updateType: 0,
      });
      if (res.code === 200) {
        hide();
        message.success('处理成功!');
        actionRef.current?.reload();
        return true;
      }
      hide();
      message.error(res.msg);
      return false;
    } catch (error) {
      hide();
      message.error('请求失败，请重试!');
      return false;
    }
  };

  // 处理驳回
  const handleReject = async () => {
    try {
      const values = await rejectForm.validateFields();
      const success = await handleEdit({
        ...currentRow?.orderAppeal,
        appealStatus: 0,
        remarks: values.remarks,
        updateId: initialState?.currentUser?.user?.uid,
      });
      if (success) {
        setRejectModalVisible(false);
        rejectForm.resetFields();
      }
    } catch (error) {
      // 表单验证错误
    }
  };

  // 获取详情数据
  const handleViewDetail = async (record: API.OrderAppeal) => {
    if (!record?.orderId) {
      message.error('订单ID不存在');
      return;
    }
    try {
      const response = await getAppeal({ 
        orderId: record.orderId.toString()
      });
      if (response.code === 200) {
        if (response.data?.[0]) {
          setCurrentRow(response.data[0]);
          setDetailModalVisible(true);
        } else {
          message.error('未找到申诉记录');
        }
      } else {
        message.error(response.msg);
      }
    } catch (error) {
      message.error('请求失败，请重试');
    }
  };

  const columns: ProColumns<API.OrderAppeal>[] = [
    {
      title: '所属校区',
      dataIndex: 'schoolId',
      valueType: 'select',
      valueEnum: Object.fromEntries(
        schoolOptions.map(({ label, value }) => [value, { text: label }])
      ),
    },
    {
      title: '订单ID',
      dataIndex: 'orderId',
      ellipsis: true,
    },
    {
      title: '申诉理由',
      dataIndex: 'appealReason',
      ellipsis: true,
      width: 200,
    },
    {
      title: '申诉状态',
      dataIndex: 'appealStatus',
      width: 100,
      render: (_, record) => getStatusTag(record?.appealStatus),
    },
    {
      title: '申诉时间',
      dataIndex: 'appealTime',
      width: 150,
      valueType: 'dateTime',
    },
    {
      title: '操作',
      width: 180,
      key: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="detail"
          onClick={() => record?.orderId && handleViewDetail(record)}
        >
          详情
        </a>,
        record?.appealStatus === 2 && (
          <Space key="actions">
            <Access 
              accessible={!!access.hasPerms('system:appeal:edit')}
            >
              <a
                onClick={() => {
                  Modal.confirm({
                    title: '确认通过',
                    content: '确定要通过该申诉并全额退款吗？',
                    okText: '确定并退款',
                    onOk: () => handleEdit({
                      ...record,
                      appealStatus: 1,
                    }),
                  });
                }}
              >
                通过
              </a>
            </Access>
            <Access 
              accessible={!!access.hasPerms('system:appeal:handle')}
            >
              <a
                style={{ color: '#ff4d4f' }}
                onClick={() => {
                  setCurrentRow({ orderAppeal: record });
                  setRejectModalVisible(true);
                }}
              >
                驳回
              </a>
            </Access>
          </Space>
        ),
      ],
    },
  ];

  return (
    <PageContainer>
      <div style={{ marginBottom: 16, display: 'flex', gap: 16, flexWrap: 'wrap', alignItems: 'center' }}>
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
        <Input
          placeholder="搜索订单ID"
          prefix={<SearchOutlined />}
          style={{ width: 200 }}
          value={searchOrderId}
          onChange={(e) => setSearchOrderId(e.target.value)}
          onPressEnter={() => actionRef.current?.reload()}
        />
        <Select
          allowClear
          style={{ width: 150 }}
          placeholder="申诉状态"
          value={selectedStatus}
          onChange={(value) => setSelectedStatus(value)}
          options={[
            { label: '不通过', value: 0 },
            { label: '通过', value: 1 },
            { label: '申诉中', value: 2 },
          ]}
        />
        <Button onClick={() => {
          setSearchOrderId('');
          setSelectedStatus(undefined);
          setSelectedSchool(undefined);
          actionRef.current?.reload();
        }}>
          重置
        </Button>
      </div>

      <ProTable<API.OrderAppeal>
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
        toolBarRender={() => [
          <Access 
            key="add" 
            accessible={!!access.hasPerms('system:appeal:add')}
          >
            <Button>
              新增
            </Button>
          </Access>
        ]}
        request={async (params) => {
          try {
            const response = await list5({
              pageQuery: {
                pageSize: params.pageSize,
                pageNum: params.current,
              },
              orderAppeal: {
                orderId: searchOrderId ? searchOrderId : undefined,
                schoolId: selectedSchool,
                appealStatus: selectedStatus,
              },
            });
            if (response.code === 200) {
              return {
                data: response.rows || [],
                success: true,
                total: response.total || 0,
              };
            }
            message.error(response.msg);
            return {
              data: [],
              success: false,
              total: 0,
            };
          } catch (error) {
            message.error('请求失败，请重试');
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
        title="驳回申诉"
        open={rejectModalVisible}
        onOk={handleReject}
        onCancel={() => {
          setRejectModalVisible(false);
          rejectForm.resetFields();
        }}
        destroyOnClose
      >
        <Form form={rejectForm}>
          <Form.Item
            name="remarks"
            label="驳回原因"
            rules={[
              { required: true, message: '请输入驳回原因' },
              { max: 100, message: '驳回原因不能超过100字' },
            ]}
          >
            <Input.TextArea
              rows={4}
              placeholder="请输入驳回原因（不超过100字）"
              maxLength={100}
              showCount
            />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="申诉详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={800}
      >
        {currentRow?.orderAppeal && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="订单ID">{currentRow.orderAppeal.orderId}</Descriptions.Item>
            <Descriptions.Item label="申诉理由">{currentRow.orderAppeal.appealReason}</Descriptions.Item>
            <Descriptions.Item label="申诉状态">
              {getStatusTag(currentRow.orderAppeal.appealStatus)}
            </Descriptions.Item>
            {currentRow.orderAppeal.appealStatus === 0 && (
              <Descriptions.Item label="驳回原因" style={{ whiteSpace: 'pre-wrap' }}>
                {currentRow.orderAppeal.remarks || '无'}
              </Descriptions.Item>
            )}
            <Descriptions.Item label="申诉时间">{currentRow.orderAppeal.appealTime}</Descriptions.Item>
            {currentRow.orderAppeal.updateId && (
              <Descriptions.Item label="处理人ID">{currentRow.orderAppeal.updateId}</Descriptions.Item>
            )}
            {currentRow.orderAppeal.updateTime && (
              <Descriptions.Item label="处理时间">{currentRow.orderAppeal.updateTime}</Descriptions.Item>
            )}
            {currentRow.imageUrls?.length > 0 && (
              <Descriptions.Item label="申诉图片">
                <Image.PreviewGroup>
                  {currentRow.imageUrls.map((url, index) => (
                    <Image
                      key={index}
                      width={200}
                      src={url}
                      style={{ marginRight: 8, marginBottom: 8 }}
                    />
                  ))}
                </Image.PreviewGroup>
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </PageContainer>
  );
};

export default OrderAppealList;
