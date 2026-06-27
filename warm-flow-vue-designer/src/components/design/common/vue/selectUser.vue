<template>
  <div class="user-select-wrapper">
    <!-- 现代化页签（无分组数据时隐藏，避免空白条） -->
    <div class="modern-tabs-wrapper" v-if="tabsList.length > 0">
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
    <wf-row :gutter="16" class="content">
      <!-- 左侧树状选择 -->
      <wf-col :span="5" :xs="24" v-show="groupOptions">
        <div class="section-card tree-card" :class="{ 'tree-collapsed': treeCollapsed }">
          <div class="tree-header tree-header-clickable" @click="treeCollapsed = !treeCollapsed">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="tree-icon">
              <path d="M15 2H6C4.9 2 4 2.9 4 4V20C4 21.1 4.9 22 6 22H18C19.1 22 20 21.1 20 20V8L15 2ZM18 20H6V4H14V9H18V20ZM8.5 12H10V17H8.5V12ZM11 12H12.5V17H11V12ZM13.5 12H15V17H13.5V12Z" fill="currentColor"/>
            </svg>
            <span>{{ t('selectUser.orgTree') }}</span>
            <span class="tree-collapse-toggle">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="toggle-arrow" :class="{ 'is-expanded': !treeCollapsed }">
                <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z" fill="currentColor"/>
              </svg>
            </span>
          </div>
          <transition name="tree-slide">
            <div class="tree-content" v-show="!treeCollapsed">
              <div class="tree-search">
            <wf-input
              v-model="groupName"
              :placeholder="t('selectUser.searchDept')"
              clearable
              size="default"
            >
              <template #prefix><svg-icon icon-class="ep:search"/></template>
            </wf-input>
          </div>
          <div class="tree-body">
            <wf-tree
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
      </wf-col>

      <!-- 右侧列表数据 -->
      <wf-col :span="groupOptions ? 19 : 24" :xs="24">
        <!-- 工具栏：筛选 + 确定（移动端） -->
        <div class="search-toggle-row mobile-only">
          <button class="search-toggle-btn" @click="searchCollapsed = !searchCollapsed" :class="{ 'is-expanded': !searchCollapsed }">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="search-icon"><path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0016 9.5 6.5 6.5 0 109.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" fill="currentColor"/></svg>
            <span>{{ searchCollapsed ? t('selectUser.filter') : t('selectUser.filterExpanded') }}</span>
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="toggle-arrow-sm" :class="{ 'is-rotated': !searchCollapsed }"><path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z" fill="currentColor"/></svg>
          </button>
          <button class="btn-confirm btn-confirm-inline mobile-only" @click="submitForm">{{ t('common.confirm') }}</button>
        </div>
        <div class="section-card search-card" v-show="showSearch && !searchCollapsed">
          <div class="search-card-body">
            <wf-form :model="queryParams" ref="queryRef" :inline="true" label-width="88px">
              <wf-form-item :label="t('selectUser.permCode')" prop="handlerCode">
                <wf-input
                  v-model="queryParams.handlerCode"
                  :placeholder="t('selectUser.permCodePlaceholder')"
                  clearable
                  style="width: 200px"
                  @keyup.enter="handleQuery"
                />
              </wf-form-item>
              <wf-form-item :label="t('between.handlerName')" prop="handlerName">
                <wf-input
                  v-model="queryParams.handlerName"
                  :placeholder="t('selectUser.permNamePlaceholder')"
                  clearable
                  style="width: 200px"
                  @keyup.enter="handleQuery"
                />
              </wf-form-item>
              <wf-form-item :label="t('selectUser.createTime')">
                <wf-date-picker
                  v-model="dateRange"
                  value-format="YYYY-MM-DD"
                  type="daterange"
                  range-separator="-"
                  :start-placeholder="t('selectUser.startDate')"
                  :end-placeholder="t('selectUser.endDate')"
                ></wf-date-picker>
              </wf-form-item>
            </wf-form>
            <div class="search-action-row">
              <div class="search-action-left">
                <button type="button" class="btn-action btn-primary" @click="handleQuery">{{ t('common.search') }}</button>
                <button type="button" class="btn-action btn-default" @click="resetQuery">{{ t('common.reset') }}</button>
              </div>
              <button class="btn-confirm btn-confirm-search" @click="submitForm">{{ t('common.confirm') }}</button>
            </div>
          </div>
        </div>

        <!-- 已选提示（内嵌在表格卡片底部）- 智能显示策略 -->
        <div class="selected-inline-bar" v-if="checkedItemList.length > 0">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="bar-icon">
            <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z" fill="currentColor"/>
          </svg>
          <span>{{ t('selectUser.selectedPrefix') }} <strong>{{ checkedItemList.length }}</strong> {{ t('selectUser.selectedUnit') }}</span>
          <div class="selected-mini-tags" v-if="checkedItemList.length <= 8">
            <wf-tag
              closable
              v-for="tag in checkedItemList"
              :key="tag.storageId"
              @close="handleClose(tag.storageId)"
              class="mini-tag"
              size="small"
            >
              {{ tag.handlerName || tag.storageId }}
            </wf-tag>
          </div>
          <div class="selected-mini-tags selected-overflow" v-else>
            <wf-tag
              closable
              v-for="tag in visibleTags"
              :key="tag.storageId"
              @close="handleClose(tag.storageId)"
              class="mini-tag"
              size="small"
            >
              {{ tag.handlerName || tag.storageId }}
            </wf-tag>
            <wf-tooltip
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
              <wf-tag class="mini-tag more-tag" size="small">+{{ checkedItemList.length - visibleTagsCount }} {{ t('selectUser.more') }}</wf-tag>
            </wf-tooltip>
          </div>
          <wf-button link type="danger" size="small" class="clear-all-btn" @click="clearAllSelected">{{ t('selectUser.clear') }}</wf-button>
        </div>

        <!-- 移动端：卡片式列表 -->
        <div class="mobile-card-list mobile-only" v-loading="loading" @scroll="onMobileScroll">
          <div
            v-for="item in tableList"
            :key="item.storageId"
            class="mobile-card-item"
            :class="{ 'is-checked': item.isChecked }"
            @click="handleCheck(item)"
          >
            <div class="card-left">
              <wf-checkbox :model-value="item.isChecked" @change.stop="handleCheck(item)" />
              <div class="card-info">
                <div class="card-name-row">
                  <span class="card-name">{{ item.handlerName || '-' }}</span>
                  <span class="card-time" v-if="item.createTime">{{ item.createTime }}</span>
                </div>
                <div class="card-sub">
                  <span class="card-code">{{ item.handlerCode || '-' }}</span>
                  <span class="card-group" v-if="item.groupName">{{ item.groupName }}</span>
                </div>
              </div>
            </div>
            <div class="card-check-icon" v-if="item.isChecked">
              <svg viewBox="0 0 24 24" fill="none"><path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z" fill="#67c23a"/></svg>
            </div>
          </div>
          <!-- 加载更多 / 没有更多提示 -->
          <div class="mobile-load-more">
            <template v-if="tableList.length > 0">
              <span v-if="mobileLoadingMore" class="load-text">{{ t('selectUser.loadingMore') }}</span>
              <span v-else-if="mobileNoMore || tableList.length >= total.value" class="load-text load-end">{{ t('selectUser.loadedAll') }}</span>
            </template>
          </div>
          <!-- 空状态 -->
          <div v-if="!loading && tableList.length === 0" class="mobile-empty">
            {{ t('common.empty') }}
          </div>
        </div>

        <!-- PC端：数据表格卡片（固定10条数据高度） -->
        <div class="section-card table-card pc-only">
          <wf-table v-loading="loading" :data="tableList" :max-height="570" @row-click="handleCheck">
            <template #empty>
              <div class="user-empty">
                <svg viewBox="0 0 24 24" class="user-empty-icon"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" fill="currentColor"/></svg>
                <span>{{ t('selectUser.emptyUsers') }}</span>
              </div>
            </template>
            <wf-table-column width="50" align="center">
              <template #header>
                <wf-checkbox
                  v-model="checkAllInfo.isChecked"
                  :indeterminate="checkAllInfo.isIndeterminate"
                  @change="handleCheckAll"
                ></wf-checkbox>
              </template>
              <template #default="scope">
                <!-- 仅展示选中态，选择统一由整行点击(@row-click=handleCheck)驱动，避免与行点击重复触发（EP/antd 一致） -->
                <wf-checkbox :model-value="scope.row.isChecked"></wf-checkbox>
              </template>
            </wf-table-column>
            <wf-table-column :label="t('between.handlerName')" align="center" key="handlerName" prop="handlerName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
            <wf-table-column :label="t('selectUser.permCode')" align="center" key="handlerCode" prop="handlerCode" v-if="columns[1].visible" :show-overflow-tooltip="true" />
            <wf-table-column :label="t('between.handlerStorageId')" align="center" key="storageId" prop="storageId" v-if="columns[0].visible" class-name="mobile-hide-col" />
            <wf-table-column :label="t('selectUser.permGroup')" align="center" key="groupName" prop="groupName" v-if="columns[3].visible" :show-overflow-tooltip="true" class-name="mobile-hide-col" />
            <wf-table-column :label="t('selectUser.createTime')" align="center" prop="createTime" v-if="columns[4].visible" width="160" class-name="mobile-hide-col">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </wf-table-column>
          </wf-table>

          <!-- 底部操作栏：分页 -->
          <div class="table-footer">
            <wf-pagination
              v-show="total > 0"
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              :total="total"
              small
              @size-change="getList"
              @current-change="getList"
            />
          </div>
        </div>
      </wf-col>
    </wf-row>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, reactive, ref, toRefs, watch } from 'vue';
import { handlerResult, handlerType } from "@/api/flow/definition";
// 库化后不再依赖 ruoyi 全局注册的 parseTime，显式引入供模板格式化「创建时间」列
import { parseTime } from "@/utils/ruoyi";
import { useI18n } from '@/i18n';

defineOptions({ name: 'User' });

const { proxy } = getCurrentInstance()!;
const { t } = useI18n();

interface SelectUserProps {
  /** 弹窗显隐（v-model:userVisible） */
  userVisible?: boolean;
  /** 已选用户存储主键集合（v-model:selectUser） */
  selectUser?: any[];
  /** 已选办理人行（回显用） */
  permissionRows?: any[];
}
const props = withDefaults(defineProps<SelectUserProps>(), {
  userVisible: false,
  selectUser: () => [],
  permissionRows: () => [],
});
const tabsValue = ref("");
const tableList = ref<any[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
let dateRange = ref<any[]>([]);
const groupName = ref("");
const groupOptions = ref<any>(undefined);
// 树形区域折叠状态（默认展开，数据加载后根据高度自动判断）
const treeCollapsed = ref(window.innerWidth <= 768);
// 搜索区域折叠状态（移动端默认折叠）
const searchCollapsed = ref(window.innerWidth <= 768);
// 移动端分页：每页10条，滚动加载更多
const MOBILE_PAGE_SIZE = 10;
const mobileLoadingMore = ref(false);
const mobileNoMore = ref(false);
const tabsList = ref<any[]>([]);
// 列显隐信息
const columns = ref([
  { key: 0, label: t('between.handlerStorageId'), visible: true },
  { key: 1, label: t('selectUser.permCode'), visible: true },
  { key: 2, label: t('between.handlerName'), visible: true },
  { key: 3, label: t('selectUser.permGroup'), visible: true },
  { key: 4, label: t('selectUser.createTime'), visible: true }
]);
const checkedItemList = ref<any[]>([]); // 已选的itemList
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
  } as Record<string, any>,
  checkAllInfo: {
    isIndeterminate: false,
    isChecked: false,
  }
});

const { queryParams, checkAllInfo } = toRefs(data);
const emit = defineEmits<{
  (e: 'update:userVisible', visible: boolean): void;
  (e: 'handleUserSelect', checkedItemList: any[]): void;
}>();

/** 通过条件过滤节点  */
const filterNode = (value: string, data: any) => {
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
    // 移动端初始加载使用10条分页
    if (window.innerWidth <= 768 && !queryParams.value.pageSize) {
      queryParams.value.pageSize = MOBILE_PAGE_SIZE;
      queryParams.value.pageNum = 1;
    }
    getList();
  }).catch(() => {
    // 接口异常也结束 loading，展示空状态而非一直转圈
    loading.value = false;
    mobileLoadingMore.value = false;
  });
}

/** 查询用户列表 */
function getList(append = false) {
  if (append) {
    mobileLoadingMore.value = true;
  } else {
    loading.value = true;
  }
  if (!tabsValue.value) {
    loading.value = false;
    mobileLoadingMore.value = false;
    return;
  }
  queryParams.value.handlerType = tabsValue.value;
  dateRange.value = Array.isArray(dateRange.value) ? dateRange.value : [];
  queryParams.value.beginTime = dateRange.value[0]
  queryParams.value.endTime = dateRange.value[1]
  handlerResult(queryParams.value).then(res => {
    loading.value = false;
    mobileLoadingMore.value = false;
    let handlerAuths = res.data.handlerAuths;
    handlerAuths.rows.forEach(item => {
      item.isChecked = checkedItemList.value.findIndex(e => e.storageId === item.storageId) !== -1;
    });
    if (append && window.innerWidth <= 768) {
      tableList.value = [...tableList.value, ...handlerAuths.rows];
    } else {
      tableList.value = handlerAuths.rows;
    }
    total.value = handlerAuths.total;
    groupOptions.value = res.data.treeSelections;
    proxy.$nextTick(() => {
      if (groupName.value) proxy.$refs["groupTreeRef"].filter(groupName.value);
    });
    isCheckedAll();
  }).catch(() => {
    // 接口异常也结束 loading，展示空状态而非一直转圈
    loading.value = false;
    mobileLoadingMore.value = false;
  });
}

/** 移动端滚动加载更多 */
function loadMoreMobile() {
  if (mobileLoadingMore.value || mobileNoMore.value || loading.value) return;
  if (tableList.value.length >= total.value) {
    mobileNoMore.value = true;
    return;
  }
  queryParams.value.pageNum++;
  queryParams.value.pageSize = MOBILE_PAGE_SIZE;
  getList(true);
}

/** 移动端卡片列表滚动事件 */
function onMobileScroll(e: any) {
  const el = e.target;
  if (!el) return;
  // 距离底部 60px 时触发加载
  if (el.scrollTop + el.clientHeight >= el.scrollHeight - 60) {
    loadMoreMobile();
  }
}

/** 重置移动端分页状态 */
function resetMobilePagination() {
  mobileLoadingMore.value = false;
  mobileNoMore.value = false;
  queryParams.value.pageNum = 1;
  if (window.innerWidth <= 768) {
    queryParams.value.pageSize = MOBILE_PAGE_SIZE;
  }
}
/** 节点单击事件 */
function handleNodeClick(data: any) {
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
  resetMobilePagination();
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

function handleCheck(row: any) {
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
function handleClose(storageId: any) {
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
@import './selectUser.scoped.scss';
</style>

<!-- 非 scoped 全局样式：用于 append-to-body 的 dialog 内元素暗黑模式 -->
<style lang="scss">
@import './selectUser.global.scss';
</style>
