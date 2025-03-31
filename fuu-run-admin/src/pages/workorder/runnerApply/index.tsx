import { PageContainer } from '@ant-design/pro-layout';
import { useRef, useState, useEffect } from 'react';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Button, Modal, Form, Input, message, Space, Select, Radio, Upload, Image, Descriptions, Tag, DatePicker } from 'antd';
import { PlusOutlined, SearchOutlined, UploadOutlined } from '@ant-design/icons';
import { list, edit, submit } from '@/services/test-swagger/runnerApplyController';
import { list5 as listSchools } from '@/services/test-swagger/schoolController';
import { useModel } from '@umijs/max';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

const RunnerApplyList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [currentRow, setCurrentRow] = useState<API.RunnerApply>();
  const [form] = Form.useForm();
  const { initialState } = useModel('@@initialState');

  // 查询条件状态
  const [searchText, setSearchText] = useState<string>('');
  const [selectedSchool, setSelectedSchool] = useState<number>();
  const [schoolOptions, setSchoolOptions] = useState<{ label: string; value: number; }[]>([]);
  const [selectedStatus, setSelectedStatus] = useState<number>();

  // 添加驳回状态
  const [rejectModalVisible, setRejectModalVisible] = useState(false);
  const [rejectForm] = Form.useForm();

  // 添加时间范围状态
  const [timeRange, setTimeRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>();
  const [updateTimeRange, setUpdateTimeRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>();

  // 获取当前用户信息
  const currentUser = initialState?.currentUser;
  const isSchoolAgent = currentUser?.user?.userType === 1;
  const agentSchoolName = currentUser?.user?.userPc?.agentSchool;

  // 获取校区列表
  const fetchSchoolList = async () => {
    const res = await listSchools({
      pageQuery: {
        pageSize: 999,
        pageNum: 1,
      },
      school: {}
    });
    const options = (res.rows || []).map((item) => ({
      label: item.name,
      value: item.id,
    }));
    setSchoolOptions(options);
  };

  useEffect(() => {
    // 只有非校区代理才获取校区列表
    if (!isSchoolAgent) {
      fetchSchoolList();
    }
  }, [isSchoolAgent]);  // 添加依赖项

  // 添加搜索函数
  const handleSearch = () => {
    actionRef.current?.reload();
  };

  const getStatusTag = (status?: number) => {
    const statusMap = {
      0: { color: 'error', text: '驳回' },
      1: { color: 'success', text: '通过' },
      2: { color: 'processing', text: '申请中' },
    };
    const { color, text } = statusMap[status as keyof typeof statusMap] || { color: 'default', text: '未知' };
    return <Tag color={color}>{text}</Tag>;
  };

  // 处理申请
  const handleEdit = async (fields: API.RunnerApply) => {
    const hide = message.loading('正在处理...');
    try {
      const res = await edit(fields);
      if (res.code === 200) {
        hide();
        message.success('处理成功');
        actionRef.current?.reload();
        return true;
      }
      hide();
      message.error(res.msg);
      return false;
    } catch (error) {
      hide();
      message.error('请求失败，请重试');
      return false;
    }
  };

  // 重置查询条件
  const handleReset = () => {
    setSearchText('');
    setSelectedSchool(undefined);
    setSelectedStatus(undefined);
    setTimeRange(undefined);
    setUpdateTimeRange(undefined);
    actionRef.current?.reload();
  };

  // 处理驳回
  const handleReject = async () => {
    try {
      const values = await rejectForm.validateFields();
      const success = await handleEdit({
        ...currentRow,
        status: 0,
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

  const columns: ProColumns<API.RunnerApply>[] = [
    {
      title: '申请人',
      dataIndex: 'uid',
      ellipsis: true,
    },
    {
      title: '真实姓名',
      dataIndex: 'realname',
      ellipsis: true,
    },
    {
      title: '性别',
      dataIndex: 'gender',
      width: 80,
      valueEnum: {
        0: { text: '女' },
        1: { text: '男' },
      },
    },
    {
      title: '所属校区',
      dataIndex: 'schoolId',
      width: 200,
      valueType: 'select',
      ellipsis: true,
      valueEnum: Object.fromEntries(
        schoolOptions.map(({ label, value }) => [value, { text: label }])
      ),
    },
    {
      title: '申请状态',
      dataIndex: 'status',
      width: 100,
      render: (_, record) => getStatusTag(record.status),
    },
    {
      title: '申请时间',
      dataIndex: 'createTime',
      width: 160,
      search: false,
    },
    {
      title: '操作',
      width: 180,
      key: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="detail"
          onClick={() => {
            setCurrentRow(record);
            setDetailModalVisible(true);
          }}
        >
          详情
        </a>,
        record.status === 2 && (
          <Space key="actions">
            <a
              onClick={() => {
                Modal.confirm({
                  title: '确认通过',
                  content: '确定要通过该申请吗？',
                  onOk: () => handleEdit({ ...record, status: 1 }),
                });
              }}
            >
              通过
            </a>
            <a
              style={{ color: '#ff4d4f' }}
              onClick={() => {
                setCurrentRow(record);
                setRejectModalVisible(true);
              }}
            >
              驳回
            </a>
          </Space>
        ),
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
        {/* 只有非校区管理员才显示校区选择 */}
        {initialState?.currentUser?.user?.userType !== 1 && (
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
          placeholder="申请状态"
          value={selectedStatus}
          onChange={(value) => setSelectedStatus(value)}
          options={[
            { label: '驳回', value: 0 },
            { label: '通过', value: 1 },
            { label: '申请中', value: 2 },
          ]}
        />
        <Input
          placeholder="搜索姓名"
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

      <ProTable<API.RunnerApply>
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
        request={async (params) => {
          try {
            const queryParams = {
              pageQuery: {
                pageSize: params.pageSize,
                pageNum: params.current,
              },
              runnerApply: {
                realname: searchText,
                schoolId: selectedSchool,
                status: selectedStatus,
              },
            };

            // 添加创建时间范围参数
            if (timeRange?.[0]) {
              queryParams.runnerApply[`params['createTimeBegin']`] = timeRange[0].format('YYYY-MM-DD HH:mm:ss');
              queryParams.runnerApply[`params['createTimeEnd']`] = timeRange[1].format('YYYY-MM-DD HH:mm:ss');
            }

            // 添加修改时间范围参数
            if (updateTimeRange?.[0]) {
              queryParams.runnerApply[`params['updateTimeBegin']`] = updateTimeRange[0].format('YYYY-MM-DD HH:mm:ss');
              queryParams.runnerApply[`params['updateTimeEnd']`] = updateTimeRange[1].format('YYYY-MM-DD HH:mm:ss');
            }

            // 移除所有空值
            const cleanParams = JSON.parse(JSON.stringify(queryParams));

            // 移除 runnerApply 中的空值
            Object.keys(cleanParams.runnerApply).forEach(key => {
              if (cleanParams.runnerApply[key] === undefined) {
                delete cleanParams.runnerApply[key];
              }
            });

            const response = await list(cleanParams);
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
        title="驳回申请"
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
        title="申请详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={600}
      >
        {currentRow && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="申请ID">{currentRow.id}</Descriptions.Item>
            <Descriptions.Item label="申请人">{currentRow.realname}</Descriptions.Item>
            <Descriptions.Item label="性别">
              {currentRow.gender === 1 ? '男' : '女'}
            </Descriptions.Item>
            <Descriptions.Item label="学校">{currentRow.schoolName}</Descriptions.Item>
            <Descriptions.Item label="申请状态">
              {getStatusTag(currentRow.status)}
            </Descriptions.Item>
            {currentRow.status === 0 && (
              <Descriptions.Item label="驳回原因" style={{ whiteSpace: 'pre-wrap' }}>
                {currentRow.remarks || '无'}
              </Descriptions.Item>
            )}
            <Descriptions.Item label="学生证">
              <Image
                width={200}
                src={currentRow.studentCardUrl}
                alt="学生证照片"
              />
            </Descriptions.Item>
            <Descriptions.Item label="申请时间">{currentRow.createTime}</Descriptions.Item>
            {currentRow.updateId && (
              <Descriptions.Item label="处理人ID">{currentRow.updateId}</Descriptions.Item>
            )}
            {currentRow.updateTime && (
              <Descriptions.Item label="处理时间">{currentRow.updateTime}</Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </PageContainer>
  );
};

export default RunnerApplyList;
