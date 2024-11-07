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
          <el-input v-model="form.nodeName" :disabled="disabled"></el-input>
        </el-form-item>
      </slot>
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

watch(() => form, n => {
  if (n) {
    emit('change', n)
  }
},{ deep: true });

</script>

<style scoped>

</style>
