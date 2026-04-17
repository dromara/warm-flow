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
          <div class="item-icon-wrap">
            <img :src="item.icon" :alt="item.label" class="item-icon" />
          </div>
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
          <div class="item-icon-wrap">
            <img :src="item.icon" :alt="item.label" class="item-icon" />
          </div>
          <span class="item-label">{{ item.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(['dragInNode'])

// 基础节点（开始/中间/结束）
const flowNodes = [
  {
    type: 'start',
    text: '开始',
    label: '开始',
    icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1OTQ3Mzg4IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjIwODA5IiB3aWR0aD0iMzYiIGhlaWdodD0iMzYiPjxwYXRoIGQ9Ik01MTIgMTAyNEMyMjkuMjMwNDMxIDEwMjQgMCA3OTQuNzY5NTY5IDAgNTEyUzIyOS4yMzA0MzEgMCA1MTIgMHs1MTIgMjI5LjIzMDQzMSA1MTIgNTEyLTIyOS4yMzA0MzEgNTEyLTUxMiA1MTJ6IG0wLTk1MC4zNzkwODVDMjY5Ljg4OTI1NSA3My42MjA5MTUgNzMuNjIwOTE1IDI2OS44ODkyNTUgNzMuNjIwOTE1IDUxMnMxOTYuMjY4MzQgNDM4LjM3OTA4NSA0MzguMzc5MDg1IDQzOC4zNzkwODUgNDM4LjM3OTA4NS0xOTYuMjY4MzQgNDM4LjM3OTA4NS00MzguMzc5MDg1Uzc1NC4xMTA3NDUgNzMuNjIwOTE1IDUxMiA3My42MjA5MTV6IiBmaWxsPSIjMDAwMDAwIiBwLWlkPSIyMDgxMCI+PC9wYXRoPjwvc3ZnPg==',
  },
  {
    type: 'between',
    text: '中间节点',
    label: '中间节点',
    icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1Mzc1ODI3IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjgzMTkiIHdpZHRoPSIzNiIgaGVpZ2h0PSIzNiI+PHBhdGggZD0iTTMzNS43NTE4MDQgMjQ0Ljc4MzE0N0MyODMuNjA3MDI5IDI0NC43ODMxNDcgMjQ2LjMwMzg5OSAyODQuODY5MDYgMjQ2LjE5OTI3IDMzMC41Mjk2MmwgMCAwLjAxMzIwNyAwIDAuMDEyNjQ4YzAuMDAzMjk0IDEzLjgwODQ2NSAzLjczOTc0MyAyOC4zODE3NDcgOS41Nzc0MzEgNDEuNTI2MzQyIDQuMjE1MTMyIDkuNDkxMTE1IDkuNDU1Nzk4IDE4LjIxNjY3NiAxNS44NDE3NDQgMjUuMjA2NjE0QzIzMy42NjU1ODggNDEwLjI3Mjg1MyAxODkuMjAxOTQ5IDQzMS42NDI4OCAxNjYuNzI4IDQ3My43ODE1M0wxNjUuNTE2OTYgNDc2LjA1MjU0NGwgMCAxMzYuNDA4NDgyIDM0MC40Njk2ODkgMCAwLTEzNi40MDg0OC0xLjIxMTA0My0yLjI3MDk4NGMtMjIuMTUwNzA4LTQxLjUzMjU2Ni02NS42NTIyMDQtNjIuODcyODQ4LTEwMy4yMzgyMDQtNzUuOTExMTE2IDE4LjM0ODQxOS0xOC42NTgyMTggMjMuNzYxMDQ5LTQyLjc0MDU3MyAyMy43NjY5MzItNjcuMzE0OTJsLTAtMC4wMTI2NDggMC0wLjAxMzIwN0M0MjUuMTk5NzA3IDI4NC44NjkwNjMgMzg3Ljg5NjU4MyAyNDQuNzgzMTQ3IDMzNS43NTE4MDQgMjQ0Ljc4MzE0N1pNMzAwLjE0ODUyMyAyOTMuNDA0NTIxYzIuNDEwMzI4IDAuMDA2MDU5IDUuMDU2NjUgMC4wODY1NzIgNy45NzQwMTUgMC4yNTg1MjIgMjMuMjQ0MDI5IDEuMzcwMDI5IDMxLjA2Njk5NiA1LjU1NDA1OSAzNy4wODAzMjMgOS41MjIyODMgNi4wMTMyOTggMy45NjgyMjMgMTAuMjUyNDE3IDcuNzQ1Njk5IDI2LjE0NDE4OSA4LjIwODk4MmwwLjAwNDcwNiAwIDAuMDA1Mjk1IDBjMTIuMzgzODktMC40NjMyMTUgMTguMzM5NTA2LTIuNjcxMTM1IDIyLjYxMDQ1Mi01LjE3MjE5MiAxLjczMDY0NS0xLjAxMzQ1MyAzLjE4MzgyNS0yLjA2NzAwMyA0LjY3Mjk1LTMuMDcyOTg0IDMuOTM2MDM2IDguNDM2NjY4IDYuMDQ5NjU0IDE3Ljc2MjkzOCA2LjA3MzU5NyAyNy40MTQ5ODEtMC4wODIzMjYgMjcuNDg0NjUyLTQuNzMzMzA4IDQ2LjczMjMwMi0yOS45MzQxNTQgNjIuNDgyODNsMi40NjUxNzcgMTguNTgwOTQ0YzUuMjQ1MzUxIDEuNTkyODg5IDEwLjY2NzMwNSAzLjM0MDY3MSAxNi4xNzAzNTQgNS4yNTcyMiAwLjc2ODUzNSAzLjIwNjE4MyAxLjY1NjQ5MiA3LjQxMTMwNiAyLjI1Mzc0OCAxMS44ODE3MzkgMC42MjU2NyA0LjY4MzQ1NCAwLjg3MTc0OSA5LjU1NjMzMyAwLjQ4NjAxMSAxMy4yMTUxMzktMC4zODU3MzggMy42NTg4MDYtMS41MjE3MTYgNS42MzM5NzItMS43MjExNzQgNS44MzM0NTktMTIuODA4OTg0IDEyLjgwODk1NS0zNS41NDYwMzYgMjAuMjc5MTM5LTU4LjYwODQzOCAyMC4yNzkxMzktMjMuMDYyMzkzIDAtNDUuNzk5NDQ0LTcuNDcwMTg0LTU4LjYwODQyMy0yMC4yNzkxMzktMC4xOTk0NjEtMC4xOTk0ODctMS4zMzU0NTYtMi4xNzQ2NTMtMS43MjExOTQtNS44MzM0NTktMC4zODU3MzUtMy42NTg4MDYtMC4xMzk2NjItOC41MzE2ODUgMC40ODYwMjYtMTMuMjE1MTM5IDAuNjAwNTI3LTQuNDk1MTE1IDEuNDk1NTUyLTguNzI1MDI4IDIuMjY2OTYzLTExLjkzNzQ2NyA1LjQ0ODA4Ni0xLjg5NDYwNiAxMC44MTU1NDctMy42MjQwNDIgMTYuMDEwMDczLTUuMjAxNDkxbDEuNDY5NTYxLTE5LjkwOTc1NmMtMS4xOTY1NzEtMS41MzQ1NjQtMi40MTU3ODItMi41NTExNjctMy44NzA5NTktMy42NDI4ODQtNS42MjQzNzctNC4yMTk1NjUtMTIuNDQ1MTQyLTEzLjUwMjI0NS0xNy4yNjMwNDktMjQuMzUwNjEzLTQuODE2MTg5LTEwLjg0NDQ5OS03LjgwMDAzOC0yMy4yNDAwMjMtNy44MDQ1MzYtMzMuMTYyODE4IDAuMDI5MjYtMTEuODk2MzgyIDMuMjMxMzc5LTIzLjI5OTMwNCA5LjExMTUxNi0zMy4xNDkwMzIgMS4wNTIxMTUtMC4zOTE2MTggMi4xNjE1NjYtMC44MDU1MTcgMy40MDg0ODgtMS4yMTU2MzQgNC4zODUwMTctMS40NDIyNSAxMC4zOTM5NzMtMi44MTg4ODkgMjAuODM4NzE2LTIuNzkyNjI4Wk0yNTUuNjMwNzggNDI1LjYzODEzNmMtMC4wMTg1MDIgMC4xMzQ5NjEtMC4wMzkzNTMgMC4yNjY2ODEtMC4wNTc0NDkgMC40MDIxNDgtMC43NjA5MTQgNS42OTU2MzYtMS4yMDgwMzEgMTEuODk1MjczLTAuNTUzODE3IDE4LjEwMDY3NSAwLjY1NDIxNCA2LjIwNTQwMiAyLjI5MTU2OCAxMi44ODY0MzIgNy42Mzg1MDcgMTguMjMzMzUgMTguMjUwODYxIDE4LjI1MDg4MSA0NS44NzE3NTkgMjYuMzEwMjMzIDczLjE2NzMxOCAyNi4zMTAyMzMgMjcuMjk1NTUxIDAgNTQuOTE2NDUyLTguMDU5MzUxIDczLjE2NzMwNC0yNi4zMTAyMzMgNS4zNDY5NDgtNS4zNDY5MTggNi45ODQzMi0xMi4wMjc5NDggNy42Mzg1MjItMTguMjMzMzUgMC42NTQyMDItNi4yMDU0MzEgMC4yMDcxMDYtMTIuNDA1MDM5LTAuNTUzODExLTE4LjEwMDY3NS0wLjAxNTAwMS0wLjExMjI0OC0wLjAzMjQxNC0wLjIyMTMwNy0wLjA0NzY4LTAuMzMzMjEgMjcuNzQ3NTMxIDEyLjE2ODM2IDU0LjU2Nzc0NiAyOS41OTUyNjEgNjkuMzY3MDE1IDU1LjYxNDczNGwwIDExMC41NDkyMjgtNDkuMjY4ODMyIDAgMC03Ny45NDc3MDQtMjAuNTg5OTYgMCAwIDc3Ljk0NzcwNC0xNjAuMDEzNCAwIDAtNzcuOTQ3NzA0LTIwLjU4OTk2IDAgMCA3Ny45NDc3MDQtNDguODI3NjE4IDAgMC0xMTAuNTQ5MjI4YzE0LjgyNzE1Ni0yNi4wNjg1MzYgNDEuNzIwNTQ3LTQzLjUxMjIzMiA2OS41MjM4Ni01NS42ODM2NzJ6TTIxOS45ODEgMTA3LjUxOTU3NWMtMTA5LjkzNDgyNCAwLTE5OS41MDEgODkuMTg4MzQ1LTE5OS41MDEgMTk4LjkxMTk5OWwwIDQxMS4xMzU5ODljMCAxMDkuNzIzNjU5IDg5LjU2NjE3NiAxOTguOTEyMDExIDE5OS41MDEgMTk4LjkxMjAxMWw1ODQuMDM3OTg5IDBjMTA5LjkzNDg1MyAwIDE5OS50MDEwMDEtODkuMTg4MzUxIDE5OS50MDEwMDEtMTk4LjkxMjAxMWwwLTQxMS4xMzU5ODljMC0xMDkuNzIzNjUzLTg5LjU2NjE0OC0xOTkuOTExOTk5LTE5OS50MDEwMDEtMTk4LjkxMTk5OWwtNTg0LjAzNzk4OSAwem0wIDYxLjQ0MDAwMWw1ODQuMDM3OTg5IDBjNzcuMDc0OTU1IDAgMTM4LjA2MTAwMyA2MC44Mzg5MTUgMTM4LjA2MTAwMyAxMzcuNDcxOTk4bDAgNDExLjEzNTk4OWMwIDc2LjYzMzA5NC02MC45ODYwNDggMTM3LjQ3MjAxMi0xMzguMDYxMDAzIDEzNy40NzIwMTJsLTU4NC4wMzc5ODkgMGMtNzcuMDc0OTYgMC0xMzguMDYxLTYwLjgzODkxOC0xMzguMDYxLTEzNy40NzIwMTJsMC00MTEuMTM1OTg5YzAtNzYuNjMzMDgyIDYwLjk4NjA0LTEzNy40NzE5OTggMTM4LjA2MS0xMzcuNDcxOTk4eiIgcC1pZD0iODMyMCI+PC9wYXRoPjwvc3ZnPg==',
    properties: { collaborativeWay: '1' },
  },
  {
    type: 'end',
    text: '结束',
    label: '结束',
    icon: "data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzUwMzg4OTY4OTA4IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIg0KICAgICB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjY5MTciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIg0KICAgICB3aWR0aD0iMzYiIGhlaWdodD0iMzYiPg0KICA8cGF0aCBkPSJNNTEyLjAwNTExNyA5NTguNzA4OTcxQzI2NS42ODMwMzUgOTU4LjcwODk3MSA2NS4yOTAwMDUgNzU4LjMxNjk2NSA2NS4yOTAwMDUgNTExLjk5Mzg2YzAtMjQ2LjMxMDgyNSAyMDAuMzkzMDMtNDQ2LjcwMzg1NSA0NDYuNzE1MTExLTQ0Ni43MDM4NTUgMjQ2LjMxMDgyNSAwIDQ0Ni43MDM4NTUgMjAwLjM5MzAzIDQ0Ni43MDM4NTUgNDQ2LjcwMzg1Qzk1OC43MDg5NzEgNzU4LjMxNjk2NSA3NTguMzE2OTY1IDk1OC43MDg5NzEgNTEyLjAwNTExNyA5NTguNzA4OTcxeiIgcC1pZD0iNjkxOCI+PC9wYXRoPg0KPC9zdmc+Cg==",
  },
]

// 网关节点
const gatewayNodes = [
  {
    type: 'serial',
    text: '',
    label: '互斥网关',
    icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzNiIgaGVpZ2h0PSIzNiIgdmlld0JveD0iMCAwIDEwMCAxMDAiPjxwb2x5Z29uIHBvaW50cz0iNTAsNSA5NSw1MCA1MCw1NSA1LDUwIiBmaWxsPSJub25lIiBzdHJva2U9IiM2YjcyODAiIHN0cm9rZS13aWR0aD0iMyIgLz48bGluZSB4MT0iMzAiIHkxPSIzMCIgeDI9IjcwIiB5Mj0iNzAiIHN0cm9rZT0iIzZiNzI4MCIgc3Ryb2tlLXdpZHRoPSIzIiAvPjxsaW5lIHgxPSI3MCIgeTE9IjMwIiB4Mj0iMzAiIHkyPSI3MCIgc3Ryb2tlPSIjNmI3MjgwIiBzdHJva2Utd2lkdGg9IjMiIC8+PC9zdmc+',
    properties: {},
  },
  {
    type: 'parallel',
    text: '',
    label: '并行网关',
    icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzNiIgaGVpZ2h0PSIzNiIgdmlld0JveD0iMCAwIDEwMCAxMDAiPjxwb2x5Z29uIHBvaW50cz0iNTAsNSA5NSw1MCA1MCw1NSA1LDUwIiBmaWxsPSJub25lIiBzdHJva2U9IiM2YjcyODAiIHN0cm9rZS13aWR0aD0iMyIgLz48bGluZSB4MT0iNTAiIHkxPSIzMiIgeDI9IjUwIiB5Mj0iNjgiIHN0cm9rZT0iIzZiNzI4MCIgc3Ryb2tlLXdpZHRoPSIzIiAvPjxsaW5lIHgxPSIzMiIgeTE9IjUwIiB4Mj0iNjgiIHkyPSI1MCIgc3Ryb2tlPSIjNmI3MjgwIiBzdHJva2Utd2lkdGg9IjMiIC8+PC9zdmc+',
    properties: {},
  },
  {
    type: 'inclusive',
    text: '',
    label: '包含网关',
    icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzNiIgaGVpZ2h0PSIzNiIgdmlld0JveD0iMCAwIDEwMCAxMDAiPjxwb2x5Z29uIHBvaW50cz0iNTAsMTAgOTAsNTAgNTAsOTAgMTAsNTAiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzZiNzI4MCIgc3Ryb2tlLXdpZHRoPSIzIiAvPjxjaXJjbGUgY3g9IjUwIiBjeT0iNTAiIHI9IjIwIiBmaWxsPSJub25lIiBzdHJva2U9IiM2YjcyODAiIHN0cm9rZS13aWR0aD0iMyIgLz48L3N2Zz4=',
    properties: {},
  },
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
}

/* 各类型节点的图标底色 */
.item-start .item-icon-wrap   { background: linear-gradient(135deg, #ecfdf5, #d1fae5); }
.item-between .item-icon-wrap { background: linear-gradient(135deg, #eff6ff, #dbeafe); }
.item-end .item-icon-wrap     { background: linear-gradient(135deg, #fef2f2, #fee2e2); }
.item-serial .item-icon-wrap  { background: linear-gradient(135deg, #fafafa, #f1f5f9); }
.item-parallel .item-icon-wrap{ background: linear-gradient(135deg, #fafafa, #f1f5f9); }
.item-inclusive .item-icon-wrap { background: linear-gradient(135deg, #fafafa, #f1f5f9); }

.item-icon {
  max-width: 22px;
  max-height: 22px;
  object-fit: contain;
  filter: grayscale(20%);
  opacity: 0.75;
  transition: all 0.2s ease;
}

.sidebar-item:hover .item-icon {
  filter: grayscale(0%);
  opacity: 1;
}

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

  .item-icon {
    max-width: 20px;
    max-height: 20px;
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

  .item-icon {
    max-width: 18px;
    max-height: 18px;
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
