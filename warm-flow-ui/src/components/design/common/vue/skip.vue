<template>
  <div class="skip-wrapper">
    <el-form ref="formRef" class="skipForm" :model="form" label-width="110px" size="small" :rules="rules" :disabled="disabled">
      <div class="base-settings-section">
        <div class="base-settings-header">
          <span class="ext-attributes-icon ext-icon-skip">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M17.59 3.41L15 6l3.59 3.59 1.42-1.42L15.82 4.83l2.77-1.42zM4.41 3.41L3 4.83l2.77 1.42L4.41 8 8 4.41 6.59 3 4.41 3.41zM12 8c-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4zm0 6c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z" fill="currentColor"/>
            </svg>
          </span>
          <span class="base-settings-title">跳转条件配置</span>
        </div>
        <div class="base-settings-content">
          <el-form-item label="跳转名称：" v-if="skipConditionShow" prop="skipName">
            <el-input v-model="form.skipName" placeholder="跳转名称"/>
          </el-form-item>
          <el-form-item label="跳转类型：" prop="skipType">
            <el-select v-model="form.skipType">
              <el-option label="审批通过" value="PASS"/>
              <el-option label="退回" value="REJECT"/>
            </el-select>
          </el-form-item>
          <el-form-item label="跳转条件：" v-if="skipConditionShow" prop="skipCondition">
            <el-input v-model="form.condition" v-if="!expressFlag" placeholder="条件名" :style="{ width: !expressFlag? '30%' : '0%' }"/>
            <el-select v-model="form.conditionType" placeholder="请选择条件方式" :style="{ width: expressFlag? '18%' : '25%', 'margin-left': '1%' }"
                       clearable @change="changeOper" @clear="handleClear">
                <el-option label="大于" value="gt"/>
                <el-option label="大于等于" value="ge"/>
                <el-option label="等于" value="eq"/>
                <el-option label="不等于" value="ne"/>
                <el-option label="小于" value="lt"/>
                <el-option label="小于等于" value="le"/>
                <el-option label="包含" value="like"/>
                <el-option label="不包含" value="notLike"/>
                <el-option label="默认" value="default" v-if="framework ==='SPRING_BOOT'"/>
                <el-option label="spel" value="spel" v-if="framework ==='SPRING_BOOT'"/>
                <el-option label="snel" value="snel" v-if="framework ==='SOLON'"/>
            </el-select>
            <el-input v-model="form.conditionValue" :placeholder="getConditionDescription()" :style="{ width: expressFlag? '80%' : '43%', 'margin-left': '1%' }"/>
          </el-form-item>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script setup name="Skip">

import {getFramework} from "@/utils/auth.js";

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

const expressFlag = ref(false)
const form = ref(props.modelValue)
const framework = getFramework()

const rules = reactive({
    skipCondition: computed(() => {
        const type = form.value.conditionType;
        if (!type) return [];

        if (type === 'default') {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validateDefault, trigger: ["change", "blur"]}
            ];
        } else if (type === 'spel') {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validateSpel, trigger: ["change", "blur"]}
            ];
        } else if (type === 'snel') {
            return [
                {required: true, message: "请输入", trigger: "change"},
                {validator: validateSnel, trigger: ["change", "blur"]}
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
    if (!/^spel/.test(n.conditionType) && !/^default/.test(n.conditionType) && !/^snel/.test(n.conditionType)) {
      skipCondition = skipCondition + (n.condition ? n.condition : '') + "|";
    }
    n.skipCondition = skipCondition + (n.conditionValue ? n.conditionValue : '')
  }

}, {deep: true});

function changeOper(obj) {
  expressFlag.value = (['spel', 'default', 'snel'].includes(obj));
}

function handleClear() {
    // 处理清空操作的逻辑
    form.value.conditionType = '';
    expressFlag.value = false;
    // 可以根据需要重置其他相关字段
    form.value.condition = '';
    form.value.conditionValue = '';
    form.value.skipCondition = '';
}

if (['spel', 'default', 'snel'].includes(props.modelValue?.conditionType)) {
  expressFlag.value = true;
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

function validateSnel(rule, value, callback) {
    value = value.replace('snel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入snel表达式'));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error('snel表达式必须以#{开头，以}结尾'));
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
        case 'snel':
            return '请输入snel表达式，格式如: #{@user.eval(flag)}';
        default:
            return '请输入';
    }
}

</script>

<style scoped lang="scss">
@import '@/assets/styles/_common.scss';

.skip-wrapper { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.skipForm { border-top: 0; width: 100%; }
.placeholder { color: #828f9e; font-size: 12px; }

/* 引入公共样式：基础配置卡片 */
@include base-settings-card;

/* skip 特有图标 */
.ext-icon-skip {
  color: var(--wf-text-regular, #606266); svg { width: 16px; height: 16px; }
}
</style>
