import React, { useEffect, useRef, useState } from 'react';
import { Drawer, Input, Button, Avatar, Tag, message, Space } from 'antd';
import { SendOutlined } from '@ant-design/icons';
import styles from './index.less';
import { initchat, pageChat as listChat } from '@/services/test-swagger/orderChatController';
import { useModel } from '@umijs/max';
import wsClient from '@/utils/websocket';

// 添加聊天初始化数据的类型定义
interface ChatInitData {
  agentAvatar: string;
  agentId: number;
  runnerAvatar: string;
  userAvatar: string;
  agentName: string;
  userName: string;
  userId: number;
  adminName: string;
  adminAvatar: string;
  runnerName: string;
  schoolId: number;
  adminId: number;
  runnerId: number;
  schoolName: string;
  schoolLogo: string;
}

// 添加消息数据类型
interface MessageData {
  orderId: string;
  isBroadcast: number;
  recipientIds: number[];
  msgType: number;
  message: string;
  senderId: number;
  senderType: number;
  createTime: string;
}

const ChatDrawer: React.FC<{
  visible: boolean;
  onClose: () => void;
  orderId: string;
}> = ({ visible, onClose, orderId }) => {
  const [messages, setMessages] = useState<API.OrderChat[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [chatData, setChatData] = useState<ChatInitData>();
  const [msgData, setMsgData] = useState<MessageData>({
    orderId: '',
    isBroadcast: 1,
    recipientIds: [],
    msgType: 1,
    message: '',
    senderId: 0,
    senderType: 0,
    createTime: '1999-02-02 12:12:12'
  });
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;

  // 添加新的状态来管理@的用户
  const [atUsers, setAtUsers] = useState<Set<number>>(new Set());
  const [atText, setAtText] = useState('');

  // 添加长按状态
  const [pressTimer, setPressTimer] = useState<NodeJS.Timeout | null>(null);
  const [isPressing, setIsPressing] = useState(false);

  // 初始化聊天数据
  useEffect(() => {
    if (visible && orderId) {
      initChat();
      fetchMessages();
      
      // 只在未连接时建立新连接
      if (!wsClient.isConnected()) {
        wsClient.connect();
      }
      
      // 监听消息
      const cleanup = wsClient.onMessage((data) => {
        // 处理接收到的消息
        if (data.orderId === orderId) {
          // 添加发送者信息到消息中
          const displayMessage = {
            ...data,
            senderAvatar: getAvatar(data.senderId, data.senderType),
            senderName: getName(data.senderId, data.senderType)
          };
          setMessages(prev => [...prev, displayMessage]);
          scrollToBottom();
        }
      });

      return () => {
        // 清理消息监听
        cleanup();
        // 只在对话框关闭时断开连接
        if (!visible) {
          wsClient.disconnect();
        }
      };
    }
  }, [visible, orderId]);

  const initChat = async () => {
    try {
      const res = await initchat({ 
        orderId
      });
      if (res.code === 200 && res.data) {
        setChatData(res.data);
        
        // 初始化消息数据
        const recipients = [res.data.agentId];
        if (res.data.runnerId) recipients.push(res.data.runnerId);
        if (res.data.userId) recipients.push(res.data.userId);

        setMsgData({
          orderId,
          isBroadcast: 1,
          recipientIds: recipients,
          msgType: 1,
          message: '',
          senderId: currentUser?.user?.uid || 0,
          senderType: currentUser?.user?.userType || 0,
          createTime: '1999-02-02 12:12:12'
        });
      }
    } catch (error) {
      message.error('初始化聊天失败');
    }
  };

  const fetchMessages = async () => {
    try {
      const res = await listChat({
        pageQuery: {
          pageSize: 50,
          pageNum: 1,
        },
        orderId
      });
      if (res.code === 200) {
        setMessages(res.rows || []);
        scrollToBottom();
      }
    } catch (error) {
      message.error('获取聊天记录失败');
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  // 获取用户头像
  const getAvatar = (senderId?: number, senderType?: number) => {
    if (!chatData) return '';
    
    if (senderType === 0) return chatData.adminAvatar;
    if (senderType === 1) return chatData.agentAvatar;
    if (senderId === chatData.userId) return chatData.userAvatar;
    if (senderId === chatData.runnerId) return chatData.runnerAvatar;
    return '';
  };

  // 获取用户名称
  const getName = (senderId?: number, senderType?: number) => {
    if (!chatData) return '';
    
    if (senderType === 0) return chatData.adminName;
    if (senderType === 1) return chatData.agentName;
    if (senderId === chatData.userId) return chatData.userName;
    if (senderId === chatData.runnerId) return chatData.runnerName;
    return '';
  };

  // 修改 getUserTypeTag 函数
  const getUserTypeTag = (senderId?: number, senderType?: number) => {
    if (!chatData) return null;

    // 根据 senderId 匹配用户身份
    let tagInfo = { text: '未知', color: 'default' };
    
    if (senderId === chatData.adminId) {
      tagInfo = { text: '管理员', color: 'red' };
    } else if (senderId === chatData.agentId) {
      tagInfo = { text: '校区代理', color: 'orange' };
    } else if (senderId === chatData.userId) {
      tagInfo = { text: '用户', color: '#cccccc' };
    } else if (senderId === chatData.runnerId) {
      tagInfo = { text: '跑腿员', color: '#0055ff' };
    }

    return <Tag color={tagInfo.color}>{tagInfo.text}</Tag>;
  };

  // 添加一个格式化日期的辅助函数
  const formatDateTime = (date: Date) => {
    const pad = (n: number) => n.toString().padStart(2, '0');
    const year = date.getFullYear();
    const month = pad(date.getMonth() + 1);
    const day = pad(date.getDate());
    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const seconds = pad(date.getSeconds());
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  };

  // 添加一个函数来检查是否已经@过该用户
  const isUserMentioned = (senderName: string) => {
    return inputValue.includes(`@${senderName}`);
  };

  // 修改长按头像的处理函数
  const handleLongPressAvatar = (senderId: number, senderName: string) => {
    // 如果已经@过这个用户，就不再添加
    if (atUsers.has(senderId) || isUserMentioned(senderName)) {
      message.info(`已经@过 ${senderName} 了`);
      return;
    }

    // 添加@用户到集合
    const newAtUsers = new Set(atUsers);
    newAtUsers.add(senderId);
    setAtUsers(newAtUsers);

    // 更新输入框文本，添加@昵称
    const newAtText = `@${senderName} `;
    setAtText(newAtText);
    // 在当前光标位置插入@文本，如果没有光标位置则添加到末尾
    const textArea = document.querySelector('.ant-input-textarea textarea') as HTMLTextAreaElement;
    if (textArea) {
      const start = textArea.selectionStart;
      const end = textArea.selectionEnd;
      const newValue = inputValue.slice(0, start) + newAtText + inputValue.slice(end);
      setInputValue(newValue);
      // 设置光标位置到@文本后面
      setTimeout(() => {
        textArea.focus();
        textArea.setSelectionRange(start + newAtText.length, start + newAtText.length);
      }, 0);
    } else {
      setInputValue(inputValue + newAtText);
    }

    // 更新消息数据
    setMsgData(prev => ({
      ...prev,
      isBroadcast: 0,
      recipientIds: Array.from(newAtUsers)
    }));
  };

  // 修改输入框的 onChange 处理函数
  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const newValue = e.target.value;
    setInputValue(newValue);
    
    // 检查是否有@符号
    if (!newValue.includes('@')) {
      // 如果没有@，恢复到初始状态
      const recipients = [];
      if (chatData) {
        if (chatData.agentId !== currentUser?.user?.uid) recipients.push(chatData.agentId);
        if (chatData.runnerId && chatData.runnerId !== currentUser?.user?.uid) recipients.push(chatData.runnerId);
        if (chatData.userId && chatData.userId !== currentUser?.user?.uid) recipients.push(chatData.userId);
      }
      
      setAtUsers(new Set());
      setMsgData(prev => ({
        ...prev,
        isBroadcast: 1,
        recipientIds: recipients
      }));
      return;
    }
    
    // 检查是否删除了@提及
    const currentMentions = Array.from(atUsers).map(uid => {
      const name = getName(uid, undefined);
      return { uid, name };
    });
    
    // 更新 atUsers 集合
    const newAtUsers = new Set(
      currentMentions
        .filter(mention => mention.name && newValue.includes(`@${mention.name}`))
        .map(mention => mention.uid)
    );
    
    setAtUsers(newAtUsers);
    
    // 更新消息数据
    setMsgData(prev => ({
      ...prev,
      isBroadcast: 0,
      recipientIds: Array.from(newAtUsers)
    }));
  };

  // 修改长按处理函数
  const handlePressStart = (e: React.MouseEvent | React.TouchEvent, senderId: number, senderName: string) => {
    // 阻止默认事件，防止触发其他事件
    e.preventDefault();
    
    // 设置长按状态
    setIsPressing(true);
    
    // 创建定时器
    const timer = setTimeout(() => {
      handleLongPressAvatar(senderId, senderName);
    }, 500);
    
    setPressTimer(timer);
  };

  const handlePressEnd = () => {
    // 清除定时器
    if (pressTimer) {
      clearTimeout(pressTimer);
      setPressTimer(null);
    }
    setIsPressing(false);
  };

  // 修改发送消息的处理函数
  const handleSend = () => {
    if (!inputValue.trim() || !chatData) {
      message.warning('不能发送空消息');
      return;
    }

    const newMessage = {
      ...msgData,
      message: inputValue.trim(),
      createTime: formatDateTime(new Date())
    };

    // 发送消息
    wsClient.send(JSON.stringify(newMessage));
    
    // 添加到本地消息列表
    const displayMessage = {
      ...newMessage,
      senderAvatar: getAvatar(newMessage.senderId, newMessage.senderType),
      senderName: getName(newMessage.senderId, newMessage.senderType)
    };
    setMessages(prev => [...prev, displayMessage]);
    
    // 清空输入框和@状态
    setInputValue('');
    setAtUsers(new Set());
    setAtText('');
    
    // 重置消息数据为广播模式
    setMsgData(prev => ({
      ...prev,
      isBroadcast: 1,
      recipientIds: []
    }));
    
    // 滚动到底部
    scrollToBottom();
  };

  return (
    <Drawer
      title={
        <Space>
          <span>在线沟通</span>
          {chatData && <Tag color="blue">{chatData.schoolName}</Tag>}
        </Space>
      }
      placement="right"
      width={500}
      onClose={onClose}
      open={visible}
    >
      <div className={styles.chatContainer}>
        <div className={styles.messageList}>
          {messages
            .sort((a, b) => new Date(a.createTime).getTime() - new Date(b.createTime).getTime())
            .map((msg, index) => (
              <div
                key={index}
                className={`${styles.messageItem} ${
                  msg.senderId === currentUser?.user?.uid ? styles.self : ''
                }`}
              >
                {/* 非自己发送的消息显示在左边 */}
                {msg.senderId !== currentUser?.user?.uid && (
                  <img 
                    className={styles.avatar}
                    src={msg.senderAvatar || getAvatar(msg.senderId, msg.senderType)}
                    alt="avatar"
                    onMouseDown={(e) => handlePressStart(e, msg.senderId || 0, msg.senderName || getName(msg.senderId, msg.senderType))}
                    onMouseUp={handlePressEnd}
                    onMouseLeave={handlePressEnd}
                    onTouchStart={(e) => handlePressStart(e, msg.senderId || 0, msg.senderName || getName(msg.senderId, msg.senderType))}
                    onTouchEnd={handlePressEnd}
                    onTouchCancel={handlePressEnd}
                    style={{ cursor: 'pointer', opacity: isPressing ? 0.7 : 1 }}
                  />
                )}
                
                <div className={styles.contentBox}>
                  <div className={`${styles.contentTop} ${
                    msg.senderId === currentUser?.user?.uid ? styles.textRight : styles.textLeft
                  }`}>
                    {getUserTypeTag(msg.senderId, msg.senderType)}
                    <span className={styles.ellipsis}>
                      {msg.senderName || getName(msg.senderId, msg.senderType)}
                    </span>
                  </div>
                  <div className={`${styles.content} ${
                    msg.senderId === currentUser?.user?.uid ? styles.right : styles.left
                  }`}>
                    <span>{msg.message}</span>
                    <span className={styles.chatTime}>{msg.createTime}</span>
                  </div>
                </div>
                
                {/* 自己发送的消息显示在右边 */}
                {msg.senderId === currentUser?.user?.uid && (
                  <img 
                    className={styles.avatar}
                    src={msg.senderAvatar || getAvatar(msg.senderId, msg.senderType)}
                    alt="avatar"
                  />
                )}
              </div>
            ))}
          <div ref={messagesEndRef} />
        </div>
        <div className={styles.inputArea}>
          <Input.TextArea
            value={inputValue}
            onChange={handleInputChange}
            placeholder="请文明沟通~"
            autoSize={{ minRows: 2, maxRows: 4 }}
            onPressEnter={(e) => {
              if (!e.shiftKey) {
                e.preventDefault();
                handleSend();
              }
            }}
          />
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={handleSend}
          >
            发送
          </Button>
        </div>
      </div>
    </Drawer>
  );
};

export default ChatDrawer;