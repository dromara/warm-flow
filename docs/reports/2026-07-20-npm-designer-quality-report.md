<!-- 由代码质量审查任务生成（2026-07-20，分支 feature-ui-npm）。
     纯只读审查产物（含 tsc 探针/产物实测）；Top 级发现已由主流程抽查核实
     （dist-lib 缺 FlowDesigner.vue.d.ts、body overflow 未恢复、window._markDrawer* 未清理）。
     注：报告审查基线早于本日 IJVBTJ/IJV7GC 修复提交，行号可能有 ±20 行漂移。 -->

# warm-flow-vue-designer 只读质量审查报告

审查对象：`warm-flow-vue-designer`（`@dromara/warm-flow-designer` v1.8.2，工作树含未提交改动）+ 三个 demo + 与 `warm-flow-ui` 基线的关系。所有结论均基于当前代码与 `dist-lib` 产物的实际检查（含 tsc 探针实测），未做任何修改。

---

## 一、缺陷（bug）

### 发布面 / 类型

**1.【P1】`FlowDesigner` 组件在发布类型中退化为 `any`**
【文件】`dist-lib/src/designer/index.d.ts:2` ← `dist-lib/src/components/design/` 缺 `FlowDesigner.vue.d.ts`
【问题】主入口 d.ts `import { default as FlowDesigner } from '../components/design/FlowDesigner.vue'`，但 dts 产物只有 `FlowDesignerHeader/Toolbar.vue.d.ts`，`FlowDesigner.vue.d.ts` 未生成（同批缺失：`selectUser.vue`、`nodeExtList.vue`、`UserPicker*.vue`、`plugins/modal.ts`、`store/index.ts`）。已用 tsc 探针实测：`const n: number = FlowDesigner` 编译通过（应报 TS2322），即旗舰组件 props/事件对消费方全部失去类型。`todo.md`「发布类型退化为 any——已修复」的验证只覆盖了类型导出（`FlowDesignerInstance` 等），没覆盖组件本体。
【建议】排查 vite-plugin-dts 对这批文件静默跳过 emit 的原因（大概率文件内 TS 诊断错误，如 `proxy.$refs.*` 调用链）；修复后在 `build:lib` 加「d.ts 覆盖率断言」（对比 src 文件清单与 dist-lib 产物清单），防回归。

**2.【P2】`package.json` 声明 `vue-router` 为必选 peer，但库零引用**
【文件】`warm-flow-vue-designer/package.json:72`
【问题】`src/` 与 `dist-lib/warm-flow-designer.es.js` 均无 vue-router import（实测产物外部 import 仅 `vue/pinia/@logicflow/*`）。消费方被迫安装无用 peer；三个 demo 的 `dependencies` 也跟着带上了。
【建议】从 peerDependencies/devDependencies/README:233/demo 依赖中移除。

**3.【P3】`file-saver` 声明为打包运行时依赖但源码零使用**
【文件】`package.json:96`；模块 `AGENTS.md` 也写「file-saver 导出」
【问题】`rg file-saver|saveAs` 在 `src/` 无命中（`downJson` 用手写 `<a download>` 实现，`useLogicFlowCanvas.ts:664-676`）。死依赖 + 文档漂移。
【建议】删除依赖并同步 AGENTS。

**4.【P3】ESM-only 形态与 `sideEffects` 风险**
【文件】`package.json:19-47`
【问题】无 `main`、exports 无 `require` 条件（ESM-only 是可接受决策但 README 未声明）；`sideEffects: ["**/*.css"]` 把 JS 全部标记为无副作用，而主入口 import 时执行 `addCollection(epIcons/wfIcons)`（`src/icons/index.ts:15-16`）注册图标，理论上在激进 tree-shake 场景可被裁剪。其余方面 exports/types/style 路径与 dist-lib 实物一一对上（已核）。
【建议】README 标注 ESM-only；将图标注册改为 `install()` 内显式调用，或在 sideEffects 中补主入口文件。

### 运行时逻辑 / 内存泄漏

**5.【P1】卸载后不恢复 `document.body.style.overflow`，宿主页面永久失去滚动**
【文件】`src/components/design/FlowDesigner.vue:343`（`onMounted` 设 `hidden`）、`420-423`（`onUnmounted` 只复位 `__WF_FLOW_DESIGN_MODE__`）
【问题】作为 npm 组件嵌入业务系统时，关闭设计器后整页不能滚动。`docs/warm-flow-npm-refactoring-plan.md` C8 早已规划「NPM 场景条件化」，未落地。
【建议】mount 时记录原值、unmount 恢复；或按 C8 仅 iframe（`window.parent !== window`）场景设置。

**6.【P1】`window._markDrawerOpen/_markDrawerClosed` 挂载后永不清理（内存泄漏 + 多实例互踩）**
【文件】`src/composables/useLogicFlowCanvas.ts:244-258`（赋值）、`694-698`（onUnmounted 只移除 resize 与 `__WF_DESIGNER_DISABLED__`）
【问题】闭包持有 `lf`/`containerRef`，组件卸载后 LogicFlow 实例与 DOM 不能被回收；两个设计器实例并存时后挂载者覆盖前者。同族问题：`window.__WF_FLOW_DESIGN_MODE__`（FlowDesigner.vue:345/422，卸载置 `false` 而非恢复，多实例互踩）、`window.isTooltipHovered`（EdgeTooltip.vue:64-68）。
【建议】unmount 时删除回调；根治方案是抽屉状态经 props/事件或模块级注册表传递，放弃 window 通道。

**7.【P2】`propertySetting.vue` 匿名 resize 监听器永不解绑**
【文件】`src/components/design/common/vue/propertySetting.vue:42-44`
【问题】setup 时 `window.addEventListener('resize', () => …)`，无对应 remove（对照 `baseInfo.vue:152-163` 是成对的）。每次挂载泄漏一个监听器与组件引用。
【建议】`onUnmounted` 解绑，或用 `useWindowWidth` 类小 composable 统一管理（`between.vue:656-659`、`useUserPicker.ts:47` 还有多处 `window.innerWidth` 一次性读取，行为也不响应）。

**8.【P2】`between.vue` 校验链两处失效：`slice` 恒空 + Promise 竞态**
【文件】`src/components/design/common/vue/between.vue:686`、`670-683`（`warm-flow-ui/src/.../between.vue:708` 同病，属复制继承）
【问题】① `tabsList.value.slice(tabsList.value.length)` 永远返回 `[]`，「扩展页签（type=2 节点扩展）必填校验」是死代码；② `validate()` 中 `formRef.validate(callback)` 的异步 `reject` 与同步 `tabsValidate → resolve(true)` 赛跑，后端无节点扩展数据时（`nodeBase` 不存在）主表单校验结果被直接绕过——票签比例、监听器必填在关抽屉时可能不拦截。
【建议】`slice(3)`（或记录初始 tab 数）；`await` Promise 形态的 validate 后再串行执行 tabs 校验。

**9.【P2】`json2LogicFlowJson` 对缺省 `nodeRatio` 直接崩溃，demo 在替库兜底**
【文件】`src/components/design/common/js/tool.ts:59`（`node.nodeRatio.toString()`）；佐证 `warm-flow-designer-demo/warm-flow-ep-designer-demo/demoProvider.ts:98-106`（注释明说「避免 …undefined.toString() 崩溃（如 nodeRatio）」）
【问题】`initialJson` / `v-model:json` 是新的公开契约，消费方手写 JSON 少个 `nodeRatio` 字段组件即抛 TypeError 白屏。
【建议】库内 `String(node.nodeRatio ?? '0')` 兜底，demo 的规范化逻辑随之删除。

**10.【P2】`request.ts` 请求拦截器错误分支吞错**
【文件】`src/utils/request.ts:68-70`
【问题】`error => { Promise.reject(error) }` 缺 `return`，拦截器错误被吞、请求链以 undefined 继续（ruoyi 继承 bug）。另：line 45 `Object.keys(JSON.stringify(...)).length` 求字符串长度的写法晦涩；响应拦截器 `getUiAdapter()` 在未注册适配器时会抛错掩盖原始网络错误。
【建议】补 `return Promise.reject(error)`；长度用 `.length`。

**11.【P2】`FlowDesigner` 保存链路无 catch + `JSON.parse` 无防护**
【文件】`src/components/design/FlowDesigner.vue:494-514`（`saveJson(...).then(...).finally(...)` 无 `.catch` → unhandled rejection）、`354`（`JSON.parse(localSource)` 对非法字符串直接抛出导致挂载失败）
【建议】补 catch（保存失败提示）与 try/catch（进入 `isEmpty` 空态）。

**12.【P3】`eventCenter.on('edge:click  ', …)` 事件名带两个尾随空格**
【文件】`src/components/design/FlowDesigner.vue:602`
【问题】恰好 LogicFlow 内部对事件名做 `split(',') + trim()`（已在 `@logicflow/core` min 包实测）才得以工作，极脆弱。
【建议】去掉空格。

**13.【P3】`propertySetting.vue` 跨选中切换时 watcher 误发**
【文件】`src/components/design/common/vue/propertySetting.vue:206-213`（`nodeName` watcher 无节点类型 guard）对照 `271-280`（`skipName` watcher 有 `['skip'].includes(...)` guard）
【问题】节点→边切换时会对边执行 `updateText(edgeId, undefined)` 与 `setProperties({nodeName: undefined})`，靠后注册的 `skipName` watcher 顺序恢复文本，属侥幸正确。
【建议】所有 watcher 按 `props.node.type` 对称加 guard，或改为显式表单提交而非 12 个细粒度 watcher。

### UI 适配层（三适配器对齐）

28 个语义键三家均已注册（已逐一核对 elementPlusAdapter:71-100 / antdvAdapter:718-748 / naiveAdapter:770-799，无键位缺失），但行为面有实际缺口：

**14.【P2】非 EP 适配器下 ref 方法转发缺失 → 运行时 TypeError**
【文件】`src/components/design/common/vue/start.vue:129`、`end.vue:50`（`proxy.$refs.nodeInput.focus()`）；`UserPickerTree.vue:94-95`（`treeRef.value?.filter(value)` / `setCurrentKey`）；根因 `src/ui/antdvAdapter.ts:118-140`（AntInput）、`568-588`（AntTree）、naive 同构——包装组件均未 `expose` `focus/filter/setCurrentKey`，而 `createNeutralComponent.ts:27-43` 的 ref 代理只能转发到包装组件实例。
【问题】antd/naive 下：开始/结束节点名称输入触发 `change` 即抛 `focus is not a function`（antd 的 a-input `change` 每击键触发一次）；人员选择弹窗部门搜索框输入即抛 `filter is not a function`，且 `filter-node-method` 语义本身未翻译（部门过滤在非 EP 下不可用）。
【建议】适配器包装组件统一 `expose` 常用方法（focus/blur/filter/setCurrentKey…）转发内层实例；树过滤在 antd/naive 侧用受控 `filteredData`/`pattern` 实现。

**15.【P2】naive 抽屉丢弃 `before-close` → 关抽屉前校验与节点编码改名链路失效**
【文件】`src/ui/naiveAdapter.ts:529`（把 `before-close` 直接 delete，`onUpdate:show` 直改 v-model）对照 `antdvAdapter.ts:505-516`（映射为 `onClose`）、EP 原生支持；消费点 `propertySetting.vue:11`（`:before-close="handleClose"`，内含表单校验 + `changeNodeId/changeEdgeId`）
【问题】naive 下点遮罩/关闭按钮直接关闭：必填不校验、修改的节点编码不落到画布 id。三栈行为不一致。
【建议】NaiveDrawer 把 `before-close` 接到 `onUpdate:show` 前置调用（或 `onMaskClick`/`onEsc`）。

**16.【P2】antd `AntSelect` 未映射 `multiple`**
【文件】`src/ui/antdvAdapter.ts:217-247`（只处理 `allow-create→tags`）；对照 `naiveAdapter.ts:231`（`multiple: !!multiple`）与 EP 原生；消费点 `nodeExtList.vue:18-19`（`<wf-select :multiple="item.multiple">`）
【问题】antd 下「节点扩展属性」的多选下拉退化为单选，数组值塞进单选 Select 行为未定义。同类小缺口：`wf-time-picker :is-range`（nodeExtList.vue:66）在 antd/naive 均未翻译成 RangePicker。
【建议】AntSelect 补 `mode: multiple ? 'multiple' : (allowCreate ? 'tags' : undefined)`；时间范围按需补映射。

**17.【P2】`isDrawerOpen` 用 `.el-drawer` DOM class 判定，非 EP 栈移动端触摸拦截失效**
【文件】`src/composables/useLogicFlowCanvas.ts:350-360`
【问题】「UI 无关」核心里写死 EP 的 DOM 结构；antd（`.ant-drawer-open`）/naive（`.n-drawer`）下抽屉打开时触摸事件照常转发给画布，移动端会隔着抽屉拖动画布。
【建议】仅依赖 `_drawerActive` 标志（本就由 show/close 维护），删除 EP class 探测。

**18.【P3】适配器命令式反馈默认文案不一致 + 未接 i18n**
【文件】`elementPlusAdapter.ts:10`（`系统提示`）、`antdvAdapter.ts:659`（notify 默认 `提示`）/`664/671-674`（`系统提示/确定/取消`）、`naiveAdapter.ts:703/709-711/721-724`
【问题】同一 `notify` 三家默认标题不同（`系统提示` vs `提示`）；所有按钮/标题硬编码中文，`setLocale('en')` 后仍是中文（`request.ts:59/91-96` 网络错误、`useFlowDesigner.ts:74` 亦同）。
【建议】适配器内文案走 `translate('common.confirm')` 等既有 key（catalog 已有 confirm/cancel/tip）。

**19.【P3】`getUiAdapter()` 报错文案与现实相反**
【文件】`src/ui/uiAdapter.ts:83-91`（「或 app.use(WarmFlowDesigner)（会默认注册 Element Plus 适配器）」）、`43`（「默认实现为 Element Plus」）
【问题】主入口已 UI 无关，`install()`（designer/index.ts:78-91）不注册任何适配器；按报错提示操作无效，误导排障。
【建议】改为「请先 setUiAdapter(elementPlusAdapter/antdvAdapter/naiveAdapter)（分别来自 /element-plus、/antdv、/naive 子入口）」。

### 数据层 / i18n / 杂项

**20.【未发现问题】DataProvider 契约与 api 委托层完全一致**：`provider.ts` 接口 11 个方法（saveJson…listenerList + config）与 `httpProvider.ts`、`mockProvider.ts` 实现、`api/flow/definition.ts` + `api/anony.ts` 委托一一对齐，`setDataProvider` 部分覆盖合并语义正确。仅注意 `AGENTS.md` 还提「DataProvider 的表单方法（getFormContent 等）暂保留」——实际已删除，文档漂移。

**21.【未发现问题】i18n 中英 catalog 完全对齐**：脚本实测 zh/en 均 187 个叶子 key，双向差集为空。次级问题【P3】：`FlowDesigner.vue:309` 注释行引用的 `flowDesigner.stepFormDesign` 不在 catalog（一旦启用表单设计步骤即回落 key 原文）；`i18n/index.ts:17` 文档示例用了不存在的 `common.deleteConfirm`；12 个 `common.*` key（cancel/delete/tip/success…）暂无人使用；`setMessages`/`WfLocale` 类型是闭合联合 `'zh'|'en'`，与注释「可新增语言」矛盾。

**22.【P2】`useDark().initFromUrl` 在库形态下形同虚设 + pinia 隐性耦合**
【文件】`src/composables/useDark.ts:28`（`useAppStore().appParams`）、`src/store/app.ts:11`（`fetchTokenName` 全库无调用点、未导出）
【问题】`appParams` 只能由 `fetchTokenName()` 填充，而它在纯库/所有 demo 中都不被调用 → `initFromUrl` 读到的 params 恒为 null，`?theme=theme-dark`、`darkColors` URL 能力实际失效（warm-flow-ui 的 App 壳才会调它）。且 `useDark` 一被调用即触达 pinia，是 pinia 成为必选 peer 的唯一实质原因。另有死 import（useDark.ts:1 的 `onMounted, onUnmounted` 未使用）。
【建议】`initFromUrl` 直接读 `window.location.search`（不再绕 store），可顺带把 pinia 降级为可选；或导出 `fetchTokenName` 并文档化。

**23.【P2】auto-import + `tsconfig.include` 残缺 → 类型质量塌陷**
【文件】`vite/plugins/auto-import.js:10`（`dts: false`）、`src/store/app.ts:4`（`defineStore` 无 import）、`src/store/index.ts:1`（`createPinia` 无 import）、`SvgIcon/index.vue:10`（`defineComponent/computed` 无 import）、`tsconfig.json:24`（include 不含 `src/components`/`store`/`plugins`/`utils`）
【问题】`dist-lib/src/store/app.d.ts` 实测内容为 `declare const useAppStore: any`；不在 include 的目录对 tsc/dts 是盲区（与缺陷 1 的 d.ts 缺失同源）。库代码依赖 auto-import 也降低可移植性。
【建议】库源码全部显式 import（组件库不该依赖 auto-import）；tsconfig include 覆盖整个 `src`。

**24.【P3】`SvgIcon` 的 `<style scope>` 拼写错误**
【文件】`src/components/SvgIcon/index.vue:40`
【问题】`scope` ≠ `scoped`，样式意外全局生效；其中 `.sub-el-icon/.nav-icon`（41-47）是 ruoyi 侧栏死选择器。
【建议】改 `scoped` 并删死规则。

**25.【P3】`isMockEnabled` 的构建期开关在产物中已失效**
【文件】`src/data/index.ts:26`；产物 `dist-lib/warm-flow-designer.es.js:9518` 编译为 `up.VITE_USE_MOCK === "true"`（`up = {}` 空对象）
【问题】`.env.production` 未定义 `VITE_USE_MOCK`，Vite 以空对象残留，构建期分支恒 false——对 npm 消费方只有 `?mock=true` 有效，文档口径需一致。`VITE_URL_PREFIX` 则被内联为 `"../"`（webjar 语义），npm 消费方无法配置前缀、只能整体换 provider，建议文档化或提供 `setUrlPrefix`。

---

## 二、重复造轮子（库内部）

**1.【P2】antdv 与 naive 适配器复制同一套基础设施（约 150 行逐字重复）**
【文件对】`antdvAdapter.ts:35-46` = `naiveAdapter.ts:45-56`（clickOutside 指令）；`49-88` = `59-98`（applyLoading/removeLoading/loadingDirective）；`402-415` = `101-114`（collectByName）；`390-399` = `117-126`（rowKeyOf + 各自独立的 WeakMap/序号）；`258-280` = `135-157`（vModelComp 工厂）
【建议】抽 `src/ui/adapterShared.ts`，新适配器（Arco/TDesign）直接复用。

**2.【P2】`plugins/modal.ts` 是 uiAdapter 的全量重复包装且基本失联**
【文件】`src/plugins/modal.ts`（14 个方法全部一行转发 `getUiAdapter()`）、`plugins/index.ts:4`（`installPlugins` 全库无调用点）、唯一引用 `FlowDesigner.vue:497`（对宿主 `$modal` 的可选降级）
【建议】删除 modal.ts/installPlugins（或仅保留说明「宿主可自备 $modal」），`$cache` 同理只余 request.ts 用到的 `cache.session`。

**3.【P2】listener 字符串编解码逻辑 4 处手写**
【文件】`baseInfo.vue:241-253`（setListenerData）、`between.vue:515-523`（getPermissionFlag 内嵌同款）、`propertySetting.vue:175-180`（watch 内第三份）、反向 join 在 `baseInfo.vue:293-295` 与 `propertySetting.vue:244-257`
【问题】`listenerType.split(",")` / `listenerPath.split("@@")` ↔ `listenerRows` 的映射是与后端共享的契约，四处各写一遍，改分隔符必漏。
【建议】抽 `utils/listener.ts`（encode/decode 纯函数）。

**4.【P3】`handleListenerPathChange` 与增删行函数逐字重复**
【文件】`baseInfo.vue:309-325` ≡ `between.vue:566-582`；`handleAddRow/handleDeleteRow` 在 `baseInfo.vue:256-264`、`between.vue:641-648`、`start.vue:142-149` 三份。TAB_ICONS 的 base/listener path 在 `between.vue:308-313` 与 `start.vue:102-105` 重复。

**5.【P3】mock 数据两份且已漂移**
【文件】`src/data/mockProvider.ts:22-42`（角色 4 条/部门 4 条/用户 6 条）vs `mock/server.mjs`（角色 2 条起）；server.mjs 头注释自称「与 mockProvider.ts 对齐」但实际已不齐。
【建议】server.mjs 从共享 JSON 或直接 import mockProvider 数据源。

**6.【未发现】** lodash 级轮子（深拷贝/防抖/uuid/树遍历）未见重复实现（全库 0 处 `JSON.parse(JSON.stringify)`/`debounce`/`cloneDeep`；下载文件仅 `useLogicFlowCanvas.downJson` 一处）。与 `warm-flow-ui` 的关系符合预期的「整树复制」：同路径文件 26 个字节级相同、20 个已漂移（如 between.vue）、67 个为 designer 独有——历史事实，不展开。

---

## 三、重复样式

**1.【P2】产物 CSS 约 600 处 `.el-*` 选择器在 antd/naive 下全部不命中**
【文件】`dist-lib/warm-flow-designer.css`（实测 `.el-table`×180、`.el-form-item`×84、`.el-input`×72、`.el-drawer`×16…）；来源为各 `*.scoped.scss` 与 `propertySetting.global.scss`
【问题】152KB 的单一 CSS 三栈共用，非 EP 栈载入大量死规则且观感缺失（naiveAdapter.ts:23-24 与 todo.md「③ EP 样式归位（Phase 4）」均已自认）。
【建议】按 todo ③ 把 `.el-*` 覆盖抽到 EP 子入口样式（`/element-plus` 附带 css），主包只留 `.wf-*`/token。

**2.【P2】「白底描边输入框」同一套规则复制 4 处**
【文件】`baseInfo.scoped.scss:82-112`、`propertySetting.global.scss:42-64`、`userPickerSearch.scoped.scss:41-61`、`userPickerTree.scoped.scss:102+`（含 hover 主色边、focus `0 0 0 4px rgba(64,158,255,.12)` 光环、is-error 红环，暗黑覆盖也各自一份）
【问题】`_common.scss` 是设计规范钦定的 mixin 归口，却没有 input mixin，导致每个组件手抄且已开始漂移。
【建议】新增 `@mixin wf-input-flat`，四处替换。

**3.【P2】`selectUser` 手抄了一份「旧版胶囊 tab」，与现行规范冲突**
【文件】`selectUser.scoped.scss:12-24`（白底卡片 + `box-shadow` + padding 5px 的 pill 风格）vs `_common.scss:8-21` `modern-tabs` mixin（扁平下划线，规范明令「禁止蓝色实心胶囊/外层白盒/上浮阴影」）
【问题】既是重复实现又是视觉漂移（todo.md:125 记录的「selectUser tab 7px 18px vs _common 10px 2px」同源）。
【建议】selectUser 改 `@include modern-tabs` 并删本地拷贝（需截图核对，todo 已列）。

**4.【P3】mixin 以 `@include` 进 5 个组件 → 编译产物 5 倍重复**
【文件】`start.vue:159-162`、`end.vue:61-62`、`gateway.vue:47-48`、`skip.vue:183-184`、`between.scoped.scss:17-21` 均 include `base-settings-card/responsive-adaption`（后者 200+ 行含三档媒体查询）
【问题】源码单点，但 CSS 产物按组件 ×5 展开，是 152K 的主要成因之一。
【建议】把与 scoped 无关的结构类（`.base-settings-*`、`.modern-tab-*`、响应式）提为一次性的全局层。

**5.【P2】约 1400 行死样式文件（库构建根本不引用）**
【文件】`src/assets/styles/index.scss:10-17` 引链的 `element-ui.scss`(95 行)、`ruoyi.scss`(286)、`sidebar.scss`(238)、`btn.scss`(99)、`transition.scss`、`mixin.scss`、`variables.module.scss` —— 全库唯一入口 `index.scss` 无人 import（只有 `warm-flow-ui/src/main.js:29` 用）；实测 dist CSS 无 `el-message-box`/`sidebar-container`/`text-navy`/`el-upload` 等标记。另：`assets/401_images`、`404_images`、`favicon.ico` 零引用；`FlowDesigner.vue:744-746` `.container:has(.diagram-sidebar)` 空规则块。
【建议】整链删除（git 可追溯），`_common.scss`/`tokens.scss` 留下即可。

**6.【P3】非法/弃用深度选择器**
【文件】`baseInfo.scoped.scss:77`（`::deep(...)` 双冒号写法无效，该条「内容区左对齐」规则实际不生效）；`_common.scss:176/190-204` 等大量 `::v-deep()`（Vue3 弃用写法，编译告警级）；`nodeExtList.vue:46-51` 直接手写 `.el-tag/.el-icon` class 的伪标签绕过 wf-tag（非 EP 下无样式）。

---

## 四、工程化问题

**1.【P2】三个子入口 vite 配置 95% 重复**
【文件】`vite.ep.config.js` / `vite.antdv.config.js` / `vite.naive.config.js`（三文件仅 `externalDeps`、`entry`、`name`、`fileName` 四行不同，其余逐字相同）
【建议】抽 `createAdapterLibConfig(entry, name, file, uiDep)` 工厂，新适配器只加一行；这也消除注释漂移（ep 配置头注释仍只提「Element Plus 与 Ant Design Vue 均为可选」，漏 naive）。

**2.【P2】`pnpm dev`（watch）只重建主包，不覆盖三个适配器子包**
【文件】`package.json:52`（`vite build --config vite.lib.config.js --watch`）
【问题】开发循环中改 `antdvAdapter.ts` 等不会反映到 demo（`antdv.es.js` 停在上次 build:lib），易被「改了没生效」坑。
【建议】dev 脚本并行 watch 四配置，或文档明示适配器改动需手动 `build:antdv`。

**3.【P3】`pnpm-workspace.yaml` 残留占位垃圾段**
【文件】`pnpm-workspace.yaml:5-10`（`allowBuilds: '@logicflow/core': set this to true or false ...`）
【问题】pnpm 交互提示的占位文案被原样提交；真正生效的是下方 `onlyBuiltDependencies`。
【建议】删除 `allowBuilds` 段。

**4.【P3】tsconfig 基线弱**：`strict: false`、include 残缺（见缺陷 23）、`allowJs` 但 vite 目录 js 不在 program；无任何 `vue-tsc --noEmit` 类型门禁脚本，类型回归只能靠 dts 构建侧信道发现。

**5.【P3】库目录遗留物**：`dist/`（旧应用构建，纯库已无 build:prod，虽被 gitignore 但本地易混淆）、`html/ie.html`（ruoyi IE 提示页）、`.env.development` 的 `/dev-api`（库无 dev server，仅 mock 联调语义）——建议清理或在 README 注明用途。

**6.【已知欠账对照】** `todo.md` 自认的欠账与本次发现互证：npm publish/CI 未接（todo:8-9）、EP 样式归位 Phase 4（todo:127 ↔ 本报告三-1）、样式 DRY 漂移（todo:125 ↔ 三-2/3）、between 704 行 god 组件（todo:126 ↔ 一-8 的校验链复杂度）、属性面板 schema 化（todo:142 ↔ 二-3/4 的重复）。dist-lib 依赖泄漏方面**未发现问题**（主/子包 external 实测干净）。

---

## 五、与 demo 的一致性

**1.【未发现不匹配】** 三个 demo 的 `main.ts` 集成序列一致且与库导出/README 完全匹配（`setUiAdapter(子入口 adapter)` → `setDataProvider(createDemoProvider())` → `createApp.use(pinia).use(UI库).use(WarmFlowDesigner)`；样式统一 `@dromara/warm-flow-designer/style`）。`#node-form-extra` 插槽三家都用中性 `wf-form-item/wf-input`，跨栈正确。

**2.【P3】三份 ~700 行 App.vue + demoProvider 近乎复制**：`demoProvider.ts` ep 与 antdv 逐字节相同、naive 仅注释差；App.vue 仅 UI 标签层不同。演示代码可接受，但三份会漂移（naive 版注释已丢失「nodeRatio 兜底原因」这一关键信息）。可考虑 demo 共享 `demoProvider` 包。

**3.【P3】demo 冗余/过时细节**：三 demo `dependencies` 都带未使用的 `vue-router`（随库 peer 被迫引入，联动缺陷 2）；EP demo `main.ts:24` 注释「+ Element Plus 图标」已过时（现为 iconify 离线集）；EP demo `vite.config.ts` 注释仍写 `yarn build:lib`（现为 pnpm）；库 `README.md:4` 标题「v1.2.7」vs 实际 1.8.2。

**4.【P2】`useFlowJson` 官方示例代码不可用**
【文件】`src/composables/useFlowJson.ts:43`（同步进 `dist-lib/src/composables/useFlowJson.d.ts` 的 JSDoc）
【问题】示例在模板里 `v-on="useFlowJson(designerRef).bind"` 二次调用 composable——每次渲染新建实例，其 bind 同步的是第二实例的 refs，script 里解构的 `json/dirty` 永远不更新。照抄示例的消费方必然踩坑（EP demo 自己就没这么用，而是 `flowJson.sync()` 手动同步）。
【建议】示例改为解构同一实例的 `bind`。

---

## Top 10 优先整改清单

| # | 级别 | 事项 | 关键位置 |
|---|------|------|---------|
| 1 | P1 | 修复 `FlowDesigner.vue.d.ts` 等 dts 缺失（旗舰组件类型 any），并加 d.ts 覆盖率断言 | `vite.lib.config.js:74-79`、`dist-lib/src/designer/index.d.ts:2` |
| 2 | P1 | 卸载时恢复 `document.body.style.overflow`（落地 C8 条件化） | `FlowDesigner.vue:343/420-423` |
| 3 | P1 | 清理 `window._markDrawerOpen/Closed` 与 `__WF_*` 全局标志族的泄漏与多实例互踩 | `useLogicFlowCanvas.ts:244-258/694-698` |
| 4 | P2 | 非 EP 适配器 ref 方法转发（focus/filter/setCurrentKey）修 TypeError；naive 抽屉接回 `before-close` | `antdvAdapter.ts:118-140`、`naiveAdapter.ts:529`、`start.vue:129`、`UserPickerTree.vue:94` |
| 5 | P2 | 修 `between.vue` 校验链：`slice(tabsList.length)` 恒空 + validate 竞态（warm-flow-ui 同步评估） | `between.vue:670-695` |
| 6 | P2 | `nodeRatio.toString()` 崩溃由库兜底，撤掉 demoProvider 的补丁 | `tool.ts:59`、`demoProvider.ts:98-106` |
| 7 | P2 | antd Select 补 `multiple` 映射；`isDrawerOpen` 去 EP DOM 依赖；适配器默认文案接 i18n 并统一 | `antdvAdapter.ts:217-247`、`useLogicFlowCanvas.ts:350-360` |
| 8 | P2 | 发布面清理：删 `vue-router` peer、删 `file-saver`、修 `getUiAdapter` 误导文案、`propertySetting` resize 解绑、`request.ts` 拦截器 return | `package.json:72/96`、`uiAdapter.ts:83-91`、`propertySetting.vue:42-44`、`request.ts:68-70` |
| 9 | P2 | 样式收敛第一批：删 1400 行死样式链（index.scss 引链 + 401/404 图）、白底输入抽 mixin、selectUser 旧 tab 对齐规范 | `assets/styles/*`、`selectUser.scoped.scss:12-24` |
| 10 | P2 | 类型工程加固：库源码去 auto-import（显式 import）、tsconfig include 全 src、加 `vue-tsc --noEmit` 门禁；三个 vite 子配置抽工厂 | `vite/plugins/auto-import.js`、`tsconfig.json:24`、`vite.{ep,antdv,naive}.config.js` |

补充说明：i18n 中英 catalog（187/187 key 完全对齐）、DataProvider 契约与 api 委托一致性、dist-lib 依赖泄漏、三 demo 用法与库导出匹配性——这四项经实测**未发现问题**。
