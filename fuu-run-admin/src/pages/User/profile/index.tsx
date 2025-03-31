import { Card, Descriptions, Avatar, Tabs, Tag, Switch, Button, Input, message, Form, Modal, Upload } from 'antd';
import { PageContainer } from '@ant-design/pro-components';
import { useModel } from 'umi';
import { useState } from 'react';
import { updateProfile, updatePwd, sendEmailCode, bindEmail } from '@/services/test-swagger/profileController';
import { upload } from '@/services/test-swagger/ossController';
import { CameraOutlined } from '@ant-design/icons';
import styles from './index.less';

const Profile: React.FC = () => {
  const { initialState, setInitialState } = useModel('@@initialState');
  const userInfo = initialState?.currentUser?.user;
  const userPc = userInfo?.userPc;
  const [emailEnable, setEmailEnable] = useState(userPc?.emailEnable || false);
  const [isPasswordModalVisible, setIsPasswordModalVisible] = useState(false);
  const [isEmailModalVisible, setIsEmailModalVisible] = useState(false);
  const [emailForm] = Form.useForm();
  const [passwordForm] = Form.useForm();
  const [countdown, setCountdown] = useState(0);
  const [avatarUrl, setAvatarUrl] = useState(userPc?.avatar);

  // 格式化性别显示
  const formatSex = (sex: number) => {
    switch (sex) {
      case 1:
        return '男';
      case 2:
        return '女';
      default:
        return '未知';
    }
  };

  // 格式化状态显示
  const formatStatus = (status: number) => {
    switch (status) {
      case 1:
        return { text: '正常', color: 'green' };
      case 0:
        return { text: '禁用', color: 'red' };
      default:
        return { text: '未知', color: 'default' };
    }
  };

  // 处理邮箱启用状态变化
  const handleEmailEnableChange = (checked: boolean) => {
    setEmailEnable(checked);
  };

  // 统一的错误处理函数
  const handleResponse = (response: any, successMsg: string) => {
    if (response?.code === 200) {
      message.success(successMsg);
      return true;
    } else {
      message.error(response?.msg || '操作失败');
      return false;
    }
  };

  // 处理发送验证码
  const handleSendCode = async () => {
    try {
      const email = emailForm.getFieldValue('email');
      if (!email) {
        message.error('请输入邮箱');
        return;
      }
      const response = await sendEmailCode({ email });
      if (handleResponse(response, '验证码已发送')) {
        setCountdown(60);
        const timer = setInterval(() => {
          setCountdown((prev) => {
            if (prev <= 1) {
              clearInterval(timer);
              return 0;
            }
            return prev - 1;
          });
        }, 1000);
      }
    } catch (error) {
      message.error('发送验证码失败');
    }
  };

  // 重新加载用户信息
  const reloadUserInfo = async () => {
    const fetchUserInfo = initialState?.fetchUserInfo;
    if (fetchUserInfo) {
      const currentUser = await fetchUserInfo();
      setInitialState((s) => ({
        ...s,
        currentUser,
      }));
    }
  };

  // 处理保存邮箱
  const handleEmailSave = async () => {
    try {
      const values = await emailForm.validateFields();
      const response = await bindEmail(values.email, values.emailCode);
      if (handleResponse(response, '邮箱修改成功')) {
        setIsEmailModalVisible(false);
        emailForm.resetFields();
        await reloadUserInfo();  // 重新加载用户信息
      }
    } catch (error) {
      message.error('邮箱修改失败');
    }
  };

  // 处理头像上传
  const handleAvatarUpload = async (file: File) => {
    try {
      const response = await upload(
        { type: 4 },  // 请求参数
        { name: '' }, // 请求体参数
        file,
      );
      
      if (response?.code === 200 && response.data) {
        const url = response.data.url;  // 假设返回的数据中有 url 字段
        setAvatarUrl(url);
        // 立即更新头像
        const profileResponse = await updateProfile({
          nickname: userPc?.name,
          avatar: url,
          emailEnable: emailEnable ? 1 : 0,
        });
        if (handleResponse(profileResponse, '头像更新成功')) {
          await reloadUserInfo();
        }
      } else {
        message.error(response?.msg || '上传失败');
      }
    } catch (error) {
      message.error('上传失败');
    }
  };

  // 处理保存基本信息
  const handleSaveProfile = async () => {
    try {
      const response = await updateProfile({
        nickname: userPc?.name,
        avatar: avatarUrl || userPc?.avatar,  // 使用最新的头像URL
        emailEnable: emailEnable ? 1 : 0,
      });
      if (handleResponse(response, '保存成功')) {
        await reloadUserInfo();
      }
    } catch (error) {
      message.error('保存失败');
    }
  };

  // 处理修改密码
  const handlePasswordChange = async () => {
    try {
      const values = await passwordForm.validateFields();
      const response = await updatePwd(values);
      if (handleResponse(response, '密码修改成功')) {
        setIsPasswordModalVisible(false);
        passwordForm.resetFields();
      }
    } catch (error) {
      message.error('密码修改失败');
    }
  };

  const items = [
    {
      key: '1',
      label: '基本信息',
      children: (
        <>
          <Descriptions column={2}>
            <Descriptions.Item label="用户ID">{userPc?.id}</Descriptions.Item>
            <Descriptions.Item label="用户名">{userPc?.username}</Descriptions.Item>
            <Descriptions.Item label="姓名">{userPc?.name}</Descriptions.Item>
            <Descriptions.Item label="手机号">{userPc?.phone}</Descriptions.Item>
            <Descriptions.Item label="邮箱">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <span>{userPc?.email || '未设置'}</span>
                <Button 
                  type="link" 
                  onClick={() => {
                    setIsEmailModalVisible(true);
                    emailForm.setFieldsValue({
                      email: userPc?.email || '',
                    });
                  }}
                  style={{ padding: '0 4px', height: 'auto', lineHeight: '1.5' }}
                >
                  {userPc?.email ? '更换邮箱' : '绑定邮箱'}
                </Button>
              </div>
            </Descriptions.Item>
            <Descriptions.Item label="性别">{formatSex(userPc?.sex)}</Descriptions.Item>
            <Descriptions.Item label="状态">
              {userPc?.status !== undefined && (
                <Tag color={formatStatus(userPc.status).color}>
                  {formatStatus(userPc.status).text}
                </Tag>
              )}
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">{userInfo?.createTime}</Descriptions.Item>
          </Descriptions>
        </>
      ),
    },
    {
      key: '2',
      label: '上次登录',
      children: (
        <Descriptions column={2}>
          <Descriptions.Item label="登录时间">{userInfo?.loginTime}</Descriptions.Item>
          <Descriptions.Item label="登录IP">{userInfo?.loginIp}</Descriptions.Item>
          <Descriptions.Item label="登录地区">{userInfo?.loginRegion}</Descriptions.Item>
          <Descriptions.Item label="设备类型">
            {userInfo?.deviceType === 0 ? 'PC端' : '移动端'}
          </Descriptions.Item>
        </Descriptions>
      ),
    },
  ];

  return (
    <PageContainer>
      <Card className={styles.profileCard}>
        <div className={styles.userInfoHeader}>
          <div className={styles.leftSection}>
            <div className={styles.avatarContainer}>
              <Upload
                accept="image/*"
                showUploadList={false}
                beforeUpload={(file) => {
                  handleAvatarUpload(file);
                  return false;  // 阻止自动上传
                }}
              >
                <div className={styles.avatarWrapper}>
                  <Avatar size={80} src={avatarUrl || userPc?.avatar} />
                  <div className={styles.avatarMask}>
                    <CameraOutlined />
                    <span>更换头像</span>
                  </div>
                </div>
              </Upload>
            </div>
            <div className={styles.userMeta}>
              <h2>{userPc?.name}</h2>
              <p>{userPc?.username} {userInfo?.admin ? '(管理员)' : ''}</p>
            </div>
          </div>
          <div className={styles.rightSection}>
            {userPc?.email && (
              <div >
                <Switch
                  checked={emailEnable}
                  onChange={handleEmailEnableChange}
                  checkedChildren="启用邮箱"
                  unCheckedChildren="禁用邮箱"
                />
              </div>
            )}
            <Button onClick={() => setIsPasswordModalVisible(true)}>
              修改密码
            </Button>
            <Button type="primary" onClick={handleSaveProfile}>
              保存修改
            </Button>
          </div>
        </div>
        <Tabs items={items} />
      </Card>

      {/* 邮箱设置模态框 */}
      <Modal
        title={userPc?.email ? '更换邮箱' : '绑定邮箱'}
        open={isEmailModalVisible}
        onOk={handleEmailSave}
        onCancel={() => {
          setIsEmailModalVisible(false);
          emailForm.resetFields();
        }}
      >
        <Form form={emailForm} layout="vertical">
          <Form.Item
            name="email"
            label="邮箱地址"
            rules={[
              { required: true, message: '请输入邮箱地址' },
              { type: 'email', message: '请输入正确的邮箱格式' }
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="emailCode"
            label="验证码"
            rules={[{ required: true, message: '请输入验证码' }]}
          >
            <div style={{ display: 'flex', gap: 8 }}>
              <Input style={{ flex: 1 }} />
              <Button 
                onClick={handleSendCode}
                disabled={countdown > 0}
              >
                {countdown > 0 ? `${countdown}秒后重试` : '发送验证码'}
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>

      {/* 修改密码模态框 */}
      <Modal
        title="修改密码"
        open={isPasswordModalVisible}
        onOk={handlePasswordChange}
        onCancel={() => {
          setIsPasswordModalVisible(false);
          passwordForm.resetFields();
        }}
      >
        <Form form={passwordForm} layout="vertical">
          <Form.Item
            name="oldPassword"
            label="原密码"
            rules={[{ required: true, message: '请输入原密码' }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[{ required: true, message: '请输入新密码' }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认新密码"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

// 登录日志组件
const LoginLogs: React.FC = () => {
  const columns = [
    {
      title: '登录时间',
      dataIndex: 'loginTime',
      key: 'loginTime',
    },
    {
      title: '登录IP',
      dataIndex: 'ip',
      key: 'ip',
    },
    {
      title: '登录地点',
      dataIndex: 'location',
      key: 'location',
    },
    {
      title: '设备类型',
      dataIndex: 'deviceType',
      key: 'deviceType',
    },
  ];

  // 这里应该从API获取登录日志数据
  const data = [
    {
      key: '1',
      loginTime: '2024-03-20 14:30:00',
      ip: '192.168.1.1',
      location: '北京市',
      deviceType: 'PC',
    },
  ];

  return <Table columns={columns} dataSource={data} />;
};

export default Profile;
