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

<script setup lang="ts">
import { computed } from 'vue';
import { useDark } from '@/composables/useDark';
import { useI18n } from '@/i18n';

defineOptions({ name: 'EdgeTooltip' });

const { isDark, themeColors } = useDark();
const { t } = useI18n();

interface EdgeTooltipProps {
  /** 浮层位置（画布坐标） */
  position?: { x: number; y: number };
  /** 关联的边 model */
  tooltipEdge?: Record<string, any>;
}
const props = withDefaults(defineProps<EdgeTooltipProps>(), {
  position: () => ({ x: 0, y: 0 }),
  tooltipEdge: () => ({}),
});

const options = [
  { icon: 'between', label: t('edgeTooltip.approval') },
  { icon: 'serial', label: t('sidebar.serial') },
  { icon: 'parallel', label: t('sidebar.parallel') },
  { icon: 'inclusive', label: t('sidebar.inclusive') },
]

const emit = defineEmits<{
  (e: 'option-click', item: any): void;
  (e: 'close-tooltip'): void;
}>();

/** 动态计算 tooltip 样式，根据暗黑模式切换颜色 */
const tooltipStyle = computed(() => ({
  top: `${props.position.y}px`,
  left: `${props.position.x + 20}px`,
  position: 'absolute',
  pointerEvents: 'auto',
  backgroundColor: themeColors.value.tooltipBg,
  border: `1px solid ${themeColors.value.tooltipBorder}`,
  borderRadius: '12px',
  boxShadow: themeColors.value.tooltipShadow,
  padding: '6px',
  fontSize: '13px',
  zIndex: 1000,
  color: themeColors.value.tooltipColor,
  display: 'flex',
  width: 'max-content'
}));

function handleTooltipEnter() {
  window.isTooltipHovered = true;
}

function handleTooltipLeave() {
  window.isTooltipHovered = false;
  emit('close-tooltip');
}

const handleClick = (item: any) => {
  item['tooltipEdge'] = props.tooltipEdge;
  emit('option-click', item);
};

</script>

<style scoped>
.tooltip-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 2px;
    width: 100%;
}

.tooltip-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 7px 14px 7px 8px;
    cursor: pointer;
    border-radius: 8px;
    white-space: nowrap;
    transition: background-color 0.18s ease;

    &:hover {
      background-color: var(--wf-primary-lighter);
    }

    /* 暗黑模式 hover */
    html.dark &,
    :global(html.dark) & {
      &:hover {
        background-color: rgba(64, 158, 255, 0.12);
      }
    }
}

.tooltip-item span {
  font-size: 13px;
  white-space: nowrap;
}

.tooltip-icon {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  background: var(--wf-primary-light);
  border-radius: 50%;
  transition: all 0.18s ease;

  svg {
    width: 17px;
    height: 17px;
  }

  /* 暗黑模式适配 */
  html.dark &,
  :global(html.dark) & {
    background: rgba(64, 158, 255, 0.16);
  }
}

/* ========== 手机端响应式适配 ========== */
@media (max-width: 768px) {
  .tooltip-item {
    padding: 7px 12px 7px 7px;
  }

  .tooltip-icon {
    width: 28px;
    height: 28px;

    svg {
      width: 15px;
      height: 15px;
    }
  }
}

@media (max-width: 480px) {
  .tooltip-container {
    grid-template-columns: 1fr;
  }
}
</style>
