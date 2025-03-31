import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Progress, Descriptions, Table, Spin } from 'antd';
import { PageContainer } from '@ant-design/pro-components';
import { getServerMonitor } from '@/services/test-swagger/systemController';
import {
  DashboardOutlined,
  DesktopOutlined,
  DatabaseOutlined,
  HddOutlined,
  SyncOutlined,
} from '@ant-design/icons';
import styles from './index.less';

const Monitor: React.FC = () => {
  const [serverInfo, setServerInfo] = useState<API.Server>();
  const [loading, setLoading] = useState(true);
  const [lastUpdate, setLastUpdate] = useState<string>('');

  const fetchServerInfo = async () => {
    try {
      const response = await getServerMonitor();
      if (response.code === 200) {
        setServerInfo(response.data);
        setLastUpdate(new Date().toLocaleTimeString());
      }
    } catch (error) {
      console.error('获取服务器信息失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchServerInfo();
    // 每10秒刷新一次数据
    const timer = setInterval(fetchServerInfo, 10000);
    return () => clearInterval(timer);
  }, []);

  const diskColumns = [
    { title: '磁盘名称', dataIndex: 'typeName', key: 'typeName' },
    { title: '文件系统', dataIndex: 'sysTypeName', key: 'sysTypeName' },
    { title: '总容量', dataIndex: 'total', key: 'total' },
    { title: '已用容量', dataIndex: 'used', key: 'used' },
    { title: '剩余容量', dataIndex: 'free', key: 'free' },
    {
      title: '使用率',
      dataIndex: 'usage',
      key: 'usage',
      render: (usage: number) => (
        <Progress 
          percent={Number(usage.toFixed(2))}
          status={usage > 90 ? 'exception' : usage > 70 ? 'warning' : 'normal'}
        />
      ),
    },
  ];

  return (
    <PageContainer
      extra={[
        <span key="lastUpdate" style={{ marginRight: 16 }}>
          最后更新时间：{lastUpdate}
        </span>,
        <SyncOutlined 
          key="refresh" 
          onClick={fetchServerInfo}
          style={{ fontSize: 18, cursor: 'pointer' }} 
        />,
      ]}
    >
      <Spin spinning={loading} delay={500}>
        <Row gutter={[16, 16]}>
          {/* CPU信息 */}
          <Col span={12}>
            <Card 
              title={<><DashboardOutlined /> CPU信息</>}
              className={styles.monitorCard}
            >
              <Row justify="center" align="middle">
                <Col span={12} className={styles.progressWrapper}>
                  <Progress
                    type="dashboard"
                    percent={Number((serverInfo?.cpu?.used || 0).toFixed(2))}
                    status={serverInfo?.cpu?.used > 90 ? 'exception' : 'normal'}
                  />
                  <div className={styles.progressLabel}>CPU使用率</div>
                </Col>
                <Col span={12}>
                  <Descriptions column={1} size="small">
                    <Descriptions.Item label="核心数">{serverInfo?.cpu?.cpuNum}</Descriptions.Item>
                    <Descriptions.Item label="系统使用率">{serverInfo?.cpu?.sys}%</Descriptions.Item>
                    <Descriptions.Item label="用户使用率">{serverInfo?.cpu?.used}%</Descriptions.Item>
                    <Descriptions.Item label="当前空闲率">{serverInfo?.cpu?.free}%</Descriptions.Item>
                  </Descriptions>
                </Col>
              </Row>
            </Card>
          </Col>

          {/* 内存信息 */}
          <Col span={12}>
            <Card 
              title={<><DatabaseOutlined /> 内存信息</>}
              className={styles.monitorCard}
            >
              <Row justify="center" align="middle">
                <Col span={12} className={styles.progressWrapper}>
                  <Progress
                    type="dashboard"
                    percent={Number((serverInfo?.mem?.usage || 0).toFixed(2))}
                    status={serverInfo?.mem?.usage > 90 ? 'exception' : 'normal'}
                  />
                  <div className={styles.progressLabel}>内存使用率</div>
                </Col>
                <Col span={12}>
                  <Descriptions column={1} size="small">
                    <Descriptions.Item label="总内存">{serverInfo?.mem?.total} GB</Descriptions.Item>
                    <Descriptions.Item label="已用内存">{serverInfo?.mem?.used} GB</Descriptions.Item>
                    <Descriptions.Item label="剩余内存">{serverInfo?.mem?.free} GB</Descriptions.Item>
                  </Descriptions>
                </Col>
              </Row>
            </Card>
          </Col>

          {/* JVM信息 */}
          <Col span={12}>
            <Card 
              title={<><DesktopOutlined /> JVM信息</>}
              className={styles.monitorCard}
            >
              <Descriptions column={2} size="small">
                <Descriptions.Item label="JDK版本">{serverInfo?.jvm?.version}</Descriptions.Item>
                <Descriptions.Item label="启动时间">{serverInfo?.jvm?.startTime}</Descriptions.Item>
                <Descriptions.Item label="运行时长">{serverInfo?.jvm?.runTime}</Descriptions.Item>
                <Descriptions.Item label="安装路径">{serverInfo?.jvm?.home}</Descriptions.Item>
                <Descriptions.Item label="已用内存">{serverInfo?.jvm?.used} MB</Descriptions.Item>
                <Descriptions.Item label="最大可用">{serverInfo?.jvm?.max} MB</Descriptions.Item>
              </Descriptions>
              <Progress
                percent={Number((serverInfo?.jvm?.usage || 0).toFixed(2))}
                status={serverInfo?.jvm?.usage > 90 ? 'exception' : 'normal'}
                strokeWidth={10}
                className={styles.jvmProgress}
              />
            </Card>
          </Col>

          {/* 服务器信息 */}
          <Col span={12}>
            <Card 
              title={<><HddOutlined /> 服务器信息</>}
              className={styles.monitorCard}
            >
              <Descriptions column={1} size="small">
                <Descriptions.Item label="服务器名称">{serverInfo?.sys?.computerName}</Descriptions.Item>
                <Descriptions.Item label="操作系统">{serverInfo?.sys?.osName}</Descriptions.Item>
                <Descriptions.Item label="系统架构">{serverInfo?.sys?.osArch}</Descriptions.Item>
                <Descriptions.Item label="服务器IP">{serverInfo?.sys?.computerIp}</Descriptions.Item>
                <Descriptions.Item label="项目路径">{serverInfo?.sys?.userDir}</Descriptions.Item>
              </Descriptions>
            </Card>
          </Col>

          {/* 磁盘信息 */}
          <Col span={24}>
            <Card 
              title={<><HddOutlined /> 磁盘信息</>}
              className={styles.monitorCard}
            >
              <Table
                dataSource={serverInfo?.sysFiles}
                columns={diskColumns}
                pagination={false}
                rowKey="dirName"
              />
            </Card>
          </Col>
        </Row>
      </Spin>
    </PageContainer>
  );
};

export default Monitor;
