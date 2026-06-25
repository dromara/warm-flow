<template>
  <div class="gateway-wrapper">
    <wf-form ref="formRef" class="gatewayForm" :model="form" label-width="110px" :disabled="disabled">
      <div class="base-settings-section">
        <div class="base-settings-content">
          <wf-form-item label="节点编码：" prop="nodeCode">
            <wf-input v-model="form.nodeCode" :disabled="disabled"></wf-input>
          </wf-form-item>
        </div>
      </div>
    </wf-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

defineOptions({ name: 'Gateway' });

interface GatewayProps {
  /** 节点表单数据（v-model） */
  modelValue?: Record<string, any>;
  /** 是否只读 */
  disabled?: boolean;
}
const props = withDefaults(defineProps<GatewayProps>(), {
  modelValue: () => ({}),
  disabled: false,
});

const form = ref<Record<string, any>>(props.modelValue);

</script>

<style scoped lang="scss">
@import '@/assets/styles/_common.scss';

.gateway-wrapper { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.gatewayForm { border-top: 0; width: 100%; }

/* 引入公共样式：基础配置卡片 */
@include base-settings-card;
@include responsive-adaption;

/* gateway 特有图标 */
.ext-icon-base {
  color: var(--wf-primary, #409eff);
  svg { width: 16px; height: 16px; }
}
</style>
