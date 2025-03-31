import { Button, message, Modal, Tag, Space, Descriptions } from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {
  ModalForm,
  ProFormSelect,
  ProFormTextArea,
} from '@ant-design/pro-form';
import { withdrawPage as listMoneyRecords, edit2 as audit } from '@/services/test-swagger/moneyController';

const WithdrawalList: React.FC = () => {
  const [currentRow, setCurrentRow] = useState<API.MoneyRecode>();
  const actionRef = useRef<ActionType>();
  const [detailVisible, setDetailVisible] = useState<boolean>(false);

  // 获取状态标签
  const getStatusTag = (status?: number) => {
    switch (status) {
      case 0:
        return <Tag color="red">驳回</Tag>;
      case 1:
        return <Tag color="green">通过</Tag>;
      case 2:
        return <Tag color="processing">审核中</Tag>;
      default:
        return <Tag color="default">未知</Tag>;
    }
  };

  // 获取用户类型标签
  const getUserTypeTag = (type?: number) => {
    switch (type) {
      case 1:
        return <Tag color="orange">校区代理</Tag>;
      case 4:
        return <Tag color="blue">跑腿用户</Tag>;
      default:
        return <Tag color="default">未知</Tag>;
    }
  };

  // 审核处理
  const handleAudit = async (id: number, status: number, feedback: string) => {
    try {
      const res = await audit({
        id,
        status,
        feedback
      });
      if (res.code === 200) {
        message.success('审核成功');
        actionRef.current?.reload();
      } else {
        message.error(res.msg);
      }
    } catch (error) {
      message.error('审核失败');
    }
  };

  const columns: ProColumns<API.MoneyRecode>[] = [
    {
      title: '提现ID',
      dataIndex: 'id',
      hideInSearch: true,
    },
    {
      title: '用户ID',
      dataIndex: 'uid',
    },
    {
      title: '用户类型',
      dataIndex: 'type',
      valueType: 'select',
      valueEnum: {
        1: { text: '校区代理', status: 'Warning' },
        4: { text: '跑腿用户', status: 'Processing' },
      },
    },
    {
      title: '提现金额',
      dataIndex: 'cash',
      hideInSearch: true,
      render: (val) => `¥${val}`,
    },
    {
      title: '提现平台',
      dataIndex: 'platform',
      valueType: 'select',
      valueEnum: {
        0: { text: '支付宝转账' },
        1: { text: '银行卡转账' },
      },
    },
    {
      title: '卡号/账号',
      dataIndex: 'card',
      hideInSearch: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueType: 'select',
      valueEnum: {
        0: { text: '驳回', status: 'Error' },
        1: { text: '通过', status: 'Success' },
        2: { text: '审核中', status: 'Processing' },
      },
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
        record.status === 2 && (
          <ModalForm
            key="audit"
            title="提现审核"
            trigger={<Button type="link">审核</Button>}
            onFinish={async (values) => {
              await handleAudit(record.id!, values.status, values.feedback);
              return true;
            }}
          >
            <ProFormSelect
              name="status"
              label="审核结果"
              rules={[{ required: true }]}
              options={[
                { label: '通过', value: 1 },
                { label: '驳回', value: 0 },
              ]}
            />
            <ProFormTextArea
              name="feedback"
              label="审核反馈"
              rules={[{ required: true }]}
              placeholder="请输入审核反馈"
            />
          </ModalForm>
        ),
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.MoneyRecode>
        headerTitle="提现记录"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
          defaultCollapsed: false,
        }}
        request={async (params) => {
          const { current, pageSize, createTime, ...rest } = params;
          const response = await listMoneyRecords({
            pageQuery: {
              pageSize,
              pageNum: current,
            },
            moneyRecode: {
              ...rest,
              createTime: createTime?.[0],
              updateTime: createTime?.[1],
            },
          });
          return {
            data: response.rows || [],
            success: response.code === 200,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />

      <Modal
        title="提现详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={800}
      >
        <Descriptions column={2}>
          <Descriptions.Item label="提现ID">{currentRow?.id}</Descriptions.Item>
          <Descriptions.Item label="用户ID">{currentRow?.uid}</Descriptions.Item>
          <Descriptions.Item label="用户类型">
            {getUserTypeTag(currentRow?.type)}
          </Descriptions.Item>
          <Descriptions.Item label="提现金额">
            ¥{currentRow?.cash}
          </Descriptions.Item>
          <Descriptions.Item label="提现平台">
            {currentRow?.platform}
          </Descriptions.Item>
          <Descriptions.Item label="卡号/账号">
            {currentRow?.card}
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            {getStatusTag(currentRow?.status)}
          </Descriptions.Item>
          <Descriptions.Item label="用户备注">
            {currentRow?.remark || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {currentRow?.createTime}
          </Descriptions.Item>
          {currentRow?.status !== 2 && (
            <>
              <Descriptions.Item label="审核人">
                {currentRow?.updateId || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="审核时间">
                {currentRow?.updateTime || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="审核反馈" span={2}>
                {currentRow?.feedback || '-'}
              </Descriptions.Item>
            </>
          )}
        </Descriptions>
      </Modal>
    </PageContainer>
  );
};

export default WithdrawalList;
