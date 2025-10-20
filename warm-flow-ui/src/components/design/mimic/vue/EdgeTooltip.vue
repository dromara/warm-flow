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

const emit = defineEmits(['option-click', 'close-tooltip']); // 新增 close-tooltip 事件

const tooltipStyle = computed(() => ({
  top: `${props.position.y}px`,
  left: `${props.position.x + 20}px`,
  position: 'absolute', /* 绝对定位，基于最近的定位祖先元素（如 container） */
  pointerEvents: 'auto', // ✅ 允许 tooltip 捕获鼠标事件
  backgroundColor: "#fff", /* 背景色为白色 */
  border: "1px solid #ccc", /* 灰色边框 */
  borderRadius: "4px", /* 添加圆角 */
  boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)", /* 阴影效果（轻微立体感） */
  padding: "4px 5px", /* 内边距（内容与边框的间距） */
  fontSize: "15px", /* 字体大小 */
  zIndex: 1000, /* 层级高于其他元素，确保提示框可见 */
  color: "#333", /* 深色文字 */
  display: "flex",
  width: "350px"
}));

function handleTooltipEnter() {
  // 鼠标进入提示框时，阻止 hide 事件触发
  window.isTooltipHovered = true;
}

function handleTooltipLeave() {
  window.isTooltipHovered = false;
  emit('close-tooltip'); // 主动通知父组件关闭
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
}

.tooltip-item:hover {
    background-color: #f0f0f0;
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
}

</style>
