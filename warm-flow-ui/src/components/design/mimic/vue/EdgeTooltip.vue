<template>
    <div :style="tooltipStyle" @mouseenter="handleTooltipEnter" @mouseleave="handleTooltipLeave">
        <div class="tooltip-container">
            <div v-for="(item, index) in options" :key="index" @click="handleClick(item)" class="tooltip-item">
                <svg-icon :icon-class="item.icon" class="tooltip-icon" />
                <span>{{ item.label }}</span>
            </div>
        </div>
    </div>
</template>

<script setup name="EdgeTooltip">
import { computed } from 'vue';
import { useDark } from '@/composables/useDark';

const { isDark } = useDark();

const props = defineProps({
  position: Object,
  tooltipEdge: Object,
});

const options = [
  { icon: 'between', label: '审批' },
  { icon: 'serial', label: '互斥网关' },
  { icon: 'parallel', label: '并行网关' },
  { icon: 'inclusive', label: '包含网关' },
]

const emit = defineEmits(['option-click', 'close-tooltip']);

/** 动态计算 tooltip 样式，根据暗黑模式切换颜色 */
const tooltipStyle = computed(() => ({
  top: `${props.position.y}px`,
  left: `${props.position.x + 20}px`,
  position: 'absolute',
  pointerEvents: 'auto',
  backgroundColor: isDark.value ? '#1f1f1f' : '#fff',
  border: `1px solid ${isDark.value ? '#333333' : '#ccc'}`,
  borderRadius: '4px',
  boxShadow: isDark.value
    ? '0 2px 12px rgba(0, 0, 0, 0.4)'
    : '0 2px 8px rgba(0, 0, 0, 0.15)',
  padding: '4px 5px',
  fontSize: '15px',
  zIndex: 1000,
  color: isDark.value ? '#e0e0e0' : '#333',
  display: 'flex',
  width: '350px'
}));

function handleTooltipEnter() {
  window.isTooltipHovered = true;
}

function handleTooltipLeave() {
  window.isTooltipHovered = false;
  emit('close-tooltip');
}

const handleClick = (item) => {
  item['tooltipEdge'] = props.tooltipEdge;
  emit('option-click', item);
};

</script>

<style scoped>
.tooltip-container {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    width: 100%;
}

.tooltip-item {
    display: flex;
    align-items: center;
    padding: 4px 5px;
    cursor: pointer;
    border-radius: 4px;
    transition: background-color 0.2s;

    &:hover {
      background-color: #f0f0f0;
    }

    /* 暗黑模式 hover */
    html.dark &,
    :global(html.dark) & {
      &:hover {
        background-color: rgba(255, 255, 255, 0.08);
      }
    }
}

.tooltip-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border: 1px solid #ddd;
    border-radius: 50%;
    margin-right: 10px;
    color: #666;
    transition: all 0.2s ease;

    /* 暗黑模式适配 */
    html.dark &,
    :global(html.dark) & {
      border-color: #444444;
      color: #b0b0b0;

      &:hover {
        border-color: var(--wf-primary, #409eff);
        color: var(--wf-primary, #409eff);
      }
    }

    &:hover {
      border-color: var(--wf-primary, #409eff);
      color: var(--wf-primary, #409eff);
    }
}

/* ========== 手机端响应式适配 ========== */
@media (max-width: 768px) {
  .tooltip-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 6px;
  }

  .tooltip-item {
    padding: 6px 8px;
    font-size: 13px;
  }

  .tooltip-icon {
    width: 26px;
    height: 26px;
    margin-right: 6px;

    svg {
      width: 14px;
      height: 14px;
    }
  }
}

@media (max-width: 480px) {
  .tooltip-container {
    grid-template-columns: 1fr;
  }
}
</style>
