<template>
  <div class="start">
    <!-- 现代化页签 -->
    <div class="modern-tabs-wrapper">
      <div class="modern-tabs">
        <div
          v-for="item in tabsList"
          :key="item.name"
          class="modern-tab-item"
          :class="{ 'is-active': tabsValue === item.name }"
          @click="tabsValue = item.name"
        >
          <span class="tab-label">{{ item.label }}</span>
        </div>
      </div>
    </div>

    <!-- 基础设置 -->
    <div v-show="tabsValue === '1'" class="tabPane">
      <el-form ref="formRef" class="startForm" :model="form" label-width="110px" size="small" :disabled="disabled">
        <div class="base-settings-section">
          <div class="base-settings-header">
            <span class="ext-attributes-icon ext-icon-base">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M19 3H5C3.89 3 3 3.89 3 5V19C3 20.11 3.89 21 5 21H19C20.11 21 21 20.11 21 19V5C21 3.89 20.11 3 19 3ZM12 6C13.93 6 15.5 7.57 15.5 9.5C15.5 11.43 13.93 13 12 13C10.07 13 8.5 11.43 8.5 9.5C8.5 7.57 10.07 6 12 6ZM18 18H6V17C6 14.67 10.33 13.34 12 13.34C13.67 13.34 18 14.67 18 17V18Z" fill="currentColor"/>
              </svg>
            </span>
            <span class="base-settings-title">基础配置</span>
          </div>
          <div class="base-settings-content">
            <slot name="form-item-task-name" :model="form" field="nodeCode">
              <el-form-item label="节点编码：">
                <el-input v-model="form.nodeCode" :disabled="disabled"></el-input>
              </el-form-item>
            </slot>
            <slot name="form-item-task-name" :model="form" field="nodeName">
              <el-form-item label="节点名称：">
                <el-input v-model="form.nodeName" ref="nodeInput" :disabled="disabled" @change="nodeNameChange"></el-input>
              </el-form-item>
            </slot>
          </div>
        </div>
      </el-form>
    </div>

    <!-- 监听器 -->
    <div v-show="tabsValue === '2'" class="tabPane tabPane-full">
      <div class="section-card section-purple">
        <div class="section-card-header section-purple-header">
          <span class="section-card-icon section-purple-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 6H16V4C16 2.89 15.11 2 14 2H10C8.89 2 8 2.89 8 4V6H2V8H4V19C4 20.11 4.89 21 6 21H18C19.11 21 20 20.11 20 19V8H22V6ZM10 4H14V6H10V4ZM18 19H6V8H18V19ZM10 10H14V17H10V10Z" fill="currentColor"/>
            </svg>
          </span>
          <span class="section-card-title section-purple-title">监听器配置</span>
        </div>
        <div class="section-card-body">
          <slot name="form-item-task-listenerType" :model="form" field="listenerType">
            <el-table :data="form.listenerRows" style="width: 100%">
              <el-table-column prop="listenerType" label="类型" width="100">
                <template #default="scope">
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerType'">
                    <el-select v-model="scope.row.listenerType" placeholder="请选择">
                      <el-option label="开始" value="start"></el-option>
                      <el-option label="分派" value="assignment"></el-option>
                      <el-option label="完成" value="finish"></el-option>
                      <el-option label="创建" value="create"></el-option>
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column prop="listenerPath" label="路径（可输入类路径）">
                <template #default="scope">
                  <el-form-item :prop="'listenerRows.' + scope.$index + '.listenerPath'">
                    <el-input v-model="scope.row.listenerPath" placeholder="请输入"></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="55" v-if="!disabled">
                <template #default="scope">
                  <el-button type="danger" :icon="Delete" @click="handleDeleteRow(scope.$index)"/>
                </template>
              </el-table-column>
            </el-table>
          </slot>
          <div class="action-buttons">
            <el-button v-if="!disabled" type="primary" style="margin-top: 10px;" @click="handleAddRow">增加行</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="Start">
import { Delete } from '@element-plus/icons-vue'

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

const tabsList = ref([
  { name: "1", label: "基础设置" },
  { name: "2", label: "监听器" }
]);

const tabsValue = ref("1");
const form = ref(props.modelValue);
const emit = defineEmits(["change"]);
const { proxy } = getCurrentInstance();

watch(() => form, n => {
  if (n) {
    emit('change', n)
  }
},{ deep: true });

function nodeNameChange() {
  proxy.$refs.nodeInput.focus();
}

if (form.value.listenerType) {
  const listenerTypes = form.value.listenerType.split(",");
  const listenerPaths = form.value.listenerPath.split("@@");
  form.value.listenerRows = listenerTypes.map((type, index) => ({
    listenerType: type,
    listenerPath: listenerPaths[index]
  }));
}

// 增加行
function handleAddRow() {
  form.value.listenerRows.push({ listenerType: '', listenerPath: '' });
}

// 删除行
function handleDeleteRow(index) {
  form.value.listenerRows.splice(index, 1);
}
</script>

<style scoped lang="scss">
.start { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.startForm { border-top: 0; width: 100%; }

/* ========== 现代化页签 ========== */
.modern-tabs-wrapper {
  background: var(--wf-bg-white, #fff);
  border-radius: var(--wf-radius-lg, 12px);
  padding: 5px;
  box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  margin-bottom: 0;
  html.dark & { background: var(--wf-bg-color, #141414); }
}
.modern-tabs {
  display: flex; align-items: center; gap: 3px;
  overflow-x: auto; &::-webkit-scrollbar { height: 0; display: none; }
}
.modern-tab-item {
  position: relative;
  padding: 8px 16px;
  cursor: pointer;
  border-radius: var(--wf-radius, 8px);
  font-size: 13px; font-weight: 500;
  color: var(--wf-text-secondary, #909399);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none; white-space: nowrap; flex-shrink: 0;

  &:hover {
    color: var(--wf-primary, #409eff);
    background: var(--wf-primary-lighter, #f0f7ff);
    html.dark & { background: rgba(64,158,255,.08); }
  }
  &.is-active {
    color: #fff;
    background: linear-gradient(135deg, var(--wf-primary,#409eff) 0%, var(--wf-primary-dark,#2b7de9) 100%);
    box-shadow: 0 2px 6px rgba(64,158,255,.3);
    font-weight: 600; transform: translateY(-1px);
  }
  .tab-label { position: relative; z-index: 1; }
}

.tabPane {
  padding: 16px 2px 8px 2px;
  border: none; background: transparent;
  animation: fadeIn .2s ease;
}
.tabPane-full { padding: 16px 2px 8px 2px; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ========== 基础配置卡片 ========== */
.base-settings-section {
  border-radius: var(--wf-radius-lg, 12px);
  overflow: hidden;
  border: 1px solid var(--wf-border-light, #e4e7ed);
  background: var(--wf-bg-white, #fff);
  box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  transition: all 0.3s ease;
  html.dark & {
    border-color: var(--wf-border-color, #333333);
    background: var(--wf-bg-color, #141414);
  }
  &:hover {
    box-shadow: var(--wf-shadow, 0 2px 12px rgba(0, 0, 0, 0.06));
    html.dark & { box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3); }
  }
}
.base-settings-header {
  display: flex; align-items: center; gap: 8px; padding: 12px 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
  html.dark & {
    background: linear-gradient(135deg, rgba(120,128,140,.06) 0%, rgba(80,88,100,.04) 100%);
    border-bottom-color: var(--wf-border-color, #333333);
  }
}
.ext-icon-base { color: var(--wf-text-regular, #606266); svg { width: 16px; height: 16px; } }
.base-settings-title { font-size: 13px; font-weight: 600; color: var(--wf-text-primary, #303133); letter-spacing: .3px; }
.base-settings-content {
  padding: 14px 16px;
  background: var(--wf-bg-color, #fafbfc);
  html.dark & { background: var(--wf-bg-color, #141414); }
}

/* ========== 监听器卡片（紫色主题） ========== */
.section-card {
  border-radius: var(--wf-radius-lg, 12px);
  overflow: hidden;
  border: 1px solid var(--wf-border-light, #e4e7ed);
  background: var(--wf-bg-white, #fff);
  box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  transition: all 0.3s ease;
  html.dark & {
    border-color: var(--wf-border-color, #333333);
    background: var(--wf-bg-color, #141414);
  }
  &:hover {
    box-shadow: var(--wf-shadow, 0 2px 12px rgba(0, 0, 0, 0.06));
    html.dark & { box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3); }
  }
}
.section-card-header {
  display: flex; align-items: center; gap: 8px; padding: 12px 16px;
  border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
  html.dark & { border-bottom-color: var(--wf-border-color, #333333); }
}
.section-card-icon { width: 20px; height: 20px; display: flex; align-items: center; justify-content: center; svg { width: 16px; height: 16px; } }
.section-card-title { font-size: 13px; font-weight: 600; color: var(--wf-text-primary, #303133); letter-spacing: .3px; }
.section-card-body {
  padding: 14px 16px;
  background: var(--wf-bg-color, #fafbfc);
  html.dark & { background: var(--wf-bg-color, #141414); }
}
.action-buttons { display: flex; gap: 8px; margin-top: 12px; }

/* 紫色主题 */
.section-purple {
  border-color: rgba(137,96,220,.25);
  html.dark & { border-color: rgba(137,96,220,.2); }
  .section-purple-header {
    background: linear-gradient(135deg, rgba(137,96,220,.06) 0%, rgba(115,78,190,.03) 100%);
    border-bottom-color: rgba(137,96,220,.12);
    html.dark & { background: linear-gradient(135deg, rgba(137,96,220,.08) 0%, rgba(115,78,190,.05) 100%); }
  }
  .section-purple-icon { color: #8960dc; }
  .section-purple-title { color: #8960dc; }
  &:hover { box-shadow: 0 2px 12px rgba(137,96,220,.1); }
}

/* 表格内表单左对齐 */
::deep(.el-table .el-form-item) {
  margin-bottom: 0;
  .el-form-item__label {
    display: none !important; width: 0 !important; min-width: 0 !important;
    padding-right: 0 !important; overflow: hidden !important; visibility: hidden !important;
    height: 0 !important;
  }
  .el-form-item__content {
    display: block !important; margin-left: 0 !important;
  }
}
::deep(.listenerItem) .el-table .el-form-item { margin-bottom: 0; }
</style>