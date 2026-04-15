<template>
  <div class="user-select-wrapper">
    <!-- 现代化页签 -->
    <div class="modern-tabs-wrapper">
      <div class="modern-tabs">
        <div
          v-for="item in tabsList"
          :key="item"
          class="modern-tab-item"
          :class="{ 'is-active': tabsValue === item }"
          @click="tabsValue = item; tabChange()"
        >
          <span class="tab-label">{{ item }}</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <el-row :gutter="16" class="content">
      <!-- 左侧树状选择 -->
      <el-col :span="5" :xs="24" v-show="groupOptions">
        <div class="section-card tree-card" :class="{ 'tree-collapsed': treeCollapsed }">
          <div class="tree-header tree-header-clickable" @click="treeCollapsed = !treeCollapsed">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="tree-icon">
              <path d="M15 2H6C4.9 2 4 2.9 4 4V20C4 21.1 4.9 22 6 22H18C19.1 22 20 21.1 20 20V8L15 2ZM18 20H6V4H14V9H18V20ZM8.5 12H10V17H8.5V12ZM11 12H12.5V17H11V12ZM13.5 12H15V17H13.5V12Z" fill="currentColor"/>
            </svg>
            <span>组织架构</span>
            <span class="tree-collapse-toggle">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="toggle-arrow" :class="{ 'is-expanded': !treeCollapsed }">
                <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z" fill="currentColor"/>
              </svg>
            </span>
          </div>
          <transition name="tree-slide">
            <div class="tree-content" v-show="!treeCollapsed">
              <div class="tree-search">
            <el-input
              v-model="groupName"
              placeholder="搜索部门名称"
              clearable
              prefix-icon="Search"
              size="default"
            />
          </div>
          <div class="tree-body">
            <el-tree
              :data="groupOptions"
              :props="{ label: 'name', children: 'children' }"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="groupTreeRef"
              node-key="id"
              highlight-current
              default-expand-all
              @node-click="handleNodeClick"
            />
          </div>
          </div>
          </transition>
        </div>
      </el-col>

      <!-- 右侧列表数据 -->
      <el-col :span="groupOptions ? 19 : 24" :xs="24">
        <!-- 搜索卡片 -->
        <div class="section-card search-card" v-show="showSearch">
          <div class="search-card-body">
            <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="88px">
              <el-form-item label="权限编码" prop="handlerCode">
                <el-input
                  v-model="queryParams.handlerCode"
                  placeholder="请输入权限编码"
                  clearable
                  style="width: 200px"
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item label="权限名称" prop="handlerName">
                <el-input
                  v-model="queryParams.handlerName"
                  placeholder="请输入权限名称"
                  clearable
                  style="width: 200px"
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item label="创建时间">
                <el-date-picker
                  v-model="dateRange"
                  value-format="YYYY-MM-DD"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                ></el-date-picker>
              </el-form-item>
              <el-form-item>
                <button class="btn-action btn-primary" @click="handleQuery">搜索</button>
                <button class="btn-action btn-default" @click="resetQuery">重置</button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 已选提示（内嵌在表格卡片底部）- 智能显示策略 -->
        <div class="selected-inline-bar" v-if="checkedItemList.length > 0">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="bar-icon">
            <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z" fill="currentColor"/>
          </svg>
          <span>已选择 <strong>{{ checkedItemList.length }}</strong> 项</span>
          <div class="selected-mini-tags" v-if="checkedItemList.length <= 8">
            <el-tag
              closable
              v-for="tag in checkedItemList"
              :key="tag.storageId"
              @close="handleClose(tag.storageId)"
              class="mini-tag"
              size="small"
            >
              {{ tag.handlerName || tag.storageId }}
            </el-tag>
          </div>
          <div class="selected-mini-tags selected-overflow" v-else>
            <el-tag
              closable
              v-for="tag in visibleTags"
              :key="tag.storageId"
              @close="handleClose(tag.storageId)"
              class="mini-tag"
              size="small"
            >
              {{ tag.handlerName || tag.storageId }}
            </el-tag>
            <el-tooltip
              placement="top"
              effect="light"
              :offset="8"
              popper-class="selected-all-tooltip"
            >
              <template #content>
                <div class="tooltip-tag-list">
                  <div class="tooltip-item" v-for="tag in checkedItemList" :key="tag.storageId">
                    {{ tag.handlerName || tag.storageId }}
                  </div>
                </div>
              </template>
              <el-tag class="mini-tag more-tag" size="small">+{{ checkedItemList.length - visibleTagsCount }} 更多</el-tag>
            </el-tooltip>
          </div>
          <el-button link type="danger" size="small" class="clear-all-btn" @click="clearAllSelected">清空</el-button>
        </div>

        <!-- 数据表格卡片 -->
        <div class="section-card table-card">
          <el-table v-loading="loading" :data="tableList" @row-click="handleCheck">
            <el-table-column width="50" align="center">
              <template #header>
                <el-checkbox
                  v-model="checkAllInfo.isChecked"
                  :indeterminate="checkAllInfo.isIndeterminate"
                  @change="handleCheckAll"
                ></el-checkbox>
              </template>
              <template #default="scope">
                <el-checkbox v-model="scope.row.isChecked" @change="handleCheck(scope.row)"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column label="入库主键" align="center" key="storageId" prop="storageId" v-if="columns[0].visible" />
            <el-table-column label="权限编码" align="center" key="handlerCode" prop="handlerCode" v-if="columns[1].visible" :show-overflow-tooltip="true" />
            <el-table-column label="权限名称" align="center" key="handlerName" prop="handlerName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
            <el-table-column label="权限分组" align="center" key="groupName" prop="groupName" v-if="columns[3].visible" :show-overflow-tooltip="true" />
            <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[4].visible" width="160">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
          </el-table>

          <!-- 底部操作栏：分页 + 确定 -->
          <div class="table-footer">
            <el-pagination
              v-show="total > 0"
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              :total="total"
              small
              @size-change="getList"
              @current-change="getList"
            />
            <button class="btn-confirm" @click="submitForm">确 定</button>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="User">
import { handlerResult, handlerType } from "@/api/flow/definition.js";

const { proxy } = getCurrentInstance();

const props = defineProps({
  userVisible: {
    type: Boolean
  },
  selectUser: {
    type: Array,
    default: () => []
  },
  permissionRows: {
    type: Array,
    default: () => []
  }
});
const tabsValue = ref("");
const tableList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
let dateRange = ref([]);
const groupName = ref("");
const groupOptions = ref(undefined);
// 树形区域折叠状态（默认展开，数据加载后根据高度自动判断）
const treeCollapsed = ref(false);
const tabsList = ref([]);
// 列显隐信息
const columns = ref([
  { key: 0, label: `入库主键`, visible: true },
  { key: 1, label: `权限编码`, visible: true },
  { key: 2, label: `权限名称`, visible: true },
  { key: 3, label: `权限分组`, visible: true },
  { key: 4, label: `创建时间`, visible: true }
]);
const checkedItemList = ref([]); // 已选的itemList
// 已选标签显示策略：超过8个时只显示前3个 + "+N 更多"
const visibleTagsCount = 8;
const visibleTags = computed(() => checkedItemList.value.slice(0, visibleTagsCount));
const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    status: "0",
    groupId: undefined
  },
  checkAllInfo: {
    isIndeterminate: false,
    isChecked: false,
  }
});

const { queryParams, checkAllInfo } = toRefs(data);
const emit = defineEmits(["update:userVisible", "handleUserSelect"]);

/** 通过条件过滤节点  */
const filterNode = (value, data) => {
 if (!value) return true;
 return data.name.indexOf(value) !== -1;
};

/** 根据名称筛选部门树 */
watch(groupName, val => {
  proxy.$refs["groupTreeRef"].filter(val);
});
watch(() => props.selectUser, (val, oldVal) => {
  if (oldVal) {
    proxy.$nextTick(() => {
      checkedItemList.value = checkedItemList.value.filter(e => {
        let index = val ? val.findIndex(v => v === e.storageId) : -1;
        tableList.value.forEach(u => {
          if (u.storageId === e.storageId) u.isChecked = index !== -1;
        });
        return index !== -1;
      });
    });
  } else checkedItemList.value = val ? props.permissionRows.filter(n => n.storageId) : [];
},{ deep: true, immediate: true });

/** 获取tabs列表 */
function getTabsType() {
  handlerType().then(res => {
    tabsList.value = res.data;
    if(res.data && res.data.length > 0) {
        tabsValue.value = res.data[0]
    }
    getList();
  });
}

/** 查询用户列表 */
function getList() {
  loading.value = true;
  if (!tabsValue.value) {
    loading.value = false;
    return;
  }
  queryParams.value.handlerType = tabsValue.value;
  dateRange.value = Array.isArray(dateRange.value) ? dateRange.value : [];
  queryParams.value.beginTime = dateRange.value[0]
  queryParams.value.endTime = dateRange.value[1]
  handlerResult(queryParams.value).then(res => {
    loading.value = false;
    let handlerAuths = res.data.handlerAuths;
    handlerAuths.rows.forEach(item => {
      item.isChecked = checkedItemList.value.findIndex(e => e.storageId === item.storageId) !== -1;
    });
    tableList.value = handlerAuths.rows;
    total.value = handlerAuths.total;
    groupOptions.value = res.data.treeSelections;
    proxy.$nextTick(() => {
      if (groupName.value) proxy.$refs["groupTreeRef"].filter(groupName.value);
    });
    isCheckedAll();
  });
}
/** 节点单击事件 */
function handleNodeClick(data) {
  queryParams.value.groupId = data.id;
  handleQuery();
}

/** tab切换 */
function tabChange() {
  queryParams.value.groupId = undefined;
  resetQuery();
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  groupName.value = "";
  proxy.$refs.queryRef.resetFields();
  queryParams.value.groupId = undefined;
  proxy.$refs.groupTreeRef.setCurrentKey(null);
  handleQuery();
}

// 是否全选中
function isCheckedAll() {
  const len = tableList.value.length;
  let count = 0;
  tableList.value.map(item => {
    if (item.isChecked) count += 1;
  });
  checkAllInfo.value.isChecked = len === count && len > 0;
  checkAllInfo.value.isIndeterminate = count > 0 && count < len;
}

// 全选
function handleCheckAll() {
  const checkedArr = checkedItemList.value;
  checkAllInfo.value.isIndeterminate = false;
  if (checkAllInfo.value.isChecked) {
    tableList.value = tableList.value.map(item => {
      item.isChecked = true;
      if (checkedItemList.value.findIndex(e => e.storageId === item.storageId) === -1) {
        checkedArr.push({ storageId: item.storageId, handlerName: item.handlerName });
      }
      return item;
    });
  } else {
    tableList.value = tableList.value.map(item => {
      item.isChecked = false;
      let index = checkedArr.findIndex(e => e.storageId === item.storageId);
      if (index !== -1) checkedArr.splice(index, 1);
      return item;
    });
  }
  checkedItemList.value = checkedArr;
}

function handleCheck(row) {
  tableList.value.forEach(e => {
    if (e.storageId === row.storageId) e.isChecked = !e.isChecked;
  });
  const checkedArr = [...checkedItemList.value];
  if (row.isChecked) {
    if (checkedArr.findIndex(e => e.storageId === row.storageId) === -1) checkedArr.push({ storageId: row.storageId, handlerName: row.handlerName });
  } else {
    const index = checkedArr.findIndex(n => n.storageId === row.storageId);
    if (index !== -1) checkedArr.splice(index, 1);
  }
  checkedItemList.value = checkedArr;
  isCheckedAll();
};
// 删除标签
function handleClose(storageId) {
  tableList.value.forEach(e => {
    if (e.storageId === storageId) e.isChecked = !e.isChecked;
  });
  const checkedArr = checkedItemList.value
  const index = checkedArr.findIndex(n => n.storageId === storageId);
  if (index !== -1) checkedArr.splice(index, 1);
  checkedItemList.value = checkedArr;
  isCheckedAll();
}

// 清空所有已选
function clearAllSelected() {
  tableList.value.forEach(e => { e.isChecked = false; });
  checkedItemList.value = [];
  checkAllInfo.value.isChecked = false;
  checkAllInfo.value.isIndeterminate = false;
}

// 取消按钮
function cancel() {
  emit("update:userVisible", false);
}

// 提交按钮
function submitForm() {
  emit("handleUserSelect", checkedItemList.value);
  cancel();
}

getTabsType();
</script>

<style scoped lang="scss">
.user-select-wrapper {
  width: 100%;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
  html.dark & {
    background: var(--wf-bg-color, #141414);
    border-radius: var(--wf-radius-lg, 12px);
    padding: 8px;
  }
}

/* ========== 现代化页签 ========== */
.modern-tabs-wrapper {
  background: var(--wf-bg-white, #fff);
  border-radius: var(--wf-radius-lg, 12px);
  padding: 5px;
  box-shadow: var(--wf-shadow-sm, 0 1px 4px rgba(0, 0, 0, 0.04));
  margin-bottom: 12px;
  html.dark & { background: var(--wf-bg-color, #141414); }
}
.modern-tabs {
  display: flex; align-items: center; gap: 3px; overflow-x: auto;
  &::-webkit-scrollbar { height: 0; display: none; }
}
.modern-tab-item {
  position: relative;
  padding: 7px 18px;
  cursor: pointer;
  border-radius: var(--wf-radius, 8px);
  font-size: 13px; font-weight: 500;
  color: var(--wf-text-secondary, #909399);
  transition: all .25s cubic-bezier(.4, 0, .2, 1);
  user-select: none; white-space: nowrap; flex-shrink: 0;

  &:hover {
    color: var(--wf-primary, #409eff);
    background: var(--wf-primary-lighter, #f0f7ff);
    html.dark & { background: rgba(64,158,255,.08); }
  }
  &.is-active {
    color: #fff;
    background: linear-gradient(135deg, var(--wf-primary,#409eff), var(--wf-primary-dark,#2b7de9));
    box-shadow: 0 2px 6px rgba(64,158,255,.3);
    font-weight: 600; transform: translateY(-1px);
  }
  .tab-label { position: relative; z-index: 1; }
}

.content {
  margin: 0;
}

/* ========== 通用卡片 ========== */
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

/* ========== 左侧树形卡片（增强版 + 可折叠） ========== */
.tree-card {
  padding-bottom: 0;
  display: flex; flex-direction: column;
  max-height: calc(100vh - 180px);

  /* 折叠状态：只显示标题头 */
  &.tree-collapsed {
    height: auto;
    min-height: auto;
    .tree-header { border-bottom: none; border-radius: var(--wf-radius-lg,12px); }
  }

  .tree-header {
    display: flex; align-items: center; gap: 8px;
    padding: 13px 16px;
    font-size: 13px; font-weight: 600;
    color: var(--wf-text-primary, #303133);
    letter-spacing: .3px;
    background: linear-gradient(135deg, #f5f7fa, #ecf1f6);
    border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
    flex-shrink: 0;
    html.dark & {
      background: linear-gradient(135deg, rgba(120,128,140,.08), rgba(80,88,100,.05));
      border-bottom-color: var(--wf-border-color, #333333);
    }
    .tree-icon {
      width: 17px; height: 17px;
      color: var(--wf-primary, #409eff);
      flex-shrink: 0;
    }
  }

  /* 标题可点击 */
  .tree-header-clickable {
    cursor: pointer;
    user-select: none;
    transition: background .25s ease;

    &:hover {
      background: linear-gradient(135deg, rgba(64,158,255,.08), rgba(43,125,233,.04));
      html.dark & { background: linear-gradient(135deg, rgba(64,158,255,.1), rgba(43,125,233,.06)); }
    }
  }

  /* 折叠箭头 */
  .tree-collapse-toggle {
    margin-left: auto;
    flex-shrink: 0;
    width: 22px; height: 22px;
    display: flex; align-items: center; justify-content: center;
    color: var(--wf-text-secondary, #909399);
    opacity: 0.5;
    transition: all .25s ease;

    .toggle-arrow {
      width: 18px; height: 18px;
      transition: transform .35s cubic-bezier(.4,0,.2,1);

      &.is-expanded { transform: rotate(180deg); }
    }

    .tree-header-clickable:hover & {
      opacity: 1;
      color: var(--wf-primary, #409eff);
    }
  }

  /* 树内容区域（可折叠） */
  .tree-content {
    display: flex; flex-direction: column;
    flex: 1; overflow: hidden;
  }

  .tree-search {
    padding: 12px;
    border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
    flex-shrink: 0;

    ::v-deep(.el-input__wrapper) {
      border-radius: 8px;
      transition: all .25s ease;
      box-shadow: 0 0 0 1px var(--wf-border-color, #dcdfe6) inset;
      &:hover { box-shadow: 0 0 0 1px var(--wf-primary, #409eff) inset; }
      &.is-focus { box-shadow: 0 0 0 2px rgba(64,158,255,.15), 0 0 0 1px var(--wf-primary, #409eff) inset !important; }
    }
  }

  .tree-body {
    flex: 1; overflow-y: auto;
    padding: 10px 4px;

    &::-webkit-scrollbar { width: 5px; }
    &::-webkit-scrollbar-track { background: transparent; }
    &::-webkit-scrollbar-thumb {
      background: rgba(0,0,0,.14); border-radius: 5px;
      &:hover { background: rgba(0,0,0,.24); }
    }
    html.dark &::-webkit-scrollbar-thumb { background: rgba(255,255,255,.18); }

    ::v-deep(.el-tree) { background: transparent; }
    ::v-deep(.el-tree-node) { display: table; min-width: 100%; }
    ::v-deep(.el-tree-node__content:hover),
    ::v-deep(.el-tree-node.is-current > .el-tree-node__content) {
      color: var(--wf-primary, #409eff);
      background-color: var(--wf-primary-lighter, #f0f7ff);
      border-radius: 7px;
      html.dark & { background-color: rgba(64,158,255,.12); }
    }

    ::v-deep(.el-tree-node__expand-icon) {
      color: var(--wf-text-secondary, #909399);
      transition: transform .2s ease;
    }
    html.dark & {
      ::v-deep(.el-tree-node__content) { color: #c0c4cc; }
    }
  }
}

/* 树折叠动画 */
.tree-slide-enter-active,
.tree-slide-leave-active {
  transition: all .35s cubic-bezier(.4,0,.2,1);
  overflow: hidden;
}
.tree-slide-enter-from,
.tree-slide-leave-to {
  max-height: 0; opacity: 0; padding-top: 0; padding-bottom: 0;
}
.tree-slide-enter-to,
.tree-slide-leave-from {
  max-height: 2000px; opacity: 1;
}

/* ========== 搜索卡片 ========== */
.search-card {
  margin-bottom: 12px;
}
.search-card-body {
  padding: 16px 18px;
  background: var(--wf-bg-color, #fafbfc);
  html.dark & { background: var(--wf-bg-color, #141414); }

  .el-form-item {
    margin-bottom: 8px;
    margin-right: 16px;
    &__label {
      font-weight: 500;
      color: var(--wf-text-regular, #606266);
      font-size: 13px;
      white-space: nowrap;
      &::after { content: '：'; }
    }
  }

  /* 输入框统一风格 */
  .el-input__inner,
  .el-date-editor {
    border-radius: 6px !important;
    transition: all .25s ease;

    &:focus {
      box-shadow: 0 0 0 2px rgba(64,158,255,.15);
    }
  }
}

/* 按钮风格 */
.btn-action {
  display: inline-flex; align-items: center; justify-content: center;
  height: 32px; padding: 0 16px;
  border: 1.5px solid transparent;
  border-radius: var(--wf-radius, 8px);
  font-size: 13px; font-weight: 500;
  cursor: pointer; transition: all .25s ease;
  outline: none;

  &.btn-primary {
    color: #fff;
    background: linear-gradient(135deg, var(--wf-primary,#409eff), var(--wf-primary-dark,#2b7de9));
    &:hover { transform: translateY(-1px); box-shadow: 0 2px 8px rgba(64,158,255,.35); }
  }
  &.btn-default {
    color: var(--wf-text-regular, #606266);
    background: var(--wf-bg-white, #fff);
    border-color: var(--wf-border-color, #dcdfe6);
    &:hover { border-color: var(--wf-primary, #409eff); color: var(--wf-primary, #409eff); background: var(--wf-primary-lighter, #f0f7ff); }
    html.dark & { background: var(--wf-bg-color, #1a1a1a); border-color: var(--wf-border-color, #444); }
  }
}

/* ========== 已选提示（内嵌在表格上方 - 智能显示） ========== */
.selected-inline-bar {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 16px;
  background: linear-gradient(135deg, rgba(103,194,58,.06), rgba(82,155,46,.03));
  border-bottom: 1px solid rgba(103,194,58,.12);
  font-size: 13px; color: #67c23a;
  flex-wrap: wrap;

  .bar-icon { width: 16px; height: 16px; flex-shrink: 0; }
  strong { font-weight: 600; margin: 0 2px; }

  .selected-mini-tags {
    display: inline-flex; align-items: center; gap: 6px; margin-left: auto;

    .mini-tag {
      border-radius: 12px; font-size: 11px;
      background: linear-gradient(135deg, rgba(103,194,58,.1), rgba(103,194,58,.05));
      border-color: rgba(103,194,58,.25); color: #529b2e;
      transition: all .2s ease;

      &:hover {
        background: linear-gradient(135deg, rgba(103,194,58,.18), rgba(103,194,58,.1));
        box-shadow: 0 1px 6px rgba(103,194,58,.15);
      }
    }

    /* 溢出模式：显示 "+N 更多" */
    &.selected-overflow { margin-left: auto; }
  }

  /* "+N 更多" 标签 */
  .more-tag {
    cursor: pointer;
    border-style: dashed !important;
    font-size: 11px !important;
    color: var(--wf-primary, #409eff) !important;
    border-color: var(--wf-primary, #409eff) !important;
    background: linear-gradient(135deg, rgba(64,158,255,.08), rgba(64,158,255,.04)) !important;

    &:hover {
      background: linear-gradient(135deg, rgba(64,158,255,.15), rgba(64,158,255,.08)) !important;
      box-shadow: 0 1px 6px rgba(64,158,255,.15) !important;
    }
  }

  .clear-all-btn {
    font-size: 12px; padding: 0 4px; flex-shrink: 0;
    &:hover { color: #f56c6c !important; }
  }
}

/* tooltip 全部列表弹窗 */
.selected-all-tooltip {
  max-width: 260px;
  padding: 10px 14px !important;
  border-radius: 10px !important;
  border: 1px solid rgba(103,194,58,.18) !important;
  box-shadow: 0 4px 16px rgba(0,0,0,.12) !important;

  .tooltip-tag-list {
    max-height: 200px;
    overflow-y: auto;
    display: flex; flex-direction: column; gap: 5px;

    &::-webkit-scrollbar { width: 4px; }
    &::-webkit-scrollbar-thumb { background: rgba(0,0,0,.12); border-radius: 4px; }
  }

  .tooltip-item {
    padding: 3px 8px;
    border-radius: 6px;
    font-size: 12px;
    color: #606266;
    background: rgba(103,194,58,.06);

    &:nth-child(even) { background: rgba(103,194,58,.02); }
  }
}

/* ========== 表格卡片（增强版） ========== */
.table-card {
  .el-table {
    --el-table-border-color: var(--wf-border-lighter, #ebeef5);
    --el-table-header-bg-color: var(--wf-bg-color, #fafbfc);

    th.el-table__cell {
      background: var(--wf-bg-color, #fafbfc) !important;
      font-weight: 600;
      font-size: 13px;
      color: var(--wf-text-primary, #303133);
      padding: 12px 0;
      html.dark & { background: var(--wf-bg-color, #1a1a1a)!important; color: #c0c4cc; }
    }
    td.el-table__cell {
      font-size: 13px;
      padding: 10px 0;
    }
    tr:hover > td.el-table__cell {
      background-color: var(--wf-primary-lighter, #f0f7ff) !important;
      html.dark & { background-color: rgba(64,158,255,.05)!important; }
    }

    /* 复选框列美化 */
    .el-checkbox__inner {
      border-radius: 4px;
      transition: all .2s ease;
    }

    /* 空状态美化 */
    .el-table__empty-block { min-height: 200px; }
  }

  /* 表格外边框圆角 */
  ::v-deep(.el-table--border),
  ::v-deep(.el-table--group) {
    border-radius: 8px;
    overflow: hidden;
  }
}

.table-footer {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 16px 14px;
  margin-top: 12px;

  .el-pagination {
    justify-content: flex-start;
    .el-pagination__rightwrapper { flex: none; }
  }
}

.btn-confirm {
  display: inline-flex; align-items: center; justify-content: center;
  height: 36px; padding: 0 28px;
  border: none; border-radius: var(--wf-radius, 8px);
  font-size: 14px; font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, var(--wf-primary, #409eff), var(--wf-primary-dark, #2b7de9));
  cursor: pointer; transition: all .3s ease;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);
  white-space: nowrap;
  letter-spacing: 1px;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 14px rgba(64, 158, 255, 0.35);
  }
  &:active {
    transform: translateY(0);
  }
}

/* 分页器美化 */
::deep(.el-pagination) {
  --el-pagination-button-bg-color: transparent;
  html.dark & {
    --el-text-color-regular: #c0c4cc;
    --el-fill-color-blank: #141414;
    --el-border-color: #414243;
  }
  .btn-prev, .btn-next {
    border-radius: 6px;
    border: 1px solid var(--wf-border-color, #dcdfe6);
    &:hover { color: var(--wf-primary, #409eff); }
  }
  .el-pager li {
    border-radius: 6px;
    background: transparent !important;
    color: var(--wf-text-regular, #606266);
    min-width: 28px; line-height: 24px;
    &.is-active { background: var(--wf-primary, #409eff) !important; color: #fff !important; }
    &:hover { color: var(--wf-primary, #409eff); }
  }
}

html.dark ::v-deep(.el-pager) li {
  background: #1a1a1a !important;
  color: #c0c4cc !important;
  border: 1px solid #414243;
  min-width: 28px;
  &.is-active { background: var(--wf-primary, #409eff) !important; color: #fff !important; border-color: transparent; }
  &:hover { color: var(--wf-primary, #409eff) !important; background: rgba(64,158,255,.08) !important; }
}
</style>

<!-- 非 scoped 全局样式：用于 append-to-body 的 dialog 内元素暗黑模式 -->
<style lang="scss">
/* 分页器页码按钮 - 暗黑模式 */
html.dark .el-pagination--small .el-pager li,
html.dark .el-pager li.number {
  background: #1a1a1a !important;
  color: #c0c4cc !important;
  border: 1px solid #414243;
  min-width: 28px;
  line-height: 24px;
}
html.dark .el-pagination--small .el-pager li.is-active,
html.dark .el-pager li.is-active {
  background: var(--wf-primary, #409eff) !important;
  color: #fff !important;
  border-color: transparent !important;
}
html.dark .el-pagination--small .el-pager li:hover,
html.dark .el-pager li.number:hover:not(.is-active) {
  color: var(--wf-primary, #409eff) !important;
  background: rgba(64,158,255,.08) !important;
}

/* el-tree 树节点区域 - 暗黑模式 */
html.dark .el-tree { background-color: transparent !important; }
html.dark .el-tree-node__content { color: #c0c4cc !important; }
html.dark .el-tree-node__content:hover,
html.dark .el-tree-node.is-current > .el-tree-node__content {
  background-color: rgba(64,158,255,.12) !important;
}

/* 分页器前后按钮 - 暗黑模式 */
html.dark .el-pagination .btn-prev,
html.dark .el-pagination .btn-next {
  background-color: transparent !important;
  border-color: #414243 !important;
  color: #c0c4cc !important;
}
html.dark .el-pagination .btn-prev:hover,
html.dark .el-pagination .btn-next:hover {
  color: var(--wf-primary, #409eff) !important;
}
</style>
