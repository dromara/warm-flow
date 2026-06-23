# AGENTS.md — warm-flow-ui 模块规则

> 本文件只写 `warm-flow-ui` 的差异化规则。通用协作原则（先思考、最小改动、外科手术式、用证据验证、品牌保护、安全边界）以仓库根 [`../AGENTS.md`](../AGENTS.md) 为准。
>
> 注意：本模块是**前端工程，不属于 Maven 反应堆**，不受根 `AGENTS.md` 中 Java / JDK / ORM / SQL 规则约束；后端编译命令与本模块无关。

## 模块职责

Warm-Flow 流程设计器前端（经典 + 仿钉钉双模式），构建产物供 `warm-flow-plugin-ui` 集成为 jar 内置设计器。

技术栈（以 `package.json` 为准）：

- Vue 3.3.9 + Vite 5 + `<script setup>`；Element Plus 2.4.3 + `@element-plus/icons-vue`。
- 流程图：`@logicflow/core` / `@logicflow/extension`。
- 表单设计：`@form-create/designer` / `@form-create/element-ui`。
- 状态管理 Pinia；路由 vue-router；请求 axios；样式 Sass；`file-saver` 导出。
- 包管理 **yarn 1.x（classic）**；`type: module`；**前端许可证为 MIT**（与后端 Apache 2.0 不同，勿混改 header）。

## 常用命令

```bash
yarn --registry=https://registry.npmmirror.com   # 安装依赖
yarn dev                                          # 本地开发（vite）
yarn build:prod                                   # 生产构建（vite build）
yarn preview                                      # 预览构建产物
```

## 改动前必读

- 与后端契约：设计器读写的流程定义 JSON、节点属性、办理人 / 条件 / 监听器结构需与 `warm-flow-plugin-ui` 后端 `dto` / `vo` 及 core 实体对齐，改字段先看后端契约。
- 现有组件、路由、Pinia store、axios 封装与公共样式，复用既有模式，不要在局部页面另起一套风格。

## 高风险点

- **双模式一致**：经典模式与仿钉钉模式都要兼顾，节点状态颜色等沿用后端 `chartStatusColor*` 配置语义。
- **前后端契约**：流程定义 JSON 结构是与后端共享的契约，改动要前后端同步，避免设计器导出的 json 后端解析不了。
- **LogicFlow / form-create 版本**：图引擎与表单设计器升级可能破坏既有流程图 / 表单，升级按高风险处理并实际验证。
- **依赖版本**：锁定版本不随意升级；新增依赖先评估必要性与体积。

## 验证

- 改动后至少 `yarn dev` 跑通本地、`yarn build:prod` 能成功构建；涉及流程图 / 表单交互时手动验证关键路径。
- 无法验证时如实报告命令、现象与风险，不要声称构建通过。

## UI 设计规范（Design System）

> 设计器整体风格：**扁平 + iOS / macOS 质感**，主色调（chroma）为蓝色 `#409eff`。**基准页面是 `基础信息`（`components/design/common/vue/baseInfo.vue`）**，所有节点属性面板、弹窗、表单都要向它对齐。新增任何表单 / 面板 / 控件前，先看 `baseInfo.vue` 和本节，复用既有 token 与 mixin，**不要另起一套风格**。

### 1. 设计 Token（颜色 / 圆角 / 阴影 / 字体）

- 颜色来源唯一：`src/config/themeConfig.js`（JS 常量）→ 运行时同步为 `--wf-*` CSS 变量；**禁止在组件里写死十六进制色**，用变量并带回退值，如 `var(--wf-primary, #409eff)`。
- 主色族：`--wf-primary #409eff`、`--wf-primary-dark #2b7de9`、`--wf-primary-light #ecf5ff`、`--wf-primary-lighter #f0f7ff`。
- 语义色：危险 `--wf-danger #f56c6c`、成功 `#67c23a`、监听器紫 `#8960dc`。
- 圆角：`--wf-radius 8px`、`--wf-radius-lg 12px`、`--wf-radius-sm 4px`。
- 字体栈（必加，保证 iOS 质感）：`-apple-system, BlinkMacSystemFont, "SF Pro Text", "PingFang SC", "Helvetica Neue", "Microsoft YaHei", sans-serif` + `-webkit-font-smoothing: antialiased`。
- 共享 SCSS mixin 在 `src/assets/styles/_common.scss`：`modern-tabs` / `base-settings-card` / `section-card` / `table-form-align` / `responsive-adaption`，改风格优先改 mixin，全局生效。

### 2. 输入控件（白底描边式）

统一为「白底 + 浅边框」，独立的 `el-input / el-select / el-textarea`（基础信息页 / 属性抽屉 / 弹窗）：

- 默认：`background: var(--wf-bg-white, #fff)`；`border: 1px solid var(--wf-border-light, #dcdfe6)`；`border-radius: 10px`；`box-shadow: none`。**未填写也保持白底**。
- hover：`border-color: var(--wf-primary)`。
- focus：白底 + `border-color: var(--wf-primary)` + 柔光环 `box-shadow: 0 0 0 4px rgba(64,158,255,0.12)`。
- 校验失败（`.is-error`）：红边 + 红光环 `0 0 0 4px rgba(245,108,108,0.12)`，**优先级要压过 focus 蓝色**，禁止红蓝混色。
- 暗黑：深底（`rgba(255,255,255,0.06)` 或 `--wf-bg-color`）+ 描边，focus 蓝环。
- **控件尺寸用默认（高 ~32px），禁止 `size="small"`**（与基础信息页一致）。
- **禁止**默认态用发灰的填充底（曾用的 `rgba(118,128,150,0.07)` iOS 灰填充已废弃——一进页面没填写就发灰、易被误读为禁用态）。
- **例外**：表格内的输入 / 下拉控件默认 `transparent`（白表格上即白底），hover 轻填充、focus 才显白底 + 蓝环（见第 5 节），避免整行被填充底染灰。

### 3. 表单标签与分区

- 标签：**右对齐**（EP 默认 `label-position`，勿设 `left`）、`label-width="110px"`、`font-weight:600`、色 `--wf-text-primary`。
- 分区：**扁平**——分区标题 15px 加粗 + 底部 1px 分隔线（`--wf-border-lighter`），下方内容直接平铺。**禁止**给分区套「边框卡片 + 灰渐变头底 + 阴影」（`base-settings-card` / `section-card` mixin 已扁平化，勿改回卡片）。
- 行间距：参考基础信息页 `el-form-item margin-bottom: 24px` 的呼吸感。

### 4. 页签（Tab）

- 用 `modern-tabs` mixin：**扁平下划线**风格——底部一条分隔线；tab 项 = `图标 + 文字`，默认灰字，激活态主色字 + 底部 2px 主色下划线指示条（带滑入动画）。**禁止**蓝色实心胶囊 / 外层白盒 / 上浮阴影。
- tab 图标：16px 单路径 SVG，`fill="currentColor"`（跟随 tab 文字色），放在 tab 上而非内容区；图标语义示例：基础设置=齿轮、办理人=人群、监听器=铃铛、扩展=拼图。
- **避免「两个标题」**：抽屉本身已有标题（如「设置边属性」），面板内容区不要再放与之重复的分区标题——有 tab 的面板 tab 名即标题；单分区无 tab 的面板（边 / 网关 / 结束）也直接平铺表单、**去掉 `.base-settings-header` 二级标题**（如「跳转条件配置 / 网关配置 / 基础配置」），保持单一标题。

### 5. 表格

`el-table` 统一：圆角 `12px` + 1px 边框 + `overflow:hidden`；**表头白底 `var(--wf-bg-white, #fff)`**、`font-weight:600`（全站表头统一为白——办理人 / 监听器 / 扩展属性表一致；勿用发灰的 `rgba(118,128,150,0.06)`，也需用 `!important` 压过 ruoyi 全局 `.el-table th { #f8f8f9 }`）；行 hover = 浅蓝底 `--wf-primary-light` + 左侧 3px 主色指示条；删除按钮 `el-button--danger` hover 红渐变 + 阴影。暗黑：表头 `rgba(255,255,255,0.04)`、hover `rgba(64,158,255,0.12)`。

**表格内的输入 / 下拉控件默认透明底**（`background: transparent`），hover 轻填充、focus 才显白底 + 蓝环——让整行读作干净白底，避免「整行都是控件」的行被填充底染灰、与「含纯文本列」的行深浅不一致。

### 6. 单选（radio-card）

统一为「卡片式单选」一套设计语言：`1.5px` 边框 + 圆角，选中 = 主色边框 + **实色浅蓝底 `--wf-primary-light`**（勿用渐变）+ 实心蓝圆点。两种尺寸变体：

- 大卡片（图标 + 标题 + 描述）：用于「设计器模型」等需要解释的重要选择。
- 紧凑 pill（圆点 + 文字 + 可选 tooltip）：用于「协作方式 / 自定义表单」等简单多选项。
- 布尔类（是 / 否）可直接用 `el-switch`（如基础信息页「自定义表单」）。

### 7. 按钮

- 「增加行 / 添加行」：虚线主色边框 + 透明底 + 居中，hover 实线边框 + 浅蓝底 `--wf-primary-light` + 主色阴影。
- 主操作（保存等）：主色实心，圆角 ~10px，图标与文字间距用包裹 `span` 的 `inline-flex + gap`（EP 会把图标+文字包进一层 span，`gap` 要落在该 span 上）。

### 8. 属性面板抽屉（append-to-body）

- `el-drawer` teleport 到 body，scoped 样式命不中，**面板内样式必须写全局 `<style>` 并挂在 `.property-drawer-modern` 下**（见 `propertySetting.vue`）。
- 抽屉内同样套：系统字体栈、白底描边输入（第 2 节）、扁平分区、下划线 tab、统一表格；并提供 `html.dark` 覆盖。
- **append-to-body 弹窗（如人员选择 `el-dialog`，见 `between.vue`）**：弹窗根与 `.el-dialog__body` 必须有**实底背景** `var(--el-dialog-bg-color, #fff)`（遮罩 `rgba(0,0,0,.5)`），避免透明导致背后画布透出看不清；写全局样式时**用扁平完整 `:global(选择器)`**，不要在 `:global()` 内再嵌套子选择器——否则会被注入 scoped 属性而失效，甚至把头部样式漏到弹窗根节点。
- **抽屉头部**：收紧与正文间距（去掉 EP 默认 `el-drawer__header { margin-bottom:32px }`，改 `0` + `padding-bottom:14px` + 底部分隔线，`el-drawer__body` `padding-top:12px`）；标题文字（`.el-drawer__header > span`）用 `--wf-text-primary` + `font-weight:600` + `16px`，**勿用 EP 默认浅灰**。亮色与暗黑都要设。

### 9. 流程画布 / LogicFlow 节点

**仿钉钉模式节点：**

- 节点卡片：纯白底、**无边框**、多层柔和阴影（`0 6px 18px rgba(0,0,0,.10), 0 1px 4px rgba(0,0,0,.05)`）；设计态头部用主色蓝渐变 `linear-gradient(135deg,#409eff,#2b7de9)`。
- **配色统一蓝 chroma，禁止橙色 `#FF8B00`/`#FFFCEB`**：网关菱形 ICON、边「+」加号、跳转条件徽标都用 `#409eff` / 浅蓝 `#ecf5ff`（对应 svg 资产同样改蓝）。

**经典模式节点 + 左侧拖拽面板（设计态语义色）：**

- 设计态（无运行状态）按节点类型上**语义色**，不用发灰的默认中性色（旧的 notDoneColor `107,114,128`、侧栏硬编码灰 `166,178,189` 已废弃），让各类节点一眼可辨：开始=绿 `103,194,58`、中间·审批=蓝 `64,158,255`、结束=红 `245,108,108`、互斥网关=橙 `245,158,11`、并行网关=青 `19,194,194`、包含网关=紫 `146,84,222`。
- 左侧拖拽面板（`DiagramSidebar.vue` + `classics/js/sidebarIcons.js`）图标描边用同套语义色、图标底用对应浅色渐变，**与画布节点严格一致**（改一处要同步另一处）。
- 实现走 `common/js/tool.js` 的 `applyClassicDesignColor(style, properties, rgb)`：在 `setCommonStyle` 之后调用，**仅经典节点（`classics/js/*`）调用，仿钉钉不调用**。阴影用同色系柔和投影（`drop-shadow` 同色 ~0.14 + 黑 ~0.04），收敛发灰的光晕 / 重投影 / 灰雾。

**两模式公共：**

- 选中 / 悬浮包围框：`setTheme` 的 `outline` 用柔和蓝实线（`rgba(64,158,255,.7)`），**禁止黑色虚线默认框**。
- **运行态语义不可破坏（红线）**：节点 `status=在办(1)/已办(2)`、或 `chartStatusColor` 存在（看流程实例）时，节点 / 边 / 网关 / 条件徽标一律保留后端状态色（通过=绿 / 待办=橙 / 未到达=灰、退回红线）。设计态语义色**只在设计器页面生效**（全局标志 `window.__WF_FLOW_DESIGN_MODE__`，由 `flow-design/index.vue` 挂载时置位、卸载复位）且节点无运行状态时才覆盖，**业务侧流程实例进度图不受影响**。双模式（经典 / 仿钉钉）保持一致。

### 10. 暗黑模式

走 `html.dark` + `--wf-*` 变量；凡新增填充输入、表格、抽屉、卡片，都要补 `html.dark` 覆盖（参考 `baseInfo.vue` / `propertySetting.vue` 既有写法），勿只做亮色。

> 一句话原则：**新表单 / 面板 = 复用 `baseInfo.vue` 的 token + `_common.scss` 的 mixin**，对齐白底描边输入、扁平分区（单一标题）、右对齐标签、默认控件高度、下划线 tab、白底表头表格与卡片单选；画布节点经典走类型语义色、仿钉钉走蓝 chroma，且**都不动运行态状态色**。
