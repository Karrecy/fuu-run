import { PageContainer, ProCard } from '@ant-design/pro-components';
import { Button, Form, Input, InputNumber, message, Space } from 'antd';
import React, { useState, useEffect } from 'react';
import { useModel } from '@umijs/max';
import {
  ClockCircleOutlined,
  ProjectOutlined,
  CopyrightOutlined,
  HeartOutlined,
  PictureOutlined,
  HomeOutlined,
} from '@ant-design/icons';
import { edit7 as editConfig } from '@/services/test-swagger/systemController';

// 定义配置类型
interface FuuConfig {
  name: string;
  version: string;
  copyrightYear: string;
  payCancelTtl: number;
  autoCompleteTtl: number;
  completionImagesLimit: number;
  creditUpperLimit: number;
  creditLowerLimit: number;
  creditDeduction: number;
  maxAddress: number;
}

const ConfigPage: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  
  // 获取全局初始状态
  const { initialState } = useModel('@@initialState');
  const config = initialState?.currentUser?.config as FuuConfig;

  // 当配置数据加载完成后，设置表单初始值
  useEffect(() => {
    if (config) {
      form.setFieldsValue(config);
    }
  }, [config]);

  const handleSave = async (values: FuuConfig) => {
    setLoading(true);
    try {
      const response = await editConfig(values);
      if (response.code === 200) {
        message.success('保存成功');
      } else {
        message.error(response.msg || '保存失败');
      }
    } catch (error) {
      message.error('保存失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageContainer>
      <ProCard>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSave}
        >
          <ProCard
            title={
              <Space>
                <ProjectOutlined />
                <span>基本设置</span>
              </Space>
            }
            headerBordered
            bordered
            style={{ marginBottom: 16 }}
          >
            <Form.Item
              label="项目名称"
              name="name"
              rules={[{ required: true, message: '请输入项目名称' }]}
            >
              <Input placeholder="请输入项目名称" />
            </Form.Item>

            <Form.Item
              label="版本号"
              name="version"
              rules={[{ required: true, message: '请输入版本号' }]}
            >
              <Input placeholder="请输入版本号" />
            </Form.Item>

            <Form.Item
              label="版权年份"
              name="copyrightYear"
              rules={[{ required: true, message: '请输入版权年份' }]}
            >
              <Input placeholder="请输入版权年份" prefix={<CopyrightOutlined />} />
            </Form.Item>
          </ProCard>

          <ProCard
            title={
              <Space>
                <ClockCircleOutlined />
                <span>时间设置</span>
              </Space>
            }
            headerBordered
            bordered
            style={{ marginBottom: 16 }}
          >
            <Form.Item
              label="支付超时时间"
              name="payCancelTtl"
              rules={[{ required: true, message: '请输入支付超时时间' }]}
              extra="超时未支付自动取消订单的时间（分钟）"
            >
              <InputNumber
                min={1}
                max={1440}
                style={{ width: 200 }}
                addonAfter="分钟"
              />
            </Form.Item>

            <Form.Item
              label="自动完成时间"
              name="autoCompleteTtl"
              rules={[{ required: true, message: '请输入自动完成时间' }]}
              extra="超时未完成自动完成订单的时间（小时）"
            >
              <InputNumber
                min={1}
                max={720}
                style={{ width: 200 }}
                addonAfter="小时"
              />
            </Form.Item>

            <Form.Item
              label="完成凭证上限"
              name="completionImagesLimit"
              rules={[{ required: true, message: '请输入完成凭证上限' }]}
              extra="订单完成凭证图片上传数量上限"
            >
              <InputNumber
                min={1}
                max={10}
                style={{ width: 200 }}
                addonAfter="张"
                prefix={<PictureOutlined />}
              />
            </Form.Item>
          </ProCard>

          <ProCard
            title={
              <Space>
                <HeartOutlined />
                <span>信用分设置</span>
              </Space>
            }
            headerBordered
            bordered
            style={{ marginBottom: 16 }}
          >
            <Form.Item
              label="信用分上限"
              name="creditUpperLimit"
              rules={[{ required: true, message: '请输入信用分上限' }]}
              extra="用户初始信用分值"
            >
              <InputNumber
                min={0}
                style={{ width: 200 }}
                addonAfter="分"
              />
            </Form.Item>

            <Form.Item
              label="信用分下限"
              name="creditLowerLimit"
              rules={[{ required: true, message: '请输入信用分下限' }]}
              extra="用户最低信用分值"
            >
              <InputNumber
                min={0}
                style={{ width: 200 }}
                addonAfter="分"
              />
            </Form.Item>

            <Form.Item
              label="信用分扣除值"
              name="creditDeduction"
              rules={[{ required: true, message: '请输入信用分扣除值' }]}
              extra="每次违规扣除的信用分值"
            >
              <InputNumber
                min={0}
                style={{ width: 200 }}
                addonAfter="分"
              />
            </Form.Item>
          </ProCard>

          <ProCard
            title={
              <Space>
                <HomeOutlined />
                <span>用户限制</span>
              </Space>
            }
            headerBordered
            bordered
            style={{ marginBottom: 16 }}
          >
            <Form.Item
              label="地址数量上限"
              name="maxAddress"
              rules={[{ required: true, message: '请输入用户地址数量上限' }]}
              extra="用户可以保存的最大地址数量"
            >
              <InputNumber
                min={1}
                max={20}
                style={{ width: 200 }}
                addonAfter="个"
              />
            </Form.Item>
          </ProCard>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              保存配置
            </Button>
          </Form.Item>
        </Form>
      </ProCard>
    </PageContainer>
  );
};

export default ConfigPage;
