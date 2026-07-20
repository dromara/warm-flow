# UI Issue 修复与回归报告（IJVBTJ / IJV7GC）

> 日期：2026-07-20 ｜ 分支：`feature-ui-npm` ｜ 涉及工程：`warm-flow-ui`（webjar 基线）、`warm-flow-vue-designer`（npm 组件库）、`warm-flow-designer-demo/*`（消费示例）

## 一、两个 issue 的结论速览

| Issue | 内容 | warm-flow-ui | warm-flow-vue-designer | designer-demo（3 个） | 处理 |
| --- | --- | --- | --- | --- | --- |
| [IJVBTJ](https://gitee.com/dromara/warm-flow/issues/IJVBTJ) 暗黑主题下节点右键菜单没有适配颜色 | 经典模式右键菜单白底 + 继承浅色文字 → 白底白字不可见 | ❌ 不支持 | ❌ 不支持 | ❌（跟随库） | **已修复** |
| [IJV7GC](https://gitee.com/dromara/warm-flow/issues/IJV7GC) 流程设计器渲染报错 `setAttribute('0') InvalidCharacterError` | 对应被关闭的 [PR #393](https://gitee.com/dromara/warm-flow/pulls/393)；旧 Chromium 内核（钉钉内嵌/部分国产浏览器）下白屏 | ❌ 不支持 | ⚠️ 部分支持（已有 `defineEmits`，缺数组防护） | ✅ 不受影响（demo 不走 form-create） | **已修复** |

## 二、根因分析

### IJVBTJ：暗黑主题右键菜单

- 右键菜单来自 LogicFlow `Menu` 扩展（`@logicflow/extension`），DOM 类名 `.lf-menu`，默认样式只有 `background:#fff`，**不设置文字颜色**。
- 暗黑模式下页面全局文字色为浅色（`--wf-text-primary: #e0e0e0`），菜单项继承后变成「白底浅字」，视觉上不可见（issue 截图即此现象）。
- 排查证据：两个工程源码与构建产物 CSS 中均只有 `.lf-control`（顶部控制栏）的暗黑适配，`rg 'lf-menu'` 仅命中 LogicFlow 自带默认样式，无任何覆盖。
- 补充：仿钉钉模式在 `initMenu()` 中已把 `nodeMenu/edgeMenu` 置空，右键菜单只在**经典模式**出现，与 issue 场景一致。

### IJV7GC：`setAttribute('0')` 渲染崩溃

根因链（PR #393 已定位，维护者因「依赖升级风险」关闭，建议自行二开）：

1. `baseInfo.vue` 用 `proxy.$emit('update:model-value')` 但未声明 `emits` → 事件被当作 attrs 透传，Vue3 告警且传播链不规范；
2. `el-tree-select`（EP 2.4.3）在旧内核浏览器下 `v-model` 可能回传**数组**而非字符串，数组渗入 `formPath` / `category` 字符串字段；
3. 下游组件（`@form-create/designer` 深度 watch / 渲染管线）把数组展开为 DOM 属性 → `setAttribute('0', value)`，属性名以数字开头不符合 XML/HTML 规范 → `InvalidCharacterError` → 设计器白屏。

## 三、修复内容（本次落地）

原 PR #393 中的**依赖大版本升级不采纳**（vue 3.3→3.4、EP 2.4→2.8 等，风险大、维护者已明确拒绝），只落地根因侧修复 + 兜底：

### warm-flow-ui（webjar 基线，只接关键修复）

| 文件 | 改动 |
| --- | --- |
| `src/assets/styles/index.scss` | `html.dark` 块内新增 `.lf-menu` 暗黑适配（深底 `--wf-bg-white` + 浅字 `--wf-text-primary` + hover/disabled 态），与既有 `.lf-control` 适配并列 |
| `src/components/design/common/vue/baseInfo.vue` | ① 新增 `defineEmits(['update:model-value','update:flow-name'])`，2 处 `proxy.$emit` → `emit`；② 新增 `normalizeStringFields()`：`formPath`/`category` 为数组时取 `[0]` 转字符串，在 `watch(logicJson)` 与 `getFormData()` 两处入口调用 |
| `src/main.js` | 入口注入 `Element.prototype.setAttribute` 包装：属性名以数字开头时 `console.warn` 并跳过，作为旧内核浏览器的最后一道防线（应用壳层做全局补丁可接受；npm 库侧不做此类全局污染） |

### warm-flow-vue-designer（npm 组件库）

| 文件 | 改动 |
| --- | --- |
| `src/components/design/FlowDesigner.vue` | 全局（非 scoped）样式块新增 `.lf-menu` 亮/暗两态着色：统一走 `--wf-*` token（`html.dark` 自动翻转），亮色也显式设置文字色，不再依赖继承 |
| `src/components/design/common/vue/baseInfo.vue` | 新增 `normalizeStringFields()` 数组防护（`defineEmits` 此前已具备），同样挂在 `watch(logicJson)` 与 `getFormData()` |

### warm-flow-designer-demo

- 无需改动：三个 demo 经 `workspace:*` 消费库 `dist-lib`，重建库后自动获得修复；demo 未引入 form-create，不在 IJV7GC 影响面内。

## 四、回归验证（全部通过）

| 项 | 命令/方式 | 结果 |
| --- | --- | --- |
| 组件库四构建 | `warm-flow-vue-designer: pnpm run build:lib` | ✅ 主包 + element-plus + antdv + naive 全部成功；产物 CSS 已含 `.lf-menu` token 化规则 |
| EP demo 消费构建 | `warm-flow-ep-designer-demo: pnpm run build` | ✅ |
| antdv4 demo 消费构建 | `warm-flow-antdv4-designer-demo: pnpm run build` | ✅ |
| naive demo 消费构建 | `warm-flow-naive-designer-demo: pnpm run build` | ✅ |
| webjar 基线构建 | `warm-flow-ui: yarn build:prod` | ✅ 产物 CSS 含 `html.dark .lf-menu` 系列规则、JS 含 setAttribute 兜底补丁 |
| **视觉回归 · npm 库（EP demo :5180）** | playwright：进入经典画布 → `html.dark` → 右键节点 | ✅ 暗黑下菜单深底浅字（删除/编辑文本/复制清晰可读）；切回亮色白底深字正常 |
| **视觉回归 · warm-flow-ui（:8083 + mock :8080）** | playwright：新建流程 → 经典模型 → 流程设计 → 暗黑右键 | ✅ 暗黑菜单深底浅字；亮色回归正常 |
| 仿钉钉模式 | 代码路径确认 | ✅ `initMenu()` 置空 nodeMenu/edgeMenu，无右键菜单，不受影响 |

> 说明：IJV7GC 的「旧 Chromium 白屏」无法在本机新内核浏览器复现（新内核下 tree-select 正常回传字符串）；已按根因链在数据入口做类型防护 + 应用壳兜底，属预防式修复。回归中亮/暗两态基础信息 → 流程设计切换均无控制台异常。

## 五、残余风险

1. `setAttribute` 兜底补丁是全局原型包装，只拦截「数字开头属性名」这一非法子集，行为可预期；但若未来有库依赖抛异常行为（几乎不可能），需注意。
2. `warm-flow-ui` 的 form-create 链路（`@form-create/designer` 3.2.x + Vue 3.3.9 旧内核组合）未做依赖升级，旧浏览器上除本 issue 外的其它兼容性问题依旧可能存在——与维护者「不专门兼容过旧内核」的取向一致。
3. `.lf-menu` 适配依赖 `--wf-*` CSS 变量：消费方若自定义主题覆盖了 `--wf-bg-white`/`--wf-text-primary`，菜单会随主题走（符合预期设计）。
