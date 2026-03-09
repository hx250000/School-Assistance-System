# 后端文档

## 1. 模块名称

SchoolAssignmentStystem 后端服务

## 2. 模块概述

CampusHub 是一个面向校园生活的任务互助平台。
用户可以在平台发布生活互助或娱乐任务，其他用户可以抢任务并完成任务获得积分奖励。

后端系统基于 **Spring Boot + MySQL** 构建 RESTful API 服务，为 Android 客户端提供数据支持。

系统架构：

Android App
↓
HTTP / HTTPS
↓
SpringBoot API
↓
MySQL

系统主要负责：

* 用户认证与账户管理
* 任务发布与任务管理
* 抢任务逻辑处理
* 积分奖励系统
* 积分商城兑换
* 用户信用值统计

---

# 3. 技术选型

| 技术            | 作用          |
| ------------- | ----------- |
| Spring Boot   | 后端核心框架      |
| Spring MVC    | REST API 实现 |
| JWT           | 用户身份认证      |
| MySQL         | 数据库         |
| JPA | 数据访问        |
| Redis（可扩展）    | 并发控制与缓存     |

开发语言：

Java 21

---

# 4. 身份认证机制

系统采用 **JWT（JSON Web Token）认证机制**。

流程：

1 用户登录
2 服务器返回 token
3 Android 客户端之后的请求需要在 Header 中携带 token

请求示例：

```
Authorization: Bearer {token}
```

服务器通过 token 解析用户身份。

---

# 5. 系统模块设计

## 5.1 用户模块

负责用户账户管理与认证。

主要功能：

* 用户注册
* 用户登录
* 获取用户信息
* 用户积分管理
* 用户信用值管理

用户信息包括：

* 用户ID
* 用户名
* 密码
* 积分
* 信用值

---

## 5.2 任务模块

任务模块是系统核心模块。

用户可以发布校园互助任务，例如：

* 帮带饭
* 代拿快递
* 游戏组队
* 学习辅导

任务信息包括：

* 任务标题
* 任务描述
* 任务类型
* 需要人数
* 当前人数
* 积分奖励
* 截止时间
* 发布者

任务状态示例：

| 状态          | 说明  |
| ----------- | --- |
| OPEN        | 待接取 |
| IN_PROGRESS | 进行中 |
| FINISHED    | 已完成 |

---

## 5.3 抢任务机制

用户可以通过抢单方式加入任务。

流程：

用户点击抢任务
↓
服务器检查任务状态是否为 OPEN
↓
检查任务人数是否已满
↓
更新任务参与人数
↓
将用户加入任务参与表

为避免多人同时抢单导致超额问题，可以使用：

* 数据库事务
* 乐观锁
* Redis 分布式锁（扩展）

---

## 5.4 积分系统

用户完成任务后可以获得积分奖励。

积分来源：

* 完成任务
* 平台活动奖励

积分用途：

* 积分商城兑换商品
* 用户等级系统

系统需要记录用户积分并支持查询。

---

## 5.5 积分商城

用户可以使用积分兑换虚拟商品。

商品示例：

* 平台头像框
* 虚拟称号
* 校园优惠券

商城系统支持：

* 商品列表查询
* 商品兑换
* 积分扣除

---

# 6. 数据库结构设计

## 用户表 user

| 字段           | 类型       | 说明   |
| ------------ | -------- | ---- |
| id           | bigint   | 用户ID |
| username     | varchar  | 用户名  |
| password     | varchar  | 密码   |
| points       | int      | 积分   |
| credit_score | int      | 信用值  |
| created_at   | datetime | 创建时间 |

---

## 任务表 task

| 字段             | 类型      | 说明   |
| -------------- | ------- | ---- |
| id             | bigint  | 任务ID |
| title          | varchar | 任务标题 |
| description    | text    | 任务描述 |
| publisher_id   | bigint  | 发布者  |
| need_people    | int     | 需要人数 |
| current_people | int     | 当前人数 |
| reward_points  | int     | 奖励积分 |
| status         | varchar | 任务状态 |

---

## 任务参与表 task_participant

| 字段      | 类型     | 说明   |
| ------- | ------ | ---- |
| id      | bigint | ID   |
| task_id | bigint | 任务ID |
| user_id | bigint | 用户ID |

---

# 7. API 接口说明

本系统通过 RESTful API 为 Android 客户端提供服务。

接口主要包括：

用户模块：

* POST /api/user/register
* POST /api/user/login
* GET /api/user/info

任务模块：

* POST /api/task/create
* GET /api/task/list
* POST /api/task/grab
* POST /api/task/finish

积分模块：

* GET /api/points/info

商城模块：

* GET /api/shop/items
* POST /api/shop/exchange

---

# 8. 项目目录结构（后端）

```
backend
 ├── controller
 │    ├── UserController
 │    ├── TaskController
 │    ├── PointsController
 │    └── ShopController
 │
 ├── service
 │
 ├── repository
 │
 ├── entity
 │
 └── config
      └── JwtConfig
```

---

# 9. 运行方式

开发环境：

Java 17
SpringBoot 3.x
MySQL 8.x

运行：

```
mvn spring-boot:run
```

或

```
java -jar campushub.jar
```

---
