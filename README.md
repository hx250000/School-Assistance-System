# CampusTask - 校园互助任务平台

## 项目简介

CampusTask 是一个面向大学校园场景的互助任务平台。  
用户可以在平台上发布各种校园生活任务，例如带饭、代拿快递、游戏开黑、学习互助等。  
其他用户可以抢任务并完成任务，从而获得系统奖励的积分。

该项目采用 **Android + SpringBoot** 的前后端分离架构，旨在实现一个简单但完整的校园任务撮合平台，并探索积分经济系统和用户信用评价机制。

---

## 项目目标

- 构建一个校园任务互助平台
- 实现任务发布与抢任务机制
- 设计积分奖励系统
- 提供积分商城兑换功能
- 建立用户信用评价体系
- 支持用户之间的实时交流（可扩展）

---

## 技术架构
Android App
│
│ HTTP / Retrofit
▼
SpringBoot 后端
│
├── MySQL 数据库
├── Redis（缓存 / 并发控制）
└── WebSocket（实时聊天）


---

## 技术栈

### 前端（Android）

- Kotlin
- MVVM 架构
- Retrofit 网络请求
- RecyclerView 列表展示
- Room 本地数据库
- Material Design UI
- WebSocket（聊天功能）

### 后端（SpringBoot）

- SpringBoot
- MyBatis / MyBatis Plus
- MySQL
- Redis
- JWT 登录认证
- WebSocket
- RESTful API
- 定时任务（任务过期处理）

---

## 核心功能模块

### 1. 用户系统

- 用户注册 / 登录
- 用户信息管理
- 查看积分
- 查看信用评分

---

### 2. 任务发布系统

用户可以发布校园任务，例如：

- 带饭
- 代拿快递
- 游戏开黑
- 学习辅导

任务信息包括：

- 任务标题
- 任务描述
- 任务类型
- 需要人数
- 是否需要报酬
- 积分奖励
- 截止时间

---

### 3. 抢任务系统

用户可以抢任务参与。

流程：
发布任务
↓
用户浏览任务列表
↓
点击抢任务
↓
系统检查任务人数是否已满
↓
成功加入任务


系统会进行并发控制，防止任务被多人同时抢到。

---

### 4. 任务状态管理

任务具有不同状态：
待接取
进行中
已完成
已取消
已过期


---

### 5. 积分系统

用户完成任务后可以获得积分奖励。

积分来源：

- 完成任务
- 发布任务
- 每日签到（可扩展）

积分用途：

- 兑换商城商品
- 参与排行榜

---

### 6. 积分商城

用户可以使用积分兑换商品，例如：

- 平台徽章
- 虚拟称号
- 头像框
- 校园优惠券（模拟）

---

### 7. 用户信用评价系统

任务完成后，用户可以互相评价。

评价内容包括：

- 完成情况
- 是否准时
- 服务态度

系统根据评价计算用户信用值。

示例：
信用值 = 好评数 × 2 - 差评数 × 3


信用较低的用户可能会受到限制，例如：

- 无法发布任务
- 抢任务优先级降低

---

### 8. 聊天系统（可扩展）

任务参与者之间可以进行实时聊天。

技术实现：

- WebSocket

支持功能：

- 私聊
- 任务群聊

---

## 数据库设计（核心表）

### 用户表（user）
id
username
password
points
credit_score
create_time


### 任务表（task）
id
title
description
type
publisher_id
max_people
current_people
reward_points
status
deadline
create_time


### 任务参与表（task_user）
id
task_id
user_id
status

### 积分记录表（points_log）
id
user_id
change_amount
reason
create_time


### 商品表（product）
id
name
points_cost
stock


---

## 系统特色

- 校园生活场景设计
- 即时任务撮合机制
- 积分奖励系统
- 用户信用评价体系
- 可扩展聊天系统
- 前后端分离架构

---

## 未来扩展

未来可以扩展以下功能：

- 地图定位任务
- 任务推荐算法
- 用户排行榜
- 成就系统
- 数据统计分析
- AI任务推荐

---

## 项目意义

本项目通过构建一个校园互助平台，实现了任务发布、任务撮合、积分系统与信用评价等核心功能。  
该系统不仅能够提升校园生活便利性，也为学习移动应用开发和后端系统设计提供了实践平台。

---

## 前端设计figma

figma链接：https://www.figma.com/make/PXZVzo1M60FPqofg27rXuK/campusTask?t=fIeBEZ06svHOHP5D-1
