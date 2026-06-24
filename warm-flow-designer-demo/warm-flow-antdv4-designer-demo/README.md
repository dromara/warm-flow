# warm-flow-antdv4-designer-demo

Warm-Flow 设计器（`@dromara/warm-flow-designer`）的 **Ant Design Vue 4** 消费示例。

## 当前状态：完全脱 Element Plus

- 应用外壳与设计器全部组件：**Ant Design Vue 4**，本 demo **完全不引入 Element Plus**（产物零 element-plus）。
- 通过 `setUiAdapter(antdvAdapter)` 把库的中性 `wf-*` 组件与命令式反馈翻译到 antd（**28 个组件全翻译**：button/input/switch/radio(-group)/select/option/input-number/checkbox(-group)/tree-select/tag/tooltip/col/row/divider/form/form-item/table/table-column/dialog/drawer/date-picker/time-picker/tree/pagination/header/icon；反馈走 message/notification/Modal/Spin + 自实现 clickOutside）。

EP 解耦进度（全部完成）：
- Phase 1 ✅：命令式 UI（消息/通知/弹框/loading/clickOutside）收口到 `uiAdapter`。
- Phase 2 ✅：库内 `el-*` 全量中性化为 `wf-*` 走 adapter。
- Phase 3 ✅：`antdvAdapter` 实现并提升为库子入口 `@dromara/warm-flow-designer/antdv`；`main.ts` `setUiAdapter(antdvAdapter)`。
- 主入口 bundle 级移除 EP ✅：库主入口 UI 库无关，EP/antd 均为可选子入口，主 bundle 零 element-plus。
- form-create ✅：旧的自定义表单设计器组件（孤儿、无引用）已移除，库不再依赖 `@form-create/*`，本 demo 也无需 `app.use(ElementPlus)`。

## 运行（pnpm workspace）

```bash
# 1) 库工程构建产物（在仓库根或库目录）
cd ../../warm-flow-vue-designer && pnpm build:lib
# 2) 启动本 demo
cd ../warm-flow-designer-demo/warm-flow-antdv4-designer-demo
pnpm dev      # http://localhost:5181
```

库通过 `"@dromara/warm-flow-designer": "workspace:*"` 本地消费其 `dist-lib`，改库后重新 `pnpm build:lib` 即生效。antd 适配器已正式化为库子入口 `@dromara/warm-flow-designer/antdv`。
