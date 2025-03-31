import { Button, Modal, Tag, Descriptions } from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { listCurr as listCapitalFlow } from '@/services/test-swagger/moneyController';
import type { SortOrder } from 'antd/lib/table/interface';

const CapitalFlowList: React.FC = () => {
  const [currentRow, setCurrentRow] = useState<API.CapitalFlow>();
  const actionRef = useRef<ActionType>();
  const [detailVisible, setDetailVisible] = useState<boolean>(false);

  // 获取类型标签
  const getTypeTag = (type?: number) => {
    switch (type) {
      case 0:
        return <Tag color="blue">订单收益</Tag>;
      case 1:
        return <Tag color="green">跑腿提现</Tag>;
      case 2:
        return <Tag color="orange">代理提现</Tag>;
      default:
        return <Tag color="default">未知</Tag>;
    }
  };

  // 处理排序参数
  const handleSorter = (sorter: Record<string, SortOrder>) => {
    const key = Object.keys(sorter)[0];
    const order = sorter[key];
    if (key && order) {
      // 转换字段名为下划线格式
      const column = key.replace(/([A-Z])/g, '_$1').toLowerCase();
      return {
        orderByColumn: column,
        isAsc: order === 'ascend' ? 'asc' : 'desc',
      };
    }
    return {};
  };

  const columns: ProColumns<API.CapitalFlow>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      hideInSearch: true,
    },
    {
      title: '订单ID',
      dataIndex: 'orderId',
    },
    {
      title: '类型',
      dataIndex: 'type',
      valueType: 'select',
      valueEnum: {
        0: { text: '订单收益', status: 'Processing' },
        1: { text: '跑腿提现', status: 'Success' },
        2: { text: '代理提现', status: 'Warning' },
      },
    },
    {
      title: '代理收益',
      dataIndex: 'profitAgent',
      hideInSearch: true,
      render: (val) => (val ? `¥${val}` : '-'),
      sorter: true,
    },
    {
      title: '跑腿收益',
      dataIndex: 'profitRunner',
      hideInSearch: true,
      render: (val) => (val ? `¥${val}` : '-'),
      sorter: true,
    },
    {
      title: '用户收益',
      dataIndex: 'profitUser',
      hideInSearch: true,
      render: (val) => (val ? `¥${val}` : '-'),
      sorter: true,
    },
    {
      title: '平台收益',
      dataIndex: 'profitPlat',
      hideInSearch: true,
      render: (val) => (val ? `¥${val}` : '-'),
      sorter: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTimeRange',
      hideInTable: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <Button
          key="detail"
          type="link"
          onClick={() => {
            setCurrentRow(record);
            setDetailVisible(true);
          }}
        >
          详情
        </Button>,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.CapitalFlow>
        headerTitle="资金流动"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
          defaultCollapsed: false,
        }}
        request={async (params, sorter) => {
          const { current, pageSize, createTime, ...rest } = params;
          // 获取排序参数
          const sortParams = handleSorter(sorter);
          
          const response = await listCapitalFlow({
            capitalFlow: {
              ...rest,
              createTime: createTime?.[0],
            },
            pageQuery: {
              pageSize,
              pageNum: current,
              ...sortParams, // 添加排序参数
            },
          });
          return {
            data: response.rows || [],
            success: response.code === 200,
            total: response.total || 0,
          };
        }}
        columns={columns}
        toolBarRender={false}
        pagination={{
          showQuickJumper: true,
          showSizeChanger: true,
        }}
      />

      <Modal
        title="资金流动详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={600}
      >
        <Descriptions column={2}>
          <Descriptions.Item label="ID">{currentRow?.id}</Descriptions.Item>
          <Descriptions.Item label="订单ID">{currentRow?.orderId}</Descriptions.Item>
          <Descriptions.Item label="类型">
            {getTypeTag(currentRow?.type)}
          </Descriptions.Item>
          <Descriptions.Item label="代理ID">
            {currentRow?.agentId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="代理收益">
            {currentRow?.profitAgent ? `¥${currentRow.profitAgent}` : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="跑腿ID">
            {currentRow?.runnerId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="跑腿收益">
            {currentRow?.profitRunner ? `¥${currentRow.profitRunner}` : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="用户ID">
            {currentRow?.userId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="用户收益">
            {currentRow?.profitUser ? `¥${currentRow.profitUser}` : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="平台收益">
            {currentRow?.profitPlat ? `¥${currentRow.profitPlat}` : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间" span={2}>
            {currentRow?.createTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </PageContainer>
  );
};

export default CapitalFlowList;
