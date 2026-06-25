<template>
  <div class="end-wrapper">
    <wf-form ref="formRef" class="endForm" :model="form" label-width="110px" :disabled="disabled">
      <div class="base-settings-section">
        <div class="base-settings-content">
          <wf-form-item label="节点编码：" prop="nodeCode">
            <wf-input v-model="form.nodeCode" :disabled="disabled"></wf-input>
          </wf-form-item>
          <wf-form-item label="节点名称：" prop="nodeName">
            <wf-input v-model="form.nodeName" ref="nodeInput" :disabled="disabled" @change="nodeNameChange"></wf-input>
          </wf-form-item>
          <!-- 自定义扩展点：消费方可注入额外表单项（透出 { form, disabled }） -->
          <slot name="node-form-extra" :form="form" :disabled="disabled" />
        </div>
      </div>
    </wf-form>
  </div>
</template>

<script setup lang="ts">
import { getCurrentInstance, ref, watch } from 'vue';

defineOptions({ name: 'End' });

interface EndProps {
  /** 节点表单数据（v-model） */
  modelValue?: Record<string, any>;
  /** 是否只读 */
  disabled?: boolean;
}
const props = withDefaults(defineProps<EndProps>(), {
  modelValue: () => ({}),
  disabled: false,
});

const form = ref<Record<string, any>>(props.modelValue);
const emit = defineEmits<{ (e: 'change', value: any): void }>();
const { proxy } = getCurrentInstance()!;

watch(() => form, n => {
  if (n) {
    emit('change', n)
  }
},{ deep: true });

function nodeNameChange() {
  proxy.$refs.nodeInput.focus();
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/_common.scss';

.end-wrapper { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.endForm { border-top: 0; width: 100%; }

/* 引入公共样式：基础配置卡片 */
@include base-settings-card;
@include responsive-adaption;
</style>
