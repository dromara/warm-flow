<template>
  <el-form ref="nodeExtRef" class="nodeExtForm" :model="form" label-width="100px" size="small" :disabled="disabled" label-position="left">
    <el-form-item :label="`${item.label}：`" :prop="item.code" v-for="(item, index) in formList" :key="index" :rules="[{ required: true, message: `${item.label}不能为空`, trigger: ['blur', 'change'] }]">
      <el-input v-if="item.type === 1" v-model="form[item.code]" placeholder="请输入"></el-input>
      <el-input v-else-if="item.type === 2" v-model="form[item.code]" :rows="2" type="textarea" placeholder="请输入"/>
      <el-select v-else-if="item.type === 3" v-model="form[item.code]">
        <el-option v-for="dItem in item.dict" :key="dItem.value" :label="dItem.label" :value="dItem.value"></el-option>
      </el-select>
      <el-checkbox-group v-else-if="item.type === 4" v-model="form[item.code]">
        <el-row :gutter="20">
          <el-col :span="8" v-for="(dItem, dIndex) in item.dict" :key="dIndex">
            <el-checkbox :label="dItem.value">{{ dItem.label }}</el-checkbox>
          </el-col>
        </el-row>
      </el-checkbox-group>
    </el-form-item>
  </el-form>
</template>

<script setup name="NodeExtList">
const { proxy } = getCurrentInstance();

const props = defineProps({
  modelValue: {
    type: Object,
    default () {
      return {}
    }
  },
  formList: { // 表单项列表
    type: Array,
    default () {
      return []
    }
  },
  disabled: { // 是否禁止
    type: Boolean,
    default: false
  },
});

const form = ref(props.modelValue);

// 表单必填校验
function validate() {
  return new Promise((resolve, reject) => {
    proxy.$refs.nodeExtRef.validate((valid) => {
      if (valid) {
        resolve(form.value);
      } else {
        reject(false);
      }
    });
  });
};

defineExpose({
  validate
})
</script>

<style scoped lang="scss"></style>
