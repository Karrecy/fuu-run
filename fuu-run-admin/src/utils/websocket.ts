class WebSocketClient {
  private ws: WebSocket | null = null;
  private url: string;
  private messageCallbacks: ((data: any) => void)[] = [];

  constructor() {
    // 使用当前域名和协议构建 WebSocket URL
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    // 如果是开发环境，使用开发服务器地址
    if (process.env.NODE_ENV === 'development') {
      this.url = 'ws://localhost:4400';
    } else {
      // 生产环境使用相对路径
      this.url = `${protocol}//${host}:4400`;
    }
  }

  connect() {
    try {
      // 获取 token
      const token = localStorage.getItem('token');
      if (!token) {
        console.error('No token found');
        return;
      }

      // 创建 WebSocket 连接，添加 token 到 URL
      const wsUrl = `${this.url}?token=${token}`;
      this.ws = new WebSocket(wsUrl);
      
      // 在连接建立之前设置请求头
      if (this.ws) {
        // 设置自定义请求头
        (this.ws as any).setRequestHeader = {
          'Authorization': `Bearer ${token}`
        };
      }

      this.ws.onopen = () => {
        console.log('WebSocket connected');
      };

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          this.messageCallbacks.forEach(callback => callback(data));
        } catch (error) {
          console.error('Failed to parse WebSocket message:', error);
        }
      };

      this.ws.onclose = () => {
        console.log('WebSocket disconnected');
        this.ws = null;
      };

      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error);
        this.ws = null;
      };
    } catch (error) {
      console.error('Failed to create WebSocket connection:', error);
      this.ws = null;
    }
  }

  send(message: string) {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(message);
    } else {
      console.error('WebSocket is not connected');
    }
  }

  onMessage(callback: (data: any) => void) {
    this.messageCallbacks.push(callback);
    return () => {
      this.messageCallbacks = this.messageCallbacks.filter(cb => cb !== callback);
    };
  }

  disconnect() {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
      this.messageCallbacks = [];
    }
  }

  isConnected() {
    return this.ws?.readyState === WebSocket.OPEN;
  }
}

// 创建单例
const wsClient = new WebSocketClient();

export default wsClient; 