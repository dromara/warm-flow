# warm-flow-designer-demo

`@dromara/warm-flow-designer`（位于同级 `../warm-flow-vue-designer`）的消费示例集合。各 demo 以「第三方业务工程」姿势，通过 pnpm workspace（`"@dromara/warm-flow-designer": "workspace:*"`）本地消费库的 `dist-lib` 产物。

三个 demo 的内容 / 文案 / 用法保持一致，仅 UI 库不同（用于验证「主入口 UI 库无关、适配器可插拔」）：均含「3 步集成」用法说明、列表 CRUD 闭环（新建 / 保存 / 修改 / 只读预览 / 导出 / 删除）、「集成案例」（`useFlowJson` 实时 JSON + 事件日志 + `useFlowDesigner` 命令式工具条）与「扩展能力验证」（`initialJson` / `customNodes` / 各扩展点 / 事件 / 插槽）三类入口。

| Demo | UI 库 | 端口 | 适配器子入口 |
|---|---|---|---|
| [`warm-flow-ep-designer-demo`](./warm-flow-ep-designer-demo) | Element Plus | 5180 | `@dromara/warm-flow-designer/element-plus` |
| [`warm-flow-antdv4-designer-demo`](./warm-flow-antdv4-designer-demo) | Ant Design Vue 4 | 5181 | `@dromara/warm-flow-designer/antdv` |
| [`warm-flow-naive-designer-demo`](./warm-flow-naive-designer-demo) | Naive UI | 5182 | `@dromara/warm-flow-designer/naive` |

> 三者均为 UI 单栈：各自只引一个 UI 库、零交叉依赖（如 antdv / naive demo 完全不引 Element Plus）。

## 运行（pnpm workspace）

```bash
# 1) 先在库工程构建产物
cd ../warm-flow-vue-designer && pnpm build:lib
# 2) 进入任一 demo 启动（端口见上表）
cd ../warm-flow-designer-demo/warm-flow-ep-designer-demo && pnpm dev
```

也可在仓库根 `pnpm install` 一次装齐库 + 三个 demo。改库后重新 `pnpm build:lib`，demo（workspace 消费 `dist-lib`）即生效。
