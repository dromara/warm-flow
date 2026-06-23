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
          <svg class="tab-icon" viewBox="0 0 24 24"><path :d="item.iconPath" fill="currentColor"/></svg>
          <span class="tab-label">{{ item.label }}</span>
        </div>
      </div>
    </div>

    <!-- 基础设置 -->
    <div v-show="tabsValue === '1'" class="tabPane">
      <el-form ref="formRef" class="startForm" :model="form" label-width="110px" :disabled="disabled">
        <div class="base-settings-section">
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
          <div class="section-card-body">
          <slot name="form-item-task-listenerType" :model="form" field="listenerType">
            <el-table :data="form.listenerRows" style="width: 100%">
              <el-table-column prop="listenerType" label="类型" :width="isMobile ? 60 : 160">
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
              <el-table-column label="操作" width="65" align="center" v-if="!disabled">
                <template #default="scope">
                  <el-button link size="small" type="danger" :icon="Delete" @click="handleDeleteRow(scope.$index)"/>
                </template>
              </el-table-column>
            </el-table>
          </slot>
          <div class="action-buttons">
            <el-button v-if="!disabled" class="add-row-btn" @click="handleAddRow">增加行</el-button>
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

// Tab 图标（单路径 SVG，viewBox 0 0 24 24，跟随 tab 文字色 currentColor）
const TAB_ICONS = {
  base: 'M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z',
  listener: 'M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z',
};
const tabsList = ref([
  { name: "1", label: "基础设置", iconPath: TAB_ICONS.base },
  { name: "2", label: "监听器", iconPath: TAB_ICONS.listener }
]);

const tabsValue = ref("1");
const form = ref(props.modelValue);
const emit = defineEmits(["change"]);

// 移动端/平板检测（与 between / baseInfo 统一）
const isMobile = computed(() => {
  if (typeof window === 'undefined') return false;
  return window.innerWidth <= 768;
});
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
@import '@/assets/styles/_common.scss';

.start { width: 100%; html.dark & { background: var(--wf-bg-color, #141414); border-radius: var(--wf-radius-lg, 12px); } }
.startForm { border-top: 0; width: 100%; }

/* 引入公共样式：现代化页签 + 基础配置卡片 + 监听器卡片 */
@include modern-tabs;
@include base-settings-card;
@include section-card;
@include responsive-adaption;

/* 紫色主题 - 监听器（扁平：保留紫色标题/图标做语义，去卡片底/边框/渐变头） */
.section-purple {
  .section-purple-header {
    background: transparent;
    border-bottom-color: var(--wf-border-lighter, #ebeef5);
    html.dark & { background: transparent; border-bottom-color: var(--wf-border-color, #333333); }
  }
  .section-purple-icon { color: #8960dc; }
  .section-purple-title { color: #8960dc; }
}

/* 增加行按钮：虚线主色（与 baseInfo / between 统一） */
.action-buttons {
  margin-top: 12px;
}
.add-row-btn {
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

/* 表格内表单左对齐 */
:deep(.el-table .el-form-item) {
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
:deep(.listenerItem) .el-table .el-form-item { margin-bottom: 0; }
</style>