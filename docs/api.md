API 文档
API 概述
本项目后端采用 Spring Boot + MySQL 构建 RESTful API 服务，供 Android 移动客户端 调用，实现校园任务互助平台的业务功能。

系统架构：

Android App
     │
 HTTP / HTTPS
     │
SpringBoot API
     │
   MySQL

身份认证（JWT）
    用户登录后服务器返回 Token。
    Android 之后请求需要在 Header 中加入：Authorization: Bearer {token}

用户模块 API
用户注册：
POST
/api/user/register

请求：
{
 "username":"test",
 "password":"123456",
 "email":"test@xx.com"
}

返回：
{
 "code":0,
 "data":{
   "userId":1
 }
}

用户登录
POST
/api/user/login

请求：
{
 "username":"test",
 "password":"123456"
}

返回：
{
 "code":0,
 "data":{
   "token":"xxxxx",
   "userId":1,
   "username":"test"
 }
}

获取用户信息
GET
/api/user/info

返回：
{
 "userId":1,
 "username":"test",
 "points":200,
 "creditScore":90
}

任务模块 API
发布任务
POST
/api/task/create

请求：
{
 "title":"帮带饭",
 "description":"东门鸡排饭",
 "type":"LIFE",
 "needPeople":1,
 "rewardPoints":20,
 "deadline":"2026-01-20 20:00"
}

返回：
{
 "taskId":1001
}

获取任务列表
GET
/api/task/list

返回：
{
 "list":[
  {
   "taskId":1001,
   "title":"帮带饭",
   "status":"OPEN",
   "rewardPoints":20
  }
 ]
}

抢任务
POST
/api/task/grab

请求：
{
 "taskId":1001
}

返回：
{
 "code":0,
 "message":"抢单成功"
}

完成任务
POST
/api/task/finish

请求：
{
 "taskId":1001
}

积分系统 API
查询积分
GET
/api/points/info

返回：
{
 "points":300,
 "level":3
}

商城 API
商品列表
GET
/api/shop/items

返回：
{
 "list":[
  {
   "itemId":1,
   "name":"头像框",
   "price":100
  }
 ]
}

积分兑换
POST
/api/shop/exchange

请求：
{
 "itemId":1
}

返回：
{
 "message":"兑换成功"
}




数据库结构设计
用户表 user
字段	类型
id	bigint
username	varchar
password	varchar
points	int
credit_score	int
created_at	datetime
任务表 task
字段	类型
id	bigint
title	varchar
description	text
publisher_id	bigint
need_people	int
current_people	int
reward_points	int
status	varchar
任务参与表 task_participant
字段	类型
id	bigint
task_id	bigint
user_id	bigint