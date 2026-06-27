<template>
  <div class="section-card tree-card" :class="{ 'tree-collapsed': treeCollapsed }">
    <div class="tree-header tree-header-clickable" @click="toggleCollapsed">
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
            v-model="groupNameProxy"
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
            ref="treeRef"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="onNodeClick"
          />
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from '@/i18n';

defineOptions({ name: 'UserPickerTree' });

const { t } = useI18n();

interface UserPickerTreeProps {
  /** 部门树数据（treeSelections） */
  groupOptions?: any;
  /** 部门搜索关键字（v-model:groupName） */
  groupName?: string;
  /** 树折叠状态（v-model:treeCollapsed） */
  treeCollapsed?: boolean;
}
const props = withDefaults(defineProps<UserPickerTreeProps>(), {
  groupOptions: undefined,
  groupName: '',
  treeCollapsed: false,
});
const emit = defineEmits<{
  (e: 'update:groupName', value: string): void;
  (e: 'update:treeCollapsed', value: boolean): void;
  (e: 'node-click', data: any): void;
}>();

const treeRef = ref<any>(null);

const groupNameProxy = computed({
  get: () => props.groupName,
  set: value => emit('update:groupName', value),
});

function toggleCollapsed() {
  emit('update:treeCollapsed', !props.treeCollapsed);
}

/** 通过条件过滤节点 */
const filterNode = (value: string, data: any) => {
  if (!value) return true;
  return data.name.indexOf(value) !== -1;
};

function onNodeClick(data: any) {
  emit('node-click', data);
}

// 暴露 el-tree 的 filter / setCurrentKey，供父组件（useUserPicker）按名称筛选与重置选中
defineExpose({
  filter: (value: string) => treeRef.value?.filter(value),
  setCurrentKey: (key: any) => treeRef.value?.setCurrentKey(key),
});
</script>

<style scoped lang="scss">
@import './userPickerTree.scoped.scss';
</style>
