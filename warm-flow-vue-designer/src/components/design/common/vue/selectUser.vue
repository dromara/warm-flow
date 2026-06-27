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
        <UserPickerTree
          ref="groupTreeRef"
          :group-options="groupOptions"
          v-model:group-name="groupName"
          v-model:tree-collapsed="treeCollapsed"
          @node-click="handleNodeClick"
        />
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
        <UserPickerSelectedBar
          :selected="checkedItemList"
          @close="handleClose"
          @clear="clearAllSelected"
        />

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
// 库化后不再依赖 ruoyi 全局注册的 parseTime，显式引入供模板格式化「创建时间」列
import { parseTime } from "@/utils/ruoyi";
import { useI18n } from '@/i18n';
import { useUserPicker } from '@/composables/useUserPicker';
import UserPickerTree from './UserPickerTree.vue';
import UserPickerSelectedBar from './UserPickerSelectedBar.vue';

defineOptions({ name: 'User' });

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
const emit = defineEmits<{
  (e: 'update:userVisible', visible: boolean): void;
  (e: 'handleUserSelect', checkedItemList: any[]): void;
}>();

// 逻辑层统一收口到 useUserPicker（state / 数据请求 / 勾选与已选维护），SFC 只保留模板与编排
const {
  // 模板 ref
  groupTreeRef,
  queryRef,
  // state
  tabsValue,
  tableList,
  loading,
  showSearch,
  total,
  dateRange,
  groupName,
  groupOptions,
  treeCollapsed,
  searchCollapsed,
  mobileLoadingMore,
  mobileNoMore,
  tabsList,
  columns,
  checkedItemList,
  queryParams,
  checkAllInfo,
  // methods（模板事件）
  getList,
  onMobileScroll,
  handleNodeClick,
  tabChange,
  handleQuery,
  resetQuery,
  handleCheckAll,
  handleCheck,
  handleClose,
  clearAllSelected,
  submitForm,
} = useUserPicker(props, emit);
</script>

<style scoped lang="scss">
@import './selectUser.scoped.scss';
</style>

<!-- 非 scoped 全局样式：用于 append-to-body 的 dialog 内元素暗黑模式 -->
<style lang="scss">
@import './selectUser.global.scss';
</style>
