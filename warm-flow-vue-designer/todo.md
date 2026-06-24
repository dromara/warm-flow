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

- [ ] `FlowDesigner.vue` 改 `<script setup lang="ts">` 获得精确 props 类型。
- [ ] 渐进将设计器子组件（属性面板等）TS 化，提升类型精度。

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

> 详细决策与验证记录见 `.codex/warm-flow-ui-npm-packaging.md`。
