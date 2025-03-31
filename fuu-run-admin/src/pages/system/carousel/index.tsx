import { PageContainer } from '@ant-design/pro-layout';
import { Card, Row, Col, Upload, message, Image, Spin, Button, Modal } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import React, { useState, useEffect } from 'react';
import { getCarouselImages, updateCarouselImage, addCarouselImage, deleteCarouselImage } from '@/services/test-swagger/loginController';
import type { RcFile } from 'antd/es/upload/interface';

const CarouselManage: React.FC = () => {
  const [images, setImages] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [uploadingIndex, setUploadingIndex] = useState<number | null>(null);
  const [addingNew, setAddingNew] = useState(false);
  const [deletingIndex, setDeletingIndex] = useState<number | null>(null);

  const fetchImages = async () => {
    setLoading(true);
    try {
      const res = await getCarouselImages();
      if (res.code === 200 && res.data) {
        // 数据中已经是base64字符串，需要加上前缀才能显示
        const formattedImages = res.data.map(base64 => `${base64}`);
        setImages(formattedImages);
      } else {
        message.error(res.msg || '获取轮播图失败');
      }
    } catch (error) {
      message.error('获取轮播图失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchImages();
  }, []);

  const beforeUpload = (file: RcFile) => {
    const isImage = file.type.startsWith('image/');
    if (!isImage) {
      message.error('只能上传图片文件！');
      return false;
    }
    const isLt2M = file.size / 1024 / 1024 < 5;
    if (!isLt2M) {
      message.error('图片大小不能超过 5MB！');
      return false;
    }
    return true;
  };

  const handleUpload = async (index: number, file: RcFile) => {
    if (!beforeUpload(file)) {
      return;
    }

    setUploadingIndex(index);
    try {
      // 将文件转换为base64
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = async () => {
        const base64Content = (reader.result as string).split(',')[1]; // 移除 "data:image/jpeg;base64," 前缀
        try {
          const res = await updateCarouselImage(index, base64Content);
          if (res.code === 200) {
            message.success('更新成功');
            fetchImages(); // 重新获取图片列表
          } else {
            message.error(res.msg || '更新失败');
          }
        } catch (error) {
          message.error('更新失败');
        }
      };
    } catch (error) {
      message.error('文件处理失败');
    } finally {
      setUploadingIndex(null);
    }
  };

  const handleAddNew = async (file: RcFile) => {
    if (!beforeUpload(file)) {
      return;
    }

    setAddingNew(true);
    try {
      // 将文件转换为base64
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = async () => {
        const base64Content = (reader.result as string).split(',')[1]; // 移除 "data:image/jpeg;base64," 前缀
        try {
          const res = await addCarouselImage(base64Content);
          if (res.code === 200) {
            message.success('添加成功');
            fetchImages(); // 重新获取图片列表
          } else {
            message.error(res.msg || '添加失败');
          }
        } catch (error) {
          message.error('添加失败');
        }
      };
    } catch (error) {
      message.error('文件处理失败');
    } finally {
      setAddingNew(false);
    }
  };

  const handleDelete = async (index: number) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这张轮播图吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        setDeletingIndex(index);
        try {
          const res = await deleteCarouselImage(index);
          if (res.code === 200) {
            message.success('删除成功');
            // 从本地状态中移除被删除的图片
            setImages(prevImages => prevImages.filter((_, i) => i !== index));
            // 重新获取最新的图片列表
            await fetchImages();
          } else {
            message.error(res.msg || '删除失败');
          }
        } catch (error) {
          message.error('删除失败');
        } finally {
          setDeletingIndex(null);
        }
      },
    });
  };

  return (
    <PageContainer>
      <Card 
        title="轮播图管理"
        extra={
          <Upload
            accept="image/*"
            showUploadList={false}
            beforeUpload={(file) => {
              handleAddNew(file);
              return false;
            }}
          >
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              loading={addingNew}
            >
              添加轮播图
            </Button>
          </Upload>
        }
      >
        <Spin spinning={loading}>
          <Row gutter={[16, 16]}>
            {images.map((base64Url, index) => (
              <Col key={index} xs={24} sm={12} md={8} lg={6}>
                <Card
                  hoverable
                  cover={
                    <div style={{ position: 'relative', height: 200 }}>
                      <Image
                        src={base64Url}
                        alt={`轮播图${index + 1}`}
                        style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                      />
                      <div style={{ position: 'absolute', top: 8, right: 8, zIndex: 1 }}>
                        <Button
                          type="primary"
                          danger
                          icon={<DeleteOutlined />}
                          loading={deletingIndex === index}
                          onClick={() => handleDelete(index)}
                        />
                      </div>
                      <Upload
                        accept="image/*"
                        showUploadList={false}
                        beforeUpload={(file) => {
                          handleUpload(index, file);
                          return false;
                        }}
                      >
                        <div
                          style={{
                            position: 'absolute',
                            bottom: 0,
                            left: 0,
                            right: 0,
                            background: 'rgba(0,0,0,0.6)',
                            color: '#fff',
                            textAlign: 'center',
                            padding: '8px 0',
                            cursor: 'pointer',
                            transition: 'opacity 0.3s',
                            opacity: uploadingIndex === index ? 0.5 : 1,
                          }}
                        >
                          {uploadingIndex === index ? (
                            <Spin size="small" />
                          ) : (
                            <>
                              <PlusOutlined /> 更换图片
                            </>
                          )}
                        </div>
                      </Upload>
                    </div>
                  }
                >
                  <Card.Meta
                    title={`轮播图 ${index + 1}`}
                    description={
                      <div style={{ fontSize: 12, color: '#999' }}>
                        点击下方更换图片
                      </div>
                    }
                  />
                </Card>
              </Col>
            ))}
          </Row>
        </Spin>
      </Card>
    </PageContainer>
  );
};

export default CarouselManage;
