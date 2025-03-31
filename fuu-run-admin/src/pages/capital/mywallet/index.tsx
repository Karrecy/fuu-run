import { Card, Row, Col, Button, Modal, Form, Input, message, Select, Descriptions, Tag, Result } from 'antd';
import React, { useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { wallet, submitRecode, lastRecode } from '@/services/test-swagger/moneyController';
import { StatisticCard } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';

const { Statistic } = StatisticCard;

const MyWallet: React.FC = () => {
  const [walletData, setWalletData] = useState<API.Wallet>();
  const [lastRecord, setLastRecord] = useState<API.MoneyRecode>();
  const [withdrawVisible, setWithdrawVisible] = useState<boolean>(false);
  const [form] = Form.useForm();
  const [cardPlaceholder, setCardPlaceholder] = useState('请输入支付宝账号/手机号');
  const [remarkPlaceholder, setRemarkPlaceholder] = useState('可注明备注');

  // 获取当前用户信息
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;
  const userType = currentUser?.user?.userType;

  // 如果是普通管理员，显示无权限页面
  if (userType === 2) {
    return (
      <PageContainer>
        <Result
          status="403"
          title="无权访问"
          subTitle="抱歉，您没有权限访问此页面"
        />
      </PageContainer>
    );
  }

  const fetchData = async () => {
    try {
      const walletRes = await wallet();
      if (walletRes.code === 200) {
        setWalletData(walletRes.data);
      }
      
      const lastRes = await lastRecode();
      if (lastRes.code === 200) {
        setLastRecord(lastRes.data);
      }
    } catch (error) {
      message.error('获取数据失败');
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleWithdraw = async (values: API.MoneyRecode) => {
    try {
      const res = await submitRecode(values);
      if (res.code === 200) {
        message.success('提现申请提交成功');
        setWithdrawVisible(false);
        form.resetFields();
        fetchData();
      } else {
        message.error(res.msg || '提交失败');
      }
    } catch (error) {
      message.error('提交失败');
    }
  };

  const getStatusTag = (status?: number) => {
    const statusMap = {
      0: <Tag color="red">已驳回</Tag>,
      1: <Tag color="green">已通过</Tag>,
      2: <Tag color="blue">审核中</Tag>,
    };
    return status !== undefined ? statusMap[status] : '-';
  };

  const handlePlatformChange = (value: number) => {
    if (value === 0) {
      setCardPlaceholder('请输入支付宝账号/手机号');
      setRemarkPlaceholder('可注明备注');
    }
    if (value === 1) {
      setCardPlaceholder('请输入银行卡号');
      setRemarkPlaceholder('注明 开户银行 和 持卡人姓名');
    }
  };

  return (
    <PageContainer>
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Card bordered={false}>
            <Row gutter={16}>
              <Col span={8}>
                <Statistic
                  title="当前余额"
                  value={walletData?.balance || 0}
                  prefix="¥"
                  precision={2}
                  style={{ color: '#52c41a' }}
                />
              </Col>
              <Col span={8}>
                <Statistic
                  title="已提现"
                  value={walletData?.withdrawn || 0}
                  prefix="¥"
                  precision={2}
                  style={{ color: '#1890ff' }}
                />
              </Col>
              <Col span={8}>
                <Statistic
                  title="总收入"
                  value={(Number(walletData?.balance) + Number(walletData?.withdrawn)) || 0}
                  prefix="¥"
                  precision={2}
                  style={{ color: '#f5222d' }}
                />
              </Col>
            </Row>
            <Row style={{ marginTop: 24 }}>
              <Col span={24} style={{ textAlign: 'right' }}>
                {/* 只有校区代理(userType === 1)才显示提现按钮 */}
                {userType === 1 && (
                  <Button 
                    type="primary" 
                    onClick={() => setWithdrawVisible(true)}
                    disabled={!walletData?.balance || walletData.balance <= 0}
                  >
                    申请提现
                  </Button>
                )}
              </Col>
            </Row>
          </Card>
        </Col>

        {lastRecord && (
          <Col span={24}>
            <Card 
              title="最近一次提现记录" 
              bordered={false}
            >
              <Descriptions column={2}>
                <Descriptions.Item label="提现金额">¥{lastRecord.cash}</Descriptions.Item>
                <Descriptions.Item label="提现状态">{getStatusTag(lastRecord.status)}</Descriptions.Item>
                <Descriptions.Item label="提现平台">{lastRecord.platform}</Descriptions.Item>
                <Descriptions.Item label="卡号/账号">{lastRecord.card}</Descriptions.Item>
                <Descriptions.Item label="申请时间">{lastRecord.createTime}</Descriptions.Item>
                <Descriptions.Item label="审核时间">{lastRecord.updateTime || '-'}</Descriptions.Item>
                {lastRecord.status !== 2 && (
                  <Descriptions.Item label="审核反馈" span={2}>
                    {lastRecord.feedback || '-'}
                  </Descriptions.Item>
                )}
              </Descriptions>
            </Card>
          </Col>
        )}
      </Row>

      <Modal
        title="申请提现"
        open={withdrawVisible}
        onCancel={() => {
          setWithdrawVisible(false);
          form.resetFields();
          setCardPlaceholder('请输入支付宝账号/手机号');
          setRemarkPlaceholder('可注明备注');
        }}
        onOk={() => form.submit()}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleWithdraw}
          initialValues={{
            platform: 0,
          }}
        >
          <Form.Item
            label="提现金额"
            name="cash"
            rules={[
              { required: true, message: '请输入提现金额' },
              {
                validator: (_, value) => {
                  if (value && value > (walletData?.balance || 0)) {
                    return Promise.reject(new Error('提现金额不能大于可用余额'));
                  }
                  if (value && value <= 0) {
                    return Promise.reject(new Error('提现金额必须大于0'));
                  }
                  return Promise.resolve();
                },
              },
            ]}
          >
            <Input prefix="¥" type="number" placeholder="请输入提现金额" />
          </Form.Item>

          <Form.Item
            label="提现平台"
            name="platform"
            rules={[{ required: true, message: '请选择提现平台' }]}
          >
            <Select onChange={handlePlatformChange}>
              <Select.Option value={0}>支付宝转账</Select.Option>
              <Select.Option value={1}>银行卡转账</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="收款账号"
            name="card"
            rules={[{ required: true, message: '请输入收款账号' }]}
          >
            <Input placeholder={cardPlaceholder} />
          </Form.Item>

          <Form.Item
            label="备注"
            name="remark"
            rules={[
              { 
                required: form.getFieldValue('platform') === 1, 
                message: '请填写开户银行和持卡人姓名' 
              }
            ]}
          >
            <Input.TextArea placeholder={remarkPlaceholder} />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default MyWallet;
