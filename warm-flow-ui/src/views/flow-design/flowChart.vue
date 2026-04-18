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
const { isDark, initFromUrl, applyDarkTheme, setupMessageListener, cleanupMessageListener } = useDark();
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
  eventCenter.on('node:click', (data) => {
    const promptArr = data.data.properties.promptContent;
    if (promptArr) {
      visible.value = true;

      nextTick(() => {
        if (tooltipContainerRef.value) {
          // // 构建 HTML 内容
          promptContent.value = data.data.properties.promptContent
          // promptContent.value = {
          //   dialogStyle: { /* 弹框样式 */
          //     position: 'absolute', /* 绝对定位，基于最近的定位祖先元素（如 container） */
          //     backgroundColor: "#fff", /* 背景色为白色 */
          //     maxHeight: "300px",
          //     overflowY: "auto",
          //     border: "1px solid #ccc", /* 灰色边框 */
          //     borderRadius: "4px", /* 添加圆角 */
          //     boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)", /* 阴影效果（轻微立体感） */
          //     padding: "8px 12px", /* 内边距（内容与边框的间距） */
          //     fontSize: "14px", /* 字体大小 */
          //     zIndex: 1000, /* 层级高于其他元素，确保提示框可见 */
          //     maxWidth: "500px", /* 最大宽度限制，防止内容过长 */
          //     color: "#333" /* 深色文字 */
          //   },
          //   info: [
          //     {
          //       prefix: "任务名称: ",
          //       prefixStyle: {},
          //       content: "组长审批",
          //       contentStyle: {
          //         border: '1px solid #d1e9ff',
          //         backgroundColor: "#e8f4ff",
          //         padding: "4px 8px",
          //         borderRadius: "4px"
          //       },
          //       rowStyle: {
          //         fontWeight: "bold",
          //         margin: "0 0 6px 0",
          //         padding: "0 0 8px 0",
          //         borderBottom: "1px solid #ccc"
          //       }
          //     },
          //     {
          //       prefix: "负责人: ",
          //       prefixStyle: { fontWeight: "bold" },
          //       content: "李四",
          //       contentStyle: {},
          //       rowStyle: {}
          //     },
          //     {
          //       prefix: "状态: ",
          //       prefixStyle: { fontWeight: "bold" },
          //       content: "进行中",
          //       contentStyle: {},
          //       rowStyle: {}
          //     }
          //   ]
          // };
          // 获取节点位置
          tooltipPosition.value = { x: data.e.clientX, y: data.e.clientY - 80 };
        }
      });
    }
  });

  eventCenter.on('blank:click', () => {
    visible.value = false;
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
    console.warn('fitViewIfMobile: 容器尺寸无效，跳过');
    return;
  }
  
  // 确保图形模型已就绪
  const graphModel = lf.value.graphModel;
  if (!graphModel || !graphModel.width || !graphModel.height || 
      graphModel.width <= 0 || graphModel.height <= 0 ||
      isNaN(graphModel.width) || isNaN(graphModel.height)) {
    console.warn('fitViewIfMobile: 图形模型尺寸无效，跳过');
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


  // 调试信息：设备检测和容器状态
  console.log('flowChart mounted, isMobileDevice:', isMobileDevice(), 'window.innerWidth:', window.innerWidth, 'container ref:', containerRef.value);
  if (containerRef.value) {
    console.log('container size:', containerRef.value.clientWidth, 'x', containerRef.value.clientHeight);
  }
  
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
              isSilentMode: true,
              textEdit: false,
              hoverOutline: isClassics(defJson.value.modelValue),   // 鼠标 hover 的时候是否显示节点的外框。
              nodeSelectedOutline: isClassics(defJson.value.modelValue),    // 节点被选中时是否显示节点的外框。
              edgeSelectedOutline: isClassics(defJson.value.modelValue),    //	边被选中时是否显示边的外框。
              grid: {
                size: 20,
                visible: 'true' === appParams.value.showGrid,
                type: 'dot',
                config: {
                  color: isDark.value ? "#404040" : "#ccc",
                  thickness: 1,
                },
              },
              background: {
                backgroundColor: isDark.value ? "#141414" : "#fff",
              },
            });
            register();
            initEvent();
            // 画布触摸事件桥接：LogicFlow v2 不支持原生 touch 事件，
            // 将触摸事件转换为鼠标事件以支持手机/平板端拖动画布
            initTouchEventBridge();
            lf.value.render(data);
            // 初始化完成后，如果当前是暗黑模式，显式应用一次主题
            if (isDark.value && lf.value) {
              applyDarkTheme(lf.value, true);
            }

            // 调试：检查工具栏按钮是否存在
            setTimeout(() => {
              const toolbarRight = document.querySelector('.toolbar-right');
              console.log('toolbarRight element:', toolbarRight);
              if (toolbarRight) {
                console.log('toolbarRight display:', window.getComputedStyle(toolbarRight).display);
                const buttons = toolbarRight.querySelectorAll('.el-button');
                console.log('button count:', buttons.length);
                buttons.forEach((btn, idx) => {
                  console.log(`button ${idx}:`, btn.innerHTML, 'visible:', btn.offsetParent !== null);
                });
              }
            }, 100);
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

function setupPointerEventCapture(el) {
  let startX = 0, startY = 0;
  let isDragging = false;
  const DOUBLE_TAP_GAP = 350;
  let tapCount = 0, lastTapTime = 0, doubleTapTimer = null;

  function getEventOpts(e) {
    return { clientX: e.clientX, clientY: e.clientY, button: 0, bubbles: true, cancelable: true, view: window };
  }

  function resetTapState() {
    tapCount = 0;
    if (doubleTapTimer) { clearTimeout(doubleTapTimer); doubleTapTimer = null; }
  }

  el.addEventListener('pointerdown', (e) => {
    if (e.pointerType === 'mouse' && e.button !== 0) return;
    startX = e.clientX; startY = e.clientY; isDragging = false;
    if (e.pointerType === 'touch') {
      e.preventDefault();
      el.dispatchEvent(new MouseEvent('mousedown', getEventOpts(e)));
    }
  }, { passive: false });

  el.addEventListener('pointermove', (e) => {
    if (!isDragging && (Math.abs(e.clientX - startX) > 5 || Math.abs(e.clientY - startY) > 5)) isDragging = true;
    if (isDragging && e.pointerType === 'touch') { e.preventDefault(); el.dispatchEvent(new MouseEvent('mousemove', getEventOpts(e))); }
    else if (e.pointerType === 'touch') e.preventDefault();
  }, { passive: false });

  function handleEnd(e) {
    if (isDragging && e.pointerType === 'touch') { el.dispatchEvent(new MouseEvent('mouseup', getEventOpts(e))); resetTapState(); return; }
    const now = Date.now(); tapCount++;
    if (tapCount === 1) {
      lastTapTime = now; el.dispatchEvent(new MouseEvent('click', getEventOpts(e)));
      doubleTapTimer = setTimeout(() => { tapCount = 0; doubleTapTimer = null; }, DOUBLE_TAP_GAP);
    } else if (tapCount >= 2 && now - lastTapTime < DOUBLE_TAP_GAP) {
      if (doubleTapTimer) { clearTimeout(doubleTapTimer); doubleTapTimer = null; }
      tapCount = 0; el.dispatchEvent(new MouseEvent('dblclick', getEventOpts(e)));
    } else {
      tapCount = 1; lastTapTime = now; el.dispatchEvent(new MouseEvent('click', getEventOpts(e)));
      doubleTapTimer = setTimeout(() => { tapCount = 0; doubleTapTimer = null; }, DOUBLE_TAP_GAP);
    }
  }
  el.addEventListener('pointerup', handleEnd);
  el.addEventListener('pointercancel', () => { if (isDragging) { isDragging = false; } resetTapState(); });
}

/**
 * 下载流程图
 */
function downLoad() {
  lf.value.getSnapshot(defJson.value.flowName, {
    fileType: 'png',
    backgroundColor: isDark.value ? '#141414' : '#f5f5f5',
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
  background-color: #141414 !important;
}
:global(html.dark) body {
  background-color: #141414 !important;
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
  border: 1px solid var(--wf-border-light, #d1e9ff);
  background-color: var(--wf-primary-lighter, #e8f4ff);
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  color: var(--wf-primary, #409eff);
  white-space: nowrap;
}

/* 暗黑模式适配：流程名称 */
html.dark .flow-name-badge,
:global(html.dark) .flow-name-badge {
  color: var(--wf-primary-light, #79bbff);
  border-color: rgba(64, 158, 255, 0.2);
  background-color: rgba(64, 158, 255, 0.08);
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
    display: flex;
    align-items: center;
    justify-content: flex-start;
    width: 100%;
    padding: 0px 8px;
    flex-wrap: nowrap;
    gap: 24px;
    padding-left: 0px;
  }

  .toolbar-left {
    margin-left: 0px;
  }

  .flow-name-badge {
    background-color: transparent !important;
    font-size: 12px !important;
    padding: 3px 8px !important;
  }

  html.dark .flow-name-badge,
  :global(html.dark) .flow-name-badge {
    background-color: transparent !important;
  }

  /* 中间状态按钮隐藏（改由第二行显示） */
  .toolbar-center {
    display: none;
  }

  /* 右侧工具栏靠右（margin-left:auto 撑开间距） */
  .toolbar-right {
    margin-left: auto;
    gap: 2px;
    margin-right: 0;
    flex-shrink: 0;
    background-color: rgba(255,0,0,0.1) !important; /* 调试：确保容器可见 */
  }

  .toolbar-right :deep(.el-button) {
    padding: 5px 6px !important;
    background-color: rgba(0, 255, 0, 0.2) !important; /* 调试：确保按钮可见 */
    min-width: 36px !important;
    min-height: 36px !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    visibility: visible !important;
    opacity: 1 !important;
    border: 2px solid red !important;
    box-shadow: 0 0 5px red !important;
    z-index: 9999 !important;
  }

  /* 第二行：节点状态演示，居中显示 */
  .chart-toolbar-mobile-status {
    display: flex !important;
    justify-content: center;
    align-items: center;
    gap: 6px;
    padding: 4px 0;
    border-top: 1px solid var(--el-border-color-lighter, #ebeef5);
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
