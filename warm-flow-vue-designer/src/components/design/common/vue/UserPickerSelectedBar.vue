<template>
  <!-- 已选提示（内嵌在表格卡片底部）- 智能显示策略 -->
  <div class="selected-inline-bar" v-if="selected.length > 0">
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="bar-icon">
      <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z" fill="currentColor"/>
    </svg>
    <span>{{ t('selectUser.selectedPrefix') }} <strong>{{ selected.length }}</strong> {{ t('selectUser.selectedUnit') }}</span>
    <div class="selected-mini-tags" v-if="selected.length <= 8">
      <wf-tag
        closable
        v-for="tag in selected"
        :key="tag.storageId"
        @close="onClose(tag.storageId)"
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
        @close="onClose(tag.storageId)"
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
            <div class="tooltip-item" v-for="tag in selected" :key="tag.storageId">
              {{ tag.handlerName || tag.storageId }}
            </div>
          </div>
        </template>
        <wf-tag class="mini-tag more-tag" size="small">+{{ selected.length - visibleTagsCount }} {{ t('selectUser.more') }}</wf-tag>
      </wf-tooltip>
    </div>
    <wf-button link type="danger" size="small" class="clear-all-btn" @click="onClear">{{ t('selectUser.clear') }}</wf-button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from '@/i18n';

defineOptions({ name: 'UserPickerSelectedBar' });

const { t } = useI18n();

interface UserPickerSelectedBarProps {
  /** 已选办理人行 */
  selected?: any[];
}
const props = withDefaults(defineProps<UserPickerSelectedBarProps>(), {
  selected: () => [],
});
const emit = defineEmits<{
  (e: 'close', storageId: any): void;
  (e: 'clear'): void;
}>();

// 已选标签显示策略：超过 8 个时只显示前若干个 + "+N 更多"
const visibleTagsCount = 8;
const visibleTags = computed(() => props.selected.slice(0, visibleTagsCount));

function onClose(storageId: any) {
  emit('close', storageId);
}

function onClear() {
  emit('clear');
}
</script>

<style scoped lang="scss">
@import './userPickerSelectedBar.scoped.scss';
</style>
