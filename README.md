# 🚀 福U跑腿 - 校园跑腿服务平台

<div align="center">
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-blue.svg)](https://vuejs.org/)
[![Ant Design Pro](https://img.shields.io/badge/Ant%20Design%20Pro-4.x-blue.svg)](https://pro.ant.design/)
[![UniApp](https://img.shields.io/badge/UniApp-3.x-green.svg)](https://uniapp.dcloud.io/)
[![Sa-Token](https://img.shields.io/badge/Sa%20Token-1.34.0-orange.svg)](https://sa-token.dev33.cn/)
[![Redis](https://img.shields.io/badge/Redis-7.x-red.svg)](https://redis.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-blue.svg)](https://www.mysql.com/)

</div>

## 📢 项目声明

- 本项目仅用于学习交流目的，不在任何平台出售，如有发现请积极举报
- 本项目是一个全栈项目，包含完整的前后端实现
- 如果本项目对您有所帮助，欢迎给个 ⭐ Star 支持



## 📚 技术栈

### 后端技术栈
| 技术 | 说明 | 版本 | 官网 |
| --- | --- | --- | --- |
| Spring Boot | 容器+MVC框架 | 2.7.6 | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) |
| Sa-Token | 权限认证框架 | 1.37.0 | [https://sa-token.dev33.cn](https://sa-token.dev33.cn) |
| MyBatis-Plus | ORM框架 | 3.5.4 | [https://mybatis.org](https://mybatis.org) |
| Redis | 分布式缓存 | 最新 | [https://redis.io](https://redis.io) |
| MySQL | 关系型数据库 | 8.0 | [https://www.mysql.com](https://www.mysql.com) |
| Redisson | 分布式锁 | 3.20.1 | [https://redisson.org](https://redisson.org) |
| Netty | 网络应用框架 | 4.1.36 | [https://netty.io](https://netty.io) |

### 前端技术栈
| 技术 | 说明 | 版本 | 官网 |
| --- | --- | --- | --- |
| Vue | 前端框架 | 3.x | [https://vuejs.org](https://vuejs.org) |
| Ant Design Pro | 后台UI框架 | 4.x | [https://pro.ant.design](https://pro.ant.design) |
| Uni-app | 跨平台框架 | 3.x | [https://uniapp.dcloud.io](https://uniapp.dcloud.io) |



## 🎯 项目简介

福U跑腿是一款专注于校园场景的综合性跑腿服务平台，采用前后端分离架构，基于 Spring Boot + Vue + Uni-app 开发。本项目致力于解决校园内快递代取、餐食代购等日常跑腿需求，为校园师生提供便捷、安全、高效的跑腿服务。

### 核心功能

##### **🎃用户服务**

- **多角色系统**：管理员、校区代理、跑腿员、普通用户
- **地址管理**：支持快捷选址、地图选点
- **申请跑腿**：审核通过可在线接单
- **在线下/接单**：绑定手机号就可以进行下单
- **订单申诉**：对异议订单申诉通过可全额退款
- **实时沟通**：订单内可与跑腿员、管理员在线沟通
- **敏感词检测**：对编辑内容进行敏感词检测
- **微信订阅消息**：订单状态、消息通知、退款状态变更时在微信内通知用户

##### **🎄订单系统**

- **订单自动化机制**：长时间未付款、未接单自动取消，长时间未确认自动完成订单
- **订单全生命周期管理**

##### **🧨支付系统**

- **集成微信支付**
- **余额提现功能**
- **资金流动记录**

##### **🎈运营管理**

- **多校区统一管理**
- **系统管理**：系统监控、轮播图管理、全局配置
- **区域管理**（用于快捷地址）
- **用户、权限管理**
- **工单处理**：跑腿申请管理、订单申诉管理
- **邮箱通知**：有新工单时会邮箱通知
- **标签管理**（快捷选择服务类型）
- **OSS对象存储管理**
- **资金管理**：余额管理、提现申请、资金流动、账户明细

### 技术亮点

##### 🔴**模块化架构设计**

- 基于 Spring Boot 的多模块开发，采用父子 POM 结构
- 模块间低耦合高内聚，便于维护和扩展
- 统一的依赖管理和版本控制
- 模块化的业务划分，包括订单、支付、系统管理等核心模块

##### 🟠**高并发**

- 基于 Redisson 实现分布式锁，保证订单接单等关键操作的并发安全
- Redis 队列实现异步任务处理机制：
  - 支付和退款回调的异步处理
  - 订单超时自动取消机制
  - 订单自动完成处理
- 基于 Redisson 对 Redis队列进行封装，降低编码维护成本，提高可用性

##### **🟡安全性与权限控制**

- 基于 Sa-Token 实现 RBAC 权限控制系统
- 基于 AOP、自定义注解和 Redis 实现接口限流
- 全局异常处理，统一异常响应

##### 🟢**实时通信**

- 基于 Netty 实现 WebSocket 服务，支持超高并发
- 用户聊天消息异步持久化处理

##### **🟣数据验证与安全处理**

- 自定义注解与 Validator 实现字段校验
- 整合 SensitiveWord 实现敏感词过滤
- 数据字段防止XSS攻击

## 🎯 项目展示（仅展示部分，其他在docImgs里查看）

### 小程序端界面

<table>
<tr>
    <td><img src="https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/mini/万能帮服务.jpg" width="250"/></td>
    <td><img src="https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/mini/%E4%B8%AA%E4%BA%BA%E4%B8%AD%E5%BF%83.jpg" width="250"/></td>
    <td><img src="https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/mini/%E4%B8%AA%E4%BA%BA%E4%BF%A1%E6%81%AF.jpg" width="250"/></td>
</tr>
<tr>
    <td><img src="https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/mini/%E5%A1%AB%E5%86%99%E5%9C%B0%E5%9D%80.jpg" width="250"/></td>
    <td><img src="https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/mini/%E8%AE%A2%E5%8D%95%E5%A4%A7%E5%8E%85.jpg" width="250"/></td>
    <td><img src="https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/mini/%E8%B7%91%E8%85%BF%E4%B8%AD%E5%BF%83.jpg" width="250"/></td>
</tr>
<tr>
    <td><img src="https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/mini/%E8%B7%91%E8%85%BF%E4%BB%8B%E7%BB%8D.jpg" width="250"/></td>
    <td><img src="https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/mini/%E9%A6%96%E9%A1%B5.jpg" width="250"/></td>
    <td></td>
</tr>
</table>


### 管理后台界面
![管理后台](https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/admin/%E8%AE%A2%E5%8D%95%E7%AE%A1%E7%90%86.png)

![管理后台](https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/admin/%E8%BD%AE%E6%92%AD%E5%9B%BE%E7%AE%A1%E7%90%86.png)

![管理后台](https://gitee.com/Karrecy/fuu-run/raw/master/docImgs/admin/%E5%8C%BA%E5%9F%9F%E7%AE%A1%E7%90%86.png)





## 🛠️ 技术架构

### 项目结构
```
福U跑腿项目/
├── deployment/                # 服务器部署
│
├── docImgs/                   # 系统图例展示
│
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

### 准备工作

- 申请高德地图 API Key
- 申请微信小程序（个体户/公司）
- 小程序认证（个体户30￥）
- 开通微信支付
- 购买OSS对象存储（建议阿里云OSS）

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
- 环境要求：
  - Node.js 16.13.1
  - npm 8.1.2

- 开发工具：
  - HBuilder X
  - 微信开发者工具

- 部署步骤：
  1. 使用 HBuilder X 打开 `fuu-run-uni`
  2. 配置微信小程序 AppID
  3. 安装依赖：`npm install`
  4. 运行到微信开发者工具

#### 管理后台
- 环境要求：
  - Node.js 16.13.1
  - npm 8.1.2

- 开发工具：VSCode
- 部署步骤：
  1. 打开 `fuu-run-admin`
  2. 安装依赖：`npm install`
  3. 启动开发服务器：`npm run start:dev`

## 🚀 服务器部署

### 环境要求
- JDK 1.8+
- MySQL 8.x
- Redis 7.x
- Nginx 1.18+
- Node.js 16+

### 部署步骤
1. 后端部署
   ```bash
   # 打包
   mvn clean package
   # 上传jar包到服务器
   scp target/*.jar root@your-server:/opt/fuu-run/
   # 运行
   nohup java -jar fuu-run.jar --spring.profiles.active=prod &
   ```

2. 前端部署
   ```bash
   # 打包
   npm run build
   # 上传到Nginx目录
   scp -r dist/* root@your-server:/usr/share/nginx/html/
   ```

3. Nginx配置
   ```nginx
   server {
       listen 80;
       server_name your-domain;
       
       location / {
           root /usr/share/nginx/html;
           try_files $uri $uri/ /index.html;
       }
       
       location /api {
           proxy_pass http://localhost:8080;
       }
   }
   ```

## 🙏 特别鸣谢

- [Ant Design Pro](https://pro.ant.design/) - 开箱即用的中台前端/设计解决方案 [![Stars](https://img.shields.io/github/stars/ant-design/ant-design-pro?style=social)](https://github.com/ant-design/ant-design-pro)

- [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) - 基于SpringBoot的权限管理系统 [![Stars](https://img.shields.io/github/stars/yangzongzhuan/RuoYi-Vue?style=social)](https://github.com/yangzongzhuan/RuoYi-Vue)

- [SensitiveWord](https://github.com/houbb/sensitive-word) - 基于 DFA 算法的敏感词过滤工具 [![Stars](https://img.shields.io/github/stars/houbb/sensitive-word?style=social)](https://github.com/houbb/sensitive-word)

- [Sa-Token](https://sa-token.dev33.cn/) - 轻量级 Java 权限认证框架 [![Stars](https://img.shields.io/github/stars/dromara/Sa-Token?style=social)](https://github.com/dromara/Sa-Token)

## 🛠️ 技术支持

如有任何问题，请联系客服微信：Tao_Bliess

---

<div align="center">
感谢您的使用！如果觉得项目不错，请给个 ⭐ Star 支持！
</div>
