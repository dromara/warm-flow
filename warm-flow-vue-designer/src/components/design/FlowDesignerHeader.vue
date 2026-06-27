<template>
  <div class="design-header">
    <!-- 左侧：流程名称（slot: header-left 可整体替换，透出 flowName） -->
    <div class="header-left">
      <slot name="header-left" :flow-name="flowName">
        <div class="flow-name-wrapper">
          <wf-tooltip :content="flowName" placement="bottom" :show-after="500">
            <div class="flow-name">
              <svg-icon icon-class="flowName" style="margin-right: 5px" />
              {{ flowName || t('flowDesigner.untitled') }}
            </div>
          </wf-tooltip>
        </div>
      </slot>
    </div>

    <!-- 中间：步骤切换（slot: header-center 可整体替换，透出 activeStep / steps / goToStep） -->
    <div class="header-center">
      <slot name="header-center" :active-step="activeStep" :steps="steps" :go-to-step="goToStep">
        <div class="steps-tabs">
          <div
            v-for="(step, index) in steps"
            :key="index"
            class="step-tab"
            :class="{ active: activeStep === index }"
            @click="goToStep(index)"
          >
            <svg-icon :icon-class="step.icon" class="tab-icon" />
            <span class="tab-text">{{ step.title }}</span>
          </div>
        </div>
      </slot>
    </div>

    <!-- 右侧：保存按钮（slot: header-actions 可追加 / 替换操作按钮，透出 save / disabled） -->
    <div class="header-right">
      <slot name="header-actions" :save="emitSave" :disabled="disabled">
        <wf-button class="save-btn" size="default" @click="emitSave" v-if="!disabled">
          <svg-icon icon-class="save" class="save-icon" />
          <span>{{ t('common.save') }}</span>
        </wf-button>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/i18n';

/** 流程设计器顶部导航：流程名（左） + 步骤切换（中） + 保存（右）。
 *  从 FlowDesigner 抽出的纯展示编排子组件，状态/逻辑仍由容器持有，经 props 入、事件出。
 *  样式沿用 FlowDesigner 的全局（非 scoped）样式表中的 .design-header 等类，无需迁移。 */
defineOptions({ name: 'FlowDesignerHeader' });

interface StepItem {
  title: string;
  icon: string;
}

defineProps<{
  flowName?: string;
  activeStep: number;
  steps: StepItem[];
  disabled?: boolean;
}>();

const emit = defineEmits<{
  (e: 'step-click', index: number): void;
  (e: 'save'): void;
}>();

const { t } = useI18n();

const goToStep = (index: number) => emit('step-click', index);
const emitSave = () => emit('save');
</script>
