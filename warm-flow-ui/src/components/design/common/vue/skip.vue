<template>
  <div>
    <el-form ref="formRef" :model="form" label-width="80px" size="small" :disabled="disabled">
      <el-form-item label="跳转名称" v-if="skipConditionShow" prop="skipName">
        <el-input v-model="form.skipName" placeholder="跳转名称"/>
      </el-form-item>
      <el-form-item label="跳转类型" prop="skipType">
        <el-select v-model="form.skipType">
          <el-option sel label="审批通过" value="PASS"/>
          <el-option label="退回" value="REJECT"/>
        </el-select>
      </el-form-item>
      <el-form-item label="跳转条件" v-if="skipConditionShow">
        <el-input v-model="form.condition" v-if="!spelFlag" placeholder="条件名" :style="{ width: !spelFlag? '30%' : '0%' }"/>
        <el-select v-model="form.conditionType" placeholder="请选择条件方式" :style="{ width: spelFlag? '18%' : '25%', 'margin-left': '1%' }" @change="changeOper">
          <el-option label="默认" value="default"/>
          <el-option label="spel" value="spel"/>
          <el-option label="大于" value="gt"/>
          <el-option label="大于等于" value="ge"/>
          <el-option label="等于" value="eq"/>
          <el-option label="不等于" value="ne"/>
          <el-option label="小于" value="lt"/>
          <el-option label="小于等于" value="le"/>
          <el-option label="包含" value="like"/>
          <el-option label="不包含" value="notLike"/>
        </el-select>
        <el-input v-model="form.conditionValue" placeholder="条件值" :style="{ width: spelFlag? '80%' : '43%', 'margin-left': '1%' }"/>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup name="Skip">

const props = defineProps({
  modelValue: {
    type: Object,
    default() {
      return {}
    }
  },
  disabled: { // 是否禁止
    type: Boolean,
    default: false
  },
  skipConditionShow: { // 是否显示跳转条件
    type: Boolean,
    default: true
  },
});

const spelFlag = ref(false);
const form = ref(props.modelValue);

watch(() => form, n => {
  n = n.value
  if (n.conditionType) {
    let skipCondition;
    skipCondition = n.conditionType + "@@";
    if (!/^spel/.test(n.conditionType) && !/^default/.test(n.conditionType)) {
      skipCondition = skipCondition
          + (n.condition ? n.condition : '') + "|";
    }
    n.skipCondition = skipCondition
        + (n.conditionValue ? n.conditionValue : '')
  }

}, {deep: true});

function changeOper(obj) {
  spelFlag.value = (obj === 'spel' || obj === 'default');
}

if (props.modelValue?.conditionType === 'spel' || props.modelValue?.conditionType === 'default') {
  spelFlag.value = true;
}

</script>

<style scoped>

</style>
