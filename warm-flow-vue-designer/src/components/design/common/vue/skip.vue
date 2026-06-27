<template>
  <div class="skip-wrapper">
    <wf-form ref="formRef" class="skipForm" :model="form" label-width="110px" :rules="rules" :disabled="disabled">
      <div class="base-settings-section">
        <div class="base-settings-content">
          <wf-form-item :label="t('skip.nameLabel')" v-if="skipConditionShow" prop="skipName">
            <wf-input v-model="form.skipName" :placeholder="t('skip.namePlaceholder')"/>
          </wf-form-item>
          <wf-form-item :label="t('skip.typeLabel')" prop="skipType">
            <wf-select v-model="form.skipType">
              <wf-option :label="t('skip.typePass')" value="PASS"/>
              <wf-option :label="t('skip.typeReject')" value="REJECT"/>
            </wf-select>
          </wf-form-item>
          <wf-form-item :label="t('skip.conditionLabel')" v-if="skipConditionShow" prop="skipCondition">
            <wf-input v-model="form.condition" v-if="!expressFlag" :placeholder="t('skip.conditionName')" :style="{ width: !expressFlag? '30%' : '0%' }"/>
            <wf-select v-model="form.conditionType" :placeholder="t('skip.conditionTypePlaceholder')" :style="{ width: expressFlag? '18%' : '25%', 'margin-left': '1%' }"
                       clearable @change="changeOper" @clear="handleClear">
                <wf-option :label="t('skip.opGt')" value="gt"/>
                <wf-option :label="t('skip.opGe')" value="ge"/>
                <wf-option :label="t('skip.opEq')" value="eq"/>
                <wf-option :label="t('skip.opNe')" value="ne"/>
                <wf-option :label="t('skip.opLt')" value="lt"/>
                <wf-option :label="t('skip.opLe')" value="le"/>
                <wf-option :label="t('skip.opLike')" value="like"/>
                <wf-option :label="t('skip.opNotLike')" value="notLike"/>
                <wf-option :label="t('skip.opDefault')" value="default" v-if="framework ==='SPRING_BOOT'"/>
                <wf-option label="spel" value="spel" v-if="framework ==='SPRING_BOOT'"/>
                <wf-option label="snel" value="snel" v-if="framework ==='SOLON'"/>
            </wf-select>
            <wf-input v-model="form.conditionValue" :placeholder="getConditionDescription()" :style="{ width: expressFlag? '80%' : '43%', 'margin-left': '1%' }"/>
          </wf-form-item>
          <!-- 自定义扩展点：消费方可注入额外表单项（边/skip，透出 { form, disabled }） -->
          <slot name="node-form-extra" :form="form" :disabled="disabled" />
        </div>
      </div>
    </wf-form>
  </div>
</template>

<script setup lang="ts">

import { computed, reactive, ref, watch } from 'vue';
import {getFramework} from "@/utils/auth";
import { useI18n } from '@/i18n';

defineOptions({ name: 'Skip' });

const { t } = useI18n();

interface SkipProps {
  /** 边表单数据（v-model） */
  modelValue?: Record<string, any>;
  /** 是否只读 */
  disabled?: boolean;
  /** 是否显示跳转条件 */
  skipConditionShow?: boolean;
}
const props = withDefaults(defineProps<SkipProps>(), {
  modelValue: () => ({}),
  disabled: false,
  skipConditionShow: true,
});

const expressFlag = ref(false)
const form = ref<Record<string, any>>(props.modelValue)
const framework = getFramework()

const rules = reactive({
    skipCondition: computed(() => {
        const type = form.value.conditionType;
        if (!type) return [];

        if (type === 'default') {
            return [
                {required: true, message: t('common.pleaseInput'), trigger: "change"},
                {validator: validateDefault, trigger: ["change", "blur"]}
            ];
        } else if (type === 'spel') {
            return [
                {required: true, message: t('common.pleaseInput'), trigger: "change"},
                {validator: validateSpel, trigger: ["change", "blur"]}
            ];
        } else if (type === 'snel') {
            return [
                {required: true, message: t('common.pleaseInput'), trigger: "change"},
                {validator: validateSnel, trigger: ["change", "blur"]}
            ];
        }
        // 其他类型不作限制
        return [{required: false, message: t('common.pleaseInput'), trigger: "change"}];
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

function changeOper(obj: string) {
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

function validateDefault(rule: any, value: any, callback: (error?: Error) => void) {
    value = value.replace('default@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error(t('skip.defaultRequired')));
    } else if (!/^\$\{.*\}$/.test(value)) {
        callback(new Error(t('skip.defaultFormat')));
    } else {
        callback();
    }
}

function validateSpel(rule: any, value: any, callback: (error?: Error) => void) {
    value = value.replace('spel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error(t('skip.spelRequired')));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error(t('skip.spelFormat')));
    } else {
        callback();
    }
}

function validateSnel(rule: any, value: any, callback: (error?: Error) => void) {
    value = value.replace('snel@@', '').replace('=', '').trim();
    if (value === '' || value === undefined || value === null) {
        callback(new Error(t('skip.snelRequired')));
    } else if (!/^\#\{.*\}$/.test(value)) {
        callback(new Error(t('skip.snelFormat')));
    } else {
        callback();
    }
}

function getConditionDescription() {
    const type = form.value.conditionType;
    switch (type) {
        case 'default':
            return t('skip.descDefault');
        case 'spel':
            return t('skip.descSpel');
        case 'snel':
            return t('skip.descSnel');
        default:
            return t('common.pleaseInput');
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
@include responsive-adaption;

/* skip 特有图标 */
.ext-icon-skip {
  color: var(--wf-text-regular, #606266); svg { width: 16px; height: 16px; }
}

/* skip 特有：手机端跳转条件区域输入框堆叠 */
@media (max-width: 768px) {
  .skip-wrapper ::v-deep(.el-form-item) {
    .el-form-item__content {
      flex-wrap: wrap;
    }
    /* 条件名 + 条件方式 + 值，手机端换行显示 */
    .el-input,
    .el-select {
      width: 100% !important;
      margin-left: 0 !important;
      margin-bottom: 4px;
    }
  }
}
</style>
