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
      <slot name="form-item-task-listenerType" :model="form" field="listenerType">
        <el-form-item label="监听器类型">
          <el-select v-model="form.listenerType" multiple>
            <el-option label="任务创建" value="create"></el-option>
            <el-option label="任务开始办理" value="start"></el-option>
            <el-option label="分派监听器" value="assignment"></el-option>
            <el-option label="权限认证" value="permission"></el-option>
            <el-option label="任务完成" value="finish"></el-option>
          </el-select>
        </el-form-item>
      </slot>
      <slot name="form-item-task-listenerPath" :model="form" field="listenerPath">
        <el-form-item label="监听器路径" description="输入监听器的路径，以@@分隔,顺序与监听器类型一致">
          <el-input type="textarea" v-model="form.listenerPath" rows="8"></el-input>
          <el-tooltip class="item" effect="dark" content="输入监听器的路径，以@@分隔，顺序与监听器类型一致">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </el-form-item>
      </slot>
    </el-form>
  </div>
</template>

<script setup name="Start">

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


if (form.value.listenerType) {
  form.value.listenerType = form.value.listenerType.split(",")
}
</script>

<style scoped>

</style>
