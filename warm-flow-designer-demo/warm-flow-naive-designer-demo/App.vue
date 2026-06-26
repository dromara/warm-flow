<template>
  <!-- n-config-provider：Naive 推荐的根上下文（主题 / 组件配置），消除 n-data-table 等组件的 config-provider 注入告警 -->
  <n-config-provider>
  <div class="demo-root">
    <!-- ========== 列表视图 ========== -->
    <div v-if="view === 'list'" class="demo-list">
      <!-- Hero 头部 -->
      <header class="demo-hero">
        <div class="demo-hero-inner">
          <div class="demo-hero-text">
            <div class="demo-hero-brand">
              <span class="demo-logo">WF</span>
              <h1>Warm-Flow Designer<span class="ui-badge ui-badge--naive">Naive UI</span></h1>
            </div>
            <p class="demo-hero-sub">轻量级工作流设计器 · npm 组件库消费示例 · 经典 + 仿钉钉双模式</p>
            <div class="demo-hero-chips">
              <span class="chip">UI 适配器可插拔 · EP / antd / naive</span>
              <span class="chip">localStorage 持久化</span>
              <span class="chip">保存 / 修改 / 预览闭环</span>
            </div>
          </div>
          <div class="demo-actions">
            <n-button type="primary" size="large" @click="onCreate">+ 新建流程</n-button>
            <n-button size="large" @click="onShowcase">集成案例</n-button>
            <n-button type="primary" ghost size="large" @click="onValidateExt">扩展能力验证</n-button>
            <n-button size="large" @click="refresh">刷新</n-button>
            <n-button type="error" ghost size="large" :disabled="!flows.length" @click="onClearAll">清空</n-button>
          </div>
        </div>
      </header>

      <main class="demo-main">
        <!-- 用法说明（3 步集成） -->
        <section class="demo-card demo-usage">
          <div class="demo-card-head"><span class="head-dot"></span>用法 · 3 步集成（Naive UI）</div>
          <pre class="demo-usage-code">{{ usageCode }}</pre>
        </section>

        <!-- 流程列表 -->
        <section class="demo-card">
          <div class="demo-card-head">
            <span class="head-dot"></span>流程列表
            <span class="demo-count">{{ flows.length }}</span>
            <span class="demo-card-hint">第三方 import 消费 dist-lib，数据持久化于浏览器 localStorage</span>
          </div>

          <n-data-table
            v-if="flows.length"
            :data="flows"
            :columns="columns"
            :row-key="(row) => row.id"
            class="demo-table"
          />

          <n-empty v-else description="暂无流程，点击「新建流程」开始设计" class="demo-empty" />
        </section>
      </main>
    </div>

    <!-- ========== 设计器视图 ========== -->
    <div v-else class="demo-design">
      <div class="demo-design-bar">
        <n-button text @click="backToList">← 返回列表</n-button>
        <span class="demo-design-mode" :class="designMode">{{ designModeText }}</span>
      </div>
      <div class="demo-design-body">
        <div class="demo-design-canvas">
          <!-- 对象式消费：designProps 是细粒度 props 的集合，v-bind 直接摊开绑定到对应 prop -->
          <FlowDesigner
            ref="designerRef"
            :key="designKey"
            v-model:json="modelJson"
            v-bind="designProps"
            @saved="onSaved"
            @close="onClose"
            @ready="onDesignerReady"
            @before-save="onBeforeSave"
            @change="onDesignerChange"
            @dirty="onDesignerDirty"
            @validate-error="onValidateError"
            @node-click="onNodeClick"
          >
            <!-- node-form-extra 插槽透传验证：仅扩展验证模式注入 -->
            <template #node-form-extra="{ form, disabled }">
              <wf-form-item v-if="designMode === 'validate' && form" label="扩展字段(验证)：">
                <wf-input v-model="form.validateExt" :disabled="disabled" placeholder="node-form-extra 插槽已生效" />
              </wf-form-item>
            </template>
            <!-- 加载态 / 空态插槽自定义（demo 演示，默认回退已够用） -->
            <template #loading><div class="demo-state">流程加载中…</div></template>
            <template #empty><div class="demo-state">没有找到流程定义</div></template>
          </FlowDesigner>
        </div>

        <!-- 集成案例：右侧实时面板（实时 JSON + 事件日志 + 命令式工具条） -->
        <aside v-if="designMode === 'showcase'" class="demo-showcase-panel">
          <div class="sc-section">
            <div class="sc-title">命令式 API（useFlowDesigner）</div>
            <div class="sc-cmds">
              <n-button v-for="c in showcaseCommands" :key="c.key" size="small" @click="c.run">{{ c.label }}</n-button>
            </div>
            <div class="sc-dirty">未保存改动：<b :class="{ 'is-dirty': flowJson.dirty.value }">{{ flowJson.dirty.value ? '是' : '否' }}</b></div>
          </div>

          <div class="sc-section sc-grow">
            <div class="sc-title">事件日志（{{ eventLog.length }}）</div>
            <div class="sc-log">
              <div v-if="!eventLog.length" class="sc-empty">操作画布 / 点击节点 / 保存，事件将实时出现在这里</div>
              <div v-for="(e, i) in eventLog" :key="i" class="sc-log-item">
                <span class="sc-log-t">{{ e.t }}</span>
                <span class="sc-log-name">{{ e.name }}</span>
                <span class="sc-log-detail">{{ e.detail }}</span>
              </div>
            </div>
          </div>

          <div class="sc-section sc-grow">
            <div class="sc-title">实时流程 JSON（useFlowJson · {{ (flowJson.json.value || '').length }} 字符）</div>
            <pre class="sc-json">{{ flowJson.json.value || '（暂无）' }}</pre>
          </div>
        </aside>
      </div>
    </div>
  </div>
  </n-config-provider>
</template>

<script setup>
import { computed, h, ref, watch } from 'vue'
import { createDiscreteApi, NButton, NTag } from 'naive-ui'
import { FlowDesigner, useFlowJson, useFlowDesigner } from '@dromara/warm-flow-designer'
// 组件库扩展能力验证用：消费方自带的 LogicFlow 基类 / 官方扩展（库已把它们 externalize 为 peer，单实例共享）
import { RectNode, RectNodeModel } from '@logicflow/core'
import { Control } from '@logicflow/extension'
import { listFlows, getFlowJsonString, removeFlow, clearFlows } from './demoProvider'

// Naive 的 message / dialog 为上下文式 API；脱上下文用 createDiscreteApi（demo 自用，与库适配器各自独立实例）
const { message, dialog } = createDiscreteApi(['message', 'dialog'])

// 顶部「用法」代码片段：第三方 3 步集成（与 ep / antd demo 仅适配器不同）
const usageCode = `// main.ts
import { WarmFlowDesigner, setUiAdapter, setDataProvider } from '@dromara/warm-flow-designer'
import { naiveAdapter } from '@dromara/warm-flow-designer/naive'   // ← Naive UI 适配器子入口
import '@dromara/warm-flow-designer/style'

setUiAdapter(naiveAdapter)        // ① 选 UI 适配器（须在渲染 FlowDesigner 前）
setDataProvider(myProvider)       // ② 注入数据源（自定义后端 / mock）
app.use(naive).use(WarmFlowDesigner)   // ③ 注册后模板里直接用 <FlowDesigner />`

// ===== 组件库扩展能力验证夹具 =====
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
const validateCustomNodes = [{ type: 'validate-custom-node', view: RectNode, model: RectNodeModel }]
const validateExtraExtensions = [Control]
const validateLfOptions = { grid: { size: 20, type: 'dot' } }
const validateOnBeforeUse = (LF) => {
  window.__WF_VALIDATE_ON_BEFORE_USE__ = !!(LF && typeof LF.use === 'function')
  console.log('[validate] onBeforeUse 调用，LogicFlow.use =', typeof LF?.use)
}
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
const validatePaletteNodes = {
  flowNodes: [
    { type: 'start', label: '开始(自定义)' },
    { type: 'between', label: '审批(自定义)', properties: { collaborativeWay: '1' } },
    { type: 'end', label: '结束(自定义)' }
  ],
  gatewayNodes: []
}

// useFlowDesigner（命令式 API，空安全）+ useFlowJson（流程 json 响应式只读视图），共享同一模板 ref
const flowDesigner = useFlowDesigner()
const designerRef = flowDesigner.designerRef
const flowJson = useFlowJson(designerRef)
watch(() => flowJson.json.value, (v) => {
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_JSON_LEN__ = v ? v.length : 0
})

// v-model:json：受控 json（初始注入 + 变更回写）。每次打开重置避免跨模式串数据
const modelJson = ref(null)
watch(modelJson, (v) => {
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_VMODEL_LEN__ = v ? String(v).length : 0
})
function onNodeClick(payload) {
  if (designMode.value === 'showcase') logEvent('node-click', `${payload?.type} #${payload?.id}`)
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_NODE_CLICK__ = (payload && payload.type) || ''
  console.log('[validate] node-click，id =', payload && payload.id, 'type =', payload && payload.type)
}

// ===== 集成案例（showcase）：把扩展点做成可见的实时面板 =====
const eventLog = ref([])
function logEvent(name, detail) {
  eventLog.value.unshift({ t: new Date().toLocaleTimeString(), name, detail: detail || '' })
  if (eventLog.value.length > 30) eventLog.value.pop()
}
const showcaseCommands = [
  { key: 'zoomIn', label: '放大', run: () => flowDesigner.zoomIn() },
  { key: 'zoomOut', label: '缩小', run: () => flowDesigner.zoomOut() },
  { key: 'fitView', label: '适应', run: () => flowDesigner.fitView() },
  { key: 'undo', label: '撤销', run: () => flowDesigner.undo() },
  { key: 'redo', label: '重做', run: () => flowDesigner.redo() },
  { key: 'downloadJson', label: '导出JSON', run: () => flowDesigner.downloadJson() },
  { key: 'resetDirty', label: '标记已存', run: () => flowDesigner.resetDirty() }
]

function onDesignerReady(payload) {
  window.__WF_LF__ = (payload && payload.lf) || null
  flowJson.sync()
  if (designMode.value === 'showcase') logEvent('ready', '画布初始化完成')
}
function onDesignerChange(payload) {
  flowJson.sync()
  if (designMode.value === 'showcase') logEvent('change', `dirty=${payload?.dirty}`)
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_CHANGE__ = true
  console.log('[validate] change，dirty =', payload && payload.dirty)
}
function onDesignerDirty(d) {
  if (designMode.value === 'showcase') logEvent('dirty', String(!!d))
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_DIRTY__ = !!d
  console.log('[validate] dirty 翻转 =', d)
}
function onBeforeSave(payload) {
  if (designMode.value === 'showcase') logEvent('before-save', `json 长度 ${payload?.json?.length || 0}`)
  if (designMode.value !== 'validate') return
  window.__WF_VALIDATE_BEFORE_SAVE__ = !!(payload && typeof payload.setJson === 'function' && typeof payload.preventDefault === 'function')
  console.log('[validate] before-save，json 长度 =', payload && payload.json && payload.json.length)
}
function onValidateError(payload) {
  if (designMode.value === 'showcase') logEvent('validate-error', `source=${payload?.source}`)
  window.__WF_VALIDATE_VALIDATE_ERROR__ = (payload && payload.source) || ''
  console.log('[validate] validate-error，source =', payload && payload.source, 'fields =', payload && payload.fields)
}

const view = ref('list')
const flows = ref([])
const designMode = ref('create')
const designProps = ref({ definitionId: null, disabled: false, onlyDesignShow: false })
const designKey = ref(0)

const designModeText = computed(() => {
  if (designMode.value === 'create') return '新建流程'
  if (designMode.value === 'edit') return '修改流程'
  if (designMode.value === 'showcase') return '集成案例 · 右侧实时面板（useFlowJson 实时 JSON + 事件日志 + useFlowDesigner 命令式工具条）'
  if (designMode.value === 'validate') return '扩展能力验证 · initialJson + node-form-extra 插槽 + customNodes/extraExtensions/lfOptions + onBeforeUse/onRegister 钩子 + paletteNodes + before-save/change/dirty/validate-error/node-click 事件 + useFlowJson + v-model:json'
  return '预览流程（只读）'
})

function isClassics(modelValue) {
  return modelValue === 'CLASSICS'
}

// n-data-table 列定义（render 函数式单元格）
const columns = [
  { title: '流程名称', key: 'flowName', ellipsis: { tooltip: true } },
  {
    title: '设计器模型',
    key: 'model',
    width: 120,
    render: (row) => h(NTag, { type: isClassics(row.modelValue) ? 'success' : 'info', size: 'small', round: true }, () => isClassics(row.modelValue) ? '经典模式' : '仿钉钉模式')
  },
  { title: '流程ID', key: 'id', ellipsis: { tooltip: true } },
  { title: '更新时间', key: 'updateTime', width: 180 },
  {
    title: '操作',
    key: 'action',
    width: 260,
    render: (row) => h('div', { style: 'display:flex;gap:4px' }, [
      h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => onEdit(row) }, () => '修改'),
      h(NButton, { text: true, type: 'success', size: 'small', onClick: () => onPreview(row) }, () => '预览'),
      h(NButton, { text: true, size: 'small', onClick: () => onExport(row) }, () => '导出JSON'),
      h(NButton, { text: true, type: 'error', size: 'small', onClick: () => onRemove(row) }, () => '删除')
    ])
  }
]

function refresh() {
  flows.value = listFlows()
}
refresh()

function openDesigner(mode, props) {
  designMode.value = mode
  designProps.value = props
  // v-model:json 跨模式重置，避免上一会话的 json 串入新会话
  modelJson.value = null
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

// 集成案例入口：直达画布，开启全部扩展点；右侧实时面板展示 useFlowJson / 事件日志 / useFlowDesigner 命令式工具条
function onShowcase() {
  eventLog.value = []
  openDesigner('showcase', {
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

function onValidateExt() {
  window.__WF_VALIDATE_ON_BEFORE_USE__ = false
  window.__WF_VALIDATE_ON_REGISTER__ = false
  window.__WF_VALIDATE_CHANGE__ = false
  window.__WF_VALIDATE_DIRTY__ = false
  window.__WF_VALIDATE_BEFORE_SAVE__ = false
  window.__WF_VALIDATE_JSON_LEN__ = 0
  window.__WF_VALIDATE_VMODEL_LEN__ = 0
  window.__WF_VALIDATE_NODE_CLICK__ = ''
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
  dialog.warning({
    title: '提示',
    content: '确认删除流程「' + row.flowName + '」？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      removeFlow(row.id)
      refresh()
      message.success('已删除')
    }
  })
}

function onClearAll() {
  dialog.warning({
    title: '提示',
    content: '确认清空所有 demo 流程？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
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
}

/* ========== 列表视图 ========== */
.demo-list {
  --accent: #18a058;
  --accent-dark: #0c7a43;
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
  box-shadow: 0 10px 30px rgba(12, 122, 67, 0.22);
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
  background: rgba(24, 160, 88, 0.12);
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
  background: #e8f8f0;
  color: #0c7a43;
}

.demo-design-mode.preview {
  background: #f0f9eb;
  color: #67c23a;
}

.demo-design-mode.validate {
  background: #f3e8ff;
  color: #7c3aed;
}

.demo-design-body {
  flex: 1;
  display: flex;
  min-height: 0;
}

.demo-design-canvas {
  flex: 1;
  position: relative;
  overflow: hidden;
}

/* ========== 集成案例右侧实时面板 ========== */
.demo-showcase-panel {
  width: 360px;
  flex: 0 0 360px;
  border-left: 1px solid #e2e8f0;
  background: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.sc-section {
  padding: 12px 14px;
  border-bottom: 1px solid #eef1f6;
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.sc-section.sc-grow { flex: 1; }
.sc-title {
  font-size: 12.5px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}
.sc-cmds { display: flex; flex-wrap: wrap; gap: 6px; }
.sc-dirty { margin-top: 10px; font-size: 12.5px; color: #64748b; }
.sc-dirty .is-dirty { color: #d03050; }
.sc-log {
  flex: 1;
  overflow-y: auto;
  font-size: 12px;
  line-height: 1.6;
}
.sc-empty { color: #94a3b8; font-size: 12px; }
.sc-log-item { display: flex; gap: 6px; padding: 2px 0; border-bottom: 1px dashed #f1f5f9; }
.sc-log-t { color: #94a3b8; flex: 0 0 auto; }
.sc-log-name { color: #18a058; font-weight: 600; flex: 0 0 auto; }
.sc-log-detail { color: #64748b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.sc-json {
  flex: 1;
  margin: 0;
  overflow: auto;
  background: #0f172a;
  color: #e2e8f0;
  border-radius: 8px;
  padding: 10px 12px;
  font-family: 'SF Mono', Monaco, Menlo, Consolas, monospace;
  font-size: 11px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}
.demo-state { font-size: 14px; color: #909399; }

@media (max-width: 900px) {
  .demo-design-body { flex-direction: column; }
  .demo-showcase-panel { width: 100%; flex: 0 0 auto; max-height: 40vh; border-left: none; border-top: 1px solid #e2e8f0; }
}
</style>
