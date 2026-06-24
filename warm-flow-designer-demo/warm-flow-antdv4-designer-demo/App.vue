<template>
  <div class="demo-root">
    <!-- ========== 列表视图 ========== -->
    <div v-if="view === 'list'" class="demo-list">
      <header class="demo-header">
        <div class="demo-title">
          <h1>Warm-Flow Designer <span class="ui-badge ui-badge--antd">Ant Design Vue 4</span></h1>
          <p>npm 组件库消费示例 · UI 库：Ant Design Vue 4 · 保存 / 修改 / 预览</p>
        </div>
        <div class="demo-actions">
          <a-button type="primary" @click="onCreate">+ 新建流程</a-button>
          <a-button @click="refresh">刷新</a-button>
          <a-button danger :disabled="!flows.length" @click="onClearAll">清空</a-button>
        </div>
      </header>

      <!-- 用法说明（3 步集成） -->
      <section class="demo-usage">
        <div class="demo-usage-title">用法 · 3 步集成（Ant Design Vue 4）</div>
        <pre class="demo-usage-code">{{ usageCode }}</pre>
      </section>

      <a-alert type="info" show-icon :closable="false" class="demo-tip">
        <template #message>
          本页以「第三方 import」方式消费 <code>@dromara/warm-flow-designer</code>（alias → dist-lib 产物），
          通过 <code>setUiAdapter(antdvAdapter)</code> 选 Ant Design Vue 适配器、<code>setDataProvider(createDemoProvider())</code>
          注入带 localStorage 持久化的数据源；保存后回传的流程会持久化到浏览器，可再次「修改 / 预览 / 导出」。
        </template>
      </a-alert>

      <a-table
        v-if="flows.length"
        :data-source="flows"
        :columns="columns"
        :pagination="false"
        :row-key="rowKey"
        class="demo-table"
        bordered
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
    </div>

    <!-- ========== 设计器视图 ========== -->
    <div v-else class="demo-design">
      <div class="demo-design-bar">
        <a-button type="text" @click="backToList">← 返回列表</a-button>
        <span class="demo-design-mode" :class="designMode">{{ designModeText }}</span>
      </div>
      <div class="demo-design-canvas">
        <FlowDesigner
          :key="designKey"
          :definition-id="designProps.definitionId"
          :disabled="designProps.disabled"
          :only-design-show="designProps.onlyDesignShow"
          @saved="onSaved"
          @close="onClose"
        />
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
import { computed, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { FlowDesigner } from '@dromara/warm-flow-designer'
import { listFlows, getFlowJsonString, removeFlow, clearFlows } from './demoProvider'

// 顶部「用法」代码片段：演示第三方 3 步集成（与 ep-demo 仅适配器不同）
const usageCode = `// main.ts
import { WarmFlowDesigner, setUiAdapter, setDataProvider } from '@dromara/warm-flow-designer'
import { antdvAdapter } from '@dromara/warm-flow-designer/antdv'   // ← antd 适配器子入口
import '@dromara/warm-flow-designer/style'

setUiAdapter(antdvAdapter)        // ① 选 UI 适配器（须在渲染 FlowDesigner 前）
setDataProvider(myProvider)       // ② 注入数据源（自定义后端 / mock）
app.use(Antd).use(WarmFlowDesigner)   // ③ 注册后模板里直接用 <FlowDesigner />`

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
  max-width: 1080px;
  margin: 0 auto;
  padding: 32px 24px;
  box-sizing: border-box;
}

.demo-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.demo-title h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.demo-title p {
  margin: 6px 0 0;
  font-size: 13px;
  color: #64748b;
}

.ui-badge {
  display: inline-block;
  margin-left: 8px;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  border-radius: 10px;
  vertical-align: middle;
}
.ui-badge--ep { background: #409eff; }
.ui-badge--antd { background: #1677ff; }

/* 用法代码块（深色） */
.demo-usage {
  margin-bottom: 16px;
  background: #0f172a;
  border-radius: 10px;
  padding: 14px 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.demo-usage-title {
  color: #94a3b8;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
}
.demo-usage-code {
  margin: 0;
  color: #e2e8f0;
  font-family: 'SF Mono', Monaco, Menlo, Consolas, monospace;
  font-size: 12.5px;
  line-height: 1.7;
  white-space: pre;
  overflow-x: auto;
}

.demo-tip {
  margin-bottom: 18px;
  border-radius: 10px;
  line-height: 1.7;
}
.demo-tip code {
  background: rgba(22, 119, 255, 0.12);
  color: #1677ff;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.demo-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.demo-empty {
  margin-top: 64px;
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
.demo-design-canvas {
  flex: 1;
  position: relative;
  overflow: hidden;
}
</style>
