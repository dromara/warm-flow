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
.gateway-wrapper { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.gatewayForm { border-top: 0; width: 100%; }

/* 基础配置卡片 */
.base-settings-section {
  border-radius: var(--wf-radius-lg, 12px);
  overflow: hidden;
  border: 1px solid var(--wf-border-light, #e4e7ed);
  background: var(--wf-bg-white, #fff);
  box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  transition: all 0.3s ease;
  html.dark & {
    border-color: var(--wf-border-color, #333333);
    background: var(--wf-bg-color, #141414);
  }
  &:hover {
    box-shadow: var(--wf-shadow, 0 2px 12px rgba(0, 0, 0, 0.06));
    html.dark & { box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3); }
  }
}
.base-settings-header {
  display: flex; align-items: center; gap: 8px; padding: 12px 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
  html.dark & {
    background: linear-gradient(135deg, rgba(120,128,140,.06) 0%, rgba(80,88,100,.04) 100%);
    border-bottom-color: var(--wf-border-color, #333333);
  }
}
.ext-icon-base {
  color: var(--wf-text-regular, #606266); svg { width: 16px; height: 16px; }
}
.base-settings-title {
  font-size: 13px; font-weight: 600; color: var(--wf-text-primary, #303133); letter-spacing: .3px;
}
.base-settings-content {
  padding: 14px 16px;
  background: var(--wf-bg-color, #fafbfc);
  html.dark & { background: var(--wf-bg-color, #141414); }
}
</style>
