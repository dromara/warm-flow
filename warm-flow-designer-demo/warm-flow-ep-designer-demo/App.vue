<template>
  <div class="demo-root">
    <!-- ========== 列表视图 ========== -->
    <div v-if="view === 'list'" class="demo-list">
      <header class="demo-header">
        <div class="demo-title">
          <h1>Warm-Flow Designer <span style="display:inline-block;margin-left:8px;padding:2px 10px;font-size:12px;font-weight:600;color:#fff;background:#409eff;border-radius:10px;vertical-align:middle;">Element Plus</span></h1>
          <p>npm 组件库消费示例 · UI 库：Element Plus · 保存 / 修改 / 预览</p>
        </div>
        <div class="demo-actions">
          <el-button type="primary" @click="onCreate">+ 新建流程</el-button>
          <el-button @click="refresh">刷新</el-button>
          <el-button type="danger" plain :disabled="!flows.length" @click="onClearAll">清空</el-button>
        </div>
      </header>

      <el-alert type="info" :closable="false" class="demo-tip">
        本页以「第三方 import」方式消费 <code>@dromara/warm-flow-designer</code>（alias → dist-lib 产物），
        通过 <code>setDataProvider(createDemoProvider())</code> 注入带 localStorage 持久化的数据源；
        保存后回传的流程会持久化到浏览器，可再次「修改 / 预览 / 导出」。
      </el-alert>

      <el-table v-if="flows.length" :data="flows" class="demo-table" border stripe>
        <el-table-column prop="flowName" label="流程名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="设计器模型" width="140">
          <template #default="{ row }">
            <el-tag :type="isClassics(row.modelValue) ? 'success' : ''" effect="light">
              {{ isClassics(row.modelValue) ? '经典模式' : '仿钉钉模式' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="流程ID" min-width="170" show-overflow-tooltip />
        <el-table-column prop="updateTime" label="更新时间" width="190" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="onEdit(row)">修改</el-button>
            <el-button size="small" type="success" link @click="onPreview(row)">预览</el-button>
            <el-button size="small" link @click="onExport(row)">导出JSON</el-button>
            <el-button size="small" type="danger" link @click="onRemove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="暂无流程，点击「新建流程」开始设计" />
    </div>

    <!-- ========== 设计器视图 ========== -->
    <div v-else class="demo-design">
      <div class="demo-design-bar">
        <el-button text @click="backToList">← 返回列表</el-button>
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
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { FlowDesigner } from '@dromara/warm-flow-designer'
import { listFlows, getFlowJsonString, removeFlow, clearFlows } from './demoProvider'

const view = ref('list')
const flows = ref([])
// create | edit | preview
const designMode = ref('create')
const designProps = ref({ definitionId: null, disabled: false, onlyDesignShow: false })
// 每次打开都自增：配合 :key 强制 FlowDesigner 重建，确保切换流程时重新 queryDef 加载
const designKey = ref(0)

const designModeText = computed(() => {
  if (designMode.value === 'create') return '新建流程'
  if (designMode.value === 'edit') return '修改流程'
  return '预览流程（只读）'
})

function isClassics(modelValue) {
  return modelValue === 'CLASSICS'
}

function refresh() {
  flows.value = listFlows()
}
refresh()

function openDesigner(mode, designerProps) {
  designMode.value = mode
  designProps.value = designerProps
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

// FlowDesigner 保存成功回调（向后兼容新增的 emit('saved')）：拿到回传的流程 id
function onSaved(payload) {
  const id = (payload && (payload.data || payload.id)) || ''
  ElMessage.success('保存成功，流程ID：' + id)
}

// 保存成功 500ms 后 FlowDesigner 会 emit('close')，此处回到列表并刷新
function onClose() {
  backToList()
}

function onExport(row) {
  const json = getFlowJsonString(row.id)
  if (!json) {
    ElMessage.warning('无可导出的内容')
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
  ElMessageBox.confirm('确认删除流程「' + row.flowName + '」？', '提示', { type: 'warning' })
    .then(() => {
      removeFlow(row.id)
      refresh()
      ElMessage.success('已删除')
    })
    .catch(() => {})
}

function onClearAll() {
  ElMessageBox.confirm('确认清空所有 demo 流程？', '提示', { type: 'warning' })
    .then(() => {
      clearFlows()
      refresh()
      ElMessage.success('已清空')
    })
    .catch(() => {})
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

.demo-tip {
  margin-bottom: 18px;
  border-radius: 10px;
  line-height: 1.7;
}

.demo-tip code {
  background: rgba(64, 158, 255, 0.12);
  color: #2b7de9;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.demo-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
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
  background: #ecf5ff;
  color: #2b7de9;
}

.demo-design-mode.preview {
  background: #f0f9eb;
  color: #67c23a;
}

.demo-design-canvas {
  flex: 1;
  position: relative;
  overflow: hidden;
}
</style>
