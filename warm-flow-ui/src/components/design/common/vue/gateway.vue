<template>
  <div class="gateway-wrapper">
    <el-form ref="formRef" class="gatewayForm" :model="form" label-width="120px" size="small" :disabled="disabled">
      <div class="base-settings-section">
        <div class="base-settings-header">
          <span class="ext-attributes-icon ext-icon-base">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 12c0-5.52-4.48-10-10-10S2 6.48 2 12s4.48 10 10 10 10-4.48 10-10zm-10 2c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm6.31-2c-.47-2.28-2.23-4.08-4.49-4.62l-.01-.03L13 4h-2l-.81 3.35c-2.27.54-4.02 2.34-4.49 4.63l-3.7.02v2l3.7-.02c.47 2.29 2.23 4.09 4.49 4.63L11 22h2l.81-3.35c2.27-.54 4.02-2.34 4.49-4.63l3.7.02v-2l-3.69-.02z" fill="currentColor"/>
            </svg>
          </span>
          <span class="base-settings-title">网关配置</span>
        </div>
        <div class="base-settings-content">
          <el-form-item label="节点编码：" prop="nodeCode">
            <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
          </el-form-item>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script setup name="Gateway">

const props = defineProps({
  modelValue: {
    type: Object,
    default () {
      return {}
    }
  },
  disabled: { // 是否禁止
    type: Boolean,
    default: false
  },
});

const form = ref(props.modelValue);

</script>

<style scoped lang="scss">
@import '@/assets/styles/_common.scss';

.gateway-wrapper { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.gatewayForm { border-top: 0; width: 100%; }

/* 引入公共样式：基础配置卡片 */
@include base-settings-card;
</style>
