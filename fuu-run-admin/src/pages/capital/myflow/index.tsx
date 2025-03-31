import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Tag } from 'antd';
import React from 'react';
import { listCurr } from '@/services/test-swagger/moneyController';
import { ReloadOutlined } from '@ant-design/icons';
import { useModel } from '@umijs/max';

const MyFlow: React.FC = () => {
  // 获取当前用户信息
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;
  const userType = currentUser?.user?.userType;

  // 根据用户类型获取流水类型选项
  const getTypeValueEnum = () => {
    const baseTypes = {
      0: { text: '订单收益', status: 'success' },
      2: { text: '代理提现', status: 'error' },
    };

    // 如果不是校区代理，添加跑腿提现选项
    if (userType !== 1) {
      return {
        ...baseTypes,
        1: { text: '跑腿提现', status: 'warning' },
      };
    }

    return baseTypes;
  };

  // 定义流水类型
  const getFlowType = (type?: number) => {
    const typeMap = {
      0: { text: '订单收益', color: 'success' },
      1: { text: '跑腿提现', color: 'warning' },
      2: { text: '代理提现', color: 'error' },
    };
    const defaultType = { text: '-', color: 'default' };
    return type !== undefined ? typeMap[type] || defaultType : defaultType;
  };

  const renderProfit = (val: number | null) => {
    if (val === null) {
      return <span>¥0.00</span>;
    }
    const amount = Number(val);
    return (
      <span style={{ 
        color: amount < 0 ? '#52c41a' : (amount > 0 ? '#f5222d' : '#000000')
      }}>
        ¥{amount.toFixed(2)}
      </span>
    );
  };

  const columns: ProColumns<API.CapitalFlow>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      width: 80,
      align: 'center',
      hideInSearch: true,
    },
    {
      title: '订单号',
      dataIndex: 'orderId',
      copyable: true,
      width: 100,
      align: 'center',
      hideInSearch: true,
    },
    {
      title: '类型',
      dataIndex: 'type',
      width: 100,
      align: 'center',
      valueEnum: getTypeValueEnum(),
      render: (_, record) => {
        const flowType = getFlowType(record.type);
        return <Tag color={flowType.color}>{flowType.text}</Tag>;
      },
    },
    {
      title: '代理收益',
      dataIndex: 'profitAgent',
      width: 120,
      align: 'right',
      hideInSearch: true,
      render: (val) => renderProfit(val),
    },
    {
      title: '跑腿收益',
      dataIndex: 'profitRunner',
      width: 120,
      align: 'right',
      hideInSearch: true,
      render: (val) => renderProfit(val),
    },
    {
      title: '用户收益',
      dataIndex: 'profitUser',
      width: 120,
      align: 'right',
      hideInSearch: true,
      render: (val) => renderProfit(val),
    },
    {
      title: '平台收益',
      dataIndex: 'profitPlat',
      width: 120,
      align: 'right',
      hideInSearch: true,
      render: (val) => renderProfit(val),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      align: 'center',
      valueType: 'dateTime',
      sorter: true,
      hideInSearch: true,
    },
    {
      title: '时间范围',
      dataIndex: 'createTime',
      valueType: 'dateTimeRange',
      hideInTable: true,
      search: {
        transform: (value) => {
          return {
            beginTime: value[0],
            endTime: value[1],
          };
        },
      },
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.CapitalFlow>
        headerTitle="账户明细"
        rowKey="id"
        search={{
          labelWidth: 120,
          defaultCollapsed: false,
        }}
        request={async (params) => {
          const { current, pageSize, beginTime, endTime, ...rest } = params;
          const response = await listCurr({
            pageQuery: {
              pageSize,
              pageNum: current,
            },
            capitalFlow: {
              ...rest,
              beginTime,
              endTime,
            },
          });
          return {
            data: response.rows || [],
            success: response.code === 200,
            total: response.total || 0,
          };
        }}
        columns={columns}
        pagination={{
          showQuickJumper: true,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条记录`,
          defaultPageSize: 10,
          pageSizeOptions: ['10', '20', '50', '100'],
        }}
        bordered
        dateFormatter="string"
        toolbar={{
          actions: [
            <ReloadOutlined
              key="reload"
              onClick={() => window.location.reload()}
              style={{ fontSize: '16px', cursor: 'pointer' }}
            />,
          ],
        }}
        options={{
          density: true,
          fullScreen: true,
          reload: true,
          setting: true,
        }}
      />
    </PageContainer>
  );
};

export default MyFlow;
