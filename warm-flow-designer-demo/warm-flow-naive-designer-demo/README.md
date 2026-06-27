# warm-flow-naive-designer-demo

Warm-Flow 设计器（`@dromara/warm-flow-designer`）的 **Naive UI** 消费示例。

以干净的第三方工程（仅 `vue` + `naive-ui` + `pinia` + 包本身）消费库的 `dist-lib`，演示「列表 / 新建 / 保存 / 修改 / 只读预览 / 导出 / 删除」完整闭环，外加「集成案例」「扩展能力验证」两个进阶入口；数据用 `demoProvider`（localStorage 持久化）脱后端运行。

## 当前状态：Naive UI 单栈（零 Element Plus / antd）

- 应用外壳与设计器全部组件：**Naive UI**，本 demo 完全不引入 Element Plus / Ant Design Vue（产物零 EP / antd）。
- 通过 `setUiAdapter(naiveAdapter)` 把库的中性 `wf-*` 组件与命令式反馈翻译到 Naive（**28 个组件全翻译**；`message` / `dialog` / `notification` 用 `createDiscreteApi` 取脱上下文实例，loading / clickOutside 自实现）。
- 根部用 `n-config-provider`（`zhCN` 语言包）本地化 select / input / date / pagination 等默认占位与文案。

## 运行（pnpm workspace）

```bash
# 1) 库工程构建产物（在仓库根或库目录）
cd ../../warm-flow-vue-designer && pnpm build:lib
# 2) 启动本 demo
cd ../warm-flow-designer-demo/warm-flow-naive-designer-demo
pnpm dev      # http://localhost:5182
```

库通过 `"@dromara/warm-flow-designer": "workspace:*"` 本地消费其 `dist-lib`，改库后重新 `pnpm build:lib` 即生效。Naive 适配器为库子入口 `@dromara/warm-flow-designer/naive`。
