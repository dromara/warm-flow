<template>
  <div class="app-container">
    <wf-form ref="formRef" :model="form" class="dialogForm" :rules="rules" label-width="150px" :disabled="disabled">
      <div class="form-section">
        <div class="section-title">基本配置</div>
        <wf-form-item label="流程编码" prop="flowCode">
          <wf-input v-model="form.flowCode" placeholder="请输入流程编码" maxlength="40" />
        </wf-form-item>

        <wf-form-item label="流程名称" prop="flowName">
          <wf-input v-model="form.flowName" placeholder="请输入流程名称" maxlength="100" @input="nameChange" />
        </wf-form-item>

        <wf-form-item label="设计器模型" prop="modelValue">
          <wf-radio-group v-model="form.modelValue" :disabled="!!definitionId || isMobile" @change="modelValueChange" class="radio-card-group">
            <wf-radio label="CLASSICS" class="radio-card">
              <div class="radio-card-content">
                <div class="radio-card-icon">
                  <svg-icon icon-class="classic" class="model-icon"/>
                </div>
                <div class="radio-card-info">
                  <div class="radio-card-title">经典模型</div>
                  <div class="radio-card-desc">自由拖拽连线，灵活编排流程</div>
                </div>
              </div>
            </wf-radio>
            <wf-radio label="MIMIC" class="radio-card">
              <div class="radio-card-content">
                <div class="radio-card-icon">
                  <svg-icon icon-class="mimic" class="model-icon"/>
                </div>
                <div class="radio-card-info">
                  <div class="radio-card-title">仿钉钉模型</div>
                  <div class="radio-card-desc">类钉钉审批流，上下结构布局</div>
                </div>
              </div>
            </wf-radio>
          </wf-radio-group>
          <div class="radio-card-warning">切换后重置节点，保存后不支持修改！</div>
          <div class="radio-card-warning radio-card-warning--mobile" v-if="isMobile">移动端不支持选择模型，如果新增默认仿钉钉</div>
        </wf-form-item>

        <wf-form-item label="流程类别" prop="category">
          <wf-tree-select
              v-model="form.category"
              :data="categoryList"
              :props="{ value: 'id', label: 'name', children: 'children' }"
              value-key="id"
              placeholder="请选择流程类别"
              check-strictly/>
        </wf-form-item>

        <wf-form-item label="自定义表单" prop="formCustom">
          <wf-switch
            v-model="form.formCustom"
            size="large"
            active-value="Y"
            inactive-value="N"
            active-text="是"
            inactive-text="否" />
          <span class="form-tip">{{ form.formCustom === 'Y' ? '选择已配置的自定义表单' : '填写审批页面路径' }}</span>
        </wf-form-item>

        <wf-form-item label="表单路径" prop="formPath" v-if="form.formCustom === 'N'">
          <wf-input v-model="form.formPath" placeholder="请输入审批表单路径" maxlength="100"/>
        </wf-form-item>

        <wf-form-item label="表单唯一标识" prop="formPath" v-else-if="form.formCustom === 'Y'">
            <wf-tree-select
                v-model="form.formPath"
                :data="formPathList"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择流程类别"
                check-strictly/>
        </wf-form-item>
      </div>

      <div class="form-section">
        <div class="section-title">监听器配置</div>
        <wf-form-item prop="listenerRows" class="listenerItem">
          <wf-table :data="form.listenerRows" style="width: 100%" empty-text="暂无监听器，点击下方「增加行」添加">
            <wf-table-column prop="listenerType" :width="isMobile ? 60 : 160" label="类型">
              <template #default="scope">
                <wf-form-item :prop="`listenerRows.${scope.$index}.listenerType`" :rules="rules.listenerType">
                  <wf-select v-model="scope.row.listenerType" placeholder="请选择类型">
                    <wf-option label="开始" value="start"></wf-option>
                    <wf-option label="分派" value="assignment"></wf-option>
                    <wf-option label="完成" value="finish"></wf-option>
                    <wf-option label="创建" value="create"></wf-option>
                  </wf-select>
                </wf-form-item>
              </template>
            </wf-table-column>

            <wf-table-column prop="listenerPath" label="监听器（可输入类路径）">
              <template #default="scope">
                <wf-form-item :prop="`listenerRows.${scope.$index}.listenerPath`" :rules="rules.listenerPath">
                    <wf-select
                        v-model="scope.row.listenerPath"
                        placeholder="请输入或选择"
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

            <wf-table-column label="操作" width="65" align="center" v-if="!disabled">
              <template #default="scope">
                <wf-button link size="small" type="danger" @click="handleDeleteRow(scope.$index)"><svg-icon icon-class="ep:delete"/></wf-button>
              </template>
            </wf-table-column>
          </wf-table>
          <wf-button v-if="!disabled" class="add-row-btn" @click="handleAddRow">增加行</wf-button>
        </wf-form-item>
      </div>
    </wf-form>
  </div>
</template>

<script setup name="BaseInfo">
import { ref, onMounted, onUnmounted, watch } from "vue";
import {listenerList} from "@/api/flow/definition";

const { proxy } = getCurrentInstance();
const emit = defineEmits(['update:flow-name', 'update:model-value']);

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
    // 移动端新增时强制默认仿钉钉（覆盖logicJson中可能为空的值）
    if (isMobile.value && !props.definitionId) {
      form.value.modelValue = 'MIMIC';
    }
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
/* ========== 1. 容器背景层 ========== */
.app-container {
  padding: 0;
  background-color: var(--wf-bg-white, #fff);
  min-height: 100%;
  /* iOS / 现代质感：系统字体栈 + 字形平滑 */
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "PingFang SC", "Helvetica Neue", "Microsoft YaHei", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
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

/* ========== 2. 扁平分区（去内层卡片，外层壳已提供容器） ========== */
.form-section {
  position: relative;
  background: transparent;
  padding: 0;
  margin-bottom: 32px;
  overflow: visible;

  &:last-child {
    margin-bottom: 0;
  }
}

/* ========== 3. 区块标题 ========== */
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--wf-text-primary, #303133);
  letter-spacing: 0.3px;
  padding: 0 0 14px 0;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
  display: flex;
  align-items: center;
}

/* ========== 4. 表单项增强 ========== */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--wf-text-primary, #303133);
  /* 右对齐：label 贴输入框侧对齐，红星紧贴首字（EP 经典表单样式） */
  width: 110px !important;
  min-width: 110px !important;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  text-align: right;
  /* 与 el-input 默认高度(32px)对齐，保证 label 与右侧控件垂直居中 */
  height: 32px;
}

/* 错误提示增加上下间距 - PC端/平板端 */
:deep(.el-form-item__error) {
  padding-top: 6px;
  padding-bottom: 4px;
}

/* 右侧内容区：统一左对齐 */
::deep(.el-form-item__content) {
  text-align: left;
  justify-content: flex-start;
}

:deep(.el-input__wrapper),
:deep(.el-select .el-input__wrapper),
:deep(.el-textarea__inner) {
  /* 默认白底 + 浅边框：未填写也清爽，不再用发灰的填充底 */
  transition: background-color 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  border-radius: 10px;
  background-color: var(--wf-bg-white, #fff);
  box-shadow: none;
  border: 1px solid var(--wf-border-light, #dcdfe6);
  position: relative;

  &:hover {
    border-color: var(--wf-primary, #409eff);
  }

  /* focus：主色描边 + 柔和光环 */
  &.is-focus,
  &:focus {
    background-color: var(--wf-bg-white, #fff);
    border-color: var(--wf-primary, #409eff);
    box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.12);
  }
}

/* 校验失败态：统一红边 + 红光环，优先级压过 focus 的蓝色，避免红蓝混色 */
:deep(.el-form-item.is-error .el-input__wrapper),
:deep(.el-form-item.is-error .el-input__wrapper.is-focus),
:deep(.el-form-item.is-error .el-select .el-input__wrapper) {
  border-color: var(--wf-danger, #f56c6c);
  box-shadow: 0 0 0 4px rgba(245, 108, 108, 0.12);
}

/* 禁用态暗黑模式覆盖（已发布/失效状态）— 完全全局选择器，避开 scoped 编译 */
:global(html.dark .dialogForm .el-form-item .el-input__wrapper.is-disabled),
:global(html.dark .dialogForm .el-form-item .el-textarea__inner.is-disabled),
:global(html.dark .dialogForm .el-form-item .el-select .el-input__wrapper.is-disabled) {
  background-color: #2a2d35 !important;
  box-shadow: none !important;
  border-color: #3a3e48 !important;
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
  border-radius: var(--wf-radius-lg, 12px);
  padding: 16px;
  margin-right: 0;
  height: auto;
  background: var(--wf-bg-white, #fff);
  box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  flex: 1;
  min-width: 0;
  overflow: hidden;

  &:hover {
    border-color: var(--wf-primary, #409eff);
    transform: translateY(-2px);
    box-shadow: var(--wf-shadow, 0 2px 12px rgba(0, 0, 0, 0.06));
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

  /* 选中态：边框 + 浅底 + 原生 radio 圆点，单一指示，去渐变/发光/角标 */
  &.is-checked {
    border-color: var(--wf-primary, #409eff);
    background: var(--wf-primary-light, #ecf5ff);

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
    }
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

  /* 禁用态暗黑模式覆盖 — html.dark 必须作为外层 */
  :global(html.dark) .radio-card.is-disabled {
    background: #2a2d35 !important;
    border-color: #3a3e48 !important;
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
  font-size: 22px;
}

/* 模型卡线性图标：跟随 radio-card-icon 字号与颜色（选中态自动变白） */
.model-icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.1em;
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
  font-size: 12px;
  color: var(--wf-text-secondary, #909399);
  line-height: 1.4;
  margin-top: 4px;
}

/* ========== 6. 监听器表格 ========== */
:deep(.el-table) {
  border-radius: var(--wf-radius-lg, 12px);
  overflow: hidden;
  border: 1px solid var(--wf-border-lighter, #ebeef5);

  .el-table__header-wrapper th {
    background: var(--wf-bg-white, #fff) !important;
    color: var(--wf-text-regular, #606266);
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

  /* 删除按钮：无实底，仅红色图标，hover 浅红圆底 */
  .el-button--danger.is-link {
    color: var(--wf-danger, #f56c6c);
    padding: 5px;
    height: auto;
    border: none;
    transition: all 0.2s ease;

    &:hover {
      color: #e04848;
      background: rgba(245, 108, 108, 0.12) !important;
      border-radius: 8px;
    }
  }

  /* 表格内控件默认透明：整行读作干净白底，hover 轻填充、focus 才显白底+蓝环 */
  .el-input__wrapper,
  .el-select .el-input__wrapper {
    background-color: transparent;

    &:hover { background-color: rgba(118, 128, 150, 0.07); }
    &.is-focus { background-color: var(--wf-bg-white, #fff); }
  }
}

/* ========== 7. 增加行按钮 ========== */
.add-row-btn {
  margin-top: 12px;
  width: 100%;
  border: 1.5px dashed var(--wf-primary, #409eff) !important;
  color: var(--wf-primary, #409eff) !important;
  background: transparent !important;
  border-radius: 10px;
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

/* 表单项警示提示样式（amber 级，非错误红） */
:deep(.el-form-item) {
  .radio-card-warning {
    margin-top: 8px;
    padding: 6px 10px;
    background: var(--wf-warning-lighter, #fdf6ec);
    border-radius: var(--wf-radius-sm, 4px);
    font-size: 12px;
    color: var(--wf-warning, #e6a23c);
    line-height: 1.5;
    width: 100%;
  }
}

/* 开关 / 字段旁的轻提示 */
.form-tip {
  margin-left: 12px;
  font-size: 12px;
  color: var(--wf-text-secondary, #909399);
  line-height: 1.5;
}

/* ========== 8. 暗黑模式增强 ========== */
:global(html.dark) {
  .app-container {
    background-color: var(--wf-bg-color, #141414) !important;
    background-image: none;
  }
}

/* iOS 填充式输入框：暗黑覆盖（优先级需高于全局 html.dark .el-input__wrapper） */
:global(html.dark) :deep(.el-input__wrapper),
:global(html.dark) :deep(.el-select .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.06);
  box-shadow: none;
  border: 1px solid transparent;
}
:global(html.dark) :deep(.el-input__wrapper:hover),
:global(html.dark) :deep(.el-select .el-input__wrapper:hover) {
  background-color: rgba(255, 255, 255, 0.10);
}
:global(html.dark) :deep(.el-input__wrapper.is-focus),
:global(html.dark) :deep(.el-input__wrapper:focus) {
  background-color: rgba(255, 255, 255, 0.04);
  border-color: var(--wf-primary, #409eff);
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.18);
}

:global(html.dark) .form-section {
  background: transparent;
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

/* ========== 响应式适配：平板端 (<= 1024px) ========== */
@media (max-width: 1024px) {
  .dialogForm {
    margin: 12px auto;
  }

  .form-section {
    margin-bottom: 24px;
  }
}

/* ========== 响应式适配：手机端 (<= 768px) ========== */
@media (max-width: 768px) {
  .dialogForm {
    margin: 8px 4px;
  }

  .app-container {
    overflow-x: hidden;
  }

  .form-section {
    margin-bottom: 20px;
  }

  .section-title {
    font-size: 14px;
    padding-bottom: 10px;
    margin-bottom: 16px;
  }

  :deep(.dialogForm .el-form-item__label),
  :deep(.el-form-item__label) {
    /* 手机端右对齐，仅收窄宽度 */
    width: 90px !important;
    min-width: 90px !important;
    font-size: 13px;
    font-weight: 600;
    text-align: right;
    justify-content: flex-end;
  }

  /* 手机端错误提示间距 */
  ::deep(.el-form-item__error) {
    padding-top: 10px;
    padding-bottom: 8px;
    line-height: 1.4;
  }

  :deep(.el-form-item) {
    margin-bottom: 18px;
  }

  :deep(.el-input__inner),
  :deep(.el-textarea__inner) {
    font-size: 13px !important;
  }

  /* Radio 卡片组：手机端全部纵向紧凑排列 */
  .radio-card-group {
    flex-direction: column;
    gap: 8px;
    width: 100% !important;
  }

  /* 确保表单项内容区不限制宽度 */
  :deep(.el-form-item__content) {
    max-width: 100% !important;
    overflow: visible !important;
  }

  .radio-card-group--compact {
    flex-direction: column; /* 手机端纵向排列，与设计器模型一致 */
    gap: 8px;
  }

  /* 设计器模型卡片：手机端占满行，显示小字提示 */
  :deep(.radio-card) {
    width: 100% !important;
    max-width: none !important;
    padding: 10px 14px;
    box-sizing: border-box !important;

    &:hover {
      transform: none;
    }

    .radio-card-desc {
      display: block; /* 显示描述文字在标题下方 */
      font-size: 10px !important;
      line-height: 1.2 !important;
      margin-top: 2px;
      color: var(--wf-text-placeholder, #c0c4cc);
    }

    .radio-card-icon {
      width: 28px;
      height: 28px;
    }

    .radio-card-title {
      font-size: 13px;
      white-space: nowrap;
      overflow: visible;
    }
  }

  /* 自定义表单小卡片：与设计器模型一致，纵向+小字提示 */
  :deep(.radio-card--sm) {
    width: 100% !important;
    max-width: none !important;
    padding: 10px 12px !important;
    box-sizing: border-box !important;

    .radio-card-desc {
      display: block !important; /* 小字提示显示在标题下方 */
      font-size: 10px !important;
      line-height: 1.2 !important;
      margin-top: 2px;
      color: var(--wf-text-placeholder, #c0c4cc);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

  }

  .radio-card-content {
    gap: 8px;
  }

  .radio-card-icon {
    width: 32px;
    height: 32px;
    font-size: 18px;
  }

  .radio-card-icon--sm {
    width: 28px;
    height: 28px;
  }

  .radio-card-title {
    font-size: 13px;
  }

  .radio-card-desc {
    font-size: 10px;
  }

  :deep(.el-form-item) {
    .radio-card-warning {
      margin-top: 4px;
      padding: 5px 8px;
      font-size: 11px;
    }
    .radio-card-warning--mobile {
      background: var(--wf-primary-lighter, #f0f7ff);
      border-color: rgba(64, 158, 255, 0.3);
      color: var(--wf-primary, #409eff);
    }
  }

  /* 监听器表格横向滚动 */
  :deep(.listenerItem) {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      height: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(144, 147, 153, 0.3);
      border-radius: 2px;
    }
  }

  :deep(.el-table) {
    /* 手机端去掉固定最小宽度，让表格自适应容器 */
    min-width: unset !important;

    .el-table__header-wrapper th {
      font-size: 12px;
    }

    /* 类型列：手机端紧凑 */
    .el-table__header-wrapper th:first-child,
    td:first-child {
      min-width: 60px !important;
      width: 60px !important;

      .cell {
        padding-left: 4px !important;
        padding-right: 4px !important;
      }

      .el-select,
      .el-select .el-input {
        width: 100% !important;
      }
    }

    /* 操作列确保可见 */
    .el-table__header-wrapper th:last-child:not(:first-child),
    td:last-child:not(:first-child) {
      min-width: 45px !important;
      width: 45px !important;
      padding: 0 !important;

      .cell {
        padding: 2px !important;
      }
    }

    /* 监听器列自适应占满剩余空间 */
    .el-table__header-wrapper th:nth-child(2),
    td:nth-child(2) {
      min-width: 100px;
      width: auto;
    }
  }

  .add-row-btn {
    height: 34px;
    font-size: 13px;
    letter-spacing: 1px;
    margin-top: 8px;
  }
}

/* ========== 响应式适配：超小屏 (<= 480px) ========== */
@media (max-width: 480px) {
  .dialogForm {
    margin: 4px 2px;
  }

  .form-section {
    margin-bottom: 16px;
  }

  .section-title {
    font-size: 13px;
    padding-bottom: 8px;
    margin-bottom: 12px;
  }

  :deep(.dialogForm .el-form-item__label),
  :deep(.el-form-item__label) {
    /* 超小屏右对齐 */
    width: 80px !important;
    min-width: 80px !important;
    font-size: 12px;
    text-align: right;
    justify-content: flex-end;
  }

  /* 超小屏错误提示间距 */
  ::deep(.el-form-item__error) {
    padding-top: 6px;
    padding-bottom: 4px;
  }

  :deep(.el-form-item) {
    margin-bottom: 12px;
  }

  :deep(.el-input__inner),
  :deep(.el-textarea__inner) {
    font-size: 12px !important;
  }

  .radio-card-group--compact {
    flex-direction: column; /* 纵向排列 */
    gap: 6px;
  }

  :deep(.radio-card) {
    padding: 8px 10px;

    .radio-card-desc {
      display: block !important;
      font-size: 9px !important;
      line-height: 1.2 !important;
      margin-top: 1px;
    }
  }

  :deep(.radio-card--sm) {
    width: 100% !important;
    padding: 8px 10px !important;

    .radio-card-desc {
      display: block !important;
      font-size: 9px !important;
      line-height: 1.2 !important;
      margin-top: 1px;
    }
  }

  .radio-card-content {
    gap: 6px;
  }

  .radio-card-icon {
    width: 26px;
    height: 26px;
    font-size: 16px;
  }

  .radio-card-icon--sm {
    width: 24px;
    height: 24px;
  }

  .radio-card-title {
    font-size: 12px;
  }

  .radio-card-desc {
    display: none;
  }

  :deep(.el-table) {
    min-width: 450px;
    font-size: 12px;

    th,
    td {
      padding: 4px 0 !important;
    }
  }

  .add-row-btn {
    height: 30px;
    font-size: 12px;
  }
}
</style>
