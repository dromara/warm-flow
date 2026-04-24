<template>
  <div class="flow-chart-root" :style="headerDiv">
    <el-header :style="headerStyle">
      <div class="chart-toolbar">
        <!-- 左侧：流程名称 -->
        <div class="toolbar-left">
          <div class="flow-name-badge" v-if="defJson.topTextShow">{{defJson.topText}}</div>
        </div>

        <!-- 中间：节点状态演示（居中） -->
        <div class="toolbar-center">
          <el-tooltip content="未完成" placement="bottom"><el-button size="small" :style="`border: 1px solid rgb(${statusColors.notDone})`">未完成</el-button></el-tooltip>
          <el-tooltip content="进行中" placement="bottom"><el-button size="small" :style="`background-color: rgb(${statusColors.todo}, 0.15); border: 1px solid rgb(${statusColors.todo})`">进行中</el-button></el-tooltip>
          <el-tooltip content="已完成" placement="bottom"><el-button size="small" :style="`background-color: rgb(${statusColors.done}, 0.15); border: 1px solid rgb(${statusColors.done})`">已完成</el-button></el-tooltip>
        </div>

        <!-- 右侧：工具栏（仅图标，tooltip悬浮显示） -->
        <div class="toolbar-right">
          <el-tooltip content="放大" placement="bottom"><el-button size="small" icon="ZoomIn" @click="zoomViewport(true)"/></el-tooltip>
          <el-tooltip content="自适应" placement="bottom"><el-button size="small" icon="Rank" @click="isMobileDevice() ? zoomViewport('fit') : zoomViewport(1)"/></el-tooltip>
          <el-tooltip content="缩小" placement="bottom"><el-button size="small" icon="ZoomOut" @click="zoomViewport(false)"/></el-tooltip>
          <el-tooltip content="下载流程图" placement="bottom"><el-button size="small" icon="Download" @click="downLoad"/></el-tooltip>
        </div>
      </div>

      <!-- 移动端/平板：第二行，节点状态演示独占一行 -->
      <div class="chart-toolbar-mobile-status">
        <el-tooltip content="未完成" placement="bottom"><el-button size="small" :style="`border: 1px solid rgb(${statusColors.notDone})`">未完成</el-button></el-tooltip>
        <el-tooltip content="进行中" placement="bottom"><el-button size="small" :style="`background-color: rgb(${statusColors.todo}, 0.15); border: 1px solid rgb(${statusColors.todo})`">进行中</el-button></el-tooltip>
        <el-tooltip content="已完成" placement="bottom"><el-button size="small" :style="`background-color: rgb(${statusColors.done}, 0.15); border: 1px solid rgb(${statusColors.done})`">已完成</el-button></el-tooltip>
      </div>
    </el-header>
    <div class="containerView" ref="containerRef"></div>

    <div
        v-if="visible"
        :style="{ left: tooltipPosition.x + 'px', top: tooltipPosition.y + 'px' }"
        ref="tooltipContainerRef">
    </div>

    <div class="log-text">Warm-Flow</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed, render, h, nextTick } from "vue";
import LogicFlow from "@logicflow/core";
import { Snapshot } from "@logicflow/extension";
import "@logicflow/core/lib/style/index.css";
import StartC from "@/components/design/classics/js/start";
import BetweenC from "@/components/design/classics/js/between";
import SerialC from "@/components/design/classics/js/serial";
import ParallelC from "@/components/design/classics/js/parallel";
import InclusiveC from "@/components/design/classics/js/inclusive";
import EndC from "@/components/design/classics/js/end";
import SkipC from "@/components/design/classics/js/skip";
import StartM from "@/components/design/mimic/js/start";
import BetweenM from "@/components/design/mimic/js/between";
import SerialM from "@/components/design/mimic/js/serial";
import ParallelM from "@/components/design/mimic/js/parallel";
import InclusiveM from "@/components/design/mimic/js/inclusive";
import EndM from "@/components/design/mimic/js/end";
import SkipM from "@/components/design/mimic/js/skip";
import useAppStore from "@/store/app";
import {isClassics, json2LogicFlowJson} from "@/components/design/common/js/tool";
import { queryFlowChart } from "@/api/flow/definition";
import { useDark } from '@/composables/useDark';

const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);
const instanceId = ref(null);
const defJson = ref({});
const containerRef = ref(null);
const tooltipPosition = ref({ x: 0, y: 0 });
const tooltipContainerRef = ref(null);
const visible = ref(false);

const promptContent = ref({
  dialogStyle: {},
  info: []
});

const statusColors = ref({
  done: "",
  todo: "",
  notDone: ""
});

// 使用统一的暗黑模式 composable
const { isDark, themeColors, initFromUrl, applyDarkTheme, setupMessageListener, cleanupMessageListener } = useDark();
const headerDiv = computed(() => {
    return {
        backgroundColor: "var(--wf-bg-white, #fff)",
    };
});
const headerStyle = computed(() => {
  return {
    top: "5px",
    right: "50px",
    zIndex: "2",
    height: "auto",
  };
});

const lf = ref(null);

const use = () => {
  LogicFlow.use(Snapshot);
};

const register = () => {
  if (isClassics(defJson.value.modelValue)) {
    lf.value.register(StartC);
    lf.value.register(BetweenC);
    lf.value.register(SerialC);
    lf.value.register(ParallelC);
    lf.value.register(InclusiveC);
    lf.value.register(EndC);
    lf.value.register(SkipC);
  } else {
    lf.value.register(StartM);
    lf.value.register(BetweenM);
    lf.value.register(SerialM);
    lf.value.register(ParallelM);
    lf.value.register(InclusiveM);
    lf.value.register(EndM);
    lf.value.register(SkipM);
  }
};

const initEvent = () => {
  const { eventCenter } = lf.value.graphModel;

  // 延迟关闭提示框，避免移动端 blank:click 与 node:click 竞态
  let hideTooltipTimer = null;

  eventCenter.on('node:click', (data) => {
    // 清除待执行的隐藏操作（解决移动端竞态问题）
    if (hideTooltipTimer) {
      clearTimeout(hideTooltipTimer);
      hideTooltipTimer = null;
    }

    const promptArr = data.data.properties.promptContent;
    if (promptArr) {
      visible.value = true;

      nextTick(() => {
        if (tooltipContainerRef.value) {
          promptContent.value = data.data.properties.promptContent
          // 兼容移动端：确保坐标有效（touch 事件转换后 clientX/Y 可能缺失）
          const clientX = data.e?.clientX ?? data.e?.x ?? window.innerWidth / 2;
          const clientY = data.e?.clientY ?? data.e?.y ?? window.innerHeight / 3;
          tooltipPosition.value = { x: clientX, y: clientY - 80 };
        }
      });
    }
  });

  eventCenter.on('blank:click', () => {
    // 延迟隐藏，给 node:click 时间取消
    hideTooltipTimer = setTimeout(() => {
      visible.value = false;
      hideTooltipTimer = null;
    }, 50);
  });
};

// 监听 promptContent 变化并动态渲染
watch(
    () => promptContent.value,
    (contentData) => {
      if (!tooltipContainerRef.value) return;
      if (!contentData) return;

      // 更新 tooltipContainerRef 的样式
      Object.entries(contentData.dialogStyle || {}).forEach(([key, value]) => {
        tooltipContainerRef.value.style[key] = value;
      });

      // 生成 <p> 元素数组
      const children = contentData.info.map((item) =>
          h("p", {
            style: item.rowStyle || {},
          }, [
            h("span", {
              style: item.prefixStyle || {}
            }, item.prefix),
            h("span", {
              style: item.contentStyle || {}
            }, item.content)
          ])
      );

      // 调用 render 方法
      const wrapper = h("div", children);
      render(wrapper, tooltipContainerRef.value);
    },
    { deep: true, immediate: true }
);

/** 检测是否移动端/平板设备（与 index.vue 保持一致） */
const isMobileDevice = () => {
  // 屏幕宽度 <= 1024px（覆盖平板横屏及以下）或支持触摸且有窄屏
  return window.innerWidth <= 1024 || ('ontouchstart' in window && window.innerWidth <= 1280);
};

/**
 * 自适应画布视图
 * @param {boolean|number|string} mode - true=放大, false=缩小, 1=PC重置缩放居中, 'fit'=fitView全部节点
 */
const zoomViewport = async (mode) => {
  if (!lf.value) return;
  if (mode === 'fit') {
    lf.value.fitView(40, 20);
  } else if (mode === 1) {
    lf.value.zoom(1);
    lf.value.translateCenter();
  } else {
    lf.value.zoom(mode);
    lf.value.translateCenter();
  }
};

/** 仅在移动端/平板执行 fitView，PC 端不干预（与 index.vue 保持一致） */
function fitViewIfMobile() {
  if (!isMobileDevice() || !lf.value?.fitView) return;

  // 确保容器已挂载且有有效尺寸
  const container = containerRef.value;
  if (!container || !container.clientWidth || !container.clientHeight) {
    return;
  }

  // 确保图形模型已就绪
  const graphModel = lf.value.graphModel;
  if (!graphModel || !graphModel.width || !graphModel.height ||
      graphModel.width <= 0 || graphModel.height <= 0 ||
      isNaN(graphModel.width) || isNaN(graphModel.height)) {
    return;
  }

  lf.value.fitView(40, 20);
}

/**
 * 真机兼容：延迟触发 LogicFlow resize + fitView（仅移动端/平板端生效）
 * PC 端不执行自动 fitView，保持用户手动缩放行为
 */
/**
 * 真机兼容：延迟触发 LogicFlow resize + fitView（仅移动端/平板端生效）
 * PC 端不执行自动 fitView，保持用户手动缩放行为
 */
function scheduleMobileResize() {
  const doFit = () => {
    if (lf.value && lf.value.resize) {
      lf.value.resize();
      // 仅在移动设备上才执行自动 fitView
      requestAnimationFrame(fitViewIfMobile);
    }
  };
  // 多重延迟策略覆盖各种场景：移动端/v-show切换/iframe嵌入
  setTimeout(doFit, 50);
  setTimeout(doFit, 150);
  setTimeout(doFit, 300);
  setTimeout(doFit, 600);
  // 最后一次保底（覆盖极端慢速场景如低端手机首次加载）
  setTimeout(doFit, 1000);
}

onMounted(async () => {



  if (!appParams.value) await appStore.fetchTokenName();
  instanceId.value = appParams.value.id;

  // 从 URL 参数初始化暗黑模式（统一使用 composable）
  initFromUrl();

  // 注册 postMessage 主题切换监听
  setupMessageListener();

  if (instanceId.value) {
    queryFlowChart(instanceId.value)
        .then((res) => {
          defJson.value = res.data;
          if (defJson.value) {
            [
              statusColors.value.done,
              statusColors.value.todo,
              statusColors.value.notDone,
            ]  = (res.data.chartStatusColor &&
                res.data.chartStatusColor.length === 3) ? res.data.chartStatusColor :
                ["56,161,105", "245,158,11", "107,114,128"]; // 提供默认值

            const data = json2LogicFlowJson(defJson.value);
            document.body.style.overflow = 'hidden';
            use();
            lf.value = new LogicFlow({
              container: containerRef.value,
              plugins: [Snapshot],
              textEdit: false,
              snapToGrid: true,   // 是否开启网格吸附，开启后拖动节点会有以网格大小为步长移动
              hideAnchors: !isClassics(defJson.value.modelValue),   // 是否隐藏节点的锚点，静默模式下默认隐藏。
              adjustNodePosition: isClassics(defJson.value.modelValue),   // 是否允许拖动节点。
              hoverOutline: isClassics(defJson.value.modelValue),   // 鼠标 hover 的时候是否显示节点的外框。
              nodeSelectedOutline: isClassics(defJson.value.modelValue),    // 节点被选中时是否显示节点的外框。
              edgeSelectedOutline: isClassics(defJson.value.modelValue),    //	边被选中时是否显示边的外框。
              grid: {
                size: 20,
                visible: 'true' === appParams.value.showGrid,
                type: 'dot',
                config: {
                  color: themeColors.value.gridColor,
                  thickness: 1,
                },
                background: {
                  backgroundColor: themeColors.value.bgPage,
                },
              },
              keyboard: isClassics(defJson.value.modelValue) ? {
                enabled: true,
                shortcuts: [
                  {
                    keys: ["delete"],
                    callback: () => {
                      const elements = lf.value.getSelectElements(true);
                      lf.value.clearSelectElements();
                      elements.edges.forEach((edge) => lf.value.deleteEdge(edge.id));
                      elements.nodes.forEach((node) => lf.value.deleteNode(node.id));
                    },
                  },
                ],
              } : {},
            });
            register();
            initEvent();
            lf.value.setTheme({
              snapline: {
                stroke: themeColors.value.snaplineStroke,
                strokeWidth: 2,
              },
              nodeText: {
                color: themeColors.value.nodeTextColor,
                fill: themeColors.value.nodeTextFill,
                fontSize: 13,
                fontWeight: 500,
              },
              edgeText: {
                fontSize: 13,
                strokeWidth: 1,
                background: {
                  fill: themeColors.value.edgeTextBg,
                },
              },
            });
            // 画布触摸事件桥接：LogicFlow v2 不支持原生 touch 事件，
            // 将触摸事件转换为鼠标事件以支持手机/平板端拖动画布
            initTouchEventBridge();
            lf.value.render(data);
            // 初始化完成后，如果当前是暗黑模式，显式应用一次主题
            if (isDark.value && lf.value) {
              applyDarkTheme(lf.value, true);
            }


            // 移动端/平板端：自适应显示全部节点；PC 端保持默认行为不干预
            nextTick(() => { fitViewIfMobile(); });
            // 真机修复：延迟触发 resize 确保 SVG 画布正确渲染尺寸（仅移动端/平板端生效）
            scheduleMobileResize();
          }
        })
        .catch(() => {
          lf.value.render({});
        });
  }
});

watch(isDark, (v) => {
  if (!lf.value) return;
  applyDarkTheme(lf.value, v);
});

/**
 * 移动端触摸事件桥接：确保手机/平板上 LogicFlow 节点/边的点击、拖拽正常工作
 * 使用 Pointer Events API 将触摸事件转换为鼠标事件
 */
function initTouchEventBridge() {
  const container = containerRef.value;
  if (!container) return;

  setupPointerEventCapture(container);

  const canvasOverlay = container.querySelector('.lf-canvas-overlay');
  if (canvasOverlay) {
    setupPointerEventCapture(canvasOverlay, { forDrag: true });
  } else {
    const observer = new MutationObserver(() => {
      const overlay = container.querySelector('.lf-canvas-overlay');
      if (overlay) {
        setupPointerEventCapture(overlay, { forDrag: true });
        observer.disconnect();
      }
    });
    observer.observe(container, { childList: true, subtree: true });
  }
}

function setupPointerEventCapture(el, options = {}) {
  // --- 双击检测状态 ---
  let tapCount = 0;
  let lastTapTime = 0;
  let doubleTapTimer = null;
  const DOUBLE_TAP_GAP = 350;

  // --- 长按右键菜单状态 ---
  let longPressTimer = null;
  let isLongPressFired = false;

  // --- 拖动检测 ---
  let startX = 0, startY = 0;
  let startTime = 0;
  let isDragging = false;
  let lastMoveX = 0, lastMoveY = 0;

  // --- 移动端兼容：保存初始触摸坐标 ---
  // pointerdown 时记录坐标，用于后续合成事件（解决 touchend 时坐标丢失问题）
  let savedClientX = 0, savedClientY = 0;

  /**
   * 统一获取坐标参数（用于构造 MouseEvent）
   * 优先使用当前事件坐标，fallback 到保存的初始触摸坐标
   */
  function getEventOpts(pointerEvent) {
    return {
      clientX: pointerEvent.clientX || savedClientX,
      clientY: pointerEvent.clientY || savedClientY,
      button: 0,
      bubbles: true,
      cancelable: true,
      view: window,
    };
  }

  // ========== pointerdown ==========
  el.addEventListener('pointerdown', (e) => {
    // 只处理主指针（手指/鼠标左键/触控笔）
    if (e.pointerType === 'mouse' && e.button !== 0) return;

    startX = e.clientX;
    startY = e.clientY;
    startTime = Date.now();
    lastMoveX = e.clientX;
    lastMoveY = e.clientY;

    // 【关键修复】保存初始触摸坐标，用于后续合成事件
    savedClientX = e.clientX;
    savedClientY = e.clientY;

    isDragging = false;
    isLongPressFired = false;

    // 仅对触摸事件阻止浏览器默认手势（滚动/缩放/选择）
    // 鼠标事件不干预，让 LogicFlow 正常处理拖拽/双击
    if (e.pointerType === 'touch') {
      e.preventDefault();
      // 主动分发一次 mousedown 确保 LogicFlow 收到
      el.dispatchEvent(new MouseEvent('mousedown', getEventOpts(e)));
    }

    // 启动长按计时器（500ms 无显著移动则视为右键菜单，仅触摸有效）
    longPressTimer = setTimeout(() => {
      if (isDragging) return; // 已在拖动，取消长按
      isLongPressFired = true;
      el.dispatchEvent(new MouseEvent('contextmenu', getEventOpts(e)));
    }, 500);
  }, { passive: false });

  // ========== pointermove ==========
  el.addEventListener('pointermove', (e) => {
    const dx = Math.abs(e.clientX - startX);
    const dy = Math.abs(e.clientY - startY);
    const elapsed = Date.now() - startTime;

    // 拖动检测：增加阈值到5像素（与index.vue保持一致），且触摸时间超过50ms才视为拖动（防止触摸抖动误判）
    if (!isDragging && (dx > 5 || dy > 5) && elapsed > 50) {
      // 首次超过阈值：进入拖动模式
      isDragging = true;
      if (longPressTimer) {
        clearTimeout(longPressTimer);
        longPressTimer = null;
      }
    }

    if (isDragging && e.pointerType === 'touch') {
      // 触摸拖动中：持续分发 mousemove 给 LogicFlow
      e.preventDefault();
      el.dispatchEvent(new MouseEvent('mousemove', getEventOpts(e)));
      lastMoveX = e.clientX;
      lastMoveY = e.clientY;
    } else if (e.pointerType === 'touch') {
      // 触摸微移动：阻止默认滚动行为
      e.preventDefault();
    }
  }, { passive: false });

  // ========== pointerup / pointercancel ==========
  function handlePointerEnd(e) {
    // 清理长按计时器
    if (longPressTimer) {
      clearTimeout(longPressTimer);
      longPressTimer = null;
    }

    // 拖动结束：分发 mouseup 给 LogicFlow 完成拖拽链路
    if (isDragging && e.pointerType === 'touch') {
      el.dispatchEvent(new MouseEvent('mouseup', getEventOpts(e)));
      resetTapState();
      isDragging = false;
      return;
    }

    // 长按已触发（右键菜单），不再处理 click/dblclick
    if (isLongPressFired) {
      resetTapState();
      return;
    }

    // ---- 点击/双击检测（仅非拖动） ----
    const now = Date.now();
    tapCount++;

    if (tapCount === 1) {
      // 第一次点击：立即触发 click
      lastTapTime = now;
      el.dispatchEvent(new MouseEvent('click', getEventOpts(e)));

      // 设置双击等待窗口
      doubleTapTimer = setTimeout(() => {
        tapCount = 0;
        doubleTapTimer = null;
      }, DOUBLE_TAP_GAP);

    } else if (tapCount >= 2 && (now - lastTapTime < DOUBLE_TAP_GAP)) {
      // 第二次快速点击：触发 dblclick
      if (doubleTapTimer) {
        clearTimeout(doubleTapTimer);
        doubleTapTimer = null;
      }
      tapCount = 0;
      el.dispatchEvent(new MouseEvent('dblclick', getEventOpts(e)));
    } else {
      // 超出双击间隔，重新开始计数
      tapCount = 1;
      lastTapTime = now;
      el.dispatchEvent(new MouseEvent('click', getEventOpts(e)));

      doubleTapTimer = setTimeout(() => {
        tapCount = 0;
        doubleTapTimer = null;
      }, DOUBLE_TAP_GAP);
    }
  }

  el.addEventListener('pointerup', handlePointerEnd);
  el.addEventListener('pointercancel', (e) => {
    // 中断：如果正在拖动需要补发 mouseup
    if (isDragging && e.pointerType === 'touch') {
      el.dispatchEvent(new MouseEvent('mouseup', getEventOpts(e)));
      isDragging = false;
    }
    if (longPressTimer) {
      clearTimeout(longPressTimer);
      longPressTimer = null;
    }
    resetTapState();
  });

  function resetTapState() {
    if (doubleTapTimer) {
      clearTimeout(doubleTapTimer);
      doubleTapTimer = null;
    }
    tapCount = 0;
  }
}

/**
 * 下载流程图
 */
function downLoad() {
  lf.value.getSnapshot(defJson.value.flowName, {
    fileType: 'png',
    backgroundColor: themeColors.value.snapshotBg,
    padding: 30,
    partial: false,
    quality: 0.92
  });
}

/** 组件卸载时清理 message 监听 */
onUnmounted(() => {
  cleanupMessageListener();
});
</script>

<style scoped>
/* ========== 暗黑模式根背景（使用 :global 确保可靠穿透） ========== */
:global(html.dark) .containerView {
  background-color: var(--wf-bg-color) !important;
}
:global(html.dark) body {
  background-color: var(--wf-bg-color) !important;
}

/* ========== 根容器：flex 纵向布局，防止溢出 ========== */
.flow-chart-root {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.containerView {
  width: 100%;
  flex: 1;
  min-height: 0;
  /* 关键：禁止浏览器默认触摸手势，让 LogicFlow 接管画布拖动 */
  touch-action: none;
}

.containerView :deep(.lf-canvas-overlay),
.containerView :deep(.lf-graph) {
  touch-action: none;
}

/* ========== 工具栏三栏布局 ========== */
.chart-toolbar {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 5px 0;
}

.toolbar-left {
  flex-shrink: 0;
}

.toolbar-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.toolbar-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-right: 12px;
}
.flow-name-badge {
  font-weight: 600;
  padding: 4px 12px;
  font-size: 14px;
  white-space: nowrap;
}

.log-text {
  position: absolute;
  font-weight: bold;
  right: 10px;
  bottom: 10px;
  font-size: 15px;
  color: var(--wf-text-primary, #333);
  z-index: 1;

  /* 暗黑模式适配 */
  html.dark &,
  :global(html.dark) & {
    color: var(--wf-text-secondary, #888888);
  }
}

/* ========== 移动端/平板响应式 ========== */
@media (max-width: 768px) {
  /* 第一行：流程名称（左）+ 工具栏（右） */
  .chart-toolbar {
    display: flex !important;
    align-items: center !important;
    justify-content: flex-start !important;
    width: 100% !important;
    padding: 0px !important;
    flex-wrap: nowrap !important;
    gap: 6px !important;  /* 减少工具栏间距 */
    margin-left: -8px;  /* 整体左移，使流程名称靠左边 */
  }

  .toolbar-left {
    margin-left: 0px !important;
    padding-left: 0px !important;
  }

  /* 移动端流程名称：移除颜色和背景，纯文本显示 */
  .flow-name-badge {
    font-size: 12px !important;
    padding: 2px 6px !important;
    font-weight: normal !important;
  }



  /* 中间状态按钮隐藏（改由第二行显示） */
  .toolbar-center {
    display: none !important;
  }

  /* 右侧工具栏靠右（margin-left:auto 撑开间距） */
  .toolbar-right {
    margin-left: auto !important;
    gap: 4px !important;  /* 减少工具栏按钮间距 */
    margin-right: 0px !important;
    flex-shrink: 0 !important;
  }

  .toolbar-right :deep(.el-button) {
    padding: 4px 6px !important;
    min-width: 30px !important;
    min-height: 30px !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
  }

  /* 第二行：节点状态演示，居中显示 */
  .chart-toolbar-mobile-status {
    display: flex !important;
    justify-content: center;
    align-items: center;
    gap: 6px;
    padding: 4px 0;
    border-top: 1px solid var(--el-border-color-lighter, #ebeef5);

    /* 暗黑模式边框 */
    html.dark &,
    :global(html.dark) & {
      border-top-color: rgba(255, 255, 255, 0.1);
    }
  }

  .chart-toolbar-mobile-status :deep(.el-button) {
    font-size: 11px;
    padding: 3px 8px;
  }
}

/* PC 端隐藏第二行状态按钮 */
.chart-toolbar-mobile-status {
  display: none;
}
</style>
