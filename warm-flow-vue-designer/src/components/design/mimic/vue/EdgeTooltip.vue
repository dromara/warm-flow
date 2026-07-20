<template>
    <div :style="tooltipStyle" class="edge-tooltip-pop" @mouseenter="handleTooltipEnter" @mouseleave="handleTooltipLeave">
        <div class="tooltip-caption">{{ t('edgeTooltip.title') }}</div>
        <div class="tooltip-container">
            <div
                v-for="(item, index) in options"
                :key="index"
                class="tooltip-item"
                :style="{ '--item-rgb': item.rgb }"
                @click="handleClick(item)"
            >
                <span class="tooltip-icon"><svg-icon :icon-class="item.icon" /></span>
                <span class="tooltip-meta">
                    <span class="tooltip-label">{{ item.label }}</span>
                    <span class="tooltip-desc">{{ item.desc }}</span>
                </span>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { CSSProperties } from 'vue';
import { useI18n } from '@/i18n';

defineOptions({ name: 'EdgeTooltip' });

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

// type 供 FlowDesigner 加节点逻辑使用；icon 为展示用线性图标；
// rgb 为类型语义色（与经典模式侧栏 / 仿钉钉网关设计态语义色严格一致：审批=蓝、互斥=橙、并行=青、包含=紫）
const options = [
  { type: 'between', icon: 'addBetween', rgb: '64,158,255', label: t('edgeTooltip.approval'), desc: t('edgeTooltip.approvalDesc') },
  { type: 'serial', icon: 'addSerial', rgb: '245,158,11', label: t('sidebar.serial'), desc: t('edgeTooltip.serialDesc') },
  { type: 'parallel', icon: 'addParallel', rgb: '19,194,194', label: t('sidebar.parallel'), desc: t('edgeTooltip.parallelDesc') },
  { type: 'inclusive', icon: 'addInclusive', rgb: '146,84,222', label: t('sidebar.inclusive'), desc: t('edgeTooltip.inclusiveDesc') },
]

const emit = defineEmits<{
  (e: 'option-click', item: any): void;
  (e: 'close-tooltip'): void;
}>();

/**
 * 仅定位走内联样式；配色/圆角/阴影全部下沉到 CSS（--wf-* token），
 * html.dark 下自动翻转，不再依赖 JS 侧 themeColors（库形态下 postMessage 通道未必接线）。
 */
const tooltipStyle = computed<CSSProperties>(() => ({
  top: `${props.position.y}px`,
  left: `${props.position.x + 20}px`,
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
.edge-tooltip-pop {
  position: absolute;
  pointer-events: auto;
  z-index: 1000;
  width: max-content;
  padding: 10px;
  font-size: 13px;
  background-color: var(--wf-tooltip-bg, #fff);
  border: 1px solid var(--wf-tooltip-border, #ebeef5);
  border-radius: 14px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.12), 0 2px 8px rgba(15, 23, 42, 0.06);
  color: var(--wf-tooltip-color, #303133);
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "PingFang SC", "Helvetica Neue", "Microsoft YaHei", sans-serif;
  -webkit-font-smoothing: antialiased;
  animation: edge-tooltip-in 0.18s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top left;

  /* 暗黑模式：阴影加深（底色/描边/文字色由 --wf-tooltip-* token 自动翻转） */
  html.dark &,
  :global(html.dark) & {
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.5), 0 2px 8px rgba(0, 0, 0, 0.35);
  }
}

@keyframes edge-tooltip-in {
  from {
    opacity: 0;
    transform: translateY(4px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 弹层小标题：弱化的说明性文字，非二级大标题 */
.tooltip-caption {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1px;
  color: var(--wf-text-secondary, #909399);
  padding: 2px 8px 8px;
  user-select: none;
}

.tooltip-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 4px;
    width: 100%;
}

.tooltip-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 14px 8px 8px;
    cursor: pointer;
    border-radius: 10px;
    white-space: nowrap;
    transition: background-color 0.18s ease, transform 0.18s ease;

    /* hover：随类型语义色轻染 + 图标翻实色，一眼区分四种节点 */
    &:hover {
      background-color: rgba(var(--item-rgb), 0.10);
      transform: translateY(-1px);

      .tooltip-icon {
        background: rgb(var(--item-rgb));
        color: #fff;
        box-shadow: 0 4px 10px rgba(var(--item-rgb), 0.35);
      }

      .tooltip-label {
        color: rgb(var(--item-rgb));
      }
    }

    &:active {
      transform: translateY(0);
    }

    /* 暗黑模式 hover：染色略加深保证可见 */
    html.dark &,
    :global(html.dark) & {
      &:hover {
        background-color: rgba(var(--item-rgb), 0.16);
      }
    }
}

.tooltip-icon {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  color: rgb(var(--item-rgb));
  background: rgba(var(--item-rgb), 0.12);
  border-radius: 10px;
  transition: all 0.18s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  /* 暗黑模式：底色透明度略升，避免沉进深色面板 */
  html.dark &,
  :global(html.dark) & {
    background: rgba(var(--item-rgb), 0.18);
  }
}

.tooltip-meta {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
}

.tooltip-label {
  font-size: 13px;
  font-weight: 600;
  line-height: 1.3;
  white-space: nowrap;
  transition: color 0.18s ease;
}

.tooltip-desc {
  font-size: 11px;
  line-height: 1.3;
  color: var(--wf-text-secondary, #909399);
  white-space: nowrap;
}

/* ========== 手机端响应式适配 ========== */
@media (max-width: 768px) {
  .tooltip-item {
    padding: 7px 12px 7px 7px;
  }

  .tooltip-icon {
    width: 30px;
    height: 30px;

    svg {
      width: 16px;
      height: 16px;
    }
  }

  .tooltip-desc {
    display: none;
  }
}

@media (max-width: 480px) {
  .tooltip-container {
    grid-template-columns: 1fr;
  }
}
</style>
