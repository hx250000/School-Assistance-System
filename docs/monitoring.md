# 监控配置

## 1. 概述

本项目使用 Spring Boot Actuator 和结构化日志实现基础监控功能，提升系统可观测性。

监控内容包括：

- 健康检查（Health Check）
- 请求计数统计
- 响应时间统计
- 错误情况统计
- 结构化日志记录

---

## 2. 结构化日志

项目采用 JSON 格式输出日志，便于后续日志分析与集中管理。

日志内容包括：

- 时间戳（timestamp）
- 日志级别（level）
- 类名（logger）
- 日志内容（message）

示例：

```json
{
    "@timestamp":"2026-05-30T11:51:33.9524945+08:00",
    "@version":"1",
    "message":"user login: ",
    "logger_name":"org.example.back.service.impl.UserServiceImpl","thread_name":"http-nio-8080-exec-7",
    "level":"INFO",
    "level_value":20000
}
```

## 3. 健康检查

项目集成 Spring Boot Actuator。

健康检查接口：

```
GET /actuator/health
```

返回示例：

```
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 568253739008,
        "free": 227448623104,
        "threshold": 10485760,
        "path": "D:\\mobiledev\\SchoolAssistanceSystem\\backend\\back\\.",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

该接口用于检测应用当前运行状态，便于运维系统进行健康状态检测。

## 4. 指标集成

项目使用 Micrometer（Actuator 默认集成）收集运行指标。

指标接口：

```
GET /actuator/metrics/http.server.requests
```

核心指标：

### 4.1 请求计数

COUNT 字段表示系统接收到的请求总数。

示例：

```
    {
      "statistic": "COUNT",
      "value": 19
    }
```

### 4.2 响应时间

关键字段：

```
{
  "statistic": "TOTAL_TIME",
  "value": 2.3942322
}
```

表示所有请求累计耗时。

```
{
  "statistic": "MAX",
  "value": 0.37256
}
```

表示最大响应时间。

### 4.3 错误统计

通过 status 和 outcome 标签统计请求结果。

示例：

```
"outcome": [
    "SUCCESS",
    "CLIENT_ERROR"
],
"status": [
    "200",
    "404"
]
```

可用于分析接口异常情况和计算错误率。

## 5. Prometheus 集成

项目集成 Micrometer Prometheus Registry。

指标暴露端点：

```
/actuator/prometheus
```

Prometheus 可定期抓取该端点，实现系统监控与告警。

## 6. 监控效果

目前已实现：

- 结构化日志输出
- 健康检查
- 请求计数统计
- 响应时间统计
- 错误状态统计

系统具备基础可观测能力，可满足项目监控需求。
