# warm-flow-antdv4-designer-demo

Warm-Flow 设计器（`@dromara/warm-flow-designer`）的 **Ant Design Vue 4** 消费示例。

## 当前状态：设计器主体已 antd 原生

- 应用外壳：**Ant Design Vue 4**。
- 设计器内部 UI：通过 `setUiAdapter(antdvAdapter)` 切换为 **Ant Design Vue 4 原生**——`antdvAdapter.ts` 把库的中性 `wf-*` 组件与命令式反馈翻译到 antd（**28 个组件已翻译**：button/input/switch/radio(-group)/select/option/input-number/checkbox(-group)/tree-select/tag/tooltip/col/row/divider/form/form-item/table/table-column/dialog/drawer/date-picker/time-picker/tree/pagination/header/icon；反馈走 message/notification/Modal/Spin + 自实现 clickOutside）。

EP 解耦进度：
- Phase 1 ✅：命令式 UI（消息/通知/弹框/loading/clickOutside）收口到 `uiAdapter`。
- Phase 2 ✅：库内 `el-*` 全量中性化为 `wf-*` 走 adapter。
- Phase 3 ✅（本 demo）：`antdvAdapter` 实现 + `main.ts` `setUiAdapter(antdvAdapter)`，设计器主体 antd 原生。

仍保留 Element Plus 的两处（按既定决策）：
- **form-create 自定义表单设计器**仍用 `@form-create/element-ui`（Q2 决定，暂留 EP），故 `main.ts` 仍 `app.use(ElementPlus)`。
- 库 `dist-lib` 仍**静态内置 EP 默认适配器**；要让消费方完全不打包 EP，需库侧把 EP 适配器拆为独立子入口（bundle 级解耦，后续）。

## 运行（pnpm workspace）

```bash
# 1) 库工程构建产物（在仓库根或库目录）
cd ../../warm-flow-vue-designer && pnpm build:lib
# 2) 启动本 demo
cd ../warm-flow-designer-demo/warm-flow-antdv4-designer-demo
pnpm dev      # http://localhost:5181
```

库通过 `"@dromara/warm-flow-designer": "workspace:*"` 本地消费其 `dist-lib`，改库后重新 `pnpm build:lib` 即生效。`antdvAdapter.ts` 验证稳定后可提升为库子入口 `@dromara/warm-flow-designer/antdv`。
