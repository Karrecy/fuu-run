import React, { useEffect, useState } from 'react';
import { Card, Tree, Select, Button, message, Modal, Form, Input, InputNumber } from 'antd';
import type { DataNode } from 'antd/es/tree';
import { listPerms, list, roleperms, roleperms1, rolepermsDel } from '@/services/test-swagger/systemController';

const PermissionPage: React.FC = () => {
  const [treeData, setTreeData] = useState<DataNode[]>([]);
  const [checkedKeys, setCheckedKeys] = useState<React.Key[]>([]);
  const [roleType, setRoleType] = useState<number>();
  const [form] = Form.useForm();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isDeleteModalVisible, setIsDeleteModalVisible] = useState(false);
  const [deleteCheckedKeys, setDeleteCheckedKeys] = useState<React.Key[]>([]);

  // 将后端返回的权限数据转换为Tree组件需要的格式
  const convertToTreeData = (perms: API.Perm[]): DataNode[] => {
    return perms.map(perm => ({
      title: `${perm.name}${perm.perms ? ` (${perm.perms})` : ''} @${perm.id}@`,
      key: perm.id,
      children: perm.children ? convertToTreeData(perm.children) : undefined,
    }));
  };

  // 初始化加载所有权限数据
  useEffect(() => {
    const fetchAllPerms = async () => {
      try {
        const result = await listPerms();
        if (result) {
          const formattedData = convertToTreeData(result);
          setTreeData(formattedData);
        }
      } catch (error) {
        console.error('获取权限列表失败:', error);
      }
    };
    fetchAllPerms();
  }, []);

  // 根据角色类型获取已有权限
  const fetchRolePerms = async (userType: number) => {
    try {
      const result = await list({ userType });
      if (result?.data) {
        const permIds = result.data.map(perm => perm.id);
        setCheckedKeys(permIds);
      }
    } catch (error) {
      console.error('获取角色权限失败:', error);
    }
  };

  // 处理角色选择变化
  const handleRoleChange = (value: number) => {
    setRoleType(value);
    fetchRolePerms(value);
  };

  // 处理权限选择变化
  const onCheck = (checked: any, info: any) => {
    const checkedKeys = Array.isArray(checked) ? checked : checked.checked;
    setCheckedKeys(checkedKeys);
    console.log('选中的权限:', checkedKeys, info);
  };

  // 处理保存权限
  const handleSave = async () => {
    if (!roleType) {
      message.warning('请先选择角色类型');
      return;
    }

    try {
      // 将 checkedKeys 转换为 number[]
      const selectedPerms = checkedKeys
        .map(key => Number(key))
        .filter(key => !isNaN(key));

      await roleperms(
        { userType: roleType },
        selectedPerms
      );
      
      message.success('权限保存成功');
    } catch (error) {
      console.error('保存权限失败:', error);
      message.error('保存权限失败');
    }
  };

  // 处理添加权限
  const handleAdd = () => {
    form.resetFields();
    setIsModalVisible(true);
  };

  // 处理模态框确认
  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      await roleperms1(values);
      message.success('添加成功');
      setIsModalVisible(false);
      // 重新加载权限树
      const result = await listPerms();
      if (result) {
        const formattedData = convertToTreeData(result);
        setTreeData(formattedData);
      }
    } catch (error) {
      console.error('添加权限失败:', error);
      message.error('添加失败');
    }
  };

  // 处理删除模态框的确认
  const handleDeleteOk = async () => {
    if (deleteCheckedKeys.length === 0) {
      message.warning('请选择要删除的权限');
      return;
    }

    try {
      const deleteIds = deleteCheckedKeys
        .map(key => Number(key))
        .filter(key => !isNaN(key));

      await rolepermsDel(deleteIds);
      message.success('删除成功');
      setIsDeleteModalVisible(false);
      setDeleteCheckedKeys([]);
      
      // 重新加载权限树
      const result = await listPerms();
      if (result) {
        const formattedData = convertToTreeData(result);
        setTreeData(formattedData);
      }
    } catch (error) {
      console.error('删除权限失败:', error);
      message.error('删除失败');
    }
  };

  // 处理删除权限的选择变化
  const onDeleteCheck = (checked: any) => {
    const checkedKeys = Array.isArray(checked) ? checked : checked.checked;
    setDeleteCheckedKeys(checkedKeys);
  };

  // 角色类型选项
  const roleOptions = [
    { label: '超级管理员', value: 0 },
    { label: '校区代理', value: 1 },
    { label: '普通管理员', value: 2 },
    { label: '普通用户', value: 3 },
    { label: '跑腿用户', value: 4 },
  ];

  return (
    <Card title="权限管理">
      <div style={{ marginBottom: 16, display: 'flex', gap: 16 }}>
        <Select
          style={{ width: 200 }}
          placeholder="请选择角色类型"
          onChange={handleRoleChange}
          value={roleType}
          options={roleOptions}
        />
        <Button 
          type="primary" 
          onClick={handleSave}
          disabled={!roleType} // 禁用条件：没有选择角色类型
        >
          保存权限
        </Button>
        <Button type="primary" onClick={handleAdd}>
          添加权限
        </Button>
        <Button danger onClick={() => setIsDeleteModalVisible(true)}>
          删除权限
        </Button>
      </div>
      <Tree
        checkable
        defaultExpandAll
        checkedKeys={checkedKeys}
        onCheck={onCheck}
        treeData={treeData}
      />

      <Modal
        title="添加权限"
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
      >
        <Form form={form} layout="vertical">
        <Form.Item
            name="id"
            label="ID"
          >
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="parentId"
            label="父级ID"
            initialValue={0}
          >
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="name"
            label="权限名称"
            rules={[{ required: true, message: '请输入权限名称' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="perms"
            label="权限标识"
            // rules={[{ required: true, message: '请输入权限标识' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="sort"
            label="排序"
            initialValue={0}
            rules={[{ required: true, message: '请输入排序号' }]}
          >
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      {/* 删除权限模态框 */}
      <Modal
        title="删除权限"
        open={isDeleteModalVisible}
        onOk={handleDeleteOk}
        onCancel={() => {
          setIsDeleteModalVisible(false);
          setDeleteCheckedKeys([]);
        }}
        width={600}
      >
        <p style={{ marginBottom: 16 }}>请选择要删除的权限（选中父节点将同时删除其所有子节点）：</p>
        <Tree
          checkable
          defaultExpandAll
          checkedKeys={deleteCheckedKeys}
          onCheck={onDeleteCheck}
          treeData={treeData}
        />
      </Modal>
    </Card>
  );
};

export default PermissionPage;
