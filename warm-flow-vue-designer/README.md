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

> 主入口 **UI 库无关**，不内置任何 UI 组件库。渲染 `FlowDesigner` 前必须先 `setUiAdapter(...)` 选择一个 UI 适配器（element-plus 或 antdv），否则设计器内的中性组件 `wf-*` 无法映射到具体 UI 库、画布不渲染。

```ts
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import { WarmFlowDesigner, setUiAdapter, setDataProvider, createMockProvider } from '@dromara/warm-flow-designer'
// UI 适配器从子入口按需引入；antdv 用 `@dromara/warm-flow-designer/antdv` 的 `antdvAdapter`
import { elementPlusAdapter } from '@dromara/warm-flow-designer/element-plus'
import '@dromara/warm-flow-designer/style'

const app = createApp(App)

// ① 选择 UI 适配器（必须在渲染 FlowDesigner 之前调用）
setUiAdapter(elementPlusAdapter)
// ② 注册插件：全局 svg-icon 组件 + 中性组件 wf-*（图标零配置）
app.use(ElementPlus)
app.use(WarmFlowDesigner)
app.mount('#app')

// ③ 可选：注入自定义数据源 / mock（脱后端）
setDataProvider(createMockProvider())
```

### 组件用法

```vue
<template>
  <!-- 后端驱动：传 definitionId，组件自行 queryDef 加载 -->
  <FlowDesigner :definition-id="defId" :only-design-show="true" :disabled="false" @close="onClose" @saved="onSaved" />

  <!-- 脱后端驱动：直接喂一段流程 JSON（initialJson 优先于 definitionId / queryDef） -->
  <FlowDesigner :initial-json="flowJson" :only-design-show="true" @saved="onSaved" />
</template>
<script setup>
import { FlowDesigner } from '@dromara/warm-flow-designer'
</script>
```

#### FlowDesigner Props

| 名称 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| definitionId | String | null | 流程定义 id，传入则 `queryDef` 加载该定义，否则新建 |
| initialJson | String \| Object | null | 初始流程 JSON（warm-flow 定义对象或其字符串）。**有值时优先于 `definitionId`/`queryDef`**，组件不再请求后端，直接渲染 / 编辑，实现纯组件用法 |
| disabled | Boolean | false | 只读模式（隐藏保存等编辑操作） |
| onlyDesignShow | Boolean | false | 仅显示画布（跳过基础信息步骤，直达流程设计） |
| showGrid | Boolean | false | 画布显示网格 |
| customNodes | Array | [] | 追加自定义 LogicFlow 节点（`lf.register`），在内置节点之后注册，可新增节点类型或覆盖内置同名 type |
| extraExtensions | Array | [] | 追加自定义 LogicFlow 扩展（`LogicFlow.use`），如 MiniMap / Control / Group |
| lfOptions | Object | {} | 透传合并到 LogicFlow 初始化选项（顶层覆盖内置 grid / keyboard / 交互开关等；`container` 由组件管理，请勿传入） |
| onBeforeUse | Function | - | 命令式扩展钩子：在 `extraExtensions` 之后、`new LogicFlow()` 之前调用，透出 LogicFlow 类，可注册**带配置**的扩展 `LF.use(Ext, { ...options })` |
| onRegister | Function | - | 命令式节点钩子：在 `customNodes` 之后、`render` 之前调用，透出 lf 实例，可批量 / 条件注册节点、注册自定义边或做渲染前设置 |

#### FlowDesigner Events

| 名称 | 回传 | 说明 |
| --- | --- | --- |
| @close | - | 宿主关闭回调（替代 iframe postMessage）；保存成功后也会自动触发 |
| @saved | `{ id, data, json }` | 保存成功：当前定义 id、后端返回 data（如新建后的 definitionId）、本次提交的流程 json |
| @ready | `{ lf }` | 画布初始化完成，透出底层 LogicFlow 实例，便于高级定制 |

#### 插槽（slots，均带回退，不传则行为不变）

| 名称 | 透出 | 说明 |
| --- | --- | --- |
| header-left | `{ flowName }` | 流程名区自定义 |
| header-actions | `{ save, disabled }` | 保存按钮区追加 |
| toolbar-extra | `{ lf, disabled }` | 工具栏追加自定义按钮 |
| logo | - | 画布水印 |
| node-form-extra | `{ form, disabled }` | 节点属性抽屉扩展点，可向任意节点注入自定义表单项 |

#### 命令式 API

通过组合式 `useFlowDesigner()`（推荐，带空安全包装）或模板 `ref` 获取实例，程序化操控设计器：

```ts
import { useFlowDesigner } from '@dromara/warm-flow-designer'

const { designerRef, isReady, save, getFlowJson, getLogicFlow, zoom, undo, redo, clear } = useFlowDesigner()
// 模板：<FlowDesigner ref="designerRef" ... />
```

可用方法：`save / validate / getGraphData / getFlowJson / getFlowName / getLogicFlow / zoom / zoomIn / zoomOut / fitView / resetZoom / undo / redo / clear / downloadImage / downloadJson`。

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
