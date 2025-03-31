import { PageContainer } from '@ant-design/pro-layout';
import { useRef, useState, useEffect } from 'react';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import type { ProListMetas } from '@ant-design/pro-list';
import ProList from '@ant-design/pro-list';
import { Button, Modal, Tag, Space, message, Segmented, Input, Select, DatePicker, Progress } from 'antd';
import { list4 as listOrders, detail, complete, add3 as cancelOrder } from '@/services/test-swagger/orderController';
import { list5 } from '@/services/test-swagger/schoolController';
import { SearchOutlined, EllipsisOutlined } from '@ant-design/icons';
import type { RangePickerProps } from 'antd/es/date-picker';
import dayjs from 'dayjs';
import ProTable from '@ant-design/pro-table';
import { history } from 'umi';
import { useModel } from 'umi';

const { RangePicker } = DatePicker;

const OrderList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [orderStatus, setOrderStatus] = useState<string>('全部');
  const [searchText, setSearchText] = useState<string>('');
  const [selectedSchool, setSelectedSchool] = useState<number>();
  const [schoolOptions, setSchoolOptions] = useState<{ label: string; value: number; }[]>([]);
  const [serviceType, setServiceType] = useState<number>();
  const [orderUid, setOrderUid] = useState<string>();
  const [takerUid, setTakerUid] = useState<string>();
  const [timeRange, setTimeRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>();

  // 获取当前用户信息
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;

  // 获取校区列表
  const fetchSchoolList = async () => {
    const res = await list5({
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
    fetchSchoolList();
  }, []);

  // 查看详情
  const handleViewDetail = (orderId: string) => {
    history.push(`/order/detail/${orderId}`);
  };

  // 完成订单
  const handleComplete = async (orderId: number) => {
    Modal.confirm({
      title: '确认完成',
      content: '确定要完成这个订单吗？',
      onOk: async () => {
        try {
          await complete({ orderId });
          message.success('订单已完成');
          actionRef.current?.reload();
        } catch (error) {
          message.error('操作失败，请重试');
        }
      },
    });
  };

  // 取消订单
  const handleCancel = async (orderId: number) => {
    Modal.confirm({
      title: '确认取消',
      content: '确定要取消这个订单吗？',
      onOk: async () => {
        try {
          await cancelOrder({ orderId });
          message.success('订单已取消');
          actionRef.current?.reload();
        } catch (error) {
          message.error('操作失败，请重试');
        }
      },
    });
  };

  // 每当查询条件改变时触发查询
  useEffect(() => {
    actionRef.current?.reload();
  }, [orderStatus, searchText, selectedSchool, serviceType, orderUid, takerUid, timeRange]);

  // 添加状态选项的常量
  const ORDER_STATUS_OPTIONS = [
    { label: '全部', value: '全部' },
    { label: '待支付', value: '0' },
    { label: '待接单', value: '1' },
    { label: '待配送', value: '2' },
    { label: '配送中', value: '3' },
    { label: '已送达', value: '4' },
    { label: '已取消', value: '5' },
    { label: '已完成', value: '10' },
    { label: '已申诉', value: '11' },
  ];

  // 修改状态查询的处理函数
  const getStatusValue = (status: string) => {
    if (status === '全部') return undefined;
    return Number(status);
  };

  // 添加重置函数
  const handleReset = () => {
    setOrderStatus('全部');
    setSearchText('');
    setSelectedSchool(undefined);
    setServiceType(undefined);
    setOrderUid(undefined);
    setTakerUid(undefined);
    setTimeRange(undefined);
    actionRef.current?.reload();
  };

  const columns: ProColumns<API.OrderMain>[] = [
    {
      title: '标签',
      dataIndex: 'tag',
      width: 100,
      render: (_, record) => (
        <div style={{ color: '#1890ff' }}>
          {record.tag}
        </div>
      ),
    },
    {
      title: '服务类型',
      dataIndex: 'serviceType',
      width: 120,
      render: (_, record) => (
        <Tag color={
          record.serviceType === 0 ? 'blue' :
          record.serviceType === 1 ? 'green' :
          record.serviceType === 2 ? 'purple' : 'default'
        }>
          {record.serviceType === 0 ? '帮取送' :
           record.serviceType === 1 ? '代买' :
           record.serviceType === 2 ? '万能服务' : '未知服务'}
        </Tag>
      ),
    },
    {
      title: '具体要求',
      dataIndex: 'detail',
      width: 200,
      ellipsis: true,
      render: (text) => (
        <div style={{ color: '#6c6c6c' }}>{text}</div>
      ),
    },
    {
      title: '订单状态',
      dataIndex: 'status',
      width: 100,
      valueEnum: {
        0: { text: '待支付', status: 'warning' },
        1: { text: '待接单', status: 'warning' },
        2: { text: '待配送', status: 'processing' },
        3: { text: '配送中', status: 'processing' },
        4: { text: '已送达', status: 'success' },
        5: { text: '已取消', status: 'default' },
        10: { text: '已完成', status: 'success' },
        11: { text: '已申诉', status: 'error' },
      },
    },
    {
      title: '订单金额',
      dataIndex: 'totalAmount',
      width: 100,
      render: (text) => (
        <div style={{ color: '#a66733' }}>
          ￥{text}
        </div>
      ),
    },
    {
      title: '下单时间',
      dataIndex: 'createTime',
      width: 150,
      render: (text) => (
        <div style={{ color: '#6c6c6c' }}>{text}</div>
      ),
    },
    {
      title: '操作',
      width: 80,
      valueType: 'option',
      render: (_, record) => [
        <a key="view" onClick={() => handleViewDetail(record.id)}>
          查看详情
        </a>,
      ],
    },
  ];

  return (
    <PageContainer>
      <div style={{ marginBottom: 16, display: 'flex', gap: 16, flexWrap: 'wrap', alignItems: 'center' }}>
        {/* 只有非校区管理员才显示校区选择 */}
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
          placeholder="下单用户ID"
          style={{ width: 150 }}
          value={orderUid}
          onChange={(e) => setOrderUid(e.target.value)}
        />
        <Input
          placeholder="接单用户ID"
          style={{ width: 150 }}
          value={takerUid}
          onChange={(e) => setTakerUid(e.target.value)}
        />
        <RangePicker
          showTime
          style={{ width: 380 }}
          value={timeRange}
          onChange={(dates) => setTimeRange(dates as [dayjs.Dayjs, dayjs.Dayjs])}
        />
        <Button onClick={handleReset}>重置</Button>
      </div>

      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Segmented
          options={ORDER_STATUS_OPTIONS}
          value={orderStatus}
          onChange={(value) => setOrderStatus(value.toString())}
        />
        <Input
          placeholder="搜索订单号"
          prefix={<SearchOutlined />}
          style={{ width: 200 }}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
        />
      </div>

      <ProTable<API.OrderMain>
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
          const response = await listOrders({
            pageQuery: {
              pageSize: params.pageSize,
              pageNum: params.current,
            },
            orderQuery: {
              status: getStatusValue(orderStatus),
              id: searchText ? searchText : undefined,
              schoolId: selectedSchool,
              serviceType,
              orderUid: orderUid ? Number(orderUid) : undefined,
              takerUid: takerUid ? Number(takerUid) : undefined,
              beginTime: timeRange?.[0]?.format('YYYY-MM-DD HH:mm:ss'),
              endTime: timeRange?.[1]?.format('YYYY-MM-DD HH:mm:ss'),
            },
          });
          return {
            data: response.rows || [],
            success: response.code === 200,
            total: response.total || 0,
          };
        }}
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
        }}
        dateFormatter="string"
        headerTitle="订单列表"
      />
    </PageContainer>
  );
};

export default OrderList;
