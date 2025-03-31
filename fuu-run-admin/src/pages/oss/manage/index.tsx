import { PlusOutlined, FileExcelOutlined, FileImageOutlined, FilePdfOutlined, FileWordOutlined, FilePptOutlined, FileOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { ProTable, ModalForm, ProFormText, ProFormUploadButton, ProFormSelect } from '@ant-design/pro-components';
import { Button, message, Modal, Tag } from 'antd';
import React, { useRef, useState } from 'react';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  remove2,
  list1,
  upload,
  download,
} from '@/services/test-swagger/ossController';
import type { OssVO } from '@/services/test-swagger/typings';

type OssFormProps = {
  onFinish: (values: any) => Promise<boolean>;
  onVisibleChange: (visible: boolean) => void;
  visible: boolean;
};

const OssForm: React.FC<OssFormProps> = (props) => {
  const { visible, onVisibleChange, onFinish } = props;

  return (
    <ModalForm
      title="上传OSS文件"
      visible={visible}
      onVisibleChange={onVisibleChange}
      onFinish={onFinish}
    >
      <ProFormSelect
        name="type"
        label="文件类型"
        options={[
          { label: '附件图片', value: 1 },
          { label: '附件文件', value: 2 },
          { label: '完成凭证', value: 3 },
          { label: '用户头像', value: 4 },
          { label: '学生证照片', value: 5 },
          { label: '订单申诉凭证', value: 6 },
          { label: '学校logo', value: 7 },
          { label: '身份证图片', value: 8 },
        ]}
        rules={[{ required: true, message: '请选择文件类型' }]}
      />
      <ProFormUploadButton
        name="file"
        label="选择文件"
        max={1}
        fieldProps={{
          name: 'file',
        }}
        rules={[{ required: true, message: '请选择文件' }]}
      />
    </ModalForm>
  );
};

const OssManage: React.FC = () => {
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const [detailModalVisible, setDetailModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<OssVO>();
  const actionRef = useRef<ActionType>();

  const handleUpload = async (fields: any) => {
    const hide = message.loading('正在上传...');
    try {
      const response = await upload(
        { 
          type: fields.type,
          name: '',
        },
        {},
        fields.file[0].originFileObj
      );
      hide();
      
      if (response.code === 200) {
        message.success('上传成功!');
        actionRef.current?.reload();
        return true;
      } else {
        message.error(response.msg || '上传失败');
        return false;
      }
    } catch (error) {
      hide();
      message.error('上传失败，请重试!');
      return false;
    }
  };

  const handleDelete = async (ossIds: string) => {
    const hide = message.loading('正在删除...');
    try {
      const response = await remove2({ ossIds });
      hide();
      
      if (response.code === 200) {
        message.success('删除成功!');
        actionRef.current?.reload();
      } else {
        message.error(response.msg || '删除失败');
      }
    } catch (error) {
      hide();
      message.error('删除失败，请重试!');
    }
  };

  const handleDownload = async (url: string, fileName: string) => {
    try {
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      message.success('开始下载');
    } catch (error) {
      message.error('下载失败，请重试');
    }
  };

  // 根据文件后缀返回对应的图标
  const getFileIcon = (fileSuffix?: string) => {
    if (!fileSuffix) return <FileOutlined />;
    
    const suffix = fileSuffix.toLowerCase();
    switch (suffix) {
      case '.xlsx':
      case '.xls':
        return <FileExcelOutlined style={{ color: '#52c41a' }} />;
      case '.jpg':
      case '.jpeg':
      case '.png':
      case '.gif':
      case '.bmp':
      case '.webp':

        return <FileImageOutlined style={{ color: '#1890ff' }} />;
      case '.pdf':
        return <FilePdfOutlined style={{ color: '#ff4d4f' }} />;
      case '.doc':
      case '.docx':

        return <FileWordOutlined style={{ color: '#1890ff' }} />;
      case '.ppt':
      case '.pptx':
        return <FilePptOutlined style={{ color: '#ff7a45' }} />;

      default:
        return <FileOutlined />;
    }
  };

  // 添加业务类型映射函数
  const getBusinessType = (type?: number) => {
    const typeMap: Record<number, { text: string; color: string }> = {
      1: { text: '附件图片', color: 'blue' },
      2: { text: '附件文件', color: 'cyan' },
      3: { text: '完成凭证', color: 'green' },
      4: { text: '用户头像', color: 'purple' },
      5: { text: '学生证照片', color: 'magenta' },
      6: { text: '订单申诉凭证', color: 'red' },
      7: { text: '学校logo', color: 'orange' },
      8: { text: '身份证图片', color: 'volcano' },
    };

    if (!type || !typeMap[type]) {
      return <Tag>未知类型</Tag>;
    }

    return (
      <Tag color={typeMap[type].color} style={{ border: '1px solid' }}>
        {typeMap[type].text}
      </Tag>
    );
  };

  const columns: ProColumns<OssVO>[] = [
    {
      title: 'ID',
      dataIndex: 'ossId',
      hideInForm: true,
      ellipsis: true,
      search: false,
    },
    {
      title: '文件名',
      dataIndex: 'fileName',
      ellipsis: true,
    },

    {
      title: '原始文件名',
      dataIndex: 'originalName',
      ellipsis: true,
    },

    {
      title: '文件后缀',
      dataIndex: 'fileSuffix',
      render: (_, record) => (
        <span>
          {getFileIcon(record.fileSuffix)}
          <span style={{ marginLeft: 8 }}>{record.fileSuffix}</span>
        </span>
      ),
    },
    {
      title: 'URL',
      dataIndex: 'url',
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInTable: true,
      search: false,
    },
    {
      title: '创建人ID',
      dataIndex: 'createId',
      hideInTable: true,
      search: false,

    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInTable: true,
      search: false,

    },
    {
      title: '更新人ID',
      dataIndex: 'updateId',
      hideInTable: true,
      search: false,
    },
    {
      title: '业务类型',
      dataIndex: 'type',
      render: (_, record) => getBusinessType(record.type),
      valueEnum: {
        1: { text: '附件图片' },
        2: { text: '附件文件' },
        3: { text: '完成凭证' },
        4: { text: '用户头像' },
        5: { text: '学生证照片' },
        6: { text: '订单申诉凭证' },
        7: { text: '学校logo' },
        8: { text: '身份证图片' },
      },
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="detail"
          onClick={() => {
            setCurrentRow(record);
            setDetailModalVisible(true);
          }}
        >
          详情
        </a>,
        <a
          key="download"
          onClick={() => handleDownload(record.url!, record.fileName!)}
        >
          下载
        </a>,
        <a
          key="delete"
          style={{ color: '#ff4d4f' }}
          onClick={() => {
            Modal.confirm({
              title: '确认删除',
              content: '确定要删除这个文件吗？',
              onOk: () => handleDelete(record.ossId!),
            });
          }}
        >
          删除
        </a>,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<OssVO>
        headerTitle="OSS文件列表"
        actionRef={actionRef}
        rowKey="ossId"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => setCreateModalVisible(true)}
          >
            <PlusOutlined /> 上传
          </Button>,
        ]}
        request={async (params) => {
          try {
            const response = await list1({
              ...params,
              pageNum: params.current,
              pageSize: params.pageSize,
            });
            
            if (response.code === 200) {
              const rows = response.rows?.map(row => ({
                ...row,
                ossId: row.ossId?.toString()
              })) || [];
              return {
                data: rows,
                success: true,
                total: response.total || 0,
              };
            } else {
              message.error(response.msg || '获取数据失败');
              return {
                data: [],
                success: false,
                total: 0,
              };
            }
          } catch (error) {
            message.error('获取数据失败，请重试');
            return {
              data: [],
              success: false,
              total: 0,
            };
          }
        }}
        columns={columns}
      />

      <OssForm
        visible={createModalVisible}
        onVisibleChange={setCreateModalVisible}
        onFinish={async (value) => {
          const success = await handleUpload(value);
          if (success) {
            setCreateModalVisible(false);
          }
        }}
      />

      <Modal
        title="文件详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={600}
      >
        {currentRow && (
          <div>
            <p><strong>文件ID：</strong> {currentRow.ossId}</p>
            <p><strong>文件名：</strong> {currentRow.fileName}</p>
            <p><strong>原始文件名：</strong> {currentRow.originalName}</p>
            <p>
              <strong>文件类型：</strong> 
              {getFileIcon(currentRow.fileSuffix)}
              <span style={{ marginLeft: 8 }}>{currentRow.fileSuffix}</span>
            </p>
            <p><strong>文件大小：</strong> {currentRow.size}</p>
            <p style={{ wordBreak: 'break-all' }}>
              <strong>URL：</strong> 
              <a 
                onClick={() => handleDownload(currentRow.url!, currentRow.fileName!)}
                style={{ cursor: 'pointer' }}
              >
                {currentRow.url}
              </a>
            </p>
            <p><strong>创建时间：</strong> {currentRow.createTime}</p>
            <p><strong>创建人ID：</strong> {currentRow.createId}</p>
            <p><strong>更新时间：</strong> {currentRow.updateTime}</p>
            <p><strong>更新人ID：</strong> {currentRow.updateId}</p>
            <p>
              <strong>业务类型：</strong> 
              {getBusinessType(currentRow.type)}
            </p>
          </div>
        )}
      </Modal>
    </PageContainer>
  );
};

export default OssManage;
