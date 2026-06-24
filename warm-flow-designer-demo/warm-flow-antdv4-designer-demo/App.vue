<template>
  <a-layout style="height: 100vh">
    <a-layout-header class="demo-header">
      <span class="demo-title">Warm-Flow 设计器 · Ant Design Vue 4 消费示例</span>
      <a-tag color="blue">@dromara/warm-flow-designer</a-tag>
    </a-layout-header>

    <a-alert
      banner
      type="success"
      message="本 demo 完全不引入 Element Plus：通过 setUiAdapter(antdvAdapter) 让设计器全部 28 个组件走 Ant Design Vue 4 原生。库主入口已做到 UI 库无关，EP / antd 均为可选子入口。注：自定义表单设计器(form-create) 暂留 Element Plus 生态（Q2 决定），本 demo 主流程不涉及。"
    />

    <a-layout-content class="demo-content">
      <FlowDesigner :show-grid="true" @saved="onSaved" @close="onClose" />
    </a-layout-content>
  </a-layout>
</template>

<script setup>
/**
 * Ant Design Vue 4 消费示例：用 antd 外壳承载 Warm-Flow 设计器。
 * 设计器内部 UI 经 setUiAdapter(antdvAdapter) 全量走 Ant Design Vue 4，无 Element Plus 依赖。
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
