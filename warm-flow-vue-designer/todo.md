# warm-flow-ui 组件库化 · 待决策 / 未来事项

> 高风险 / 需拍板的事项，暂缓，未来再考虑。
> 当前优先：npm 包（@dromara/warm-flow-designer）开发测试调试 + FlowDesigner 组件 + mock 完善。

## 待拍板（未来再议）

- [ ] **npm publish**：发布 `@dromara/warm-flow-designer` 到 npm（需 `@dromara` 组织凭证 / 权限）。
- [ ] **CI**：前端库构建 / 发布纳入 CI 流水线（当前为手动 `npm publish`）。
- [ ] **物理 monorepo**：是否拆 `packages/*`（设计器库 + playground + plugin-ui 产物）。
      默认不拆——单仓双构建（`build:prod`→`dist/` 打 jar，`build:lib`→`dist-lib/` 出 npm）已满足需求。
- [ ] **剔除 Element Plus 硬依赖**：消费方现被迫 `app.use(ElementPlus)` + 引 EP 样式（peerDep）。
      方向已记录（抽象可插拔 UI 适配，默认支持 element-plus + antdv），耦合面调研 + 分阶段路线已出（见 `.codex` 第 9 节）；
      实施前仍需就 5 个取舍点拍板（antdv 版本 / form-create 范围 / 样式对齐 / 节奏 B 或 C / 过渡期容忍，见 `.codex` 9.4），尚未开始大规模实施。

## 可选优化（未来）

- [x] `FlowDesigner.vue` 改 `<script setup lang="ts">` 获得精确 props 类型。（已完成）
- [x] 渐进将设计器子组件（属性面板等）TS 化，提升类型精度。（已完成，见下）

## 进行中 / 已完成

- [x] 库构建脚手架 `vite.lib.config.js` → `dist-lib/`（ESM + dts + 合并样式）。
- [x] 包名 `@dromara/warm-flow-designer` + 发布元信息（publishConfig/types/peerDeps）。
- [x] svg 图标自带（`WarmFlowDesigner` 插件 install 注册 svg-icon + EP 图标 + sprite 注入）。
- [x] 对外发布层全 TS 化（`src/designer` + `src/data` + `src/api`）。
- [x] README「组件库使用」章节 + `playground/` 最小消费示例（端到端验证 import 即用）。
- [x] **mock 完善**：`src/data/mockProvider.ts` 已按后端 vo 充实办理人 / 监听器 / 表单 / 配置等数据（2b-5），多轮 playwright 验证脱后端 0 报错。
- [x] **保存 / 修改 / 预览 demo 闭环**：`playground` 完整 demo（列表 / 新建 / 保存 / 修改 / 预览 / 导出 / 删除）+ `demoProvider` localStorage 持久化（2f），playwright 三态闭环 0 error。
- [x] **库侧零配置加法**：`FlowDesigner` 加 `emit('saved')` 回传 definitionId + `$modal` 调用降级（脱 ruoyi 不崩），向后兼容（2f）。
- [x] **预览只读 BUG 修复**：预览态（`disabled+onlyDesignShow`）画布彻底只读——经典 `isSilentMode`、仿钉钉编辑图标 / ✕删除 / 边「+」加号隐藏（`canEdit` + `skip.ts` getPlusElements）、4 个编辑事件 `disabled` 拦截（2g）。playwright 预览态三控件全消失、console 0 报错；`build:lib` exit 0（es.js 304.63KB）。

- [x] **设计器 TS 化**：`FlowDesigner.vue` 及设计器全部 12 个 `.vue` 子组件（属性面板 `baseInfo`/`propertySetting`/`start`/`between`/`end`/`gateway`/`skip`/`nodeExtList`/`selectUser` + `DiagramSidebar`/`EdgeTooltip`/`baseNode`）改 `<script setup lang="ts">`：props 用 `defineProps<泛型>() + withDefaults` 精确化，`defineEmits`/`ref`/事件参数 TS 化，`getCurrentInstance()!` 收敛；新增 `src/types/global.d.ts` 声明 window 运行期标志（`__WF_*` / 抽屉桥接 / tooltip hover）。`build:lib` 三产物 exit 0（es.js 307.97KB），ReadLints 0 报错。

- [x] **组件库通用化（slot / hook / 命令式 API / 子组件导出）**：让 `FlowDesigner` 从「黑盒页面」升级为可编排组件库。
  - **slot 扩展点**（全部带回退，不传则行为不变）：`header-left`（流程名区，透出 flowName）、`header-actions`（保存按钮区，透出 save/disabled）、`toolbar-extra`（工具栏追加自定义按钮，透出 lf/disabled）、`logo`（水印）。
  - **命令式 API**（`defineExpose`）：`save / validate / getGraphData / getFlowJson / getFlowName / getLogicFlow / zoom / zoomIn / zoomOut / fitView / resetZoom / undo / redo / clear / downloadImage / downloadJson`，宿主无需依赖内部按钮即可程序化操控。
  - **`ready` 事件**：画布初始化完成透出底层 LogicFlow 实例，便于高级定制（自定义事件 / 主题 / 扩展）。
  - **组合式 hook**：新增 `src/composables/useFlowDesigner.ts`（`{ designerRef, isReady, save, ... }`，空安全包装）；并对外导出既有 `useDark`。
  - **可组合子组件导出**：`BaseInfo` / `PropertySetting` / `DiagramSidebar`（高级用法，需先 `app.use` + `setUiAdapter`）。
  - **公共类型集中**：新增 `src/designer/types.ts`（`FlowDesignerProps` / `FlowDesignerInstance` / `FlowDesignerSavedPayload` / `FlowDesignerReadyPayload`），入口统一 re-export；`vite.lib.config.js` dts include 增补 `src/composables/**` 使 hook/类型声明随包产出。
  - `build:lib` 三产物 exit 0（es.js 311.5KB），ReadLints 0 报错，完全向后兼容（纯增量，未改默认行为）。

## 已知问题 / 待修

- [x] **发布类型在消费方退化为 any（既有缺陷，影响所有导出类型）——已修复**。
  - **根因（两个叠加问题）**：① `vite-plugin-dts@5.0.2` 实为 `unplugin-dts@1.0.2` 转发，对**`@/` 别名派生**的相对 import 计算偏移一级——产物落在 `dist-lib/src/**`，却把别名目标按 entryRoot=src 锚定（少一层 `src`），生成 `../../data` 越出到 `dist-lib/data`（不存在）→ `tsc` 解析失败、`FlowDesignerInstance`/`DataProvider`/`ComponentSize`/`UiAdapter` 等**全部导出类型在消费方退化为 any**（源码原生 `./xxx` 相对导入不受影响）。② 主入口 UI 无关，不引用 `ui/elementPlusAdapter`、`ui/antdvAdapter` 等，**传递依赖 dts 漏产**（import 悬空，同样退化为 any）。
  - **排查中排除的歧路**：`rollupTypes` 该版本不存在（被静默忽略）；`bundleTypes:true`（api-extractor 打平）入口识别错误，产物只剩 `export {}`+global；`entryRoot` 行为异常（试出 `dist-lib/ui/src/ui/**` 双层嵌套，与上一轮记录一致）。故均不可用。
  - **最终修法（确定性、不加依赖、不改发布元信息）**：(1) `vite.lib.config.js` 把 dts `include` 放宽到整个 `src` 并作为**唯一 dts 产出点**；(2) `tsconfig.json` `include` 补 `src/ui`、`src/composables`，使两个适配器与 composables 进入程序图被产出；(3) `beforeWriteFile` 钩子 `fixAliasDtsImports`：**只重锚定「解析后越出 `dist-lib/src`」的坏 specifier** 回 `dist-lib/src/**`，正确导入原样保留；(4) `vite.ep.config.js` / `vite.antdv.config.js` 移除各自 dts 插件（统一由主构建产出，规避 `entryRoot` 嵌套错乱）。`package.json` 四处 types 路径维持 `dist-lib/src/**` 不变。
  - **验证**：`build:lib` 三产物 exit 0；全量扫描 48 个 `.d.ts` / 25 处相对 import **0 处未解析**；临时 tsc 消费用例对主入口 + `/element-plus` + `/antdv` 五个探针由「退化为 any（TS2578）」转为**全部正确解析（exit 0）**；EP / antdv 两个 demo `vite build` 均 exit 0。

## 组件库扩展性 Roadmap（插拔 / slot / hook）

> 2026-06 盘点：FlowDesigner 已具备基础扩展面，但作为通用组件库仍有缺口。按价值排优先级，逐项落地。

### 已具备 ✓
- **插拔**：`setUiAdapter`（EP/antdv 内置）、`setDataProvider`（部分覆盖合并）、`setComponentSize`、`setCustomThemeColors` / `useDark`。
- **slot**：`header-left` / `header-actions` / `toolbar-extra` / `logo`。
- **hook**：`useFlowDesigner`（命令式 API）、`useDark`（主题）。
- **事件**：`close` / `saved` / `ready`。

### 实施中 / 计划
- [x] **① 属性面板插槽透传（断链修复 + 统一扩展点）**：`FlowDesigner` 渲染 `PropertySetting` 时未透传插槽，导致 `propertySetting.vue` 既有的 `v-slot` 转发链对外不可用。
      已修：FlowDesigner 透传全部插槽 → PropertySetting → 节点属性子组件；并为 5 个节点子组件（start/between/gateway/end/skip）统一新增 `#node-form-extra` 扩展插槽（透出 `{ form, disabled }`），消费方可往任意节点属性抽屉注入自定义表单项；`start.vue` 既有 `form-item-task-*` 插槽保留并随之可用。
- [ ] **② initialJson / v-model 脱后端驱动**：`onMounted` 恒走 `queryDef(definitionId)`，消费方无法直接用一段流程 JSON 渲染/编辑。计划加 `props.initialJson`（或 `v-model:json`），有值时优先于 `queryDef`，实现脱后端纯组件用法。
- [ ] **③ 自定义节点 / LogicFlow 扩展注册透传**：`register()`（节点）与 `use()`（扩展）写死。计划开放 `props.customNodes` / `props.extraExtensions` 或 `onRegister(lf)` / `onBeforeUse(LogicFlow)` 钩子，并支持 `props.lfOptions` 透传合并 LogicFlow 初始化选项。

### 待排期（按需）
- **事件补全**：`change` / `dirty`（未保存离开拦截）、`before-save`（可改写 payload）、`validate-error`。
- **数据 hook**：`useFlowJson()`（响应式读写流程 JSON）、变更订阅 `onChange` / `onNodeClick`。
- **拖拽侧边栏可配置**：`DiagramSidebar` 的 `flowNodes` / `gatewayNodes` 写死，开放 `props.paletteNodes` 或 `#sidebar` 插槽。
- **空 / 加载态插槽**、**步骤区 `header-center` 自定义**。
- **第三方 UI 库适配器**：补 Naive / Arco / TDesign 示例（接口已具备）。
- **i18n**：UI 文案目前写死中文，开放语言包。
- **表单设计步骤**（当前注释）、**流程结构校验钩子**（必须有开始/结束、网关成对等）。
- **文档**：自定义 `UiAdapter` / `DataProvider` 端到端示例与最佳实践。

> 详细决策与验证记录见 `.codex/warm-flow-ui-npm-packaging.md`。
