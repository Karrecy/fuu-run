import { PlusOutlined } from '@ant-design/icons';
import { Button, message, Modal, Descriptions, Image, Divider, Dropdown } from 'antd';
import React, { useState, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Access, useAccess } from '@umijs/max';
import { addAgent, edit, listPc, remove1 as remove, resetPwd } from '@/services/test-swagger/userPcController';
import type { ProFormInstance } from '@ant-design/pro-form';
import {
  ModalForm,
  ProFormText,
  ProFormSelect,
  ProFormUploadButton,
  ProFormRadio,
} from '@ant-design/pro-form';
import { upload } from '@/services/test-swagger/ossController';
import type { MenuProps } from 'antd';
import { EllipsisOutlined } from '@ant-design/icons';

const UserPcList: React.FC = () => {
  const [createModalVisible, handleCreateModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [detailModalVisible, handleDetailModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<API.User>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedRows, setSelectedRows] = useState<API.User[]>([]);
  
  const actionRef = useRef<ActionType>();
  const formRef = useRef<ProFormInstance>();
  const access = useAccess();

  const columns: ProColumns<API.User>[] = [
    {
      title: 'UID',
      dataIndex: 'uid',
      key: 'uid',
      hideInTable: true,
    },
    {
      title: '用户名',
      dataIndex: ['userPc', 'username'],
      key: 'username',
      formItemProps: {
        rules: [{ required: true, message: '请输入用户名' }],
      },
    },
    {
      title: '手机号',
      dataIndex: ['userPc', 'phone'],
      key: 'phone',
    },
    {
      title: '真实姓名',
      dataIndex: ['userPc', 'name'],
      key: 'name',
    },
    {
      title: '头像',
      dataIndex: ['userPc', 'avatar'],
      hideInSearch: true,
      render: (_, record) => (
        <Image
          src={record.userPc?.avatar}
          width={32}
          height={32}
          style={{ borderRadius: '50%' }}
          preview={{
            src: record.userPc?.avatar,
          }}
        />
      ),
    },
    {
      title: '性别',
      dataIndex: ['userPc', 'sex'],
      key: 'sex',
      valueEnum: {
        0: { text: '女', status: 'Default' },
        1: { text: '男', status: 'Success' },
      },
      valueType: 'select',
    },
    {
      title: '状态',
      dataIndex: ['userPc', 'status'],
      key: 'status',
      valueEnum: {
        0: { text: '禁用', status: 'Error' },
        1: { text: '启用', status: 'Success' },
      },
      valueType: 'select',
    },
    {
      title: '用户类型',
      dataIndex: 'userType',
      key: 'userType',
      valueEnum: {
        0: { text: '超级管理员', status: 'Success' },
        1: { text: '校区管理员', status: 'Processing' },
        2: { text: '普通管理员', status: 'Default' },
      },
      valueType: 'select',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTimeRange',
      hideInTable: true,
      search: {
        transform: (value) => {
          return {
            createTimeBegin: value[0],
            createTimeEnd: value[1],
          };
        },
      },
    },
    {
      title: '最后登录时间',
      dataIndex: 'loginTime',
      valueType: 'dateTimeRange',
      hideInTable: true,
      search: {
        transform: (value) => {
          return {
            loginTimeBegin: value[0],
            loginTimeEnd: value[1],
          };
        },
      },
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTimeRange',
      hideInTable: true,
      search: {
        transform: (value) => {
          return {
            updateTimeBegin: value[0],
            updateTimeEnd: value[1],
          };
        },
      },
    },
    {
      title: '操作',
      valueType: 'option',
      render: (_, record) => [
        <Access key="edit" accessible={access.hasPerms('system:userpc:edit')}>
          <a onClick={() => {
            setCurrentRow(record);
            handleUpdateModalVisible(true);
          }}>
            编辑
          </a>
        </Access>,
        <a key="detail" onClick={() => {
          setCurrentRow(record);
          handleDetailModalVisible(true);
        }}>
          详情
        </a>,
        <Dropdown
          key="more"
          menu={{
            items: [
              {
                key: 'resetPwd',
                label: (
                  <Access accessible={access.hasPerms('system:userpc:resetPwd')}>
                    <a onClick={() => {
                      Modal.confirm({
                        title: '确认重置密码',
                        content: '确定要重置该用户的密码吗？',
                        onOk: () => handleResetPwd(record.userPc?.uid),
                      });
                    }}>
                      重置密码
                    </a>
                  </Access>
                ),
              },
              {
                key: 'delete',
                label: (
                  <Access accessible={access.hasPerms('system:userpc:delete')}>
                    <a onClick={() => {
                      Modal.confirm({
                        title: '确认删除',
                        content: '确定要删除该用户吗？',
                        onOk: () => handleDelete([record.userPc?.uid || 0]),
                      });
                    }}>
                      删除
                    </a>
                  </Access>
                ),
              },
            ],
          }}
        >
          <a>
            <EllipsisOutlined />
          </a>
        </Dropdown>,
      ],
    },
  ];

  // 处理函数
  const handleAdd = async (fields: API.UserPc) => {
    const hide = message.loading('正在添加...');
    console.log(fields);
    try {
      const res = await addAgent({
        ...fields,
        id: 0,
      });
      if (res.code === 200) {
        message.success('添加成功');
        handleCreateModalVisible(false);
        actionRef.current?.reload();
        return true;
      }
      message.error(res.msg || '添加失败');
      return false;
    } finally {
      hide();
    }
  };

  const handleUpdate = async (fields: API.UserPc) => {
    const hide = message.loading('正在更新...');
    try {
      const res = await edit({
        ...fields,
        id: currentRow?.userPc?.id,
        uid: currentRow?.userPc?.uid,
      });
      if (res.code === 200) {
        message.success('更新成功');
        handleUpdateModalVisible(false);
        actionRef.current?.reload();
        return true;
      }
      message.error(res.msg || '更新失败');
      return false;
    } finally {
      hide();
    }
  };

  const handleDelete = async (uIds: number[]) => {
    const hide = message.loading('正在删除...');
    try {
      const res = await remove({ uIds: uIds.join(',') });
      if (res.code === 200) {
        message.success('删除成功');
        actionRef.current?.reload();
        setSelectedRows([]);
        setSelectedRowKeys([]);
        return true;
      }
      message.error(res.msg || '删除失败');
      return false;
    } finally {
      hide();
    }
  };

  const handleResetPwd = async (uId: number) => {
    const hide = message.loading('正在重置密码...');
    try {
      const res = await resetPwd({ uId });
      if (res.code === 200) {
        message.success('密码重置成功');
        return true;
      }
      message.error(res.msg || '密码重置失败');
      return false;
    } finally {
      hide();
    }
  };

  return (
    <PageContainer>
      <ProTable<API.User>
        actionRef={actionRef}
        rowKey="uid"
        search={{
          labelWidth: 120,
          defaultCollapsed: false,
        }}
        toolBarRender={() => [
          <Access accessible={access.hasPerms('system:userpc:add')}>
            <Button
              type="primary"
              onClick={() => handleCreateModalVisible(true)}
            >
              <PlusOutlined /> 新建
            </Button>
          </Access>,
          selectedRowKeys.length > 0 && (
            <Access accessible={access.hasPerms('system:userpc:delete')}>
              <Button
                danger
                onClick={() => {
                  if (!selectedRowKeys.length) {
                    message.warning('请选择要删除的记录');
                    return;
                  }
                  const uIds = selectedRows
                    .map(row => row.userPc?.uid)
                    .filter((uid): uid is number => uid !== undefined && uid !== null);
                  Modal.confirm({
                    title: '确认删除',
                    content: `确定要删除这${selectedRowKeys.length}条记录吗？`,
                    onOk: () => handleDelete(uIds),
                  });
                }}
              >
                批量删除
              </Button>
            </Access>
          ),
        ]}
        request={async (params, sorter, filter) => {
          try {
            const queryParams: API.listPcParams = {
              userPcQuery: {
                uid: params.uid ? Number(params.uid) : undefined,
                username: params.username,
                phone: params.phone,
                name: params.name,
                sex: params.sex ? Number(params.sex) : undefined,
                status: params.status ? Number(params.status) : undefined,
                userType: params.userType ? Number(params.userType) : undefined,
                loginRegion: params.loginRegion,
                createId: params.createId,
              },
              pageQuery: {
                pageSize: params.pageSize,
                pageNum: params.current,
              },
            };

            // 添加时间范围参数
            if (params.createTimeBegin) {
              queryParams.userPcQuery[`params['createTimeBegin']`] = params.createTimeBegin;
              queryParams.userPcQuery[`params['createTimeEnd']`] = params.createTimeEnd;
            }
            if (params.loginTimeBegin) {
              queryParams.userPcQuery[`params['loginTimeBegin']`] = params.loginTimeBegin;
              queryParams.userPcQuery[`params['loginTimeEnd']`] = params.loginTimeEnd;
            }
            if (params.updateTimeBegin) {
              queryParams.userPcQuery[`params['updateTimeBegin']`] = params.updateTimeBegin;
              queryParams.userPcQuery[`params['updateTimeEnd']`] = params.updateTimeEnd;
            }

            // 移除所有空值
            const cleanParams = JSON.parse(JSON.stringify(queryParams));

            // 移除 userPcQuery 中的空值
            Object.keys(cleanParams.userPcQuery).forEach(key => {
              if (cleanParams.userPcQuery[key] === undefined) {
                delete cleanParams.userPcQuery[key];
              }
            });

            console.log('查询参数:', cleanParams);

            const res = await listPc(cleanParams);
            return {
              data: res.rows || [],
              success: res.code === 200,
              total: res.total || 0,
            };
          } catch (error) {
            message.error('获取数据失败');
            return {
              data: [],
              success: false,
              total: 0,
            };
          }
        }}
        columns={columns}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys, rows) => {
            setSelectedRowKeys(keys);
            setSelectedRows(rows);
          },
        }}
      />

      <ModalForm
        title="新建用户"
        width="500px"
        visible={createModalVisible}
        onVisibleChange={handleCreateModalVisible}
        onFinish={handleAdd}
        formRef={formRef}
      >
        <ProFormSelect
          name="userType"
          label="用户类型"
          options={[
            { label: '超级管理员', value: 0 },
            { label: '校区管理员', value: 1 },
            { label: '普通管理员', value: 2 },
          ]}
          rules={[{ required: true, message: '用户类型不能为空' }]}
        />
        <ProFormText
          name="username"
          label="用户名"
          rules={[{ required: true, message: '请输入用户名' }, { min: 6, max: 20, message: '账户长度必须在6到20个字符之间' }]}
        />
        <ProFormText
          name="password"
          label="密码"
          rules={[{ required: true, message: '密码不能为空' }, { min: 6, max: 20, message: '密码长度必须在6到20个字符之间' }]}
          type="password"
        />
        <ProFormText
          name="phone"
          label="手机号"
          rules={[{ required: true, message: '请输入手机号' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确' }]}
        />
        <ProFormText
          name="name"
          label="真实姓名"
          rules={[{ required: true, message: '请输入真实姓名' }]}
        />
        <ProFormUploadButton
          name="studentCardUrl"
          label="学生证"
          max={1}
          fieldProps={{
            name: 'file',
            listType: 'picture-card',
            maxCount: 1,
            customRequest: async ({ file, onSuccess, onError }) => {
              try {
                const res = await upload(
                  {}, // params
                  { type: 5, name: '' }, // body, type为学生证
                  file as File
                );
                if (res.code === 200 && res.data) {
                  onSuccess?.(res);
                } else {
                  onError?.(new Error(res.msg || '上传失败'));
                }
              } catch (error) {
                onError?.(error);
              }
            },
          }}
          transform={(value) => {
            if (value && value.length > 0 && value[0].response) {
              return value[0].response.data.url;
            }
            return undefined;
          }}
          rules={[{ required: true, message: '缺少学生证' }]}
        />
        <ProFormUploadButton
          name="idCardUrl"
          label="身份证"
          max={1}
          fieldProps={{
            name: 'file',
            listType: 'picture-card',
            maxCount: 1,
            customRequest: async ({ file, onSuccess, onError }) => {
              try {
                const res = await upload(
                  {}, // params
                  { type: 8, name: '' }, // body, type为身份证
                  file as File
                );
                if (res.code === 200 && res.data) {
                  onSuccess?.(res);
                } else {
                  onError?.(new Error(res.msg || '上传失败'));
                }
              } catch (error) {
                onError?.(error);
              }
            },
          }}
          transform={(value) => {
            if (value && value.length > 0 && value[0].response) {
              return value[0].response.data.url;
            }
            return undefined;
          }}
          rules={[{ required: true, message: '缺少身份证' }]}
        />
        <ProFormRadio.Group
          name="sex"
          label="性别"
          options={[
            { label: '男', value: 1 },
            { label: '女', value: 0 },
          ]}
          rules={[{ required: true, message: '请先设置性别' }]}
        />
        <ProFormRadio.Group
          name="status"
          label="状态"
          options={[
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 },
          ]}
          rules={[{ required: true, message: '请先设置用户状态' }]}
        />
        <ProFormUploadButton
          name="avatar"
          label="头像"
          max={1}
          fieldProps={{
            name: 'file',
            listType: 'picture-card',
            maxCount: 1,
            customRequest: async ({ file, onSuccess, onError }) => {
              try {
                const res = await upload(
                  {}, // params
                  { type: 4, name: '' }, // body, type为用户头像
                  file as File
                );
                if (res.code === 200 && res.data) {
                  onSuccess?.(res);
                } else {
                  onError?.(new Error(res.msg || '上传失败'));
                }
              } catch (error) {
                onError?.(error);
              }
            },
          }}
          transform={(value) => {
            if (value && value.length > 0 && value[0].response) {
              return value[0].response.data.url;
            }
            return undefined;
          }}
          rules={[{ required: true, message: '请先设置用户头像' }]}
        />
        <ProFormRadio.Group
          name="emailEnable"
          label="邮件启用状态"
          options={[
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 },
          ]}
          rules={[{ required: true, message: '请先设置邮件启用状态' }]}
        />
        <ProFormText
          name="email"
          label="邮箱"
          rules={[{ required: true, type: 'email', message: '请先设置邮件' }]}
        />
      </ModalForm>

      <ModalForm
        title="编辑用户"
        width="500px"
        visible={updateModalVisible}
        onVisibleChange={handleUpdateModalVisible}
        onFinish={handleUpdate}
        initialValues={{
          ...currentRow?.userPc,
          id: currentRow?.userPc?.id,
          userType: currentRow?.userType,
        }}
        modalProps={{
          destroyOnClose: true,
          onCancel: () => {
            handleUpdateModalVisible(false);
            setCurrentRow(undefined);
          }
        }}
        request={async () => {
          return {
            ...currentRow?.userPc,
            id: currentRow?.userPc?.id,
            userType: currentRow?.userType,
          };
        }}
      >
        <ProFormText
          name="id"
          hidden
        />
        <ProFormSelect
          name="userType"
          label="用户类型"
          options={[
            { label: '超级管理员', value: 0 },
            { label: '校区管理员', value: 1 },
            { label: '普通管理员', value: 2 },
          ]}
          rules={[{ required: true, message: '用户类型不能为空' }]}
        />
        <ProFormText
          name="username"
          label="用户名"
          rules={[{ required: true, message: '请输入用户名' }]}
        />
        <ProFormText
          name="phone"
          label="手机号"
          rules={[{ required: true, message: '请输入手机号' }]}
        />
        <ProFormText
          name="name"
          label="真实姓名"
          rules={[{ required: true, message: '请输入真实姓名' }]}
        />
        <ProFormRadio.Group
          name="sex"
          label="性别"
          options={[
            { label: '男', value: 1 },
            { label: '女', value: 0 },
          ]}
          rules={[{ required: true, message: '请先设置性别' }]}
        />
        <ProFormRadio.Group
          name="status"
          label="状态"
          options={[
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 },
          ]}
          rules={[{ required: true, message: '请先设置用户状态' }]}
        />
        <ProFormRadio.Group
          name="emailEnable"
          label="邮件启用状态"
          options={[
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 },
          ]}
          rules={[{ required: true, message: '请先设置邮件启用状态' }]}
        />
        <ProFormText
          name="email"
          label="邮箱"
          rules={[{ required: true, type: 'email', message: '请先设置邮件' }]}
        />
      </ModalForm>

      <Modal
        title="用户详情"
        width="800px"
        open={detailModalVisible}
        onCancel={() => handleDetailModalVisible(false)}
        footer={null}
      >
        <Descriptions column={2}>
          <Descriptions.Item label="UID">{currentRow?.uid}</Descriptions.Item>
          <Descriptions.Item label="用户类型">
            {currentRow?.userType === 0 ? '超级管理员' : 
             currentRow?.userType === 1 ? '校区管理员' : 
             currentRow?.userType === 2 ? '普通管理员' : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">{currentRow?.createTime}</Descriptions.Item>
          <Descriptions.Item label="最后登录时间">{currentRow?.loginTime}</Descriptions.Item>
          <Descriptions.Item label="登录IP">{currentRow?.loginIp}</Descriptions.Item>
          <Descriptions.Item label="登录地点">{currentRow?.loginRegion}</Descriptions.Item>
          <Descriptions.Item label="创建人ID">{currentRow?.createId}</Descriptions.Item>
          <Descriptions.Item label="更新时间">{currentRow?.updateTime}</Descriptions.Item>
        </Descriptions>
        
        <Divider>PC用户信息</Divider>
        
        <Descriptions column={2}>
          <Descriptions.Item label="用户名">{currentRow?.userPc?.username}</Descriptions.Item>
          <Descriptions.Item label="手机号">{currentRow?.userPc?.phone}</Descriptions.Item>
          <Descriptions.Item label="真实姓名">{currentRow?.userPc?.name}</Descriptions.Item>
          <Descriptions.Item label="邮箱">{currentRow?.userPc?.email}</Descriptions.Item>
          <Descriptions.Item label="性别">
            {currentRow?.userPc?.sex === 1 ? '男' : '女'}
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            {currentRow?.userPc?.status === 1 ? '启用' : '禁用'}
          </Descriptions.Item>
          <Descriptions.Item label="头像">
            <Image
              src={currentRow?.userPc?.avatar}
              width={64}
              height={64}
              style={{ borderRadius: '50%' }}
            />
          </Descriptions.Item>
          <Descriptions.Item label="学生证">
            {currentRow?.userPc?.studentCardUrl && (
              <Image src={currentRow?.userPc?.studentCardUrl} width={100} />
            )}
          </Descriptions.Item>
          <Descriptions.Item label="身份证">
            {currentRow?.userPc?.idCardUrl && (
              <Image src={currentRow?.userPc?.idCardUrl} width={100} />
            )}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </PageContainer>
  );
};

export default UserPcList;
