import { PlusOutlined, EyeOutlined } from '@ant-design/icons';
import { Button, message, Modal, Descriptions, Image, Tag, Space, Select } from 'antd';
import React, { useState, useRef, useEffect } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProForm, {
  ModalForm,
  ProFormText,
  ProFormSelect,
  ProFormDigit,
} from '@ant-design/pro-form';
import { listWx as list, editAgent } from '@/services/test-swagger/userWxController';
import { list5 as listSchools } from '@/services/test-swagger/schoolController';
import { list as listXcxUsers } from '@/services/test-swagger/userWxController';

const TableList: React.FC = () => {
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [detailModalVisible, handleDetailModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<API.User>();
  const actionRef = useRef<ActionType>();

  // 查询条件状态
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
  }, [selectedSchool]);

  const columns: ProColumns<API.User>[] = [
    {
      title: 'UID',
      dataIndex: 'uid',
      key: 'uid',
      hideInTable: true,
    },
    {
      title: '设备类型',
      dataIndex: 'deviceType',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '用户类型',
      dataIndex: 'userType',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '头像',
      dataIndex: ['userWx', 'avatar'],
      hideInSearch: true,
      render: (avatar) => avatar ? (
        <Image
          src={avatar}
          width={40}
          height={40}
          style={{ borderRadius: '50%', objectFit: 'cover' }}
          preview={{
            mask: <EyeOutlined />,
            maskStyle: { borderRadius: '50%' }
          }}
        />
      ) : null,
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      render: (_, record) => record.userWx?.nickname,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      render: (_, record) => record.userWx?.phone,
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
      title: '登录IP',
      dataIndex: 'loginIp',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '登录地区',
      dataIndex: 'loginRegion',
      hideInSearch: true,
    },
    {
      title: '创建人ID',
      dataIndex: 'createId',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '更新人ID',
      dataIndex: 'updateId',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: 'OpenID',
      dataIndex: 'openid',
      hideInTable: true,
    },
    {
      title: '积分',
      dataIndex: ['userWx', 'points'],
      hideInTable: true,
      hideInSearch: true, 
    },
    {
      title: '是否跑腿',
      dataIndex: 'isRunner',
      valueEnum: {
        0: { text: '否', status: 'Default' },
        1: { text: '是', status: 'Success' },
      },
      render: (_, record) => record.userWx?.isRunner === 1 ? '是' : '否',
    },
    {
      title: '下单权限',
      dataIndex: 'canOrder',
      valueEnum: {
        0: { text: '禁止', status: 'Error' },
        1: { text: '允许', status: 'Success' },
      },
      render: (_, record) => record.userWx?.canOrder === 1 ? '允许' : '禁止',
    },
    {
      title: '接单权限',
      dataIndex: 'canTake',
      valueEnum: {
        0: { text: '禁止', status: 'Error' },
        1: { text: '允许', status: 'Success' },
      },
      render: (_, record) => record.userWx?.canTake === 1 ? '允许' : '禁止',
    },
    {
      title: '学校ID',
      dataIndex: ['userWx', 'schoolId'],
      hideInTable: true,
    },
    {
      title: '真实姓名',
      dataIndex: 'realname',
      hideInTable: true,
      render: (_, record) => record.userWx?.realname,
    },
    {
      title: '性别',
      dataIndex: 'gender',
      valueEnum: {
        0: { text: '女', status: 'Default' },
        1: { text: '男', status: 'Success' },
      },
      hideInTable: true,
      render: (_, record) => record.userWx?.gender === 1 ? '男' : '女',
    },
    {
      title: '用户类型',
      dataIndex: 'userType',
      valueEnum: {
        0: { text: '超级管理员', status: 'Success' },
        1: { text: '校区管理员', status: 'Processing' },
        2: { text: '普通管理员', status: 'Default' },
        3: { text: '普通用户', status: 'Default' },
        4: { text: '跑腿用户', status: 'Processing' },
      },
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="detail"
          onClick={() => {
            setCurrentRow(record);
            handleDetailModalVisible(true);
          }}
        >
          详情
        </a>,
        <a
          key="edit"
          onClick={() => {
            handleUpdateModalVisible(true);
            setCurrentRow(record);
          }}
        >
          编辑
        </a>,
      ],
    },
  ];

  const detailColumns: { label: string; key: string }[] = [
    { label: 'UID', key: 'uid' },
    { label: '创建时间', key: 'createTime' },
    { label: '更新时间', key: 'updateTime' },
    { label: '最后登录时间', key: 'loginTime' },
    { label: '登录地区', key: 'loginRegion' },
    { label: '创建人ID', key: 'createId' },
  ];

  const handleEdit = async (fields: API.UserWx) => {
    const hide = message.loading('正在更新...');
    try {
      const res = await editAgent({
        id: currentRow?.userWx?.id,
        uid: currentRow?.userWx?.uid,
        canOrder: fields.canOrder,
        canTake: fields.canTake,
      });
      if (res.code === 200) {
        message.success('更新成功');
        handleUpdateModalVisible(false);
        actionRef.current?.reload();
        return true;
      }
      message.error(res.msg || '更新失败');
      return false;
    } catch (error) {
      message.error('更新失败，请重试');
      return false;
    } finally {
      hide();
    }
  };

  const handleRemove = async (selectedRows: API.UserWx[]) => {
    message.warning('功能暂未实现');
    return false;
  };

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
        <Button onClick={() => {
          setSelectedSchool(undefined);
          actionRef.current?.reload();
        }}>
          重置
        </Button>
      </div>

      <ProTable<API.User>
        headerTitle="小程序用户列表"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          
        ]}
        request={async (params, sorter, filter) => {
          try {
            const queryParams: API.listWxParams = {
              userWxQuery: {
                // 基本查询条件
                uid: params.uid ? Number(params.uid) : undefined,
                openid: params.openid,
                nickname: params.nickname,
                phone: params.phone,
                isRunner: params.isRunner ? Number(params.isRunner) : undefined,
                canOrder: params.canOrder ? Number(params.canOrder) : undefined,
                canTake: params.canTake ? Number(params.canTake) : undefined,
                schoolId: selectedSchool, // 保持校区查询不变
                realname: params.realname,
                gender: params.gender ? Number(params.gender) : undefined,
                loginRegion: params.loginRegion,
                userType: params.userType ? Number(params.userType) : undefined,
                createId: params.createId ? Number(params.createId) : undefined,
              },
              pageQuery: {
                pageSize: params.pageSize,
                pageNum: params.current,
              },
            };

            // 添加时间范围参数
            if (params.createTimeBegin) {
              queryParams.userWxQuery[`params['createTimeBegin']`] = params.createTimeBegin;
              queryParams.userWxQuery[`params['createTimeEnd']`] = params.createTimeEnd;
            }
            if (params.loginTimeBegin) {
              queryParams.userWxQuery[`params['loginTimeBegin']`] = params.loginTimeBegin;
              queryParams.userWxQuery[`params['loginTimeEnd']`] = params.loginTimeEnd;
            }
            if (params.updateTimeBegin) {
              queryParams.userWxQuery[`params['updateTimeBegin']`] = params.updateTimeBegin;
              queryParams.userWxQuery[`params['updateTimeEnd']`] = params.updateTimeEnd;
            }

            // 移除所有空值
            const cleanParams = JSON.parse(JSON.stringify(queryParams));

            // 移除 userWxQuery 中的空值
            Object.keys(cleanParams.userWxQuery).forEach(key => {
              if (cleanParams.userWxQuery[key] === undefined) {
                delete cleanParams.userWxQuery[key];
              }
            });

            console.log('查询参数:', cleanParams);

            const res = await list(cleanParams);
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
      />

      <ModalForm
        title="编辑用户"
        width="400px"
        visible={updateModalVisible}
        onVisibleChange={handleUpdateModalVisible}
        onFinish={handleEdit}
        initialValues={{
          canOrder: currentRow?.userWx?.canOrder,
          canTake: currentRow?.userWx?.canTake,
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
            canOrder: currentRow?.userWx?.canOrder,
            canTake: currentRow?.userWx?.canTake,
          };
        }}
      >
        <ProFormSelect
          name="canOrder"
          label="下单权限"
          options={[
            { label: '允许', value: 1 },
            { label: '禁止', value: 0 },
          ]}
          rules={[{ required: true, message: '请选择下单权限' }]}
        />
        <ProFormSelect
          name="canTake"
          label="接单权限"
          options={[
            { label: '允许', value: 1 },
            { label: '禁止', value: 0 },
          ]}
          rules={[{ required: true, message: '请选择接单权限' }]}
        />
      </ModalForm>

      <Modal
        title="用户详情"
        width="600px"
        open={detailModalVisible}
        onCancel={() => handleDetailModalVisible(false)}
        footer={null}
      >
        <Descriptions column={1}>
          {detailColumns.map(({ label, key }) => (
            <Descriptions.Item key={key} label={label}>
              {currentRow?.[key as keyof API.User]}
            </Descriptions.Item>
          ))}
          <Descriptions.Item label="用户信息">
            <Descriptions column={1}>
              <Descriptions.Item label="OpenID">{currentRow?.userWx?.openid}</Descriptions.Item>
              <Descriptions.Item label="昵称">{currentRow?.userWx?.nickname}</Descriptions.Item>
              <Descriptions.Item label="手机号">{currentRow?.userWx?.phone}</Descriptions.Item>
              <Descriptions.Item label="积分">{currentRow?.userWx?.points}</Descriptions.Item>
              <Descriptions.Item label="是否跑腿">
                {currentRow?.userWx?.isRunner === 1 ? '是' : '否'}
              </Descriptions.Item>
              <Descriptions.Item label="下单权限">
                {currentRow?.userWx?.canOrder === 1 ? '允许' : '禁止'}
              </Descriptions.Item>
              <Descriptions.Item label="接单权限">
                {currentRow?.userWx?.canTake === 1 ? '允许' : '禁止'}
              </Descriptions.Item>
              <Descriptions.Item label="真实姓名">{currentRow?.userWx?.realname}</Descriptions.Item>
              <Descriptions.Item label="性别">
                {currentRow?.userWx?.gender === 1 ? '男' : '女'}
              </Descriptions.Item>
              <Descriptions.Item label="绑定学校">{currentRow?.userWx?.schoolName}</Descriptions.Item>
              <Descriptions.Item label="头像">
                {currentRow?.userWx?.avatar && (
                  <Image
                    width={100}
                    src={currentRow.userWx.avatar}
                    style={{ borderRadius: '50%', objectFit: 'cover' }}
                  />
                )}
              </Descriptions.Item>
            </Descriptions>
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </PageContainer>
  );
};

export default TableList;
