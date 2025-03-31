import { PlusOutlined } from '@ant-design/icons';
import { Button, message, Modal, Form } from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { upload } from '@/services/test-swagger/ossController';

import ProForm, {
  ModalForm,
  ProFormText,
  ProFormDigit,
  ProFormSelect,
  ProFormUploadButton,
} from '@ant-design/pro-form';
import { addAgent1, editAgent1, list5 as list6, get1 } from '@/services/test-swagger/schoolController';

const TableList: React.FC = () => {
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<API.School>();
  const actionRef = useRef<ActionType>();
  const [form] = Form.useForm();

  const handleUpdateModalClose = () => {
    handleUpdateModalVisible(false);
    setCurrentRow(undefined);
    form.resetFields();
  };

  const handleEdit = (record: API.School) => {
    form.resetFields();
    setCurrentRow(record);
    form.setFieldsValue({
      ...record,
      logo: record.logo ? [
        {
          uid: '-1',
          name: 'image.png',
          status: 'done',
          url: record.logo,
          response: { url: record.logo },
        }
      ] : undefined,
    });
    handleUpdateModalVisible(true);
  };

  const columns: ProColumns<API.School>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      hideInForm: true,
    },
    {
      title: '学校名称',
      dataIndex: 'name',
    },
    {
      title: '学校Logo',
      dataIndex: 'logo',
      valueType: 'image',
      hideInSearch: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueEnum: {
        0: { text: '禁用', status: 'Error' },
        1: { text: '启用', status: 'Success' },
      },
    },
    {
      title: '平台收益占比',
      dataIndex: 'profitPlat',
      renderText: (val: number) => `${val}%`,
      hideInSearch: true,
    },
    {
      title: '代理收益占比',
      dataIndex: 'profitAgent',
      renderText: (val: number) => `${val}%`,
      hideInSearch: true,
    },
    {
      title: '跑腿收益占比',
      dataIndex: 'profitRunner',
      renderText: (val: number) => `${val}%`,
      hideInSearch: true,
    },
    {
      title: '底价',
      dataIndex: 'floorPrice',
      valueType: 'money',
      hideInSearch: true,
    },
    {
      title: '代理人ID',
      dataIndex: 'belongUid',
      hideInSearch: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="edit"
          onClick={() => {
            handleEdit(record);
          }}
        >
          编辑
        </a>,
        <a
          key="delete"
          onClick={() => {
            Modal.confirm({
              title: '确认删除',
              content: '确定要删除这条记录吗？',
              onOk: async () => {
                try {
                  await get1({ id: record.id! });
                  message.success('删除成功');
                  actionRef.current?.reload();
                } catch (error) {
                  message.error('删除失败，请重试');
                }
              },
            });
          }}
        >
          删除
        </a>,
      ],
    },
  ];

  const handleAdd = async (fields: API.School) => {
    const hide = message.loading('正在添加');
    try {
      await addAgent1(fields);
      hide();
      message.success('添加成功');
      handleModalVisible(false);
      actionRef.current?.reload();
      return true;
    } catch (error) {
      hide();
      message.error('添加失败，请重试');
      return false;
    }
  };

  const handleUpdate = async (fields: API.School) => {
    const hide = message.loading('正在更新');
    try {
      await editAgent1({
        ...currentRow,
        ...fields,
      });
      hide();
      message.success('更新成功');
      handleUpdateModalVisible(false);
      actionRef.current?.reload();
      return true;
    } catch (error) {
      hide();
      message.error('更新失败，请重试');
      return false;
    }
  };

  return (
    <PageContainer>
      <ProTable<API.School>
        headerTitle="校区列表"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => handleModalVisible(true)}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params, sort, filter) => {
          const { current, pageSize, ...restParams } = params;
          
          const response = await list6({
            pageQuery: {
              pageSize,
              pageNum: current,
            },
            school: restParams,
          });
          
          return {
            data: response.rows || [],
            success: response.code === 200,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />

      <ModalForm
        title="新建校区"
        width="400px"
        visible={createModalVisible}
        onVisibleChange={handleModalVisible}
        onFinish={handleAdd}
      >
        <ProFormText
          rules={[
            {
              required: true,
              message: '校区名称为必填项',
            },
          ]}
          label="校区名称"
          name="name"
        />
        <ProFormUploadButton
          label="学校Logo"
          name="logo"
          max={1}
          fieldProps={{
            name: 'file',
            listType: 'picture-card',
            showUploadList: true,
            customRequest: async ({ file, onSuccess, onError }) => {
              console.log(22222);
              try {
                const res = await upload(
                  { type: 7, name: 'school' },
                  {},
                  file as File
                );
                if (res.code === 200 && res.data) {
                  onSuccess({ url: res.data.url || res.data });
                } else {
                  onError(new Error('上传失败'));
                }
              } catch (error) {
                onError(new Error('上传失败'));
              }
            },
          }}
          transform={(value) => {
            if (!value) return { logo: '' };
            return { logo: value?.[0]?.response?.url || '' };
          }}
        />
        <ProFormSelect
          name="status"
          label="状态"
          options={[
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 },
          ]}
          rules={[{ required: true }]}
        />
        <ProFormDigit
          label="平台收益占比"
          name="profitPlat"
          min={0}
          max={100}
          fieldProps={{ addonAfter: '%' }}
        />
        <ProFormDigit
          label="代理收益占比"
          name="profitAgent"
          min={0}
          max={100}
          fieldProps={{ addonAfter: '%' }}
        />
        <ProFormDigit
          label="跑腿收益占比"
          name="profitRunner"
          min={0}
          max={100}
          fieldProps={{ addonAfter: '%' }}
        />
        <ProFormDigit
          label="底价"
          name="floorPrice"
          min={0}
          fieldProps={{ precision: 2 }}
        />
        <ProFormText
          name="adcode"
          label="行政区划代码"
          rules={[
            {
              required: true,
              message: '请输入行政区划代码',
            },
            {
              pattern: /^\d{6}$/,
              message: '行政区划代码必须是6位数字',
            },
          ]}
          placeholder="请输入6位数字的行政区划代码"
          fieldProps={{
            maxLength: 6,
          }}
        />
        <ProFormDigit
          name="belongUid"
          label="代理人ID"
          rules={[
            {
              required: true,
              message: '请输入代理人ID',
            },
          ]}
          placeholder="请输入代理人ID"
          min={1}
          fieldProps={{
            precision: 0,
          }}
        />
      </ModalForm>

      <ModalForm
        title="编辑校区"
        width="400px"
        visible={updateModalVisible}
        onVisibleChange={(visible) => {
          if (!visible) {
            handleUpdateModalClose();
          }
        }}
        form={form}
        onFinish={handleUpdate}
      >
        <ProFormText
          rules={[
            {
              required: true,
              message: '校区名称为必填项',
            },
          ]}
          label="校区名称"
          name="name"
        />
        <ProFormUploadButton
          label="学校Logo"
          name="logo"
          max={1}
          fieldProps={{
            name: 'file',
            listType: 'picture-card',
            showUploadList: true,
            customRequest: async ({ file, onSuccess, onError }) => {
              try {
                const res = await upload(
                  { type: 7, name: 'school' },
                  {},
                  file as File
                );
                if (res.code === 200 && res.data) {
                  const url = res.data.url || res.data;
                  onSuccess({ url });
                } else {
                  onError(new Error('上传失败'));
                }
              } catch (error) {
                onError(new Error('上传失败'));
              }
            },
          }}
          transform={(value) => {
            if (!value || value.length === 0) return { logo: '' };
            if (value[0].url) return { logo: value[0].url };
            return { logo: value[0]?.response?.url || '' };
          }}
        />
        <ProFormSelect
          name="status"
          label="状态"
          options={[
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 },
          ]}
          rules={[{ required: true }]}
        />
        <ProFormDigit
          label="平台收益占比"
          name="profitPlat"
          min={0}
          max={100}
          fieldProps={{ addonAfter: '%' }}
        />
        <ProFormDigit
          label="代理收益占比"
          name="profitAgent"
          min={0}
          max={100}
          fieldProps={{ addonAfter: '%' }}
        />
        <ProFormDigit
          label="跑腿收益占比"
          name="profitRunner"
          min={0}
          max={100}
          fieldProps={{ addonAfter: '%' }}
        />
        <ProFormDigit
          label="底价"
          name="floorPrice"
          min={0}
          fieldProps={{ precision: 2 }}
        />
        <ProFormText
          name="adcode"
          label="行政区划代码"
          rules={[
            {
              required: true,
              message: '请输入行政区划代码',
            },
            {
              pattern: /^\d{6}$/,
              message: '行政区划代码必须是6位数字',
            },
          ]}
          placeholder="请输入6位数字的行政区划代码"
          fieldProps={{
            maxLength: 6,
          }}
        />
        <ProFormDigit
          name="belongUid"
          label="代理人ID"
          rules={[
            {
              required: true,
              message: '请输入代理人ID',
            },
          ]}
          placeholder="请输入代理人ID"
          min={1}
          fieldProps={{
            precision: 0,
          }}
        />
      </ModalForm>
    </PageContainer>
  );
};

export default TableList; 