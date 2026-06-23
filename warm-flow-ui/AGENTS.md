# AGENTS.md — warm-flow-ui 模块规则

> 本文件只写 `warm-flow-ui` 的差异化规则。通用协作原则（先思考、最小改动、外科手术式、用证据验证、品牌保护、安全边界）以仓库根 [`../AGENTS.md`](../AGENTS.md) 为准。
>
> 注意：本模块是**前端工程，不属于 Maven 反应堆**，不受根 `AGENTS.md` 中 Java / JDK / ORM / SQL 规则约束；后端编译命令与本模块无关。

## 模块职责

Warm-Flow 流程设计器前端（经典 + 仿钉钉双模式），构建产物供 `warm-flow-plugin-ui` 集成为 jar 内置设计器。

技术栈（以 `package.json` 为准）：

- Vue 3.3.9 + Vite 5 + `<script setup>`；Element Plus 2.4.3 + `@element-plus/icons-vue`。
- 流程图：`@logicflow/core` / `@logicflow/extension`。
- 表单设计：`@form-create/designer` / `@form-create/element-ui`。
- 状态管理 Pinia；路由 vue-router；请求 axios；样式 Sass；`file-saver` 导出。
- 包管理 **yarn 1.x（classic）**；`type: module`；**前端许可证为 MIT**（与后端 Apache 2.0 不同，勿混改 header）。

## 常用命令

```bash
yarn --registry=https://registry.npmmirror.com   # 安装依赖
yarn dev                                          # 本地开发（vite）
yarn build:prod                                   # 生产构建（vite build）
yarn preview                                      # 预览构建产物
```

## 改动前必读

- 与后端契约：设计器读写的流程定义 JSON、节点属性、办理人 / 条件 / 监听器结构需与 `warm-flow-plugin-ui` 后端 `dto` / `vo` 及 core 实体对齐，改字段先看后端契约。
- 现有组件、路由、Pinia store、axios 封装与公共样式，复用既有模式，不要在局部页面另起一套风格。

## 高风险点

- **双模式一致**：经典模式与仿钉钉模式都要兼顾，节点状态颜色等沿用后端 `chartStatusColor*` 配置语义。
- **前后端契约**：流程定义 JSON 结构是与后端共享的契约，改动要前后端同步，避免设计器导出的 json 后端解析不了。
- **LogicFlow / form-create 版本**：图引擎与表单设计器升级可能破坏既有流程图 / 表单，升级按高风险处理并实际验证。
- **依赖版本**：锁定版本不随意升级；新增依赖先评估必要性与体积。

## 验证

- 改动后至少 `yarn dev` 跑通本地、`yarn build:prod` 能成功构建；涉及流程图 / 表单交互时手动验证关键路径。
- 无法验证时如实报告命令、现象与风险，不要声称构建通过。
