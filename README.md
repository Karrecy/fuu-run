

```
🕙 分享是一种美德，右上随手点个 🌟 Star，谢谢
```

**温馨提醒**

1. 本项目仅适用学习交流
2. 本项目不在任何平台出售,如有发现请积极举报<br/>
3. 为了更好的体验，友情提示此项目是一个全栈项目<br/>
4. 不要只是白嫖 ,如果帮到你了麻烦**点个Star**<br/>

## 运行图示

- 后台


- 前台



## 1.项目简介

**福U跑腿项目是一款集成多校区管理，提供用户在线下单、接单功能，图片文件上传，集成微信支付，提供余额提现，在线沟通功能的校园类跑腿微信小程序项目**

### 1.1功能概况



### 1.3项目前台展示

小程序端前台



![img](https://gitee.com/Karrecy/fuu-run/raw/master/xcx.png)




### 1.5项目后台展示

 ![img](https://gitee.com/Karrecy/fuu-run/raw/master/admin.png)




## 2.项目完整运行步骤

### 2.1架构图



 


### 2.2项目结构

```
 福U跑腿项目/
├── fuu-fun/                   # 后端服务目录
│   ├── fuu-admin/   		   # 服务入口模块
│   ├── fuu-common/   		   # 通用工具模块
│   ├── fuu-framework/   	   # 框架模块
│   ├── fuu-order/   		   # 跑腿订单模块
│   ├── fuu-oss/   		       # OSS管理模块
│   ├── fuu-payment/   		   # 支付模块
│   ├── fuu-system/   		   # 系统管理模块
│   ├── fuu-admin/   		   # 服务入口
│   └── pom.xml                # 父pom文件
│
├── fuu-fun-admin/             # 管理端前端目录 （Antd-Pro）
│   ├── config/                # 配置文件
│   ├── public/                # 静态资源
│   ├── src/                   # 源代码
│   ├── type/                  # 类型定义
│   ├── package.json           # 依赖配置
│   ├── jsconfig.json          # 编辑器配置
│   ├── jest.config.ts.json    # 测试配置 
│   └── tsconfig.json          # TS配置  
│
├── fuu-fun-uni/               # 小程序端目录（uniapp）
│   ├── common/           	   # 公共模块
│   ├── pages/                 # 页面目录
│   ├── request/               # 网络请求
│   ├── static/                # 静态文件
│   ├── store/                 # 状态管理
│   ├── utils/                 # 工具函数
│   ├── App.vue                # 根组件
│   ├── index.html             # 入口HTML
│   ├── main.js                # 入口JS
│   ├── manifest.json          # 小程序信息
│   ├── package.json           # 项目配置
│   ├── pages.json             # 页面配置
│   ├── uni.promisify.adaptor.js           # Promise适配
│   └── uni.scss               # 全局样式
│
├── init.sql                     # 数据库初始化脚本
│
└── README.md                    # 项目总说明文档

```

### 2.3后端代码本地部署

#### 2.3.1环境配置

**运行环境**：Springboot 2.7.6， Mysql 8.X， Redis

**项目配置**：fuu-admin -> src -> main -> resources -> application.yml进行更改

1. 申请高德地图key <==> amap.key
2. 申请微信小程序(开通微信支付) 相关配置 <==> wx.miniapp.configs
3. 申请微信支付 相关配置 <==> wx.miniapp.pay
4. 申请对象存储oss

#### 2.3.2后端代码运行


操作步骤：

1. idea打开后端fuu-run目录
2. 连接并创建数据库fuudb
3. 执行init.sql脚本，初始化数据库
4. 导入支付密钥文件 至 recourse下的cert/apiclient_key.pem和cert/apiclient_cert.pem
5. 引入依赖，构建项目
6. 运行

### 2.4项目前端代码运行步骤

#### 2.4.1项目前端前台代码运行

**前台运行环境：**node 16.13.1 npm 8.1.2

**开发工具：**HBuilder X  微信开发者工具 

操作步骤：

1. HBuilder X 打开fuu-run-uni
2. 进入manifest.json->微信小程序配置->微信小程序AppID（改为你自己的）
3. 终端打开项目，npm install下载依赖
4. 上方的 运行->运行到小程序模拟器

#### 2.4.2项目前端后台代码运行

**后台运行环境：** node 16.13.1 npm 8.1.2

**开发工具：** vscode

操作步骤：

1. vscode打开fuu-fun-admin
2. npm install
3. npm run start:dev



## 添加客服微信

Tao_Bliess
