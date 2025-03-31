import { PlusOutlined } from '@ant-design/icons';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import { Button, message, Modal, Switch, Form, Input, Space } from 'antd';
import React, { useRef, useState } from 'react';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ModalForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import {
  add,
  edit1,
  remove3,
  list2,
  changeStatus,
} from '@/services/test-swagger/ossConfigController';

interface OssConfigVO {
  ossConfigId: number;
  configKey: string;
  accessKey: string;
  secretKey: string;
  bucketName: string;
  prefix: string;
  endpoint: string;
  domain: string;
  isHttps: string;
  region: string;
  status: number;
  ext1: string;
  remark: string;
  accessPolicy: string;
  createTime: string;
  updateTime: string;
}

const OssConfigPage: React.FC = () => {
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<OssConfigVO>();
  const actionRef = useRef<ActionType>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);

  const handleAdd = async (fields: OssConfigVO) => {
    const hide = message.loading('正在添加...');
    try {
      await add(fields);
      hide();
      message.success('添加成功!');
      actionRef.current?.reload();
      return true;
    } catch (error) {
      hide();
      message.error('添加失败，请重试!');
      return false;
    }
  };

  const handleUpdate = async (fields: OssConfigVO) => {
    const hide = message.loading('正在更新...');
    try {
      await edit1({
        ...fields,
        ossConfigId: currentRow?.ossConfigId,
      });
      hide();
      message.success('更新成功!');
      actionRef.current?.reload();
      return true;
    } catch (error) {
      hide();
      message.error('更新失败，请重试!');
      return false;
    }
  };

  const handleDelete = async (ossConfigId: number) => {
    const hide = message.loading('正在删除...');
    try {
      await remove3({ ossConfigIds: [ossConfigId] });
      hide();
      message.success('删除成功!');
      actionRef.current?.reload();
    } catch (error) {
      hide();
      message.error('删除失败，请重试!');
    }
  };

  const handleBatchDelete = async () => {
    if (!selectedRowKeys.length) {
      message.warning('请选择要删除的配置');
      return;
    }
    Modal.confirm({
      title: '批量删除',
      content: `确定要删除选中的 ${selectedRowKeys.length} 个配置吗？`,
      onOk: async () => {
        await remove3({ ossConfigIds: selectedRowKeys.map(key => parseInt(key)) });
        setSelectedRowKeys([]);
      },
    });
  };

  const handleStatusChange = async (record: OssConfigVO) => {
    const hide = message.loading('正在修改状态...');
    try {
      await changeStatus({
        ...record,
        status: record.status === 0 ? 1 : 0,
      });
      hide();
      message.success('状态修改成功!');
      actionRef.current?.reload();
    } catch (error) {
      hide();
      message.error('状态修改失败，请重试!');
    }
  };

  const columns: ProColumns<OssConfigVO>[] = [
    {
      title: '配置ID',
      dataIndex: 'ossConfigId',
      hideInForm: true,
      search: false,
      ellipsis: true,
    },
    {
      title: '配置名称',
      dataIndex: 'configKey',
      ellipsis: true,
    },
    {
      title: '配置值',
      dataIndex: 'configValue',
      ellipsis: true,
      search: false,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      ellipsis: true,
      search: false,
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueEnum: {
        0: { text: '停用', status: 'Error' },
        1: { text: '正常', status: 'Success' },
      },
      render: (_, record) => (
        <Switch
          checked={record.status === 1}
          onChange={() => handleStatusChange(record)}
          checkedChildren="启用"
          unCheckedChildren="停用"
        />
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      search: false,
      ellipsis: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="edit"
          onClick={() => {
            setCurrentRow(record);
            setUpdateModalVisible(true);
          }}
        >
          编辑
        </a>,
        <a
          key="delete"
          onClick={() => {
            Modal.confirm({
              title: '确认删除',
              content: '确定要删除该配置吗？',
              onOk: () => handleDelete(record.ossConfigId),
            });
          }}
        >
          删除
        </a>,
      ],
    },
  ];

  const ConfigForm: React.FC<{
    visible: boolean;
    onVisibleChange: (visible: boolean) => void;
    onFinish: (values: any) => Promise<boolean>;
    values?: Partial<OssConfigVO>;
  }> = (props) => {
    return (
      <ModalForm
        title={props.values ? '编辑OSS配置' : '新建OSS配置'}
        visible={props.visible}
        onVisibleChange={props.onVisibleChange}
        onFinish={props.onFinish}
        initialValues={props.values}
        width={600}
      >
        <ProFormText
          name="configKey"
          label="配置名称"
          rules={[{ required: true, message: '请输入配置名称' }]}
        />
        <ProFormText
          name="accessKey"
          label="AccessKey"
          rules={[{ required: true, message: '请输入AccessKey' }]}
        />
        <ProFormText
          name="secretKey"
          label="SecretKey"
          rules={[{ required: true, message: '请输入SecretKey' }]}
        />
        <ProFormText
          name="bucketName"
          label="存储桶名称"
          rules={[{ required: true, message: '请输入存储桶名称' }]}
        />
        <ProFormText name="prefix" label="前缀" />
        <ProFormText
          name="endpoint"
          label="访问站点"
          rules={[{ required: true, message: '请输入访问站点' }]}
        />
        <ProFormText name="domain" label="自定义域名" />
        <ProFormText
          name="isHttps"
          label="是否HTTPS"
          rules={[{ required: true, message: '请选择是否启用HTTPS' }]}
        />
        <ProFormText name="region" label="区域" />
        <ProFormText
          name="accessPolicy"
          label="访问权限"
          rules={[{ required: true, message: '请选择访问权限' }]}
        />
        <ProFormTextArea
          name="remark"
          label="备注"
          placeholder="请输入备注信息"
        />
      </ModalForm>
    );
  };

  return (
    <PageContainer>
      <ProTable<OssConfigVO>
        headerTitle="OSS配置列表"
        actionRef={actionRef}
        rowKey="ossConfigId"
        search={{
          labelWidth: 120,
        }}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys as string[]),
        }}
        toolBarRender={() => [
          <Button
            key="delete"
            danger
            onClick={handleBatchDelete}
            disabled={!selectedRowKeys.length}
          >
            批量删除
          </Button>,
          <Button
            type="primary"
            key="add"
            onClick={() => setCreateModalVisible(true)}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params) => {
          const msg = await list2({
            ...params,
            current: params.current,
            pageSize: params.pageSize,
          });
          return {
            data: msg.rows || [],
            success: true,
            total: msg.total || 0,
          };
        }}
        columns={columns}
      />

      <ConfigForm
        visible={createModalVisible}
        onVisibleChange={setCreateModalVisible}
        onFinish={async (value) => {
          const success = await handleAdd(value as OssConfigVO);
          if (success) {
            setCreateModalVisible(false);
          }
          return success;
        }}
      />

      <ConfigForm
        visible={updateModalVisible}
        onVisibleChange={setUpdateModalVisible}
        values={currentRow}
        onFinish={async (value) => {
          const success = await handleUpdate(value as OssConfigVO);
          if (success) {
            setUpdateModalVisible(false);
          }
          return success;
        }}
      />
    </PageContainer>
  );
};

export default OssConfigPage;
