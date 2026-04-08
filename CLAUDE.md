# 项目规则

## 技术栈
- **前端 / 客户端**：Android (Kotlin + XML)  
  - UI 框架：Jetpack Fragment + RecyclerView + Adapter  
  - 设计模式：MVVM（可选）  
  - 第三方库：Material Components、Glide/Coil、Coroutines  
- **后端**：Spring Boot (Java / Kotlin)  
  - 数据库：MySQL  
  - 接口：REST API，JSON 交互  
  - ORM：Spring Data JPA  
- **部署 / 运行环境**：本地开发 + Docker 可选

---

## 目录结构

android-app/
├─ src/main/java/com/example/campustask/
│ ├─ adapter/ # RecyclerView Adapter
│ ├─ fragment/ # 各页面 Fragment
# 项目规则

本文档列出本仓库的技术栈、目录结构、代码规范与 AI 辅助开发注意事项，便于团队协作和代码一致性。

## 技术栈

- 前端 / 客户端：Android (Kotlin + XML)
  - UI 框架：Jetpack Fragment + RecyclerView + Adapter
  - 设计模式：MVVM（可选）
  - 第三方库：Material Components、Glide / Coil、Kotlin Coroutines

# 项目规则

本文档列出本仓库的技术栈、目录结构、代码规范与 AI 辅助开发注意事项，便于团队协作和代码一致性。

## 技术栈

- 前端 / 客户端：Android (Kotlin + XML)
  - UI 框架：Jetpack Fragment + RecyclerView + Adapter
  - 设计模式：MVVM（可选）
  - 第三方库：Material Components、Glide / Coil、Kotlin Coroutines

- 后端：Spring Boot (Java / Kotlin)
  - 数据库：MySQL
  - 接口：REST API（JSON 交互）
  - ORM：Spring Data JPA

- 部署 / 运行环境：本地开发；支持 Docker 容器化部署（可选）

---

## 目录结构

以下为建议的项目目录示例，供参考：

```
android-app/
├─ src/main/java/com/example/campustask/
│  ├─ adapter/        # RecyclerView Adapter
│  ├─ fragment/       # 各页面 Fragment
│  ├─ model/          # 数据类（Task、User 等）
│  ├─ network/        # API 调用类（Retrofit 等）
│  ├─ ui/             # Activity 及 UI 相关
│  └─ utils/          # 工具类（时间、格式化、Toast 等）
├─ res/
│  ├─ layout/         # XML 布局文件
│  ├─ drawable/       # 图片、Shape、Gradient 等
│  ├─ values/         # colors.xml、strings.xml、dimens.xml
│  └─ menu/           # BottomNavigationView 菜单

spring-boot-backend/
├─ src/main/java/com/example/backend/
│  ├─ controller/     # REST 控制器
│  ├─ service/        # 业务逻辑
│  ├─ repository/     # 数据库操作
│  └─ model/          # 实体类
└─ src/main/resources/
   ├─ application.yml # 配置文件
   └─ db/             # 初始化 SQL
```

---

## 代码规范

### Android 端

- 使用 Fragment + RecyclerView + Adapter 组合实现列表与页面切换。
- 所有 Adapter 支持复用，避免重复代码。
- 数据类使用 Kotlin `data class`。
- UI 布局 XML 遵循 LinearLayout / ConstraintLayout 分层规范。
- Tab / 状态切换逻辑放在 Fragment 内部处理。
- 避免直接调用 `findViewById`，优先使用 ViewBinding。
- 异步操作使用 Kotlin Coroutines。

### 后端

- Controller 层只负责请求转发。
- Service 层处理业务逻辑。
- Repository 层负责数据库操作。
- 数据交互统一使用 JSON 格式。
- 命名统一采用驼峰命名法（camelCase）。

---

## 禁止事项

- 不要在 Adapter 或 Fragment 内写重复的 UI / 逻辑代码。
- 不要在 Fragment 中直接操作数据库（必须通过 Repository / Service）。
- 不要在 XML 中写 inline style，全部使用资源文件（colors / dimens / styles）。
- 不要使用 Kotlin `!!` 强制解包，避免 NPE。
- 不要在 Activity 中处理业务逻辑，保持 Activity 仅做展示。

---

## AI 辅助开发注意事项

- 生成新 Fragment / Adapter 时，请遵循项目目录结构。
- 数据类、RecyclerView Adapter、布局 XML 等应优先复用，避免重复生成。
- Tab / 分类 / 状态逻辑可封装为函数或组件并复用。
- 生成的 UI 代码应与现有 Material 风格一致。
- 生成的 XML 与 Kotlin 代码应支持 ViewBinding，确保安全访问控件。
