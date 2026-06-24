# warm-flow-designer-demo

`@dromara/warm-flow-designer`（位于同级 `../warm-flow-vue-designer`）的消费示例集合。各 demo 以「第三方业务工程」姿势，通过本地 `link:../../warm-flow-vue-designer` 消费库的 `dist-lib` 产物。

| Demo | UI 库 | 端口 | 说明 |
|---|---|---|---|
| [`warm-flow-ep-designer-demo`](./warm-flow-ep-designer-demo) | Element Plus | 5180 | 完整消费示例：列表 / 新建 / 保存 / 修改 / 预览闭环 + localStorage 持久化 |
| [`warm-flow-antdv4-designer-demo`](./warm-flow-antdv4-designer-demo) | Ant Design Vue 4 | 5181 | 过渡态：antd 外壳 + 设计器（内部暂由 EP 驱动），Phase 3 接入 antdv 适配后切换为 antdv 单栈 |

## 运行前置

先在库工程生成产物：

```bash
cd ../warm-flow-vue-designer && yarn build:lib
```

再进入任一 demo `yarn install && yarn dev`。改库后重新 `yarn build:lib`，demo（link 消费）即生效。
