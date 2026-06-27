<template>
  <!-- 工具栏：筛选 + 确定（移动端） -->
  <div class="search-toggle-row mobile-only">
    <button class="search-toggle-btn" @click="searchCollapsed = !searchCollapsed" :class="{ 'is-expanded': !searchCollapsed }">
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="search-icon"><path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0016 9.5 6.5 6.5 0 109.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" fill="currentColor"/></svg>
      <span>{{ searchCollapsed ? t('selectUser.filter') : t('selectUser.filterExpanded') }}</span>
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="toggle-arrow-sm" :class="{ 'is-rotated': !searchCollapsed }"><path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z" fill="currentColor"/></svg>
    </button>
    <button class="btn-confirm btn-confirm-inline mobile-only" @click="onConfirm">{{ t('common.confirm') }}</button>
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
            @keyup.enter="onQuery"
          />
        </wf-form-item>
        <wf-form-item :label="t('between.handlerName')" prop="handlerName">
          <wf-input
            v-model="queryParams.handlerName"
            :placeholder="t('selectUser.permNamePlaceholder')"
            clearable
            style="width: 200px"
            @keyup.enter="onQuery"
          />
        </wf-form-item>
        <wf-form-item :label="t('selectUser.createTime')">
          <wf-date-picker
            v-model="dateRangeProxy"
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
          <button type="button" class="btn-action btn-primary" @click="onQuery">{{ t('common.search') }}</button>
          <button type="button" class="btn-action btn-default" @click="onReset">{{ t('common.reset') }}</button>
        </div>
        <button class="btn-confirm btn-confirm-search" @click="onConfirm">{{ t('common.confirm') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from '@/i18n';

defineOptions({ name: 'UserPickerSearch' });

const { t } = useI18n();

interface UserPickerSearchProps {
  /** 查询参数（与父共享的响应式对象，handlerCode/handlerName 双向写入） */
  queryParams?: Record<string, any>;
  /** 创建时间区间（v-model:dateRange） */
  dateRange?: any[];
}
const props = withDefaults(defineProps<UserPickerSearchProps>(), {
  queryParams: () => ({}),
  dateRange: () => [],
});
const emit = defineEmits<{
  (e: 'update:dateRange', value: any[]): void;
  (e: 'query'): void;
  (e: 'reset'): void;
  (e: 'confirm'): void;
}>();

// 搜索表单 ref（移动端折叠状态、显隐均为搜索区内部 UI 状态）
const queryRef = ref<any>(null);
const showSearch = ref(true);
const searchCollapsed = ref(window.innerWidth <= 768);

const dateRangeProxy = computed({
  get: () => props.dateRange,
  set: value => emit('update:dateRange', value),
});

function onQuery() {
  emit('query');
}
function onReset() {
  emit('reset');
}
function onConfirm() {
  emit('confirm');
}

// 暴露 el-form 的 resetFields 供父组件（useUserPicker 的 resetQuery）重置查询表单
defineExpose({
  resetFields: () => queryRef.value?.resetFields(),
});
</script>

<style scoped lang="scss">
@import './userPickerSearch.scoped.scss';
</style>
