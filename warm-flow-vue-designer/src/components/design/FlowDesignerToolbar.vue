<template>
  <div class="design-toolbar" style="padding: 5px 0; text-align: right;">
    <div v-if="activeStep === 1">
      <span class="toolbar-group">
        <wf-tooltip :content="t('flowDesigner.zoomOut')" placement="bottom"><wf-button size="small" @click="emit('zoom-out')"><svg-icon icon-class="ep:zoom-out"/></wf-button></wf-tooltip>
        <!-- 自适应：所有端均 fitView 显示全部节点（最大缩放 100%） -->
        <wf-tooltip :content="t('flowDesigner.fitView')" placement="bottom"><wf-button size="small" @click="emit('fit-view')"><svg-icon icon-class="ep:rank"/></wf-button></wf-tooltip>
        <wf-tooltip :content="t('flowDesigner.zoomIn')" placement="bottom"><wf-button size="small" @click="emit('zoom-in')"><svg-icon icon-class="ep:zoom-in"/></wf-button></wf-tooltip>
      </span>
      <span class="toolbar-group">
        <wf-tooltip :content="t('flowDesigner.undo')" placement="bottom"><wf-button size="small" @click="emit('undo')"><svg-icon icon-class="ep:d-arrow-left"/></wf-button></wf-tooltip>
        <wf-tooltip :content="t('flowDesigner.redo')" placement="bottom"><wf-button size="small" @click="emit('redo')"><svg-icon icon-class="ep:d-arrow-right"/></wf-button></wf-tooltip>
        <wf-tooltip :content="t('flowDesigner.clear')" placement="bottom"><wf-button size="small" @click="emit('clear')"><svg-icon icon-class="ep:delete"/></wf-button></wf-tooltip>
      </span>
      <span class="toolbar-group">
        <wf-tooltip :content="t('flowDesigner.downloadImage')" placement="bottom"><wf-button size="small" @click="emit('download-image')"><svg-icon icon-class="ep:picture"/></wf-button></wf-tooltip>
        <wf-tooltip :content="t('flowDesigner.downloadJson')" placement="bottom"><wf-button size="small" @click="emit('download-json')"><svg-icon icon-class="ep:download"/></wf-button></wf-tooltip>
      </span>
      <span class="toolbar-group" v-if="onlyDesignShow && !disabled">
        <wf-tooltip :content="t('common.save')" placement="bottom"><wf-button size="small" class="toolbar-save-btn" @click="emit('save')">
          <svg-icon icon-class="save" style="width: 14px; height: 14px;"/>
        </wf-button></wf-tooltip>
      </span>
      <!-- slot: toolbar-extra 追加自定义工具栏按钮（透出底层 lf / disabled） -->
      <slot name="toolbar-extra" :lf="lf" :disabled="disabled" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/i18n';

/** 流程设计器画布工具栏：缩放（缩小 / 适应 / 放大）+ 撤销 / 重做 / 清空 + 下载（图片 / JSON）+ 只读态保存。
 *  从 FlowDesigner 抽出的纯展示编排子组件，画布操作仍由容器（useLogicFlowCanvas）持有，经 props 入、事件出。
 *  样式沿用 FlowDesigner 的全局（非 scoped）样式表中的 .design-toolbar / .toolbar-group 等类，无需迁移。 */
defineOptions({ name: 'FlowDesignerToolbar' });

defineProps<{
  /** 当前步骤索引（仅 1=流程设计 时显示工具栏） */
  activeStep: number;
  /** 仅画布模式（无基础信息页时，工具栏内提供保存按钮） */
  onlyDesignShow?: boolean;
  /** 是否只读 */
  disabled?: boolean;
  /** 底层 LogicFlow 实例（仅用于透出给 toolbar-extra 插槽） */
  lf?: any;
}>();

const emit = defineEmits<{
  (e: 'zoom-out'): void;
  (e: 'fit-view'): void;
  (e: 'zoom-in'): void;
  (e: 'undo'): void;
  (e: 'redo'): void;
  (e: 'clear'): void;
  (e: 'download-image'): void;
  (e: 'download-json'): void;
  (e: 'save'): void;
}>();

const { t } = useI18n();
</script>
