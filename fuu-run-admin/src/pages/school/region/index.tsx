import { PlusOutlined, EnvironmentOutlined, EyeOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { Button, message, Modal, Select, Form, Input, Popconfirm, Space, Card, Empty, Descriptions, Divider } from 'antd';
import React, { useState, useRef, useEffect } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProForm, {
  ModalForm,
  ProFormText,
  ProFormSelect,
  ProFormTextArea,
  ProFormDigit,
} from '@ant-design/pro-form';
import { add3 as addRegion, edit5 as editRegion, list7 as listRegion, remove5 as removeRegion } from '@/services/test-swagger/regionController';
import { list5 as listSchools } from '@/services/test-swagger/schoolController';
import { useModel } from '@umijs/max';

const RegionList: React.FC = () => {
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<API.SchoolRegion>();
  const [selectedSchool, setSelectedSchool] = useState<number>();
  const [schoolOptions, setSchoolOptions] = useState<{ label: string; value: number; }[]>([]);
  const [regionData, setRegionData] = useState<API.RegionVO[]>([]);
  const [form] = Form.useForm();
  const actionRef = useRef<ActionType>();
  const [detailVisible, setDetailVisible] = useState<boolean>(false);
  const [detailData, setDetailData] = useState<API.RegionVO | API.SchoolRegion>();

  // 获取当前用户信息
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser as API.CurrentUser & {
    user: {
      userType: number;
      userPc: {
        agentSchool: API.School;
        agentSchoolId: number;
      };
    };
  };
  const isSchoolAgent = currentUser?.user?.userType === 1;
  const agentSchool = currentUser?.user?.userPc?.agentSchool;
  const agentSchoolId = currentUser?.user?.userPc?.agentSchoolId;

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
          label: item.name || '',
          value: item.id || 0,
        }));
        setSchoolOptions(options);
      }
    } catch (error) {
      message.error('获取校区列表失败');
    }
  };

  // 获取区域数据
  const fetchRegionData = async (schoolId: number) => {
    try {
      const res = await listRegion({ schoolId });
      if (res.code === 200) {
        setRegionData(res.data || []);
      }
    } catch (error) {
      message.error('获取区域数据失败');
    }
  };

  useEffect(() => {
    // 只有非校区代理才获取校区列表
    if (!isSchoolAgent) {
      fetchSchoolList();
    } else if (agentSchoolId) {
      // 校区代理直接设置选中的校区并获取数据
      setSelectedSchool(agentSchoolId);
    }
  }, [isSchoolAgent, agentSchoolId]);

  useEffect(() => {
    if (selectedSchool) {
      fetchRegionData(selectedSchool);
    } else {
      setRegionData([]);
    }
  }, [selectedSchool]);

  const columns: ProColumns<API.RegionVO>[] = [
    {
      title: '区域名称',
      dataIndex: 'name',
      ellipsis: true,
      width: 180,
    },
    {
      title: '楼栋数量',
      width: 100,
      render: (_, record) => record.childrens?.length || 0,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      ellipsis: true,
      width: 200,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
    },
    {
      title: '操作',
      width: 120,
      valueType: 'option',
      render: (_, record) => [
        <Button
          key="detail"
          type="link"
          icon={<EyeOutlined />}
          onClick={() => handleDetailOpen(record)}
        />,
        <Button
          key="addBuilding"
          type="link"
          icon={<PlusOutlined />}
          onClick={() => handleModalOpen(undefined, record.id)}
        />,
        <Button
          key="edit"
          type="link"
          icon={<EditOutlined />}
          onClick={() => handleModalOpen(record)}
        />,
        <Popconfirm
          key="delete"
          title="确定要删除这个区域吗？删除后其下所有楼栋也将被删除！"
          onConfirm={() => handleDelete(record.id)}
        >
          <Button
            type="link"
            danger
            icon={<DeleteOutlined />}
          />
        </Popconfirm>,
      ],
    },
  ];

  const buildingColumns: ProColumns<API.SchoolRegion>[] = [
    {
      title: '楼栋名称',
      dataIndex: 'name',
      ellipsis: true,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      ellipsis: true,
    },
    {
      title: '操作',
      width: 90,
      valueType: 'option',
      render: (_, record) => [
        <Button
          key="detail"
          type="link"
          icon={<EyeOutlined />}
          onClick={() => handleDetailOpen(record)}
        />,
        <Button
          key="edit"
          type="link"
          icon={<EditOutlined />}
          onClick={() => handleModalOpen(record)}
        />,
        <Popconfirm
          key="delete"
          title="确定要删除这个楼栋吗？"
          onConfirm={() => handleDelete(record.id)}
        >
          <Button
            type="link"
            danger
            icon={<DeleteOutlined />}
          />
        </Popconfirm>,
      ],
    },
  ];

  // 删除区域
  const handleDelete = async (id: number) => {
    try {
      const response = await removeRegion({ schoolRegionIds: [id] });
      if (response.code === 200) {
        message.success('删除成功');
        // 刷新数据
        if (selectedSchool) {
          fetchRegionData(selectedSchool);
        }
      } else {
        message.error(response.msg || '删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 提交表单
  const handleSubmit = async (values: API.SchoolRegion) => {
    try {
      if (currentRow) {
        const response = await editRegion({
          ...values,
          id: currentRow.id,
        });
        if (response.code === 200) {
          message.success('更新成功');
          // 刷新数据
          if (selectedSchool) {
            fetchRegionData(selectedSchool);
          }
        } else {
          message.error(response.msg || '更新失败');
          return false;
        }
      } else {
        const response = await addRegion(values);
        if (response.code === 200) {
          message.success('添加成功');
          // 刷新数据
          if (selectedSchool) {
            fetchRegionData(selectedSchool);
          }
        } else {
          message.error(response.msg || '添加失败');
          return false;
        }
      }
      handleModalVisible(false);
      return true;
    } catch (error) {
      message.error('操作失败');
      return false;
    }
  };

  // 打开新增/编辑弹窗
  const handleModalOpen = (record?: API.RegionVO | API.SchoolRegion, parentId?: number) => {
    if (record) {
      // 编辑模式：设置所有字段
      setCurrentRow(record);
      form.setFieldsValue(record);
    } else {
      // 新建模式：只设置必要的默认字段
      setCurrentRow(undefined);
      form.resetFields(); // 先清空所有字段
      form.setFieldsValue({
        schoolId: selectedSchool,
        type: parentId ? 1 : 0,  // 有 parentId 时为楼栋，否则为区域
        parentId: parentId || 0,
      });
    }
    handleModalVisible(true);
  };

  // 打开详情弹窗
  const handleDetailOpen = (record: API.RegionVO | API.SchoolRegion) => {
    setDetailData(record);
    setDetailVisible(true);
  };

  return (
    <PageContainer>
      {isSchoolAgent && agentSchool && (
        <div 
          style={{ 
            marginBottom: 16,
            padding: '12px 24px',
            background: '#f5f5f5',
            borderRadius: '4px',
            fontSize: '14px'
          }}
        >
          当前校区：<span style={{ fontWeight: 'bold', color: '#1890ff' }}>{agentSchool}</span>
        </div>
      )}

      <div style={{ marginBottom: 16, display: 'flex', gap: 16, flexWrap: 'wrap', alignItems: 'center' }}>
        {/* 只有非校区代理才显示校区选择 */}
        {!isSchoolAgent && (
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
        {selectedSchool && (
          <Button
            type="primary"
            onClick={() => handleModalOpen()}
            icon={<PlusOutlined />}
          >
            新建区域
          </Button>
        )}
      </div>

      {!selectedSchool ? (
        <Empty description="请先选择校区" />
      ) : (
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(3, 1fr)', 
          gap: 16,
          padding: '8px 0'
        }}>
          {regionData.map((region) => (
            <Card
              key={region.id}
              title={
                <Space>
                  <EnvironmentOutlined />
                  {region.name}
                  {region.remark && (
                    <span style={{ fontSize: '12px', color: '#999' }}>
                      ({region.remark})
                    </span>
                  )}
                </Space>
              }
              style={{ 
                minWidth: 300,
                height: 400,
                display: 'flex',
                flexDirection: 'column'
              }}
              bodyStyle={{ 
                flex: 1,
                padding: '12px',
                overflow: 'hidden',
                display: 'flex',
                flexDirection: 'column'
              }}
              extra={
                <Space>
                  <Button
                    type="link"
                    size="small"
                    icon={<EyeOutlined />}
                    onClick={() => handleDetailOpen(region)}
                  />
                  <Button
                    type="link"
                    size="small"
                    icon={<PlusOutlined />}
                    onClick={() => handleModalOpen(undefined, region.id)}
                  />
                  <Button
                    type="link"
                    size="small"
                    icon={<EditOutlined />}
                    onClick={() => handleModalOpen(region)}
                  />
                  <Popconfirm
                    title="确定要删除这个区域吗？删除后其下所有楼栋也将被删除！"
                    onConfirm={() => handleDelete(region.id)}
                  >
                    <Button
                      type="link"
                      size="small"
                      danger
                      icon={<DeleteOutlined />}
                    />
                  </Popconfirm>
                </Space>
              }
            >
              {region.childrens && region.childrens.length > 0 ? (
                <div style={{ flex: 1, overflow: 'hidden' }}>
                  <ProTable<API.SchoolRegion>
                    columns={buildingColumns}
                    dataSource={region.childrens}
                    search={false}
                    options={false}
                    pagination={false}
                    rowKey="id"
                    scroll={{ y: 280 }}
                    tableStyle={{ 
                      height: '100%',
                    }}
                    cardProps={{ 
                      bodyStyle: { 
                        padding: 0,
                      },
                    }}
                  />
                </div>
              ) : (
                <Empty 
                  description="暂无楼栋" 
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                  style={{
                    margin: 'auto'
                  }}
                />
              )}
            </Card>
          ))}
        </div>
      )}

      <ModalForm
        title={currentRow ? '编辑' : (form.getFieldValue('type') === 0 ? '新建区域' : '新建楼栋')}
        width="500px"
        visible={modalVisible}
        onVisibleChange={(visible) => {
          if (!visible) {
            form.resetFields(); // 关闭时清空表单
          }
          handleModalVisible(visible);
        }}
        onFinish={handleSubmit}
        form={form}
      >
        <ProFormSelect
          name="schoolId"
          label="所属校区"
          rules={[{ required: true, message: '请选择校区' }]}
          disabled
          options={schoolOptions}
        />
        <ProFormSelect
          name="type"
          label="类型"
          disabled
          options={[
            { label: '区域', value: 0 },
            { label: '楼栋', value: 1 },
          ]}
          rules={[{ required: true, message: '请选择类型' }]}
        />
        {form.getFieldValue('type') === 1 && (
          <ProFormSelect
            name="parentId"
            label="所属区域"
            disabled
            options={regionData.map(region => ({
              label: region.name,
              value: region.id,
            }))}
            rules={[{ required: true, message: '请选择所属区域' }]}
          />
        )}
        <ProFormText
          name="name"
          label="名称"
          rules={[{ required: true, message: '请输入名称' }]}
          placeholder="请输入名称"
        />
        {form.getFieldValue('type') === 1 && (
          <>
            <ProFormText
              name="lon"
              label="经度"
              rules={[
                {
                  pattern: /^-?((0|1?[0-7]?[0-9]?)(([.][0-9]*)?)|180(([.][0]*)?))$/,
                  message: '请输入有效的经度',
                },
              ]}
              placeholder="请输入经度"
            />
            <ProFormText
              name="lat"
              label="纬度"
              rules={[
                {
                  pattern: /^-?([0-8]?[0-9]|90)([.][0-9]*)?$/,
                  message: '请输入有效的纬度',
                },
              ]}
              placeholder="请输入纬度"
            />
          </>
        )}
        <ProFormTextArea
          name="remark"
          label="备注"
          placeholder="请输入备注信息"
        />
      </ModalForm>

      {/* 添加详情弹窗 */}
      <Modal
        title={detailData?.type === 0 ? '区域详情' : '楼栋详情'}
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={600}
      >
        <Descriptions column={2} bordered>
          <Descriptions.Item label="名称" span={2}>
            {detailData?.name}
          </Descriptions.Item>
          <Descriptions.Item label="类型">
            {detailData?.type === 0 ? '区域' : '楼栋'}
          </Descriptions.Item>
          <Descriptions.Item label="所属校区">
            {schoolOptions.find(item => item.value === detailData?.schoolId)?.label}
          </Descriptions.Item>
          {detailData?.type === 1 && (
            <>
              <Descriptions.Item label="所属区域">
                {regionData.find(item => item.id === detailData?.parentId)?.name}
              </Descriptions.Item>
              <Descriptions.Item label="经度">
                {detailData?.lon || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="纬度">
                {detailData?.lat || '-'}
              </Descriptions.Item>
            </>
          )}
          {detailData?.type === 0 && (detailData as API.RegionVO).childrens?.length > 0 && (
            <Descriptions.Item label="楼栋数量" span={2}>
              {(detailData as API.RegionVO).childrens?.length || 0}
            </Descriptions.Item>
          )}
          <Descriptions.Item label="备注" span={2}>
            {detailData?.remark || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {detailData?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="创建人">
            {detailData?.createId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="修改时间">
            {detailData?.updateTime || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="修改人">
            {detailData?.updateId || '-'}
          </Descriptions.Item>
        </Descriptions>
        {detailData?.type === 0 && (detailData as API.RegionVO).childrens?.length > 0 && (
          <>
            <Divider>楼栋列表</Divider>
            <ProTable<API.SchoolRegion>
              columns={buildingColumns.filter(col => col.dataIndex !== 'type')}
              dataSource={(detailData as API.RegionVO).childrens}
              search={false}
              options={false}
              pagination={false}
              rowKey="id"
            />
          </>
        )}
      </Modal>
    </PageContainer>
  );
};

export default RegionList;
