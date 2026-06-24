# warm-flow-antdv4-designer-demo

Warm-Flow 设计器（`@dromara/warm-flow-designer`）的 **Ant Design Vue 4** 消费示例。

## 当前状态：过渡态（先骨架，后功能）

- 应用外壳：**Ant Design Vue 4**（`a-layout` / `a-alert` / `a-tag` 等）。
- 设计器内部 UI：**当前仍由 Element Plus 驱动**（库的默认 UI 适配器）。因此本 demo 现阶段同时引入了 `ant-design-vue` 与 `element-plus`。

这是 Element Plus 解耦工作进行中的预期状态：
- Phase 1（已完成）：命令式 UI（消息 / 通知 / 弹框 / loading / clickOutside）收口到 `uiAdapter`。
- Phase 2（进行中）：`el-*` 组件中性化为 `Wf*` 走 adapter。
- **Phase 3（待做）**：实现 `antdvAdapter`（同 `UiAdapter` 契约 + 组件映射），本 demo `main.ts` 改为：

```ts
import { setUiAdapter } from '@dromara/warm-flow-designer'
import { antdvAdapter } from './antdvAdapter' // Phase 3 产出
setUiAdapter(antdvAdapter)
// 并移除 element-plus 的引入
```

完成后设计器内部即为 antdv 原生，可移除 `element-plus` 依赖，实现真正的「antdv4 单栈」。

## 运行

```bash
# 1) 先在库工程构建产物
cd ../../warm-flow-vue-designer && yarn build:lib
# 2) 安装并启动本 demo
cd ../warm-flow-designer-demo/warm-flow-antdv4-designer-demo
yarn install
yarn dev      # http://localhost:5181
```

库通过 `"@dromara/warm-flow-designer": "link:../../warm-flow-vue-designer"` 本地消费其 `dist-lib`，改库后重新 `yarn build:lib` 即生效。
