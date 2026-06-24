<template>
  <a-layout style="height: 100vh">
    <a-layout-header class="demo-header">
      <span class="demo-title">Warm-Flow 设计器 · Ant Design Vue 4 消费示例</span>
      <a-tag color="blue">@dromara/warm-flow-designer</a-tag>
    </a-layout-header>

    <a-alert
      banner
      type="success"
      message="本 demo 通过 setUiAdapter(antdvAdapter) 让设计器 UI 走 Ant Design Vue 4 原生（28 个组件已翻译为 antd）。注：自定义表单设计器(form-create)仍用 Element Plus（Q2 决定）；库 dist-lib 仍内置 EP 默认适配器，bundle 级移除 EP 需库侧拆子入口（后续）。"
    />

    <a-layout-content class="demo-content">
      <FlowDesigner :show-grid="true" @saved="onSaved" @close="onClose" />
    </a-layout-content>
  </a-layout>
</template>

<script setup>
/**
 * Ant Design Vue 4 消费示例（过渡态）：用 antd 外壳承载 Warm-Flow 设计器。
 * 设计器内部 UI 当前为 Element Plus 默认适配器，Phase 3 接入 antdv 适配器后切换。
 *
 * @author warm
 */
import { FlowDesigner } from '@dromara/warm-flow-designer'

function onSaved(payload) {
  console.log('[antdv4-demo] saved:', payload)
}
function onClose() {
  console.log('[antdv4-demo] close')
}
</script>

<style>
.demo-header {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}
.demo-title {
  font-weight: 600;
  font-size: 16px;
}
.demo-content {
  position: relative;
  overflow: hidden;
}
</style>
