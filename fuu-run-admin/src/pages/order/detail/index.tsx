import { PageContainer } from '@ant-design/pro-layout';
import { useParams } from 'umi';
import { useEffect, useState } from 'react';
import { Card, Descriptions, Tag, Divider, message, Avatar, Image, Space, Button, Dropdown, Steps, Modal, Collapse, Form, Input } from 'antd';
import { detail, cancel } from '@/services/test-swagger/orderController';
import { EllipsisOutlined, ReloadOutlined, FileOutlined, FileExcelOutlined, FileImageOutlined, FilePdfOutlined, FileWordOutlined, FilePptOutlined } from '@ant-design/icons';
import { getAppeal } from '@/services/test-swagger/orderAppealController';
import ChatDrawer from '@/components/ChatDrawer';
import { useModel } from '@umijs/max';

const OrderDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [orderDetail, setOrderDetail] = useState<API.OrderDetailVO>();
  const [loading, setLoading] = useState<boolean>(true);
  const [appealModalVisible, setAppealModalVisible] = useState(false);
  const [appealDetail, setAppealDetail] = useState<API.OrderAppealVO[]>([]);
  const [chatVisible, setChatVisible] = useState(false);
  const [cancelModalVisible, setCancelModalVisible] = useState(false);
  const [cancelForm] = Form.useForm();

  // 获取当前用户信息
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;
  const isSuperAdmin = currentUser?.user?.userType === 0; // 超级管理员类型为0

  const fetchOrderDetail = async () => {
    try {
      const response = await detail({ orderId: id });
      if (response.code === 200 && response.data) {
        setOrderDetail(response.data);
      } else {
        message.error('获取订单详情失败');
      }
    } catch (error) {
      message.error('获取订单详情失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrderDetail();
  }, [id]);

  const getStatusTag = (status?: number) => {
    const statusMap = {
      0: { text: '待支付', color: 'warning' },
      1: { text: '待接单', color: 'warning' },
      2: { text: '待配送', color: 'processing' },
      3: { text: '配送中', color: 'processing' },
      4: { text: '已送达', color: 'success' },
      5: { text: '已取消', color: 'default' },
      10: { text: '已完成', color: 'success' },
      11: { text: '已申诉', color: 'error' },
    };
    const status_info = statusMap[status as keyof typeof statusMap] || { text: '未知状态', color: 'default' };
    return <Tag color={status_info.color}>{status_info.text}</Tag>;
  };

  const handleRefresh = () => {
    setLoading(true);
    fetchOrderDetail();
  };

  const menuItems = [
    { key: '3', label: '预留操作' },
    { key: '4', label: '预留操作' },
  ];

  const getOrderSteps = (orderDetail: API.OrderDetailVO) => {
    const steps: { title: string; description: string | React.ReactNode; time: string }[] = [];
    
    // 添加下单时间
    if (orderDetail.orderMain?.createTime) {
      steps.push({
        title: '下单',
        description: '用户提交订单',
        time: orderDetail.orderMain.createTime,
      });
    }

    // 添加支付时间
    if (orderDetail.orderPayment?.paymentTime) {
      steps.push({
        title: '支付',
        description: '用户完成支付',
        time: orderDetail.orderPayment.paymentTime,
      });
    }

    // 添加接单时间
    if (orderDetail.progress?.acceptedTime) {
      steps.push({
        title: '接单',
        description: `跑腿员 ${orderDetail.nicknameRunner} 接单`,
        time: orderDetail.progress.acceptedTime,
      });
    }

    // 添加配送时间
    if (orderDetail.progress?.deliveringTime) {
      steps.push({
        title: '配送中',
        description: '跑腿员开始配送',
        time: orderDetail.progress.deliveringTime,
      });
    }

    // 添加送达时间
    if (orderDetail.progress?.deliveredTime) {
      steps.push({
        title: '已送达',
        description: '订单已送达',
        time: orderDetail.progress.deliveredTime,
      });
    }

    // 添加完成时间
    if (orderDetail.progress?.completedTime) {
      steps.push({
        title: '已完成',
        description: '订单已完成',
        time: orderDetail.progress.completedTime,
      });
    }

    // 修改取消时间的步骤显示
    if (orderDetail.progress?.cancelTime) {
      steps.push({
        title: '已取消',
        description: (
          <div>
            <div>{orderDetail.progress.cancelReason || '订单已取消'}</div>
            <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
              取消人：{getUserTypeTag(orderDetail.progress.cancelUserType)}
              <span style={{ marginLeft: '8px' }}>ID: {orderDetail.progress.cancelUserId}</span>
            </div>
          </div>
        ),
        time: orderDetail.progress.cancelTime,
      });
    }

    // 如果是申诉状态，添加申诉步骤
    if (orderDetail.orderMain?.status === 11 && orderDetail.orderMain?.updateTime) {
      steps.push({
        title: '已申诉',
        description: (
          <div>
            <div>订单已申诉</div>
            <Button type="link" onClick={handleViewAppeal} style={{ padding: 0 }}>
              查看申诉详情
            </Button>
          </div>
        ),
        time: orderDetail.orderMain.updateTime, // 使用更新时间作为申诉时间
      });
    }

    // 按时间排序
    return steps.sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime());
  };

  const getCurrentStep = (orderDetail: API.OrderDetailVO) => {
    const steps = getOrderSteps(orderDetail);
    // 返回最后一个步骤的索引
    return steps.length - 1;
  };

  const getStepStatus = (orderDetail: API.OrderDetailVO): "wait" | "process" | "finish" | "error" => {
    const status = orderDetail.orderMain?.status;
    if (status === 11) return 'error';  // 申诉状态显示为 error
    if (status === 30) return 'error';  // 取消状态显示为 error
    if (status === 10) return 'finish'; // 完成状态显示为 finish
    return 'process';                   // 其他状态显示为 process
  };

  // 查看申诉详情
  const handleViewAppeal = async () => {
    try {
      const response = await getAppeal({ orderId: id });
      if (response.code === 200 && response.data) {
        setAppealDetail(response.data); // 保存整个列表
        setAppealModalVisible(true);
      } else {
        message.error(response.msg);
      }
    } catch (error) {
      message.error('获取申诉详情失败');
    }
  };

  const getUserTypeTag = (type?: number) => {
    const typeMap = {
      0: { text: '超级管理员', color: 'red' },
      1: { text: '校区代理', color: 'orange' },
      2: { text: '普通管理员', color: 'blue' },
      3: { text: '普通用户', color: 'green' },
      4: { text: '跑腿用户', color: 'purple' },
      5: { text: '系统', color: 'default' },
    };
    const typeInfo = typeMap[type as keyof typeof typeMap] || { text: '未知', color: 'default' };
    return <Tag color={typeInfo.color}>{typeInfo.text}</Tag>;
  };

  // 渲染申诉记录的内容
  const renderAppealContent = (appeal: API.OrderAppealVO) => (
    <Descriptions column={1} bordered>
      <Descriptions.Item label="申诉原因">
        {appeal.orderAppeal?.appealReason}
      </Descriptions.Item>
      <Descriptions.Item label="申诉时间">
        {appeal.orderAppeal?.appealTime}
      </Descriptions.Item>
      <Descriptions.Item label="申诉状态">
        <Tag color={appeal.orderAppeal?.appealStatus === 0 ? 'warning' : 
                    appeal.orderAppeal?.appealStatus === 1 ? 'success' : 'error'}>
          {appeal.orderAppeal?.appealStatus === 0 ? '待处理' : 
           appeal.orderAppeal?.appealStatus === 1 ? '已通过' : '已驳回'}
        </Tag>
      </Descriptions.Item>
      {appeal.orderAppeal?.remarks && (
        <Descriptions.Item label="备注">
          {appeal.orderAppeal.remarks}
        </Descriptions.Item>
      )}
      <Descriptions.Item label="更新时间">
        {appeal.orderAppeal?.updateTime}
      </Descriptions.Item>
      <Descriptions.Item label="更新人">
        <Space>
          <span>ID: {appeal.orderAppeal?.updateId}</span>
          {getUserTypeTag(appeal.orderAppeal?.updateType)}
        </Space>
      </Descriptions.Item>
      {appeal.imageUrls?.length > 0 && (
        <Descriptions.Item label="申诉图片">
          {appeal.imageUrls.map((image, index) => (
            <Image
              key={index}
              src={image}
              width={100}
              style={{ marginRight: 8 }}
            />
          ))}
        </Descriptions.Item>
      )}
    </Descriptions>
  );

  // 添加 getFileIcon 函数
  const getFileIcon = (fileSuffix?: string) => {
    if (!fileSuffix) return <FileOutlined />;
    
    switch (fileSuffix) {
      case 'xlsx':
      case 'xls':
        return <FileExcelOutlined style={{ color: '#52c41a' }} />;
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif':
      case 'bmp':
      case 'webp':
        return <FileImageOutlined style={{ color: '#1890ff' }} />;
      case 'pdf':
        return <FilePdfOutlined style={{ color: '#ff4d4f' }} />;
      case 'doc':
      case 'docx':
        return <FileWordOutlined style={{ color: '#1890ff' }} />;
      case 'ppt':
      case 'pptx':
        return <FilePptOutlined style={{ color: '#ff7a45' }} />;
      default:
        return <FileOutlined />;
    }
  };

  // 处理取消订单
  const handleCancelOrder = () => {
    setCancelModalVisible(true);
  };

  // 确认取消订单
  const confirmCancelOrder = async (values: { cancelReason: string }) => {
    try {
      const hide = message.loading('正在取消订单...');
      const response = await cancel({
        orderId: id as string, // 直接使用字符串类型的 id
        cancelReason: values.cancelReason,
      });
      hide();
      
      if (response.code === 200) {
        message.success('订单取消成功');
        setCancelModalVisible(false);
        cancelForm.resetFields();
        handleRefresh(); // 刷新订单详情
      } else {
        message.error(response.msg || '取消订单失败');
      }
    } catch (error) {
      message.error('取消订单失败，请重试');
    }
  };

  // 判断订单是否可以取消
  const canCancelOrder = (status?: number) => {
    return status !== 5 && status !== 11; // 已取消或已申诉状态不能取消
  };

  // 添加支付状态显示函数
  const getPaymentStatusTag = (status?: number) => {
    const statusMap = {
      0: { text: '未支付', color: 'warning' },
      1: { text: '已支付', color: 'success' },
      2: { text: '退款中', color: 'processing' },
      3: { text: '已退款', color: 'default' },
    };
    const status_info = statusMap[status as keyof typeof statusMap] || { text: '未知状态', color: 'default' };
    return <Tag color={status_info.color}>{status_info.text}</Tag>;
  };

  return (
    <PageContainer
      ghost
      header={{
        title: orderDetail ? `订单号：${orderDetail.orderMain?.id}` : '加载中...',
        breadcrumb: {},
        extra: (
          <Space>
            <Button.Group>    
              <Button onClick={() => setChatVisible(true)}>进入聊天</Button>
              {isSuperAdmin && (
                <>
                  <Button 
                    onClick={() => handleCancelOrder()}
                    disabled={!canCancelOrder(orderDetail?.orderMain?.status)}
                  >
                    取消订单
                  </Button>
                  {/* <Button onClick={() => handleCompleteOrder()}>完成订单</Button> */}
                </>
              )}

              <Dropdown
                menu={{ items: menuItems }}
                placement="bottomRight"
                trigger={['hover']}
              >
                <Button>
                  <EllipsisOutlined />
                </Button>
              </Dropdown>
            </Button.Group>
            <Button type="primary" onClick={handleRefresh} icon={<ReloadOutlined />}>
              刷新
            </Button>
          </Space>
        ),
      }}
      content={
        loading ? (
          <Descriptions>
            <Descriptions.Item>加载中...</Descriptions.Item>
          </Descriptions>
        ) : orderDetail && (
          <Descriptions column={3}>
            <Descriptions.Item label="订单状态" span={1}>{getStatusTag(orderDetail.orderMain?.status)}</Descriptions.Item>
            <Descriptions.Item label="服务类型" span={1}>
              <Tag color={
                orderDetail.orderMain?.serviceType === 0 ? 'blue' :
                orderDetail.orderMain?.serviceType === 1 ? 'green' : 'purple'
              }>
                {orderDetail.orderMain?.serviceType === 0 ? '帮取送' :
                 orderDetail.orderMain?.serviceType === 1 ? '代买' : '万能服务'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="订单金额" span={1}>
              <span style={{ color: '#a66733' }}>￥{orderDetail.orderMain?.totalAmount}</span>
            </Descriptions.Item>
            <Descriptions.Item label="标签" span={1}>
              {orderDetail.orderMain?.tag && <Tag color="blue">{orderDetail.orderMain.tag}</Tag>}
            </Descriptions.Item>
            <Descriptions.Item label="重量" span={2}>
              {orderDetail.orderMain?.weight || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="具体要求" span={3}>
              {orderDetail.orderMain?.detail || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="支付状态">
              {getPaymentStatusTag(orderDetail.orderPayment?.paymentStatus)}
            </Descriptions.Item>
          </Descriptions>
        )
      }
    >
      {loading ? (
        <Card loading bordered={false} />
      ) : orderDetail && (
        <>
          <Card bordered={false} style={{ marginTop: 24 }}>
            <Descriptions title="支付信息" bordered>
              <Descriptions.Item label="实付金额" span={1}>
                <span style={{ color: '#f50', fontWeight: 'bold' }}>
                  ￥{orderDetail.orderPayment?.actualPayment}
                </span>
              </Descriptions.Item>
              <Descriptions.Item label="额外费用" span={1}>
                ￥{orderDetail.orderPayment?.additionalAmount || '0.00'}
              </Descriptions.Item>
              <Descriptions.Item label="支付状态" span={1}>
                 {getPaymentStatusTag(orderDetail.orderPayment?.paymentStatus)}
              </Descriptions.Item>
              {orderDetail.orderPayment?.paymentTime && (
                <Descriptions.Item label="支付时间" span={3}>
                  {orderDetail.orderPayment.paymentTime}
                </Descriptions.Item>
              )}
              {orderDetail.orderPayment?.refundTime && (
                <Descriptions.Item label="退款时间" span={3}>
                  {orderDetail.orderPayment.refundTime}
                </Descriptions.Item>
              )}
              {orderDetail.orderPayment?.refundPendingTime && (
                <Descriptions.Item label="退款申请时间" span={3}>
                  {orderDetail.orderPayment.refundPendingTime}
                </Descriptions.Item>
              )}
              {orderDetail.orderPayment?.isCouponed === 1 && (
                <>
                  <Descriptions.Item label="优惠券ID" span={1}>
                    {orderDetail.orderPayment.couponId}
                  </Descriptions.Item>
                  <Descriptions.Item label="优惠金额" span={2}>
                    ￥{orderDetail.orderPayment.discountAmount || '0.00'}
                  </Descriptions.Item>
                </>
              )}
            </Descriptions>
          </Card>

          <Card bordered={false} style={{ marginTop: 24 }}>
            <Descriptions title="用户信息" bordered>
              <Descriptions.Item label="下单用户" span={3}>
                <Avatar src={orderDetail.avatarUser} style={{ marginRight: 8 }} />
                {orderDetail.nicknameUser}
              </Descriptions.Item>
              {orderDetail.avatarRunner && (
                <Descriptions.Item label="接单用户" span={3}>
                  <Avatar src={orderDetail.avatarRunner} style={{ marginRight: 8 }} />
                  {orderDetail.nicknameRunner}
                </Descriptions.Item>
              )}
            </Descriptions>
          </Card>

          <Card bordered={false} style={{ marginTop: 24 }}>
            <Descriptions title="地址信息" bordered>
              {orderDetail.orderMain?.startAddress && (
                <Descriptions.Item label="取货地址" span={3}>
                  <div>
                    <div>{orderDetail.orderMain.startAddress.title}</div>
                    <div style={{ color: '#666', fontSize: '13px', marginTop: '4px' }}>
                      {orderDetail.orderMain.startAddress.detail}
                    </div>
                    <div style={{ color: '#666', fontSize: '13px' }}>
                      联系人：{orderDetail.orderMain.startAddress.name} {orderDetail.orderMain.startAddress.phone}
                    </div>
                  </div>
                </Descriptions.Item>
              )}
              {orderDetail.orderMain?.endAddress && (
                <Descriptions.Item label="送货地址" span={3}>
                  <div>
                    <div>{orderDetail.orderMain.endAddress.title}</div>
                    <div style={{ color: '#666', fontSize: '13px', marginTop: '4px' }}>
                      {orderDetail.orderMain.endAddress.detail}
                    </div>
                    <div style={{ color: '#666', fontSize: '13px' }}>
                      联系人：{orderDetail.orderMain.endAddress.name} {orderDetail.orderMain.endAddress.phone}
                    </div>
                  </div>
                </Descriptions.Item>
              )}
            </Descriptions>
          </Card>

          <Card bordered={false} style={{ marginTop: 24 }}>
            <Descriptions title="订单进度" >
              <Descriptions.Item span={3}>
                <div style={{ 
                  width: '100%', 
                  overflowX: 'auto',
                  padding: ''
                }}>
                  <div style={{ 
                    minWidth: '800px',  // 确保有足够的最小宽度
                  }}>
                    <Steps
                      current={getCurrentStep(orderDetail)}
                      status={getStepStatus(orderDetail)}
                      direction="horizontal"
                      items={getOrderSteps(orderDetail).map(step => ({
                        title: step.title,
                        description: (
                          <div style={{ minWidth: '120px' }}>  {/* 确保每个步骤有合适的最小宽度 */}
                            <div>{step.description}</div>
                            <div style={{ color: '#999', fontSize: '12px' }}>{step.time}</div>
                          </div>
                        ),
                      }))}
                    />
                  </div>
                </div>
              </Descriptions.Item>
            </Descriptions>
          </Card>

          {(orderDetail.attachImages?.length > 0 || orderDetail.attachFiles?.length > 0 || orderDetail.completionImages?.length > 0) && (
            <Card bordered={false} style={{ marginTop: 24 }}>
              <Descriptions title="相关附件" bordered>
                {orderDetail.attachImages?.length > 0 && (
                  <Descriptions.Item label="订单图片" span={3}>
                    {orderDetail.attachImages.map((image, index) => (
                      <Image
                        key={index}
                        src={image.fileUrl}
                        width={100}
                        style={{ marginRight: 8 }}
                      />
                    ))}
                  </Descriptions.Item>
                )}
                {orderDetail.attachFiles?.length > 0 && (
                  <Descriptions.Item label="订单文件" span={3}>
                    {orderDetail.attachFiles.map((file, index) => (
                      <Button
                        key={index}
                        type="link"
                        icon={getFileIcon(file.fileType)}
                        onClick={() => window.open(file.fileUrl)}
                        style={{ marginRight: 8 }}
                      >
                        {file.fileName}
                      </Button>
                    ))}
                  </Descriptions.Item>
                )}
                {orderDetail.completionImages?.length > 0 && (
                  <Descriptions.Item label="完成凭证" span={3}>
                    {orderDetail.completionImages.map((image, index) => (
                      <Image
                        key={index}
                        src={image.fileUrl}
                        width={100}
                        style={{ marginRight: 8 }}
                      />
                    ))}
                  </Descriptions.Item>
                )}
              </Descriptions>
            </Card>
          )}
        </>
      )}

      {/* 修改申诉详情弹窗 */}
      <Modal
        title="申诉记录"
        open={appealModalVisible}
        onCancel={() => setAppealModalVisible(false)}
        footer={null}
        width={800}
      >
        {appealDetail && appealDetail.length > 0 && (
          <Collapse
            defaultActiveKey={['0']}  // 默认展开第一条记录
            style={{ background: '#fff' }}
          >
            {appealDetail.map((appeal, index) => (
              <Collapse.Panel
                key={index}
                header={
                  <Space>
                    <span>申诉记录 #{index + 1}</span>
                    <Tag color={appeal.orderAppeal?.appealStatus === 0 ? 'warning' : 
                              appeal.orderAppeal?.appealStatus === 1 ? 'success' : 'error'}>
                      {appeal.orderAppeal?.appealStatus === 0 ? '待处理' : 
                       appeal.orderAppeal?.appealStatus === 1 ? '已通过' : '已驳回'}
                    </Tag>
                    <span style={{ color: '#999' }}>{appeal.orderAppeal?.appealTime}</span>
                  </Space>
                }
              >
                {renderAppealContent(appeal)}
              </Collapse.Panel>
            ))}
          </Collapse>
        )}
      </Modal>

      {/* 添加取消订单的弹窗 */}
      <Modal
        title="取消订单"
        open={cancelModalVisible}
        onCancel={() => {
          setCancelModalVisible(false);
          cancelForm.resetFields();
        }}
        footer={null}
      >
        <Form
          form={cancelForm}
          onFinish={confirmCancelOrder}
          layout="vertical"
        >
          <Form.Item
            name="cancelReason"
            label="取消原因"
            rules={[
              { required: true, message: '请输入取消原因' },
              { max: 200, message: '取消原因不能超过200个字符' }
            ]}
          >
            <Input.TextArea
              rows={4}
              placeholder="请输入取消订单的原因"
              maxLength={200}
              showCount
            />
          </Form.Item>
          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button
                onClick={() => {
                  setCancelModalVisible(false);
                  cancelForm.resetFields();
                }}
              >
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                确认取消订单
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      <ChatDrawer
        visible={chatVisible}
        onClose={() => setChatVisible(false)}
        orderId={id}
      />
    </PageContainer>
  );
};

export default OrderDetail;
