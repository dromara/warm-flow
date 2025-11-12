<template>
  <div>
    <el-form ref="formRef" :model="form" label-width="80px" size="small" :rules="rules" :disabled="disabled">
      <el-form-item label="跳转名称" v-if="skipConditionShow" prop="skipName">
        <el-input v-model="form.skipName" placeholder="跳转名称"/>
      </el-form-item>
      <el-form-item label="跳转类型" prop="skipType">
        <el-select v-model="form.skipType">
          <el-option sel label="审批通过" value="PASS"/>
          <el-option label="退回" value="REJECT"/>
        </el-select>
      </el-form-item>
      <el-form-item label="跳转条件" v-if="skipConditionShow" prop="skipCondition">
        <el-input v-model="form.condition" v-if="!spelFlag" placeholder="条件名" :style="{ width: !spelFlag? '30%' : '0%' }"/>
        <el-select v-model="form.conditionType" placeholder="请选择条件方式" :style="{ width: spelFlag? '18%' : '25%', 'margin-left': '1%' }"
                   clearable @change="changeOper" @clear="handleClear">
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
        <el-input v-model="form.conditionValue" :placeholder="getConditionDescription()" :style="{ width: spelFlag? '80%' : '43%', 'margin-left': '1%' }"/>
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

const rules = reactive({
    skipCondition: computed(() => {
        const type = form.value.conditionType;
        if (!type) return [];

        debugger
        if (type === 'default') {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validateDefault, trigger: ["change", "blur"]}
            ];
        }  else if (type === 'spel') {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validateSpel, trigger: ["change", "blur"]}
            ];
        }
        // 其他类型不作限制
        return [{required: false, message: "请输入", trigger: "change"}];
    }),
});

watch(() => form, n => {
  n = n.value
  if (n.conditionType) {
    let skipCondition;
    skipCondition = n.conditionType + "@@";
    if (!/^spel/.test(n.conditionType) && !/^default/.test(n.conditionType)) {
      skipCondition = skipCondition + (n.condition ? n.condition : '') + "|";
    }
    n.skipCondition = skipCondition + (n.conditionValue ? n.conditionValue : '')
  }

}, {deep: true});

function changeOper(obj) {
  spelFlag.value = (obj === 'spel' || obj === 'default');
}

function handleClear() {
    // 处理清空操作的逻辑
    form.value.conditionType = '';
    spelFlag.value = false;
    // 可以根据需要重置其他相关字段
    form.value.condition = '';
    form.value.conditionValue = '';
    form.value.skipCondition = '';
}

if (props.modelValue?.conditionType === 'spel' || props.modelValue?.conditionType === 'default') {
  spelFlag.value = true;
}

function validateDefault(rule, value, callback) {
    value = value.replace('default@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入默认表达式'));
    } else if (!/^\$\{.*\}$/.test(value)) {
        callback(new Error('默认表达式必须以${开头，以}结尾'));
    } else {
        callback();
    }
}

function validateSpel(rule, value, callback) {
    value = value.replace('spel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入spel表达式'));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error('spel表达式必须以#{开头，以}结尾'));
    } else {
        callback();
    }
}

function getConditionDescription() {
    const type = form.value.conditionType;
    switch (type) {
        case 'default':
            return '请输入默认表达式,格式如: ${flag == 5 && flag > 4}';
        case 'spel':
            return '请输入spel表达式，格式如: #{@user.eval(#flag)}';
        default:
            return '请输入';
    }
}

</script>

<style scoped>
.placeholder {
    color: #828f9e;
    font-size: 12px;
}
</style>
