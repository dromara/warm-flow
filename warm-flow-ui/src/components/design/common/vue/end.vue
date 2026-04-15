<template>
  <div class="end-wrapper">
    <el-form ref="formRef" class="endForm" :model="form" label-width="120px" size="small" :disabled="disabled">
      <div class="base-settings-section">
        <div class="base-settings-header">
          <span class="ext-attributes-icon ext-icon-base">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 3H5C3.89 3 3 3.89 3 5V19C3 20.11 3.89 21 5 21H19C20.11 21 21 20.11 21 19V5C21 3.89 20.11 3 19 3ZM12 6C13.93 6 15.5 7.57 15.5 9.5C15.5 11.43 13.93 13 12 13C10.07 13 8.5 11.43 8.5 9.5C8.5 7.57 10.07 6 12 6ZM18 18H6V17C6 14.67 10.33 13.34 12 13.34C13.67 13.34 18 14.67 18 17V18Z" fill="currentColor"/>
            </svg>
          </span>
          <span class="base-settings-title">基础配置</span>
        </div>
        <div class="base-settings-content">
          <el-form-item label="节点编码：" prop="nodeCode">
            <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
          </el-form-item>
          <el-form-item label="节点名称：" prop="nodeName">
            <el-input v-model="form.nodeName" ref="nodeInput" :disabled="disabled" @change="nodeNameChange"></el-input>
          </el-form-item>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script setup name="End">

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
const emit = defineEmits(["change"]);
const { proxy } = getCurrentInstance();

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
.end-wrapper { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.endForm { border-top: 0; width: 100%; }

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
