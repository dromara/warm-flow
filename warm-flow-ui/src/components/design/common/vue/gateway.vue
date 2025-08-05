<template>
  <div>
    <el-form ref="formRef" :model="form" label-width="120px" size="small" :disabled="disabled">
      <el-form-item label="节点编码" prop="nodeCode">
        <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
      </el-form-item>
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

<style scoped>

</style>
