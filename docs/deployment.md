# 校园帮帮系统 - 云服务部署说明文档

本项目后端及数据库已成功部署至云端平台。

## 1. 部署平台与架构选择

* **部署平台**：Railway
* **部署模式**：Dockerfile 容器化自动构建与部署 (CI/CD)
* **服务架构**：同项目画布（Canvas）下多服务内网高内聚架构
    * **后端服务**：`School-Assistance-System` (基于 Spring Boot / Java 17)
    * **数据库服务**：`MySQL` (基于 MySQL 8)

## 2. 目录与多模块根路径配置

由于本项目采用前后端多模块（Monorepo）的目录结构，在 Railway 中进行了手动根目录指定：

* **Root Directory (项目根路径)**：`/backend/back`
* **构建上下文**：Railway 会自动进入 `/backend/back` 目录读取 `Dockerfile`，并以此目录作为上下文执行 Docker 镜像的分步编译。

## 3. 环境变量配置 (Environment Variables)

为了实现敏感信息隔离、确保多环境切换的规范性，生产环境的所有关键核心配置均通过云平台系统的 Environment 进行注入，完全脱离硬编码。

在 Railway 中配置的最终变量矩阵如下：

| 环境变量名 (Key) | 配置值类型 (Value 说明) | 对应作用说明 |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `docker` | 激活生产环境专用的 `application-docker.yml` 配置文件 |
| `SPRING_DATASOURCE_URL` | `jdbc:${{MySQL.MYSQL_URL}}` | 动态引用同项目下 MySQL 的内网专属长连接串，并自动补全 `jdbc:` 协议头 |
| `SPRING_DATASOURCE_USERNAME`| `${{MySQL.MYSQLUSER}}` | 动态引用内网 MySQL 的管理用户名 |
| `SPRING_DATASOURCE_PASSWORD`| `${{MySQL.MYSQLPASSWORD}}` | 动态引用内网 MySQL 的安全访问密码 |
| `MYSQL_DATABASE` | `${{MySQL.MYSQLDATABASE}}` | 动态注入业务数据库名称 |
| `JWT_SECRET` | ******* | 用于后端安全框架的 JWT 令牌加解密 |
| `JWT_EXPIRATION` | `86400000` | 登录凭证的有效过期时间 (24小时) |
| `AI_API_KEY` | ******* | 讯飞星火大模型 AI 开放平台授权 Key |
| `AI_API_SECRET` | ******* | 讯飞星火大模型 AI 开放平台授权 Secret |
| `AI_API_URL` | `https://spark-api-open.xf-yun.com/v1/chat/completions` | 星火大模型微调/对话的指定端点 API 地址 |
| `AI_MODEL` | `lite` | 业务调用的 AI 模型规格版本 |
| `AI_TEMPLATE` | `你是使用“校园帮帮系统”的学生...` | 用于任务描述自动生成的系统级 Prompt 预设模板 |

## 4. 关键问题排查与踩坑记录

### 4.1 多模块构建脚本未找到 (Script start.sh not found)

* **问题现象**：首次绑定 GitHub 仓库直接发布时，Railway 提示无法判定构建类型，导致中断报错。
* **原因分析**：Railway 默认在仓库最外层根目录 `/` 检索配置文件，无法感知藏在子目录中的后端代码。
* **解决方案**：进入后端服务的 Settings 页面，将 **Root Directory** 显式声明修改为 `/backend/back`，使其精准定位到后端的 `pom.xml` 和 `Dockerfile`。

### 4.2 跨项目公网数据库链路中断 (Communications link failure)

* **问题现象**：将后端与数据库分散在独立的 Railway 项目中时，后端在启动阶段抛出连接握手超时异常。
* **原因分析**：不同项目间只能走外网域名进行公网互连。由于 Railway 免费层的安全防火墙及出入站端口拦截策略，导致 Spring Boot 的数据源驱动连接请求被阻断。
* **解决方案**：在同一个大项目画布中直接使用 `+ New` 新建 MySQL 节点。通过将两个服务放置在同一命名空间，利用内网 `${{MySQL.MYSQL_URL}}` 互连，彻底解决网络墙导致的 Crash 问题。

### 4.3 数据库 URL 协议头缺失 (URL must start with 'jdbc')

* **问题现象**：使用内网变量直连时，后端启动抛出 `Factory method 'dataSource' threw exception; URL must start with 'jdbc'`。
* **原因分析**：Railway 自动分配的 `MYSQL_URL` 以标准协议 `mysql://` 开头，而 Java HikariCP 数据源和 JDBC 驱动要求必须显式以 `jdbc:mysql://` 开头。
* **解决方案**：进入后端服务变量页面的 Raw Editor，将表达式订正为 `jdbc:${{MySQL.MYSQL_URL}}`。

---
**项目线上访问链接**：https://school-assistance-system-production.up.railway.app