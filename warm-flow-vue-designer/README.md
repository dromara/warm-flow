<p align="center">
	<img alt="logo" src="https://foruda.gitee.com/images/1726820610127990120/c8c5f3a4_2218307.png" width="100">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Warm-Flow工作流 v1.2.7</h1>
<p align="center">
	<a href="https://gitee.com/dromara/warm-flow/stargazers"><img src="https://gitee.com/dromara/warm-flow/badge/star.svg?theme=dark"></a>
</p>

## 介绍

Warm-Flow国产工作流引擎🎉，其特点简洁轻量，五脏俱全，灵活扩展性强，是一个可通过jar引入设计器的工作流。

1. 简洁易用：只有7张表，代码量少，可快速上手和集成
2. 审批功能：支持通过、退回、任意跳转、转办、终止、会签、票签、委派和加减签、互斥和并行网关
3. 监听器与流程变量：支持五种监听器，可应对不同场景，灵活可扩展，参数传递，动态权限
4. 流程图：流程引擎自带流程图，可在不集成流程设计器情况下使用
5. 条件表达式：内置常见的和spel条件表达式，并且支持自定义扩展
6. 办理人表达式：内置${handler}和spel格式的表达式，可满足不同场景，灵活可扩展
7. orm框架扩展：目前支持MyBatis、Mybatis-Plus、Mybatis-Flex和Jpa，后续会由社区提供其他支持，扩展方便
8. 数据库支持：目前支持MySQL 、Oracle 和PostgreSQL，后续会继续支持其他数据库或者国产数据库
9. 多租户与软删除：流程引擎自身维护多租户和软删除实现，也可使用对应orm框架的实现方式
10. 支持角色、部门和用户等权限配置
11. 同时支持spring和solon
12. 兼容java8和java17,理论11也可以
13. 官方提供基于ruoyi-vue封装实战项目，很实用

```shell
希望一键三连，你的⭐️ Star ⭐️是我持续开发的动力，项目也活的更长
```
## 前端运行

```bash

# 安装依赖
yarn --registry=https://registry.npmmirror.com

# 启动服务
yarn dev

# 构建测试环境 yarn build:stage
# 构建生产环境 yarn build:prod
# 前端访问地址 http://localhost:80
```

## 作为 npm 组件库使用（实验性）

除作为 jar 内置设计器（iframe 集成）外，设计器也可作为 Vue3 组件库被业务方直接 `import` 使用，实现数据层与具体后端解耦。

### 构建库产物

```bash
yarn build:lib   # 输出到 dist-lib/（ESM + 类型声明 .d.ts + 合并样式）
```

### 安装与初始化

```ts
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import { WarmFlowDesigner, setDataProvider, createMockProvider } from '@dromara/warm-flow-designer'
import '@dromara/warm-flow-designer/style'

const app = createApp(App)
app.use(ElementPlus)
app.use(WarmFlowDesigner)   // 注册全局 svg-icon 组件 + Element Plus 图标（图标零配置）
app.mount('#app')

// 可选：注入自定义数据源 / mock（脱后端）
setDataProvider(createMockProvider())
```

### 组件用法

```vue
<template>
  <FlowDesigner :definition-id="defId" :only-design-show="true" :disabled="false" @close="onClose" />
</template>
<script setup>
import { FlowDesigner } from '@dromara/warm-flow-designer'
</script>
```

#### FlowDesigner Props / Events

| 名称 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| definitionId | String | null | 流程定义 id，传入则加载该定义，否则新建 |
| disabled | Boolean | false | 只读模式（隐藏保存等编辑操作） |
| onlyDesignShow | Boolean | false | 仅显示画布（跳过基础信息步骤，直达流程设计） |
| showGrid | Boolean | false | 画布显示网格 |
| @close | event | - | 宿主关闭回调（替代 iframe postMessage） |

### 数据层（与后端解耦）

所有后端交互经由「数据源」(DataProvider)，默认内置 axios；可注入自定义实现，仅覆盖关心的方法，其余自动回退：

```ts
import { setDataProvider } from '@dromara/warm-flow-designer'

setDataProvider({
  queryDef: (id) => myHttp.get('/flow/def/' + id),
  saveJson: (data) => myHttp.post('/flow/save', data)
})
```

也可用 `createMockProvider()` 脱后端运行，或运行期 URL 加 `?mock=true`。

### 依赖要求（peerDependencies）

宿主需提供：`vue`、`vue-router`、`pinia`、`@logicflow/core`、`@logicflow/extension`（必选）；UI 库二选一——`element-plus`（配子入口 `@dromara/warm-flow-designer/element-plus`）或 `ant-design-vue@4`（配子入口 `@dromara/warm-flow-designer/antdv`），均为可选 peer，按所选适配器 `setUiAdapter(...)`。图标已内置（iconify 离线集 `ep` + `wf`，随库打包，无需额外依赖）。

### 本地体验 playground

```bash
yarn build:lib        # 先产出 dist-lib
yarn dev:playground   # 启动最小消费示例（端口 5180，以包名 import dist-lib 产物）
```

> 包名 `@dromara/warm-flow-designer` 与正式发布仍在确认中，当前以本地 `dist-lib` 产物体验为主。
