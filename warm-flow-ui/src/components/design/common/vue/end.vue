<template>
  <div>
    <el-form ref="formRef" :model="form" label-width="120px" size="small" :disabled="disabled">
      <el-form-item label="节点编码" prop="nodeCode">
        <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
      </el-form-item>
      <el-form-item label="节点名称" prop="nodeName">
        <el-input v-model="form.nodeName" ref="nodeInput" :disabled="disabled" @change="nodeNameChange"></el-input>
      </el-form-item>
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

<style scoped>

</style>
