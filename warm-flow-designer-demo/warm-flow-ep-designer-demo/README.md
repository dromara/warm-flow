# warm-flow-ep-designer-demo

Warm-Flow 设计器（`@dromara/warm-flow-designer`）的 **Element Plus** 消费示例。

以干净的第三方工程（仅 `vue` + `element-plus` + `pinia` + 包本身）消费库的 `dist-lib`，演示「列表 / 新建 / 保存 / 修改 / 只读预览 / 导出 / 删除」完整闭环，数据用 `demoProvider`（localStorage 持久化）脱后端运行。

## 运行（pnpm workspace）

```bash
# 1) 先在库工程构建产物
cd ../../warm-flow-vue-designer && pnpm build:lib
# 2) 启动本 demo（依赖随仓库根 pnpm install 一次装齐）
cd ../warm-flow-designer-demo/warm-flow-ep-designer-demo
pnpm dev      # http://localhost:5180
```

库通过 `"@dromara/warm-flow-designer": "workspace:*"` 本地消费其 `dist-lib`，改库后重新 `pnpm build:lib` 即生效。
