import React, { useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Card, Row, Col, DatePicker, Radio, Space, Statistic, message } from 'antd';
import { Line, Column } from '@ant-design/plots';
import { statistic as getStatistics } from '@/services/test-swagger/loginController';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

const Analysis: React.FC = () => {
  const [data, setData] = useState<API.StatisticsDaily[]>([]);
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>([
    dayjs().subtract(7, 'days'),
    dayjs()
  ]);
  const [chartType, setChartType] = useState<'sales' | 'visits'>('sales');

  const fetchData = async () => {
    try {
      const res = await getStatistics({
        statisticsDaily: {
          beginTime: dateRange[0].format('YYYY-MM-DD'),
          endTime: dateRange[1].format('YYYY-MM-DD'),
        },
        pageQuery: {
          pageSize: 999,
          pageNum: 1,
        },
      });
      if (res.code === 200) {
        setData(res.rows || []);
      } else {
        message.error(res.msg);
      }
    } catch (error) {
      message.error('获取统计数据失败');
    }
  };

  useEffect(() => {
    fetchData();
  }, [dateRange]);

  const salesData = data.map(item => ({
    date: dayjs(item.createTime).format('MM-DD'),
    totalSales: item.totalPayment,
    totalOrders: item.totalOrders,
  }));

  const visitsData = data.map(item => ({
    date: dayjs(item.createTime).format('MM-DD'),
    totalVisits: item.totalVisits,
  }));

  return (
    <PageContainer>
      <Row gutter={[16, 16]}>
        <Col span={6}>
          <Card>
            <Statistic 
              title="总销售额" 
              value={data[data.length - 1]?.totalPayment || 0} 
              prefix="¥"
              precision={2}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic 
              title="访问量" 
              value={data[data.length - 1]?.totalVisits || 0} 
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic 
              title="支付笔数" 
              value={data[data.length - 1]?.totalOrders || 0} 
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic 
              title="转化率" 
              value={data[data.length - 1]?.completionRate || 0} 
              suffix="%"
              precision={2}
            />
          </Card>
        </Col>
      </Row>

      <Card style={{ marginTop: 16 }}>
        <Space style={{ marginBottom: 16 }}>
          <RangePicker
            value={dateRange}
            onChange={(dates) => {
              if (dates && dates[0] && dates[1]) {
                setDateRange([dates[0], dates[1]]);
              }
            }}
          />
          <Radio.Group value={chartType} onChange={(e) => setChartType(e.target.value)}>
            <Radio.Button value="sales">销售额</Radio.Button>
            <Radio.Button value="visits">访问量</Radio.Button>
          </Radio.Group>
        </Space>
        <div style={{ height: 400 }}>
          {chartType === 'sales' ? (
            <Column
              data={salesData}
              xField="date"
              yField="totalSales"
              xAxis={{
                label: {
                  formatter: (text) => text,
                }
              }}
              yAxis={{
                label: {
                  formatter: (value) => `¥${Number(value).toFixed(2)}`,
                },
              }}
              tooltip={{
                formatter: (datum) => {
                  return {
                    name: '销售额',
                    value: `¥${datum.totalSales.toFixed(2)}`,
                  };
                }
              }}
              columnStyle={{
                radius: [4, 4, 0, 0],
              }}
            />
          ) : (
            <Column
              data={visitsData}
              xField="date"
              yField="totalVisits"
              xAxis={{
                label: {
                  formatter: (text) => text,
                }
              }}
              tooltip={{
                formatter: (datum) => {
                  return {
                    name: '访问量',
                    value: datum.totalVisits,
                  };
                }
              }}
              columnStyle={{
                radius: [4, 4, 0, 0],
              }}
            />
          )}
        </div>
      </Card>
    </PageContainer>
  );
};

export default Analysis;