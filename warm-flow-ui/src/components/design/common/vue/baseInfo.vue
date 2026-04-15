<template>
  <div class="app-container">
    <el-form ref="formRef" :model="form" class="dialogForm" :rules="rules" label-width="150px" :disabled="disabled">
      <div class="form-section">
        <div class="section-title">基本配置</div>
        <el-form-item label="流程编码" prop="flowCode">
          <el-input v-model="form.flowCode" placeholder="请输入流程编码" maxlength="40" show-word-limit />
        </el-form-item>

        <el-form-item label="流程名称" prop="flowName">
          <el-input v-model="form.flowName" placeholder="请输入流程名称" maxlength="100" show-word-limit @input="nameChange" />
        </el-form-item>

        <el-form-item label="设计器模型" prop="modelValue">
          <el-radio-group v-model="form.modelValue" :disabled="!!definitionId" @change="modelValueChange" class="radio-card-group">
            <el-radio label="CLASSICS" class="radio-card">
              <div class="radio-card-content">
                <div class="radio-card-icon">
                  <el-icon :size="22"><Monitor /></el-icon>
                </div>
                <div class="radio-card-info">
                  <div class="radio-card-title">经典模型</div>
                  <div class="radio-card-desc">自由拖拽连线，灵活编排流程</div>
                </div>
              </div>
            </el-radio>
            <el-radio label="MIMIC" class="radio-card">
              <div class="radio-card-content">
                <div class="radio-card-icon">
                  <el-icon :size="22"><Connection /></el-icon>
                </div>
                <div class="radio-card-info">
                  <div class="radio-card-title">仿钉钉模型</div>
                  <div class="radio-card-desc">类钉钉审批流，上下结构布局</div>
                </div>
              </div>
            </el-radio>
          </el-radio-group>
          <div class="radio-card-warning">切换后重置节点，保存后不支持修改！</div>
        </el-form-item>

        <el-form-item label="流程类别" prop="category">
          <el-tree-select
              v-model="form.category"
              :data="categoryList"
              :props="{ value: 'id', label: 'name', children: 'children' }"
              value-key="id"
              placeholder="请选择流程类别"
              check-strictly/>
        </el-form-item>

        <el-form-item label="自定义表单" prop="formCustom">
          <el-radio-group v-model="form.formCustom" class="radio-card-group radio-card-group--compact">
            <el-radio label="N" class="radio-card radio-card--sm">
              <div class="radio-card-content">
                <div class="radio-card-icon radio-card-icon--sm">
                  <el-icon :size="18"><Document /></el-icon>
                </div>
                <div class="radio-card-info">
                  <div class="radio-card-title">否</div>
                  <div class="radio-card-desc">填写页面路径</div>
                </div>
              </div>
              <el-tooltip class="box-item" effect="dark" placement="top" content="填写页面地址：如system/process/approve">
                <el-icon :size="14" class="radio-card-tip"><WarningFilled /></el-icon>
              </el-tooltip>
            </el-radio>
            <el-radio label="Y" class="radio-card radio-card--sm">
              <div class="radio-card-content">
                <div class="radio-card-icon radio-card-icon--sm">
                  <el-icon :size="18"><EditPen /></el-icon>
                </div>
                <div class="radio-card-info">
                  <div class="radio-card-title">是</div>
                  <div class="radio-card-desc">选择自定义表单</div>
                </div>
              </div>
              <el-tooltip class="box-item" effect="dark" placement="top" content="填写自定义表单的唯一标识：如formCode+version">
                <el-icon :size="14" class="radio-card-tip"><WarningFilled /></el-icon>
              </el-tooltip>
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="表单路径" prop="formPath" v-if="form.formCustom === 'N'">
          <el-input v-model="form.formPath" placeholder="请输入审批表单路径" maxlength="100" show-word-limit/>
        </el-form-item>

        <el-form-item label="自定义表单唯一标识" prop="formPath" v-else-if="form.formCustom === 'Y'">
            <el-tree-select
                v-model="form.formPath"
                :data="formPathList"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择流程类别"
                check-strictly/>
        </el-form-item>
      </div>

      <div class="form-section">
        <div class="section-title">监听器配置</div>
        <el-form-item prop="listenerRows" class="listenerItem">
          <el-table :data="form.listenerRows" style="width: 100%">
            <el-table-column prop="listenerType" width="150" label="类型">
              <template #default="scope">
                <el-form-item :prop="`listenerRows.${scope.$index}.listenerType`" :rules="rules.listenerType">
                  <el-select v-model="scope.row.listenerType" placeholder="请选择类型">
                    <el-option label="开始" value="start"></el-option>
                    <el-option label="分派" value="assignment"></el-option>
                    <el-option label="完成" value="finish"></el-option>
                    <el-option label="创建" value="create"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>

            <el-table-column prop="listenerPath" label="监听器（可输入类路径）">
              <template #default="scope">
                <el-form-item :prop="`listenerRows.${scope.$index}.listenerPath`" :rules="rules.listenerPath">
                    <el-select
                        v-model="scope.row.listenerPath"
                        placeholder="请输入或选择"
                        allow-create
                        filterable
                        clearable
                        style="width: 100%"
                        @change="(value) => handleListenerPathChange(value, scope.row)">
                        <el-option
                            v-for="item in ListenerVo"
                            :key="item.path"
                            :label="item.description"
                            :value="item.path"/>
                    </el-select>
                </el-form-item>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="65" v-if="!disabled">
              <template #default="scope">
                <el-button size="small" type="danger" icon="Delete" @click="handleDeleteRow(scope.$index)" />
              </template>
            </el-table-column>
          </el-table>
          <el-button v-if="!disabled" class="add-row-btn" type="primary" @click="handleAddRow">增加行</el-button>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script setup name="BaseInfo">
import { ref } from "vue";
import {listenerList} from "@/api/flow/definition.js";

const { proxy } = getCurrentInstance();
const props = defineProps({
  disabled: { // 是否禁止
    type: Boolean,
    default: false
  },
  logicJson: {
    type: Object,
    default () {
      return {}
    }
  },
  categoryList: {
    type: Array,
    default () {
      return []
    }
  },
  formPathList: {
      type: Array,
      default () {
          return []
      }
  },
  definitionId: {
    type: String,
    default () {
      return null
    }
  },
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
  }
});

const definitionList = ref([]);
const ListenerVo = ref([]); // 监听器列表


const rules = {
  modelValue: [
    { required: true, message: "设计器模型不能为空", trigger: "blur" }
  ],
  flowCode: [
    { required: true, message: "流程编码不能为空", trigger: "blur" }
  ],
  flowName: [
    { required: true, message: "流程名称不能为空", trigger: "blur" }
  ],
  formCustom: [
    { required: true, message: "请选择审批表单是否自定义", trigger: "change" }
  ],
  listenerType: [
    { required: true, message: '监听器不能为空', trigger: ['change', 'blur'] }
  ],
  listenerPath: [
    { required: true, message: '监听器不能为空', trigger: ['change', 'blur'] }
  ]
};

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
function handleDeleteRow(index) {
  form.value.listenerRows.splice(index, 1);
}

// 表单必填校验
function validate() {
  return new Promise((resolve, reject) => {
    proxy.$nextTick(() => {
      proxy.$refs.formRef.validate(valid => {
        if (valid) {
          resolve(true);
        } else {
          resolve(false);
        }
      });
    });
  });
}

function nameChange(flowName) {
  // 可以在这里添加额外的逻辑，比如验证或格式化
  proxy.$emit('update:flow-name', flowName); // 如果需要通知父组件
}

function modelValueChange() {
  proxy.$emit('update:model-value'); // 如果需要通知父组件
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
function handleListenerPathChange(path, row) {
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
/* ========== 关键帧动画 ========== */
@keyframes pulse-soft {
  0%, 100% { box-shadow: 0 0 0 0 rgba(64, 158, 255, 0.2); }
  50% { box-shadow: 0 0 0 6px rgba(64, 158, 255, 0); }
}

@keyframes check-in {
  0% { transform: scale(0) rotate(-45deg); opacity: 0; }
  60% { transform: scale(1.2) rotate(-45deg); }
  100% { transform: scale(1) rotate(-45deg); opacity: 1; }
}

/* ========== 1. 容器背景层 ========== */
.app-container {
  padding: 0;
  background-color: var(--wf-bg-white, #fff);
  min-height: 100%;
  html.dark & {
    background-color: var(--wf-bg-color, #141414);
  }
}

.dialogForm {
  max-width: 960px;
  width: 100%;
  margin: 24px auto;
  padding: 0;
  background: transparent;
  border-top: 0;
}

/* ========== 2. 卡片区块 ========== */
.form-section {
  position: relative;
  background: var(--wf-bg-white, #fff);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: var(--wf-radius-lg, 12px);
  box-shadow: var(--wf-shadow, 0 2px 12px rgba(0, 0, 0, 0.06));
  padding: 24px 32px 24px 35px;
  margin-bottom: 16px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  overflow: hidden;

  /* 左侧渐变装饰条 */
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 3px;
    background: linear-gradient(180deg, var(--wf-primary, #409eff), var(--wf-primary-dark, #2b7de9));
    border-radius: var(--wf-radius-lg, 12px) 0 0 var(--wf-radius-lg, 12px);
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--wf-shadow-lg, 0 4px 16px rgba(0, 0, 0, 0.1));
  }
}

/* ========== 3. 区块标题 ========== */
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--wf-text-primary, #303133);
  padding: 0 0 16px 0;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
  display: flex;
  align-items: center;

  &::before {
    content: '';
    display: inline-block;
    width: 4px;
    height: 18px;
    background: linear-gradient(180deg, var(--wf-primary, #409eff), var(--wf-primary-dark, #2b7de9));
    border-radius: 2px;
    margin-right: 10px;
    flex-shrink: 0;
  }

  &::after {
    content: '';
    display: inline-block;
    margin-left: 10px;
    padding: 2px 0;
    flex: 0;
    height: 20px;
    border-radius: var(--wf-radius-round, 20px);
  }
}

/* ========== 4. 表单项增强 ========== */
:deep(.el-form-item) {
  margin-bottom: 22px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--wf-text-primary, #303133);
  letter-spacing: 0.5px;
}

:deep(.el-input__wrapper),
:deep(.el-select .el-input__wrapper),
:deep(.el-textarea__inner) {
  transition: all 0.3s ease;
  border-radius: var(--wf-radius-sm, 4px);
  position: relative;

  &:hover {
    box-shadow: 0 0 0 1px var(--wf-primary, #409eff) inset;
  }

  &.is-focus,
  &:focus {
    box-shadow: 0 0 0 1px var(--wf-primary, #409eff) inset, 0 0 0 3px rgba(64, 158, 255, 0.12);
  }
}

/* 输入框底部渐变线（hover） */
:deep(.el-input) {
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    width: 0;
    height: 2px;
    background: linear-gradient(90deg, var(--wf-primary, #409eff), var(--wf-primary-dark, #2b7de9));
    border-radius: 0 0 var(--wf-radius-sm, 4px) var(--wf-radius-sm, 4px);
    transition: width 0.3s ease, left 0.3s ease;
    z-index: 1;
  }

  &:hover::after {
    width: 100%;
    left: 0;
  }
}

/* ========== 5. 卡片式 Radio 选择器 ========== */
.radio-card-group {
  display: flex;
  gap: 12px;
  width: 100%;
}

.radio-card-group--compact {
  gap: 10px;
}

:deep(.radio-card) {
  position: relative;
  border: 1.5px solid var(--wf-border-color, #dcdfe6);
  border-radius: var(--wf-radius, 8px);
  padding: 14px 18px;
  margin-right: 0;
  height: auto;
  background: var(--wf-bg-white, #fff);
  transition: all 0.25s ease;
  flex: 1;
  min-width: 0;
  overflow: hidden;

  &:hover {
    border-color: var(--wf-primary, #409eff);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  }

  /* 隐藏原生 radio 圆点 */
  .el-radio__input {
    position: absolute;
    top: 10px;
    right: 10px;

    .el-radio__inner {
      width: 16px;
      height: 16px;
      border-width: 1.5px;
      transition: all 0.25s ease;
    }
  }

  .el-radio__label {
    padding-left: 0;
    width: 100%;
    color: var(--wf-text-primary, #303133);
    font-weight: normal;
  }

  /* 选中态 */
  &.is-checked {
    border-color: var(--wf-primary, #409eff);
    background: var(--wf-primary-light, #ecf5ff);
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2), inset 0 0 20px rgba(64, 158, 255, 0.06);

    .el-radio__input .el-radio__inner {
      background: var(--wf-primary, #409eff);
      border-color: var(--wf-primary, #409eff);
    }

    .radio-card-title {
      color: var(--wf-primary, #409eff);
    }

    .radio-card-icon {
      background: var(--wf-primary, #409eff);
      color: #fff;
      animation: pulse-soft 2s ease-in-out infinite;
    }

    /* 选中对勾 */
    &::after {
      content: '';
      position: absolute;
      bottom: 8px;
      right: 10px;
      width: 10px;
      height: 6px;
      border-left: 2px solid var(--wf-primary, #409eff);
      border-bottom: 2px solid var(--wf-primary, #409eff);
      transform: rotate(-45deg);
      animation: check-in 0.35s ease forwards;
    }
  }

  /* 非选中态隐藏对勾 */
  &:not(.is-checked)::after {
    content: none;
  }

  /* 禁用态 */
  &.is-disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background: var(--wf-bg-color, #f5f7fa);
    border-color: var(--wf-border-light, #e4e7ed);

    &:hover {
      box-shadow: none;
      border-color: var(--wf-border-light, #e4e7ed);
      transform: none;
    }
  }
}

:deep(.radio-card--sm) {
  padding: 10px 14px;
}

.radio-card-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.radio-card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--wf-bg-color, #f5f7fa);
  color: var(--wf-primary, #409eff);
  flex-shrink: 0;
  transition: all 0.25s ease;
}

.radio-card-icon--sm {
  width: 34px;
  height: 34px;
  border-radius: 50%;
}

.radio-card-info {
  flex: 1;
  min-width: 0;
}

.radio-card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--wf-text-primary, #303133);
  line-height: 1.4;
  transition: color 0.25s ease;
}

.radio-card-desc {
  font-size: 11px;
  color: var(--wf-text-placeholder, #c0c4cc);
  line-height: 1.4;
  margin-top: 2px;
}

.radio-card-tip {
  position: absolute;
  top: 10px;
  right: 30px;
  color: var(--wf-text-secondary, #909399);
  cursor: help;
  transition: color 0.2s ease;

  &:hover {
    color: var(--wf-primary, #409eff);
  }
}

/* ========== 6. 监听器表格 ========== */
:deep(.el-table) {
  border-radius: var(--wf-radius-lg, 12px);
  overflow: hidden;
  border: 1px solid var(--wf-border-lighter, #ebeef5);

  .el-table__header-wrapper th {
    background: linear-gradient(135deg, var(--wf-primary-lighter, #f0f7ff), var(--wf-primary-light, #ecf5ff)) !important;
    color: var(--wf-text-primary, #303133);
    font-weight: 600;
    font-size: 13px;
  }

  .el-table__row {
    transition: background-color 0.2s ease;
    position: relative;

    & > td:first-child {
      position: relative;
    }

    /* hover 左侧蓝色指示条 */
    & > td:first-child::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 0;
      background: var(--wf-primary, #409eff);
      border-radius: 0 2px 2px 0;
      transition: height 0.25s ease;
    }

    &:hover > td:first-child::before {
      height: 60%;
    }

    &:hover > td {
      background-color: var(--wf-primary-light, #ecf5ff) !important;
    }
  }

  /* 删除按钮红色渐变 hover */
  .el-button--danger {
    transition: all 0.25s ease;

    &:hover {
      background: linear-gradient(135deg, var(--wf-danger, #f56c6c), #e04848) !important;
      border-color: transparent !important;
      color: #fff !important;
      box-shadow: 0 2px 8px rgba(245, 108, 108, 0.35);
    }
  }
}

/* ========== 7. 增加行按钮 ========== */
.add-row-btn {
  margin-top: 12px;
  width: 100%;
  border: 1.5px dashed var(--wf-primary, #409eff) !important;
  color: var(--wf-primary, #409eff) !important;
  background: transparent !important;
  border-radius: var(--wf-radius, 8px);
  transition: all 0.3s ease;
  height: 40px;
  letter-spacing: 2px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: var(--wf-primary-light, #ecf5ff) !important;
    border-style: solid !important;
    border-color: var(--wf-primary, #409eff) !important;
    box-shadow: var(--wf-shadow-primary, 0 2px 8px rgba(64, 158, 255, 0.3));
  }
}

/* ========== 旧样式兼容覆盖 ========== */
:deep(.Tabs) {
  margin-top: -20px;
  box-shadow: none;
  border-bottom: 0;
  .el-tabs__content {
    display: none;
  }
  .el-tabs__item.is-active {
    margin-left: 0;
    border-top: 1px solid var(--el-border-color);
    margin-top: 0;
  }
}

:deep(.listenerItem) {
  margin-bottom: 0;

  .el-form-item__label {
    float: none;
    display: inline-block;
    text-align: left;
  }
  .el-form-item__content {
    margin-left: 0 !important;
  }
  .el-table .el-form-item {
    margin-bottom: 0;
  }
}

/* 表单项警告提示样式 */
:deep(.el-form-item) {
  .radio-card-warning {
    margin-top: 8px;
    padding: 8px 12px;
    background: var(--wf-warning-light, #fef0f0);
    border: 1px solid var(--wf-warning-border, #fbc4c4);
    border-radius: var(--wf-radius-sm, 4px);
    font-size: 12px;
    color: var(--wf-danger, #f56c6c);
    line-height: 1.5;
    width: 100%;
  }
}

/* ========== 8. 暗黑模式增强 ========== */
:global(html.dark) {
  .app-container {
    background-color: var(--wf-bg-color, #141414) !important;
    background-image: none;
  }
}

:global(html.dark) .form-section {
  background: rgba(31, 31, 31, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2), inset 0 0 0 1px var(--wf-border-color, #333333);
}

:global(html.dark) :deep(.el-table) {
  .el-table__header-wrapper th {
    background: linear-gradient(135deg, rgba(30, 40, 60, 0.95), rgba(25, 35, 55, 0.95)) !important;
  }
}

:global(html.dark) :deep(.radio-card) {
  /* Radio 卡片暗黑模式 - 非选中态也要深色背景 */
  & { background: #1a1a1a; border-color: #444444; }

  .el-radio__label { color: #c0c4cc; }
  .radio-card-title { color: #e0e0e0; }
  .radio-card-desc { color: #888888; }
  .radio-card-icon { background: rgba(255,255,255,.06); }

  &:hover { border-color: #409eff; }

  &.is-checked {
    background: #1a2744;
    box-shadow: 0 2px 16px rgba(64,158,255,.25), inset 0 0 24px rgba(64,158,255,.08);
    border-color: #409eff;
    .radio-card-icon { background: #409eff; }
    .radio-card-title { color: #409eff; }
  }

  &.is-disabled {
    background: rgba(120,120,120,.1);
    border-color: #3a3a3a;
  }
}

/* ========== 9. 滚动条美化 ========== */
.app-container {
  &::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(144, 147, 153, 0.3);
    border-radius: 3px;
    transition: all 0.3s ease;
  }

  &:hover::-webkit-scrollbar-thumb {
    background: rgba(144, 147, 153, 0.5);
  }

  &::-webkit-scrollbar-thumb:hover {
    background: rgba(144, 147, 153, 0.7);
    width: 8px;
  }
}
</style>
