<template>
  <div class="diagram-sidebar">
    <div class="sidebar-header">
      <span class="sidebar-title">基础节点</span>
    </div>
    <div class="sidebar-content">
      <!-- 基础流程节点组 -->
      <div class="node-group">
        <div
          v-for="item in flowNodes"
          :key="item.type"
          class="sidebar-item"
          :class="[`item-${item.type}`]"
          @mousedown="handleDragInNode(item)"
        >
          <div class="item-icon-wrap" v-html="item.icon"></div>
          <span class="item-label">{{ item.label }}</span>
        </div>
      </div>

      <!-- 分隔线 -->
      <div class="sidebar-divider"></div>

      <!-- 网关节点组标题 -->
      <div class="group-header">
        <span class="group-title">网关节点</span>
      </div>

      <!-- 网关节点组 -->
      <div class="node-group">
        <div
          v-for="item in gatewayNodes"
          :key="item.type"
          class="sidebar-item"
          :class="[`item-${item.type}`]"
          @mousedown="handleDragInNode(item)"
        >
          <div class="item-icon-wrap" v-html="item.icon"></div>
          <span class="item-label">{{ item.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  startIcon, betweenIcon, endIcon,
  serialIcon, parallelIcon, inclusiveIcon,
} from '@/components/design/classics/js/sidebarIcons.js'

const emit = defineEmits(['dragInNode'])

// 基础节点（开始/中间/结束）
const flowNodes = [
  { type: 'start', text: '开始', label: '开始', icon: startIcon },
  { type: 'between', text: '中间节点', label: '中间节点', icon: betweenIcon, properties: { collaborativeWay: '1' } },
  { type: 'end', text: '结束', label: '结束', icon: endIcon },
]

// 网关节点（互斥/并行/包含）
const gatewayNodes = [
  { type: 'serial', text: '', label: '互斥网关', icon: serialIcon, properties: {} },
  { type: 'parallel', text: '', label: '并行网关', icon: parallelIcon, properties: {} },
  { type: 'inclusive', text: '', label: '包含网关', icon: inclusiveIcon, properties: {} },
]

function handleDragInNode(item) {
  emit('dragInNode', item.type, item.properties, item.text || {})
}
</script>

<style scoped>
.diagram-sidebar {
  position: absolute;
  left: 12px;
  top: 70px;
  width: 76px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--wf-border-light, rgba(226, 232, 240, 0.8));
  box-shadow:
    0 4px 24px rgba(0, 0, 0, 0.06),
    0 1px 4px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  z-index: 10;
  user-select: none;
  overflow: hidden;
  transition: box-shadow 0.25s ease;
}

.diagram-sidebar:hover {
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.08),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

/* ====== 头部 ====== */
.sidebar-header {
  padding: 12px 4px 8px;
  border-bottom: 1px solid var(--wf-border-light, #f1f5f9);
}

.sidebar-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--wf-text-primary, #303133);
  text-align: center;
  letter-spacing: 2px;
}

/* ====== 内容区 ====== */
.sidebar-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 6px 10px;

  &::-webkit-scrollbar {
    width: 3px;
  }
  &::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 3px;
  }
}

.node-group {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* ====== 分隔线 ====== */
.sidebar-divider {
  height: 1px;
  margin: 8px 8px;
  background: linear-gradient(90deg, transparent, var(--wf-border-light, #e2e8f0) 30%, var(--wf-border-light, #e2e8f0) 70%, transparent);
}

/* ====== 网关分组标题 ====== */
.group-header {
  padding: 6px 0 4px;
}

.group-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--wf-text-primary, #303133);
  text-align: center;
  letter-spacing: 2px;
}

/* ====== 节点项 ====== */
.sidebar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 7px 4px;
  cursor: grab;
  border-radius: 10px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.sidebar-item:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(99, 102, 241, 0.06));
  transform: translateY(-1px);

  .item-icon-wrap {
    transform: scale(1.08);
  }

  .item-label {
    color: var(--wf-primary, #409eff);
    font-weight: 500;
  }
}

.sidebar-item:active {
  cursor: grabbing;
  transform: translateY(0) scale(0.97);
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12), rgba(99, 102, 241, 0.08));
}

/* ====== 图标容器 ====== */
.item-icon-wrap {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 9px;
  background: #f8fafc;
  transition: all 0.2s ease;
  position: relative;

  /* v-html 渲染的 SVG 自适应容器 */
  :deep(svg) {
    width: 100%;
    height: 100%;
    display: block;
  }
}

/* 各类型节点的图标底色 */
.item-start .item-icon-wrap   { background: linear-gradient(135deg, #ecfdf5, #d1fae5); }
.item-between .item-icon-wrap { background: linear-gradient(135deg, #eff6ff, #dbeafe); }
.item-end .item-icon-wrap     { background: linear-gradient(135deg, #fef2f2, #fee2e2); }
.item-serial .item-icon-wrap  { background: linear-gradient(135deg, #fafafa, #f1f5f9); }
.item-parallel .item-icon-wrap{ background: linear-gradient(135deg, #fafafa, #f1f5f9); }
.item-inclusive .item-icon-wrap { background: linear-gradient(135deg, #fafafa, #f1f5f9); }

/* ====== 标签文字 ====== */
.item-label {
  font-size: 12px;
  color: var(--wf-text-regular, #606266);
  text-align: center;
  margin-top: 5px;
  line-height: 1.3;
  word-break: break-word;
  max-width: 68px;
  transition: all 0.2s ease;
  letter-spacing: 0.3px;
}

/* ====== 暗黑模式（统一走 wf-* 变量） ====== */
html.dark .diagram-sidebar {
  background: rgba(30, 30, 35, 0.92);
  border-color: var(--wf-border-color, rgba(60, 60, 70, 0.6));
  box-shadow:
    0 4px 24px rgba(0, 0, 0, 0.3),
    0 1px 4px rgba(0, 0, 0, 0.2);
}

html.dark .diagram-sidebar:hover {
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.2);
}

html.dark .sidebar-header,
html.dark .group-header {
  border-bottom-color: var(--wf-border-color, #333);
}

html.dark .sidebar-title,
html.dark .group-title {
  color: var(--wf-text-primary, #e0e0e0);
}

html.dark .sidebar-divider {
  background: linear-gradient(90deg, transparent, var(--wf-border-color, #333) 30%, var(--wf-border-color, #333) 70%, transparent);
}

html.dark .item-icon-wrap {
  background: var(--wf-bg-color, #222) !important;
}

/* 暗黑模式下 SVG 图标内部背景色切换 */
html.dark .diagram-sidebar {
  --wf-icon-bg: #1e2028;
}

html.dark .sidebar-item:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(99, 102, 241, 0.1));
}

html.dark .item-label {
  color: var(--wf-text-regular, #c0c4cc);
}

html.dark .sidebar-item:hover .item-label {
  color: var(--wf-primary, #409eff);
}

/* ====== 响应式：平板端 ====== */
@media (max-width: 1024px) {
  .diagram-sidebar {
    left: 8px;
    top: 48px;
    width: 72px;
    border-radius: 12px;
  }

  .sidebar-header {
    padding: 10px 4px 6px;
  }

  .sidebar-title {
    font-size: 12px;
    letter-spacing: 1.5px;
  }

  .group-header {
    padding: 5px 0 3px;
  }

  .group-title {
    font-size: 11px;
    letter-spacing: 1.5px;
  }

  .sidebar-item {
    padding: 7px 3px;
  }

  .item-icon-wrap {
    width: 32px;
    height: 32px;
    border-radius: 8px;
  }

  .item-label {
    font-size: 11px;
    max-width: 64px;
  }
}

/* ====== 响应式：手机端 ====== */
@media (max-width: 768px) {
  .diagram-sidebar {
    left: 6px;
    top: 42px;
    width: 62px;
    border-radius: 10px;
  }

  .sidebar-header {
    padding: 8px 2px 5px;
  }

  .sidebar-title {
    font-size: 11px;
    letter-spacing: 1px;
  }

  .group-header {
    padding: 4px 0 2px;
  }

  .group-title {
    font-size: 10px;
    letter-spacing: 1px;
  }

  .sidebar-content {
    padding: 6px 4px 8px;
  }

  .sidebar-item {
    padding: 6px 2px;
    border-radius: 8px;
  }

  .item-icon-wrap {
    width: 28px;
    height: 28px;
    border-radius: 7px;
  }

  .item-label {
    font-size: 10px;
    max-width: 56px;
    margin-top: 4px;
  }

  .sidebar-divider {
    margin: 6px 6px;
  }
}
</style>
