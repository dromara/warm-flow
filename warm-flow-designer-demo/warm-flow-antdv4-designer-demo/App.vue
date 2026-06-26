<template>
  <div class="demo-root">
    <!-- ========== 列表视图 ========== -->
    <div v-if="view === 'list'" class="demo-list">
      <!-- Hero 头部 -->
      <header class="demo-hero">
        <div class="demo-hero-inner">
          <div class="demo-hero-text">
            <div class="demo-hero-brand">
              <span class="demo-logo">WF</span>
              <h1>Warm-Flow Designer<span class="ui-badge ui-badge--antd">Ant Design Vue 4</span></h1>
            </div>
            <p class="demo-hero-sub">轻量级工作流设计器 · npm 组件库消费示例 · 经典 + 仿钉钉双模式</p>
            <div class="demo-hero-chips">
              <span class="chip">UI 适配器可插拔 · EP / antd</span>
              <span class="chip">localStorage 持久化</span>
              <span class="chip">保存 / 修改 / 预览闭环</span>
            </div>
          </div>
          <div class="demo-actions">
            <a-button type="primary" size="large" @click="onCreate">+ 新建流程</a-button>
            <a-button type="primary" ghost size="large" @click="onValidateExt">扩展能力验证</a-button>
            <a-button size="large" @click="refresh">刷新</a-button>
            <a-button danger size="large" :disabled="!flows.length" @click="onClearAll">清空</a-button>
          </div>
        </div>
      </header>

      <main class="demo-main">
        <!-- 用法说明（3 步集成） -->
        <section class="demo-card demo-usage">
          <div class="demo-card-head"><span class="head-dot"></span>用法 · 3 步集成（Ant Design Vue 4）</div>
          <pre class="demo-usage-code">{{ usageCode }}</pre>
        </section>

        <!-- 流程列表 -->
        <section class="demo-card">
          <div class="demo-card-head">
            <span class="head-dot"></span>流程列表
            <span class="demo-count">{{ flows.length }}</span>
            <span class="demo-card-hint">第三方 import 消费 dist-lib，数据持久化于浏览器 localStorage</span>
          </div>

          <a-table
            v-if="flows.length"
            :data-source="flows"
            :columns="columns"
            :pagination="false"
            :row-key="rowKey"
            class="demo-table"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'model'">
                <a-tag :color="isClassics(record.modelValue) ? 'green' : 'blue'">
                  {{ isClassics(record.modelValue) ? '经典模式' : '仿钉钉模式' }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button type="link" size="small" @click="onEdit(record)">修改</a-button>
                <a-button type="link" size="small" @click="onPreview(record)">预览</a-button>
                <a-button type="link" size="small" @click="onExport(record)">导出JSON</a-button>
                <a-button type="link" size="small" danger @click="onRemove(record)">删除</a-button>
              </template>
            </template>
          </a-table>

          <a-empty v-else description="暂无流程，点击「新建流程」开始设计" class="demo-empty" />
        </section>
      </main>
    </div>

    <!-- ========== 设计器视图 ========== -->
    <div v-else class="demo-design">
      <div class="demo-design-bar">
        <a-button type="text" @click="backToList">← 返回列表</a-button>
        <span class="demo-design-mode" :class="designMode">{{ designModeText }}</span>
      </div>
      <div class="demo-design-canvas">
        <!-- 对象式消费：designProps 是细粒度 props 的集合，v-bind 直接摊开绑定到对应 prop，
             无需组件提供单一 designProps prop —— 仍保留 per-prop 默认值 / 模板类型校验 / DevTools 粒度 -->
        <FlowDesigner
          ref="designerRef"
          :key="designKey"
          v-bind="designProps"
          @saved="onSaved"
          @close="onClose"
          @ready="onDesignerReady"
          @before-save="onBeforeSave"
          @change="onDesignerChange"
          @dirty="onDesignerDirty"
          @validate-error="onValidateError"
        >
          <!-- ① node-form-extra 插槽透传验证：仅扩展验证模式注入，证明 FlowDesigner→PropertySetting→节点子组件 链路打通 -->
          <template #node-form-extra="{ form, disabled }">
            <wf-form-item v-if="designMode === 'validate' && form" label="扩展字段(验证)：">
              <wf-input v-model="form.validateExt" :disabled="disabled" placeholder="node-form-extra 插槽已生效" />
            </wf-form-item>
          </template>
        </FlowDesigner>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Ant Design Vue 4 消费示例：与 ep-demo 同一套列表 / CRUD 流程（新建 / 保存 / 修改 / 预览 / 导出 / 删除），
 * 区别仅在 UI 库——本 demo 应用外壳与设计器全部走 Ant Design Vue 4（main.ts 中 setUiAdapter(antdvAdapter)），无 Element Plus。
 *
 * @author warm
 */
import { computed, ref, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { FlowDesigner, useFlowJson } from '@dromara/warm-flow-designer'
// 组件库扩展能力验证用：消费方自带的 LogicFlow 基类 / 官方扩展（库已把它们 externalize 为 peer，单实例共享）
import { RectNode, RectNodeModel } from '@logicflow/core'
import { Control } from '@logicflow/extension'
import { listFlows, getFlowJsonString, removeFlow, clearFlows } from './demoProvider'

// 顶部「用法」代码片段：演示第三方 3 步集成（与 ep-demo 仅适配器不同）
const usageCode = `// main.ts
import { WarmFlowDesigner, setUiAdapter, setDataProvider } from '@dromara/warm-flow-designer'
import { antdvAdapter } from '@dromara/warm-flow-designer/antdv'   // ← antd 适配器子入口
import '@dromara/warm-flow-designer/style'

setUiAdapter(antdvAdapter)        // ① 选 UI 适配器（须在渲染 FlowDesigner 前）
setDataProvider(myProvider)       // ② 注入数据源（自定义后端 / mock）
app.use(Antd).use(WarmFlowDesigner)   // ③ 注册后模板里直接用 <FlowDesigner />`

// ===== 组件库扩展能力验证夹具：端到端验证本轮新增的 ①②③ props / slot =====
// ② initialJson：脱后端直接喂 warm-flow 定义对象（经典模式 开始→部门审批→结束），组件不再调用 queryDef
const validateInitialJson = {
  flowName: '扩展能力验证流程（initialJson 脱后端驱动）',
  modelValue: 'CLASSICS',
  flowCode: 'validate_ext_flow',
  version: '1',
  isPublish: 0,
  nodeList: [
    {
      nodeType: 0, nodeCode: 'node_start', nodeName: '开始', nodeRatio: '0', coordinate: '180,260|180,260',
      skipList: [{ id: 'skip_1', nowNodeCode: 'node_start', nextNodeCode: 'node_approve', skipName: '', skipType: 'PASS' }]
    },
    {
      nodeType: 1, nodeCode: 'node_approve', nodeName: '部门审批', nodeRatio: '0', coordinate: '430,260|430,260',
      skipList: [{ id: 'skip_2', nowNodeCode: 'node_approve', nextNodeCode: 'node_end', skipName: '', skipType: 'PASS' }]
    },
    { nodeType: 2, nodeCode: 'node_end', nodeName: '结束', nodeRatio: '0', coordinate: '680,260|680,260' }
  ]
}
// ③ customNodes：用消费方自带 @logicflow/core 基类注册自定义节点类型（与库内 LogicFlow 同实例 → 注册不报错）
const validateCustomNodes = [{ type: 'validate-custom-node', view: RectNode, model: RectNodeModel }]
// ③ extraExtensions：追加官方 Control 扩展（画布缩放/适配控件），验证 LogicFlow.use 透传
const validateExtraExtensions = [Control]
// ③ lfOptions：合并进 new LogicFlow 初始化选项（点状网格），验证顶层选项透传
const validateLfOptions = { grid: { size: 20, type: 'dot' } }
// ③ onBeforeUse：透出 LogicFlow 类（可注册带配置扩展，extraExtensions 无法传配置）。
// 验证钩子被调用且拿到的是 LogicFlow 类（含静态 use）。
const validateOnBeforeUse = (LF) => {
  window.__WF_VALIDATE_ON_BEFORE_USE__ = !!(LF && typeof LF.use === 'function')
  console.log('[validate] onBeforeUse 调用，LogicFlow.use =', typeof LF?.use)
}
// ③ onRegister：透出 lf 实例，命令式注册一个自定义节点（区别于声明式 customNodes），验证可访问实例 + 注册成功。
const validateOnRegister = (lf) => {
  let ok = false
  try {
    if (lf && typeof lf.register === 'function') {
      lf.register({ type: 'validate-on-register-node', view: RectNode, model: RectNodeModel })
      ok = true
    }
  }
  catch (e) {
    console.error('[validate] onRegister 注册失败', e)
  }
  window.__WF_VALIDATE_ON_REGISTER__ = ok
  console.log('[validate] onRegister 调用，lf.register =', typeof lf?.register, 'registered =', ok)
}
// ④ paletteNodes：自定义经典模式左侧拖拽面板节点（重命名基础节点 + 传空数组隐藏网关组），验证覆盖生效
const validatePaletteNodes = {
  flowNodes: [
    { type: 'start', label: '开始(自定义)' },
    { type: 'between', label: '审批(自定义)', properties: { collaborativeWay: '1' } },
    { type: 'end', label: '结束(自定义)' }
  ],
  gatewayNodes: []
}

// ⑨ useFlowJson：流程 json 响应式只读视图（配合模板 ref，随就绪 / 变更自动 sync）
const designerRef = ref(null)
const flowJson = useFlowJson(designerRef)
// 验证 useFlowJson 响应式：json 刷新后暴露长度供冒烟断言
watch(() => flowJson.json.value, (v) => {
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_JSON_LEN__ = v ? v.length : 0
})

// ⑤ @ready：暴露底层 lf 实例（高级定制；本 demo 供冒烟测试经此程序化触发画布变更）+ 同步 useFlowJson
function onDesignerReady(payload) {
  window.__WF_LF__ = (payload && payload.lf) || null
  flowJson.sync()
}
// ⑥ @change：画布图数据变更（基于 history:change），带惰性 getJson / getGraphData；同步 useFlowJson
function onDesignerChange(payload) {
  flowJson.sync()
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_CHANGE__ = true
  console.log('[validate] change，dirty =', payload && payload.dirty)
}
// ⑥ @dirty：未保存状态翻转（首次变更 false→true，保存成功 true→false）
function onDesignerDirty(d) {
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_DIRTY__ = !!d
  console.log('[validate] dirty 翻转 =', d)
}
// ⑦ @before-save：保存提交前可改写 json / 取消保存（同步事件）
function onBeforeSave(payload) {
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_BEFORE_SAVE__ = !!(payload && typeof payload.setJson === 'function' && typeof payload.preventDefault === 'function')
  console.log('[validate] before-save，json 长度 =', payload && payload.json && payload.json.length)
}
// ⑧ @validate-error：基础信息校验失败（保存 / 切步骤 / 命令式 validate 触发），透出来源与无效字段
function onValidateError(payload) {
  window.__WF_VALIDATE_VALIDATE_ERROR__ = (payload && payload.source) || ''
  console.log('[validate] validate-error，source =', payload && payload.source, 'fields =', payload && payload.fields)
}

const columns = [
  { title: '流程名称', dataIndex: 'flowName', key: 'flowName', ellipsis: true },
  { title: '设计器模型', key: 'model', width: 140 },
  { title: '流程ID', dataIndex: 'id', key: 'id', ellipsis: true },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 190 },
  { title: '操作', key: 'action', width: 300 }
]

const view = ref('list')
const flows = ref([])
// create | edit | preview
const designMode = ref('create')
const designProps = ref({ definitionId: null, disabled: false, onlyDesignShow: false })
// 每次打开自增，配合 :key 强制 FlowDesigner 重建，确保切换流程时重新 queryDef 加载
const designKey = ref(0)

const designModeText = computed(() => {
  if (designMode.value === 'create') return '新建流程'
  if (designMode.value === 'edit') return '修改流程'
  if (designMode.value === 'validate') return '扩展能力验证 · initialJson + node-form-extra 插槽 + customNodes/extraExtensions/lfOptions + onBeforeUse/onRegister 钩子 + paletteNodes + before-save/change/dirty/validate-error 事件 + useFlowJson'
  return '预览流程（只读）'
})

function rowKey(row) {
  return row.id
}
function isClassics(modelValue) {
  return modelValue === 'CLASSICS'
}

function refresh() {
  flows.value = listFlows()
}
refresh()

function openDesigner(mode, props) {
  designMode.value = mode
  designProps.value = props
  designKey.value++
  view.value = 'design'
}
function onCreate() {
  openDesigner('create', { definitionId: null, disabled: false, onlyDesignShow: false })
}
function onEdit(row) {
  openDesigner('edit', { definitionId: row.id, disabled: false, onlyDesignShow: false })
}
function onPreview(row) {
  openDesigner('preview', { definitionId: row.id, disabled: true, onlyDesignShow: true })
}

// 扩展能力验证：一次性启用 initialJson(②) + customNodes/extraExtensions/lfOptions(③)，
// 直达画布（onlyDesignShow）且可编辑（disabled:false），点击节点即可在属性面板看到 node-form-extra 插槽(①)
function onValidateExt() {
  // 复测前复位钩子 / 事件标记，避免跨导航的旧值干扰断言
  window.__WF_VALIDATE_ON_BEFORE_USE__ = false
  window.__WF_VALIDATE_ON_REGISTER__ = false
  window.__WF_VALIDATE_CHANGE__ = false
  window.__WF_VALIDATE_DIRTY__ = false
  window.__WF_VALIDATE_BEFORE_SAVE__ = false
  window.__WF_VALIDATE_JSON_LEN__ = 0
  openDesigner('validate', {
    definitionId: null,
    disabled: false,
    onlyDesignShow: true,
    initialJson: validateInitialJson,
    customNodes: validateCustomNodes,
    extraExtensions: validateExtraExtensions,
    lfOptions: validateLfOptions,
    onBeforeUse: validateOnBeforeUse,
    onRegister: validateOnRegister,
    paletteNodes: validatePaletteNodes
  })
}
function backToList() {
  view.value = 'list'
  refresh()
}

function onSaved(payload) {
  const id = (payload && (payload.data || payload.id)) || ''
  message.success('保存成功，流程ID：' + id)
}
function onClose() {
  backToList()
}

function onExport(row) {
  const json = getFlowJsonString(row.id)
  if (!json) {
    message.warning('无可导出的内容')
    return
  }
  const blob = new Blob([json], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = (row.flowName || 'flow') + '.json'
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}

function onRemove(row) {
  Modal.confirm({
    title: '提示',
    content: '确认删除流程「' + row.flowName + '」？',
    okText: '确认',
    cancelText: '取消',
    onOk() {
      removeFlow(row.id)
      refresh()
      message.success('已删除')
    }
  })
}
function onClearAll() {
  Modal.confirm({
    title: '提示',
    content: '确认清空所有 demo 流程？',
    okText: '确认',
    cancelText: '取消',
    onOk() {
      clearFlows()
      refresh()
      message.success('已清空')
    }
  })
}
</script>

<style>
.demo-root {
  height: 100vh;
  width: 100vw;
  background: #f4f6fa;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'PingFang SC', 'Helvetica Neue', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  overflow: auto;
}

/* ========== 列表视图 ========== */
.demo-list {
  --accent: #1677ff;
  --accent-dark: #0958d9;
  min-height: 100vh;
  box-sizing: border-box;
  padding-bottom: 40px;
}

/* Hero 头部 */
.demo-hero {
  background:
    radial-gradient(1100px 280px at 14% -50%, rgba(255, 255, 255, 0.28), transparent 60%),
    linear-gradient(135deg, var(--accent) 0%, var(--accent-dark) 100%);
  color: #fff;
  padding: 40px 24px 34px;
  box-shadow: 0 10px 30px rgba(9, 88, 217, 0.22);
}
.demo-hero-inner {
  max-width: 1120px;
  margin: 0 auto;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  flex-wrap: wrap;
}
.demo-hero-brand { display: flex; align-items: center; gap: 14px; }
.demo-logo {
  width: 46px;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.35);
  border-radius: 12px;
  font-weight: 800;
  font-size: 18px;
  letter-spacing: 0.5px;
}
.demo-hero-text h1 {
  margin: 0;
  font-size: 26px;
  font-weight: 800;
  letter-spacing: 0.3px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.demo-hero-sub { margin: 12px 0 0; font-size: 13.5px; color: rgba(255, 255, 255, 0.9); }
.demo-hero-chips { margin-top: 16px; display: flex; flex-wrap: wrap; gap: 8px; }
.chip {
  font-size: 12px;
  padding: 5px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
}
.ui-badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  font-size: 12px;
  font-weight: 700;
  color: var(--accent-dark);
  background: #fff;
  border-radius: 999px;
  vertical-align: middle;
}
.demo-actions { display: flex; gap: 10px; flex-wrap: wrap; }

/* 主体内容 */
.demo-main {
  max-width: 1120px;
  margin: -16px auto 0;
  padding: 0 24px;
  box-sizing: border-box;
  position: relative;
}

/* 卡片 */
.demo-card {
  background: #fff;
  border: 1px solid #eef1f6;
  border-radius: 14px;
  box-shadow: 0 6px 24px rgba(15, 23, 42, 0.06);
  padding: 18px;
  margin-bottom: 18px;
}
.demo-card-head {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 14px;
}
.head-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent), var(--accent-dark));
}
.demo-count {
  min-width: 22px;
  height: 20px;
  padding: 0 7px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: var(--accent-dark);
  background: rgba(22, 119, 255, 0.12);
  border-radius: 999px;
}
.demo-card-hint { margin-left: auto; font-size: 12px; font-weight: 400; color: #94a3b8; }

/* 用法代码块（深色） */
.demo-usage-code {
  margin: 0;
  background: #0f172a;
  border-radius: 10px;
  padding: 14px 16px;
  color: #e2e8f0;
  font-family: 'SF Mono', Monaco, Menlo, Consolas, monospace;
  font-size: 12.5px;
  line-height: 1.7;
  white-space: pre;
  overflow-x: auto;
}

.demo-table {
  border-radius: 10px;
  overflow: hidden;
}
.demo-empty {
  margin: 32px 0;
}

/* ========== 设计器视图 ========== */
.demo-design {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.demo-design-bar {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 12px;
  height: 48px;
  padding: 0 16px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  z-index: 10;
}
.demo-design-mode {
  font-size: 13px;
  font-weight: 600;
  padding: 3px 12px;
  border-radius: 20px;
  background: #e6f4ff;
  color: #1677ff;
}
.demo-design-mode.preview {
  background: #f6ffed;
  color: #52c41a;
}
.demo-design-mode.validate {
  background: #f3e8ff;
  color: #7c3aed;
}
.demo-design-canvas {
  flex: 1;
  position: relative;
  overflow: hidden;
}
</style>
