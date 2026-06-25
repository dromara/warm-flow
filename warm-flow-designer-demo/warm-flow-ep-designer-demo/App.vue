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
              <h1>Warm-Flow Designer<span class="ui-badge ui-badge--ep">Element Plus</span></h1>
            </div>
            <p class="demo-hero-sub">轻量级工作流设计器 · npm 组件库消费示例 · 经典 + 仿钉钉双模式</p>
            <div class="demo-hero-chips">
              <span class="chip">UI 适配器可插拔 · EP / antd</span>
              <span class="chip">localStorage 持久化</span>
              <span class="chip">保存 / 修改 / 预览闭环</span>
            </div>
          </div>
          <div class="demo-actions">
            <el-button type="primary" size="large" @click="onCreate">+ 新建流程</el-button>
            <el-button size="large" @click="refresh">刷新</el-button>
            <el-button type="danger" plain size="large" :disabled="!flows.length" @click="onClearAll">清空</el-button>
          </div>
        </div>
      </header>

      <main class="demo-main">
        <!-- 用法说明（3 步集成） -->
        <section class="demo-card demo-usage">
          <div class="demo-card-head"><span class="head-dot"></span>用法 · 3 步集成（Element Plus）</div>
          <pre class="demo-usage-code">{{ usageCode }}</pre>
        </section>

        <!-- 流程列表 -->
        <section class="demo-card">
          <div class="demo-card-head">
            <span class="head-dot"></span>流程列表
            <span class="demo-count">{{ flows.length }}</span>
            <span class="demo-card-hint">第三方 import 消费 dist-lib，数据持久化于浏览器 localStorage</span>
          </div>

          <el-table v-if="flows.length" :data="flows" class="demo-table" stripe>
            <el-table-column prop="flowName" label="流程名称" min-width="180" show-overflow-tooltip />
            <el-table-column label="设计器模型" width="140">
              <template #default="{ row }">
                <el-tag :type="isClassics(row.modelValue) ? 'success' : ''" effect="light" round>
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
        </section>
      </main>
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

// 顶部「用法」代码片段：演示第三方 3 步集成（与 antd4-demo 仅适配器不同）
const usageCode = `// main.ts
import { WarmFlowDesigner, setUiAdapter, setDataProvider } from '@dromara/warm-flow-designer'
import { elementPlusAdapter } from '@dromara/warm-flow-designer/element-plus'   // ← Element Plus 适配器子入口
import '@dromara/warm-flow-designer/style'

setUiAdapter(elementPlusAdapter)   // ① 选 UI 适配器（须在渲染 FlowDesigner 前）
setDataProvider(myProvider)        // ② 注入数据源（自定义后端 / mock）
app.use(ElementPlus).use(WarmFlowDesigner)   // ③ 注册后模板里直接用 <FlowDesigner />`

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
  --accent: #409eff;
  --accent-dark: #2b7de9;
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
  box-shadow: 0 10px 30px rgba(43, 125, 233, 0.22);
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
  background: rgba(64, 158, 255, 0.12);
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
