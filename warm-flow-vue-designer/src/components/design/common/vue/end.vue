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
        </div>
      </div>
    </wf-form>
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
@import '@/assets/styles/_common.scss';

.end-wrapper { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.endForm { border-top: 0; width: 100%; }

/* 引入公共样式：基础配置卡片 */
@include base-settings-card;
@include responsive-adaption;
</style>
