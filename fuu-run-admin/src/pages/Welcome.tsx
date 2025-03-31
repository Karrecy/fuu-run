import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Card, Typography, theme } from 'antd';
import React from 'react';

const { Title, Paragraph } = Typography;

const Welcome: React.FC = () => {
  const { token } = theme.useToken();
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;

  return (
    <PageContainer>
      <Card
        style={{
          borderRadius: 8,
          height: '70vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          background: token.colorBgContainer,
        }}
        bodyStyle={{
          padding: '48px',
          textAlign: 'center',
          maxWidth: '800px',
          width: '100%',
        }}
        bordered={false}
      >
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: '24px',
          }}
        >
          <Title level={1} style={{ marginBottom: 0, fontSize: '36px' }}>
            欢迎使用校园跑腿管理系统
          </Title>
          
          {currentUser?.name && (
            <Title 
              level={3} 
              style={{ 
                marginTop: 0,
                fontWeight: 'normal',
                color: token.colorTextSecondary 
              }}
            >
              {`您好，${currentUser.name}`}
            </Title>
          )}

          <Paragraph
            style={{
              fontSize: '16px',
              color: token.colorTextSecondary,
              marginBottom: '32px',
              lineHeight: '1.8',
            }}
          >
            这是一个专业的校园跑腿服务管理平台，帮助您高效管理订单、用户和业务数据。
            <br />
            让我们开始使用系统的强大功能，为校园服务提供更好的体验。
          </Paragraph>
        </div>
      </Card>
    </PageContainer>
  );
};

export default Welcome;
