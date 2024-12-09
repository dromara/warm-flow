<template>
  <div>
    <el-form ref="formRef" :model="form" label-width="120px" size="small" :disabled="disabled">
      <slot name="form-item-task-name" :model="form" field="nodeCode">
        <el-form-item label="节点编码">
          <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
        </el-form-item>
      </slot>
      <slot name="form-item-task-name" :model="form" field="nodeName">
        <el-form-item label="节点名称">
          <el-input v-model="form.nodeName" ref="nodeInput" :disabled="disabled" @change="nodeNameChange"></el-input>
        </el-form-item>
      </slot>
    </el-form>
  </div>
</template>

<script setup name="Parallel">

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

<style scoped>

</style>
