<template>
  <div class="app-container">
    <wf-form ref="formRef" :model="form" class="dialogForm" :rules="rules" label-width="150px" :disabled="disabled">
      <div class="form-section">
        <div class="section-title">{{ t('baseInfo.sectionBasic') }}</div>
        <wf-form-item :label="t('baseInfo.flowCode')" prop="flowCode">
          <wf-input v-model="form.flowCode" :placeholder="t('baseInfo.flowCodePlaceholder')" maxlength="40" />
        </wf-form-item>

        <wf-form-item :label="t('baseInfo.flowName')" prop="flowName">
          <wf-input v-model="form.flowName" :placeholder="t('baseInfo.flowNamePlaceholder')" maxlength="100" @input="nameChange" />
        </wf-form-item>

        <wf-form-item :label="t('baseInfo.model')" prop="modelValue">
          <wf-radio-group v-model="form.modelValue" :disabled="!!definitionId || isMobile" @change="modelValueChange" class="radio-card-group">
            <wf-radio label="CLASSICS" class="radio-card">
              <div class="radio-card-content">
                <div class="radio-card-icon">
                  <svg-icon icon-class="classic" class="model-icon"/>
                </div>
                <div class="radio-card-info">
                  <div class="radio-card-title">{{ t('baseInfo.modelClassic') }}</div>
                  <div class="radio-card-desc">{{ t('baseInfo.modelClassicDesc') }}</div>
                </div>
              </div>
            </wf-radio>
            <wf-radio label="MIMIC" class="radio-card">
              <div class="radio-card-content">
                <div class="radio-card-icon">
                  <svg-icon icon-class="mimic" class="model-icon"/>
                </div>
                <div class="radio-card-info">
                  <div class="radio-card-title">{{ t('baseInfo.modelMimic') }}</div>
                  <div class="radio-card-desc">{{ t('baseInfo.modelMimicDesc') }}</div>
                </div>
              </div>
            </wf-radio>
          </wf-radio-group>
          <div class="radio-card-warning">{{ t('baseInfo.modelSwitchWarning') }}</div>
          <div class="radio-card-warning radio-card-warning--mobile" v-if="isMobile">{{ t('baseInfo.modelMobileWarning') }}</div>
        </wf-form-item>

        <wf-form-item :label="t('baseInfo.category')" prop="category">
          <wf-tree-select
              v-model="form.category"
              :data="categoryList"
              :props="{ value: 'id', label: 'name', children: 'children' }"
              value-key="id"
              :placeholder="t('baseInfo.categoryPlaceholder')"
              check-strictly/>
        </wf-form-item>

        <wf-form-item :label="t('baseInfo.formCustom')" prop="formCustom">
          <wf-switch
            v-model="form.formCustom"
            size="large"
            active-value="Y"
            inactive-value="N"
            :active-text="t('common.yes')"
            :inactive-text="t('common.no')" />
          <span class="form-tip">{{ form.formCustom === 'Y' ? t('baseInfo.formCustomTipY') : t('baseInfo.formCustomTipN') }}</span>
        </wf-form-item>

        <wf-form-item :label="t('baseInfo.formPath')" prop="formPath" v-if="form.formCustom === 'N'">
          <wf-input v-model="form.formPath" :placeholder="t('baseInfo.formPathPlaceholder')" maxlength="100"/>
        </wf-form-item>

        <wf-form-item :label="t('baseInfo.formKey')" prop="formPath" v-else-if="form.formCustom === 'Y'">
            <wf-tree-select
                v-model="form.formPath"
                :data="formPathList"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                :placeholder="t('baseInfo.categoryPlaceholder')"
                check-strictly/>
        </wf-form-item>
      </div>

      <div class="form-section">
        <div class="section-title">{{ t('baseInfo.sectionListener') }}</div>
        <wf-form-item prop="listenerRows" class="listenerItem">
          <wf-table :data="form.listenerRows" style="width: 100%" :empty-text="t('baseInfo.listenerEmpty')">
            <wf-table-column prop="listenerType" :width="isMobile ? 60 : 160" :label="t('common.type')">
              <template #default="scope">
                <wf-form-item :prop="`listenerRows.${scope.$index}.listenerType`" :rules="rules.listenerType">
                  <wf-select v-model="scope.row.listenerType" :placeholder="t('baseInfo.listenerTypePlaceholder')">
                    <wf-option :label="t('start.listenerStart')" value="start"></wf-option>
                    <wf-option :label="t('start.listenerAssignment')" value="assignment"></wf-option>
                    <wf-option :label="t('start.listenerFinish')" value="finish"></wf-option>
                    <wf-option :label="t('start.listenerCreate')" value="create"></wf-option>
                  </wf-select>
                </wf-form-item>
              </template>
            </wf-table-column>

            <wf-table-column prop="listenerPath" :label="t('baseInfo.listenerPathLabel')">
              <template #default="scope">
                <wf-form-item :prop="`listenerRows.${scope.$index}.listenerPath`" :rules="rules.listenerPath">
                    <wf-select
                        v-model="scope.row.listenerPath"
                        :placeholder="t('baseInfo.listenerPathPlaceholder')"
                        allow-create
                        filterable
                        clearable
                        style="width: 100%"
                        @change="(value) => handleListenerPathChange(value, scope.row)">
                        <wf-option
                            v-for="item in ListenerVo"
                            :key="item.path"
                            :label="item.description"
                            :value="item.path"/>
                    </wf-select>
                </wf-form-item>
              </template>
            </wf-table-column>

            <wf-table-column :label="t('common.operation')" width="65" align="center" v-if="!disabled">
              <template #default="scope">
                <wf-button link size="small" type="danger" @click="handleDeleteRow(scope.$index)"><svg-icon icon-class="ep:delete"/></wf-button>
              </template>
            </wf-table-column>
          </wf-table>
          <wf-button v-if="!disabled" class="add-row-btn" @click="handleAddRow">{{ t('common.addRow') }}</wf-button>
        </wf-form-item>
      </div>
    </wf-form>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, onMounted, onUnmounted, ref, watch } from "vue";
import {listenerList} from "@/api/flow/definition";
import { useI18n } from '@/i18n';

defineOptions({ name: 'BaseInfo' });

const { proxy } = getCurrentInstance()!;
const { t } = useI18n();
const emit = defineEmits<{
  (e: 'update:flow-name', flowName: string): void;
  (e: 'update:model-value'): void;
  (e: 'validate-error', fields?: Record<string, any>): void;
}>();

// 响应式屏幕检测
const isMobile = ref(false);

function checkMobile() {
  isMobile.value = window.innerWidth <= 768;
}

onMounted(() => {
  checkMobile();
  window.addEventListener('resize', checkMobile);
  // 移动端新增（无definitionId）时强制默认仿钉钉
  if (isMobile.value && !props.definitionId) {
    form.value.modelValue = 'MIMIC';
    emit('update:model-value');
  }
});

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile);
});
interface BaseInfoProps {
  /** 是否只读 */
  disabled?: boolean;
  /** 流程基础信息（数据源） */
  logicJson?: Record<string, any>;
  /** 流程类别树 */
  categoryList?: any[];
  /** 自定义表单路径树 */
  formPathList?: any[];
  /** 流程定义 id（新建态为 null） */
  definitionId?: string | null;
}
const props = withDefaults(defineProps<BaseInfoProps>(), {
  disabled: false,
  logicJson: () => ({}),
  categoryList: () => [],
  formPathList: () => [],
  definitionId: null,
});

const form = ref({
  id: null,
  flowCode: "",
  flowName: "",
  modelValue: "",
  category: "",
  formCustom: "N",
  formPath: "",
  listenerType: "",
  listenerPath: "",
  listenerRows: []
});

watch(() => props.logicJson, newValue => {
  if (newValue && Object.keys(newValue).length > 0) {
    Object.assign(form.value, newValue);
    setListenerData();
    // 移动端新增时强制默认仿钉钉（覆盖logicJson中可能为空的值）
    if (isMobile.value && !props.definitionId) {
      form.value.modelValue = 'MIMIC';
    }
  }
});

const definitionList = ref([]);
const ListenerVo = ref([]); // 监听器列表


const rules = computed(() => ({
  modelValue: [
    { required: true, message: t('baseInfo.ruleModelRequired'), trigger: "blur" }
  ],
  flowCode: [
    { required: true, message: t('baseInfo.ruleFlowCodeRequired'), trigger: "blur" }
  ],
  flowName: [
    { required: true, message: t('baseInfo.ruleFlowNameRequired'), trigger: "blur" }
  ],
  formCustom: [
    { required: true, message: t('baseInfo.ruleFormCustomRequired'), trigger: "change" }
  ],
  listenerType: [
    { required: true, message: t('baseInfo.ruleListenerRequired'), trigger: ['change', 'blur'] }
  ],
  listenerPath: [
    { required: true, message: t('baseInfo.ruleListenerRequired'), trigger: ['change', 'blur'] }
  ]
}));

// 表单引用（用于校验）
const formRef = ref();

function setListenerData() {
  // 处理监听器数据
  if (form.value.listenerType) {
    const listenerTypes = form.value.listenerType.split(",");
    const listenerPaths = form.value.listenerPath.split("@@");
    form.value.listenerRows = listenerTypes.map((type, index) => ({
      listenerType: type,
      listenerPath: listenerPaths[index]
    }));
  } else {
    form.value.listenerRows = [];
  }
}

// 增加行
function handleAddRow() {
  form.value.listenerRows.push({ listenerType: "", listenerPath: "" });
  formRef.value?.clearValidate("listenerRows");
}

// 删除行
function handleDeleteRow(index: number) {
  form.value.listenerRows.splice(index, 1);
}

// 表单必填校验
function validate() {
  return new Promise((resolve) => {
    proxy.$nextTick(() => {
      // EP 回调签名为 (valid, fields)，校验失败透出 invalid fields；antd 适配器仅回传 valid（fields 为空）
      proxy.$refs.formRef.validate((valid: boolean, fields?: Record<string, any>) => {
        if (valid) {
          resolve(true);
        } else {
          // 透出校验失败字段，供上层 FlowDesigner 转发 validate-error 事件（best-effort，随 UI 适配器）
          emit('validate-error', fields);
          resolve(false);
        }
      });
    });
  });
}

function nameChange(flowName: string) {
  // 可以在这里添加额外的逻辑，比如验证或格式化
  emit('update:flow-name', flowName); // 如果需要通知父组件
}

function modelValueChange() {
  emit('update:model-value'); // 如果需要通知父组件
}

function getFormData() {
  form.value.listenerType = form.value.listenerRows.map(row => row.listenerType).join(",")
  form.value.listenerPath = form.value.listenerRows.map(row => row.listenerPath).join("@@")
  return form.value;
}

/** 获取监听器列表 */
function getListenerList() {
    listenerList().then(response => {
        if (response.code === 200 && response.data) {
            ListenerVo.value = response.data;
        }
    });
}

// 处理监听器路径变化，级联更新类型
function handleListenerPathChange(path: string, row: any) {
    if (!path) {
        // 清空时，也清空类型
        row.listenerType = '';
        return;
    }

    // 在下拉选项中查找匹配的项
    const matchedItem = ListenerVo.value.find(item => item.path === path);
    if (matchedItem && matchedItem.type) {
        // 如果找到了匹配项且有 type，则更新 listenerType
        row.listenerType = matchedItem.type;
    } else {
        // 如果是手动输入的，清空类型（或者保持原值，根据需求决定）
        row.listenerType = '';
    }
}

defineExpose({ getFormData, validate });

getListenerList()
</script>

<style scoped lang="scss">
@import './baseInfo.scoped.scss';
</style>
