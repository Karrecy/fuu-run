import { Button, Modal, Tag, Descriptions } from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { walletPage as listWallets } from '@/services/test-swagger/moneyController';
import type { SortOrder } from 'antd/lib/table/interface';

const WalletList: React.FC = () => {
  const [currentRow, setCurrentRow] = useState<API.Wallet>();
  const actionRef = useRef<ActionType>();
  const [detailVisible, setDetailVisible] = useState<boolean>(false);

  const handleSorter = (sorter: Record<string, SortOrder>) => {
    const key = Object.keys(sorter)[0];
    const order = sorter[key];
    if (key && order) {
      const column = key.replace(/([A-Z])/g, '_$1').toLowerCase();
      return {
        orderByColumn: column,
        isAsc: order === 'ascend' ? 'asc' : 'desc',
      };
    }
    return {};
  };

  const columns: ProColumns<API.Wallet>[] = [
    {
      title: '用户ID',
      dataIndex: 'uid',
      sorter: true,
      width: 100,
      align: 'center',
    },
    {
      title: '当前余额',
      dataIndex: 'balance',
      hideInSearch: true,
      render: (val) => (
        <span style={{ color: '#52c41a', fontWeight: 'bold' }}>
          ¥{Number(val || 0).toFixed(2)}
        </span>
      ),
      sorter: true,
      width: 120,
      align: 'right',
    },
    {
      title: '已提现',
      dataIndex: 'withdrawn',
      hideInSearch: true,
      render: (val) => (
        <span style={{ color: '#1890ff', fontWeight: 'bold' }}>
          ¥{Number(val || 0).toFixed(2)}
        </span>
      ),
      sorter: true,
      width: 120,
      align: 'right',
    },
    {
      title: '总收入',
      hideInSearch: true,
      render: (_, record) => (
        <span style={{ color: '#f5222d', fontWeight: 'bold' }}>
          ¥{(Number(record.balance) + Number(record.withdrawn)).toFixed(2)}
        </span>
      ),
      width: 120,
      align: 'right',
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
      width: 180,
      align: 'center',
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      hideInSearch: true,
      sorter: true,
      width: 180,
      align: 'center',
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 100,
      align: 'center',
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
      <ProTable<API.Wallet>
        headerTitle="钱包管理"
        actionRef={actionRef}
        rowKey="uid"
        search={{
          labelWidth: 120,
          defaultCollapsed: false,
        }}
        request={async (params, sorter) => {
          const { current, pageSize, createTime, ...rest } = params;
          
          let sortParams = {};
          if (sorter) {
            const key = Object.keys(sorter)[0];
            const order = sorter[key];
            if (key && order) {
              const column = key.replace(/([A-Z])/g, '_$1').toLowerCase();
              sortParams = {
                orderByColumn: column,
                isAsc: order === 'ascend' ? 'asc' : 'desc'
              };
            }
          }

          const response = await listWallets({
            wallet: {
              ...rest,
              createTime: createTime?.[0],
              updateTime: createTime?.[1],
            },
            pageQuery: {
              pageSize,
              pageNum: current,
              ...sortParams,
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
          showTotal: (total) => `共 ${total} 条记录`,
          defaultPageSize: 10,
          pageSizeOptions: ['10', '20', '50', '100'],
        }}
        bordered
      />

      <Modal
        title="钱包详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={800}
      >
        <Descriptions column={2}>
          <Descriptions.Item label="用户ID">{currentRow?.uid}</Descriptions.Item>
          <Descriptions.Item label="当前余额">
            ¥{currentRow?.balance || 0}
          </Descriptions.Item>
          <Descriptions.Item label="已提现">
            ¥{currentRow?.withdrawn || 0}
          </Descriptions.Item>
          <Descriptions.Item label="总收入">
            ¥{(Number(currentRow?.balance) + Number(currentRow?.withdrawn)) || 0}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {currentRow?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {currentRow?.updateTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </PageContainer>
  );
};

export default WalletList;
