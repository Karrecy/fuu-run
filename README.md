# 🚀 福U跑腿 - 校园跑腿服务平台

<div align="center">

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Stars](https://img.shields.io/github/stars/yourusername/fuu-run.svg?style=social)](https://github.com/yourusername/fuu-run/stargazers)

</div>

## 📢 项目声明

- 本项目仅用于学习交流目的
- 本项目不在任何平台出售，如有发现请积极举报
- 本项目是一个全栈项目，包含完整的前后端实现
- 如果本项目对您有所帮助，欢迎给个 ⭐ Star 支持

## 🎯 项目简介

福U跑腿是一款专注于校园场景的跑腿服务平台，提供以下核心功能：

- 多校区管理
- 在线下单与接单
- 图片文件上传
- 微信支付集成
- 余额提现功能
- 实时在线沟通

## 🖼️ 项目展示

### 小程序端界面
![小程序界面](https://gitee.com/Karrecy/fuu-run/raw/master/xcx.png)

### 管理后台界面
![管理后台](https://gitee.com/Karrecy/fuu-run/raw/master/admin.png)

## 🛠️ 技术架构

### 项目结构
```
福U跑腿项目/
├── fuu-fun/                   # 后端服务
│   ├── fuu-admin/   		   # 服务入口模块
│   ├── fuu-common/   		   # 通用工具模块
│   ├── fuu-framework/   	   # 框架模块
│   ├── fuu-order/   		   # 跑腿订单模块
│   ├── fuu-oss/   		       # OSS管理模块
│   ├── fuu-payment/   		   # 支付模块
│   ├── fuu-system/   		   # 系统管理模块
│   └── pom.xml                # 父pom文件
│
├── fuu-fun-admin/             # 管理端前端 (Antd-Pro)
│   ├── config/                # 配置文件
│   ├── public/                # 静态资源
│   ├── src/                   # 源代码
│   └── ...                    # 其他配置文件
│
├── fuu-fun-uni/               # 小程序端 (uniapp)
│   ├── common/           	   # 公共模块
│   ├── pages/                 # 页面目录
│   ├── static/                # 静态文件
│   └── ...                    # 其他配置文件
│
└── init.sql                   # 数据库初始化脚本
```

## 🚀 快速开始

### 后端部署

#### 环境要求
- JDK 1.8+
- Spring Boot 2.7.6
- MySQL 8.X
- Redis

#### 配置步骤
1. 克隆项目到本地
2. 创建数据库 `fuudb`
3. 执行 `init.sql` 初始化数据库
4. 配置 `application.yml`：
   - 高德地图 API Key
   - 微信小程序配置
   - 微信支付配置
   - OSS 存储配置
5. 导入支付密钥文件至 `recourse/cert/` 目录
6. 构建并运行项目

### 前端部署

#### 小程序端
- 环境要求：Node.js 16.13.1, npm 8.1.2
- 开发工具：HBuilder X, 微信开发者工具
- 部署步骤：
  1. 使用 HBuilder X 打开 `fuu-run-uni`
  2. 配置微信小程序 AppID
  3. 安装依赖：`npm install`
  4. 运行到小程序模拟器

#### 管理后台
- 环境要求：Node.js 16.13.1, npm 8.1.2
- 开发工具：VSCode
- 部署步骤：
  1. 打开 `fuu-fun-admin`
  2. 安装依赖：`npm install`
  3. 启动开发服务器：`npm run start:dev`

## �� 技术支持

如有任何问题，请联系客服微信：Tao_Bliess

---

<div align="center">
感谢您的使用！如果觉得项目不错，请给个 ⭐ Star 支持！
</div>
