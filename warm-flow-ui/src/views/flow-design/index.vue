<template>
  <div :style="headerDiv">
    <!-- 顶部导航栏 -->
    <div class="design-header" v-if="!onlyDesignShow">
      <!-- 左侧：流程名称 -->
      <div class="header-left">
        <div class="flow-name-wrapper">
          <el-tooltip :content="logicJson.flowName" placement="bottom" :show-after="500">
            <div class="flow-name">
                <svg-icon icon-class="flowName" style="margin-right: 5px"/>
                {{ logicJson.flowName }}
            </div>
          </el-tooltip>
        </div>
      </div>

      <!-- 中间：步骤切换 -->
      <div class="header-center">
        <div class="steps-tabs">
          <div
              v-for="(step, index) in steps"
              :key="index"
              class="step-tab"
              :class="{ 'active': activeStep === index }"
              @click="handleStepClick(index)"
          >
            <svg-icon :icon-class="step.icon" class="tab-icon"/>
            <span class="tab-text">{{ step.title }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧：保存按钮 -->
      <div class="header-right">
        <el-button class="save-btn" size="default" @click="saveJsonModel" v-if="!disabled">
          <svg-icon icon-class="save" class="save-icon"/>
          <span>保存</span>
        </el-button>
      </div>
    </div>

    <el-header :style="headerStyle">
      <div style="padding: 5px 0; text-align: right;">
        <div v-if="activeStep === 1">
          <span class="toolbar-group">
            <el-tooltip content="缩小" placement="bottom"><el-button size="small" icon="ZoomOut" @click="zoomViewport(false)"></el-button></el-tooltip>
            <!-- PC端：原有 zoom(1)+居中；移动端/平板：fitView 自适应显示全部节点 -->
            <el-tooltip content="自适应" placement="bottom"><el-button size="small" icon="Rank" @click="isMobileDevice() ? zoomViewport('fit') : zoomViewport(1)"></el-button></el-tooltip>
            <el-tooltip content="放大" placement="bottom"><el-button size="small" icon="ZoomIn" @click="zoomViewport(true)"></el-button></el-tooltip>
          </span>
          <span class="toolbar-group">
            <el-tooltip content="上一步" placement="bottom"><el-button size="small" icon="DArrowLeft" @click="undoOrRedo(true)"></el-button></el-tooltip>
            <el-tooltip content="下一步" placement="bottom"><el-button size="small" icon="DArrowRight" @click="undoOrRedo(false)"></el-button></el-tooltip>
            <el-tooltip content="清空" placement="bottom"><el-button size="small" icon="Delete" @click="clear()"></el-button></el-tooltip>
          </span>
          <span class="toolbar-group">
            <el-tooltip content="下载流程图" placement="bottom"><el-button size="small" icon="Picture" @click="downLoad"></el-button></el-tooltip>
            <el-tooltip content="下载JSON" placement="bottom"><el-button size="small" icon="Download" @click="downJson"></el-button></el-tooltip>
          </span>
          <span class="toolbar-group" v-if="onlyDesignShow && !disabled">
            <el-tooltip content="保存" placement="bottom"><el-button size="small" class="toolbar-save-btn" @click="saveJsonModel">
              <svg-icon icon-class="save" style="width: 14px; height: 14px;"/>
            </el-button></el-tooltip>
          </span>
        </div>
      </div>

      <BaseInfo :style="baseInfoStyle" ref="baseInfoRef" v-if="!onlyDesignShow" v-show="activeStep === 0"
                :logic-json="logicJson" :category-list="categoryList" :form-path-list="formPathList"
                :definition-id="definitionId" :disabled="disabled"
                @update:flow-name="handleFlowNameUpdate" @update:model-value="handleModelValueUpdate"/>

      <!-- 自定义拖拽侧边栏：仅流程设计页签显示 + 延迟显示避免与 LogicFlow DOM 冲突 -->
      <DiagramSidebar
        v-if="sidebarVisible && activeStep === 1"
        class="diagram-sidebar"
        @dragInNode="handleDragInNode"
      />

      <div class="container" ref="containerRef" v-show="activeStep === 1">
        <PropertySetting ref="propertySettingRef" :node="nodeClick" :lf="lf" :disabled="disabled"
                         :skipConditionShow="skipConditionShow" :nodes="nodes" :skips="skips"
                         :form-path-list="formPathList">
        </PropertySetting>
      </div>
      <div class="logo-text" v-if="activeStep === 1">Warm-Flow</div>
    </el-header>

    <!-- 弹框组件 -->
    <EdgeTooltip
        v-if="tooltipVisible"
        :position="tooltipPosition"
        :tooltipEdge="tooltipEdge"
        @option-click="handleOptionClick"
        @close-tooltip="tooltipVisible = false"
    />
  </div>
</template>

<script setup name="Design">
import LogicFlow from "@logicflow/core";
import "@logicflow/core/lib/style/index.css";
import {InsertNodeInPolyline, Menu, Snapshot} from '@logicflow/extension';
import '@logicflow/extension/lib/style/index.css'
import { ElLoading } from 'element-plus'
import PropertySetting from '@/components/design/common/vue/propertySetting.vue'
import { queryDef, saveJson } from "@/api/flow/definition";
import {
    getPreviousNodes,
    isClassics, isGateWay,
    json2LogicFlowJson,
    logicFlowJsonToWarmFlow
} from "@/components/design/common/js/tool";
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
import {computed, onMounted, onUnmounted, ref, watch} from "vue";
import BaseInfo from "@/components/design/common/vue/baseInfo.vue";
import initClassicsData from "@/components/design/classics/initClassicsData.json";
import initMimicData from "@/components/design/mimic/initMimicData.json";
import {addBetweenNode, addGatewayNode, gatewayAddNode, removeNode} from "@/components/design/mimic/js/mimic.js";
import EdgeTooltip from "@/components/design/mimic/vue/EdgeTooltip.vue";
import DiagramSidebar from "@/components/design/common/vue/DiagramSidebar.vue";
import { useDark } from '@/composables/useDark';

const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);

const { proxy } = getCurrentInstance();

const lf = ref(null);
const definitionId = ref();
const nodeClick = ref(null);
const disabled = ref(false);
const propertySettingRef = ref({});
const logicJson = ref({});
const jsonString = ref('');
const skipConditionShow = ref(true);
const nodes = ref([]);
const skips = ref([]);
const categoryList = ref([]);
const formPathList = ref([]);
// 控制侧边栏显示：延迟到 initLogicFlow 完成后显示，避免与 LogicFlow DOM 初始化冲突
const sidebarVisible = ref(false);
// 使用统一的暗黑模式 composable
const { isDark, initFromUrl, applyDarkTheme, setupMessageListener, cleanupMessageListener } = useDark();

/**
 * 判断当前是否为移动端/平板设备
 * 通过屏幕宽度 + 触摸能力综合判断（PC 端不生效自动 fitView）
 */
const isMobileDevice = () => {
  // 屏幕宽度 <= 1024px（覆盖平板横屏及以下）或支持触摸且有窄屏
  return window.innerWidth <= 1024 || ('ontouchstart' in window && window.innerWidth <= 1280);
};

/** 仅在移动端/平板执行 fitView，PC 端不干预 */
function fitViewIfMobile() {
  if (isMobileDevice() && lf.value?.fitView) {
    lf.value.fitView(40, 20);
  }
}

const activeStep = ref(0);
const onlyDesignShow = ref(false);

const headerStyle = computed(() => {
  return {
    top: "5px",
    right: "50px",
    left: isClassics(logicJson.value.modelValue) ? "60px" : "50px",
    zIndex: "2",
    height: "auto",
    backgroundColor: "var(--wf-bg-white, #fff)",
    border: "1px solid var(--wf-border-light, #e2e8f0)",
    borderRadius: "6px",
    margin: "5px",
    color: "var(--wf-text-primary, #303133)",
    transition: "left 0.3s ease",
  };
});
const baseInfoStyle = computed(() => {
  return {
    margin: "5px",
    backgroundColor: "var(--wf-bg-white, #fff)",
  };
});

const headerDiv = computed(() => {
    return {
        backgroundColor: "var(--wf-bg-white, #fff)",
    };
});

// 步骤数据
const steps = [
  { title: '基础信息', icon: 'baseInfo' },
  { title: '流程设计', icon: 'flowDesign' },
  // { title: '表单设计', icon: 'formDesign' }
];

const tooltipVisible = ref(false);
const tooltipPosition = ref({ x: 0, y: 0 });
const tooltipEdge = ref({});

const handleOptionClick = (item) => {
  if (item.icon === "between") {
    addBetweenNode(lf.value, item.tooltipEdge);
  } else {
    addGatewayNode(lf.value, item.tooltipEdge, item.icon);
  }
  tooltipVisible.value = false;
  // 移动端/平板端：新增节点后自适应画布，确保所有节点可见
  proxy.$nextTick(() => { fitViewIfMobile(); });
};

async function handleStepClick(index) {
  if (activeStep.value === 0 && !onlyDesignShow.value) {
    let validate = await proxy.$refs.baseInfoRef.validate();
    if (!validate) return
  }
  activeStep.value = index;

  if (index === 1) {
    handleModelValueUpdate()
  }
}


onMounted(() => {
    // 隐藏滚动条
    document.body.style.overflow = 'hidden';
    if (!appParams.value) appStore.fetchTokenName();
    if (appParams.value.id) {
      definitionId.value = appParams.value.id;
    }
    onlyDesignShow.value = appParams.value.onlyDesignShow === 'true' ||
        appParams.value.onlyDesignShow === true;

    // 从 URL 参数初始化暗黑模式（统一使用 composable）
    initFromUrl();

    // 注册 postMessage 主题切换监听
    setupMessageListener();

    queryDef(definitionId.value).then(res => {
    jsonString.value = res.data;
    if (res.data.isPublish && res.data.isPublish !== 0) {
      disabled.value = true
    }
    if (appParams.value.disabled === 'true') {
        disabled.value = true
    }
    if (res.data.categoryList && res.data.categoryList.length > 0) {
      categoryList.value = res.data.categoryList;
    }
    if (res.data.formPathList && res.data.formPathList.length > 0) {
        formPathList.value = res.data.formPathList;
    }
    if (jsonString.value) {
      logicJson.value = json2LogicFlowJson(jsonString.value);
      if (!logicJson.value.nodes || logicJson.value.nodes.length === 0) {
        let initData = isClassics(logicJson.value.modelValue) ? initClassicsData: initMimicData
        // 读取本地文件/initClassicsData.json文件，并将数据转换json对象
        logicJson.value = {
          ...logicJson.value,
          ...initData
        };
      }
      if (onlyDesignShow.value) {
        handleStepClick(1)
      }
    }
  });
})


// 提取初始化逻辑到单独的函数
function initLogicFlow() {
  if (proxy.$refs.containerRef) {
    use();
    lf.value = new LogicFlow({
      container: proxy.$refs.containerRef,
      textEdit: false,      // 是否开启文本编辑。
      snapToGrid: true,   // 是否开启网格吸附，开启后拖动节点会有以网格大小为补步长移动
      hideAnchors: !isClassics(logicJson.value.modelValue),   // 是否隐藏节点的锚点，静默模式下默认隐藏。
      adjustNodePosition: isClassics(logicJson.value.modelValue),   // 是否允许拖动节点。
      hoverOutline: isClassics(logicJson.value.modelValue),   // 鼠标 hover 的时候是否显示节点的外框。
      nodeSelectedOutline: isClassics(logicJson.value.modelValue),    // 节点被选中时是否显示节点的外框。
      edgeSelectedOutline: isClassics(logicJson.value.modelValue),    //	边被选中时是否显示边的外框。
      grid: {
        size: 20,
        visible: 'true' === appParams.value.showGrid,
        type: 'dot',
        config: {
          color: isDark.value ? '#404040' : '#ccc',
          thickness: 1,
        },
        background: {
          backgroundColor: isDark.value ? "#141414" : "#fff",
        },
      },
      keyboard: isClassics(logicJson.value.modelValue) ? {
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
    lf.value.setTheme({
      snapline: {
        stroke: '#1E90FF',
        strokeWidth: 2,
      },
      nodeText: {
        color: isDark.value ? '#e0e0e0' : '#303133',
        fill: isDark.value ? '#e0e0e0' : '#303133',
        fontSize: 13,
        fontWeight: 500,
      },
      edgeText: {
        fontSize: 13,
        strokeWidth: 1,
        background: {
          fill: isDark.value ? "#141414" : "#fff",
        },
      },
    });

    register();
    initMenu()
    initEvent();
    // 画布触摸事件桥接：LogicFlow v2 不支持原生 touch 事件，
    // 将触摸事件转换为鼠标事件以支持手机/平板端拖动画布
    initTouchEventBridge();
    if (logicJson.value) {
      lf.value.render(logicJson.value);
      // 移动端/平板端：自适应显示全部节点；PC 端保持默认行为不干预
      fitViewIfMobile();
    }
    // 初始化完成后，如果当前是暗黑模式，显式应用一次主题（解决 URL 参数初始化时序问题）
    if (isDark.value && lf.value) {
      applyDarkTheme(lf.value, true);
    }
    // 真机修复：延迟触发一次 resize 确保 SVG 画布正确渲染尺寸
    // 移动端 v-show 切换后容器可能还未完成布局计算
    scheduleMobileResize();

    // LogicFlow 完全初始化后，再显示自定义拖拽侧边栏（避免 DOM 操作冲突）
    if (isClassics(logicJson.value.modelValue)) {
      sidebarVisible.value = true;
    }
  }
}

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

/**
 * 监听步骤切换：从"基础信息"切到"流程设计"时（仅移动端/平板端生效）
 * PC 端不执行自动 fitView，保持用户手动缩放行为
 */
watch(activeStep, (newVal) => {
  if (newVal === 1 && lf.value) {
    const doFit = () => {
      if (lf.value?.resize) {
        lf.value.resize();
        requestAnimationFrame(fitViewIfMobile);
      }
    };
    // 多次延迟确保在各种时序下移动端能正确适配
    nextTick(doFit);
    setTimeout(doFit, 150);
    setTimeout(doFit, 400);
    setTimeout(doFit, 800);
  }
});

watch(isDark, (v) => {
  if (!lf.value) {
    return;
  }
  applyDarkTheme(lf.value, v);
});

/** 组件卸载时清理 message 监听 */
onUnmounted(() => {
  cleanupMessageListener();
  window.removeEventListener('resize', handleMobileResize);
});

// 监听窗口变化，真机旋转屏幕时重绘（带防抖 + 标志位屏蔽抽屉操作干扰）
let _resizeTimer = null;
let _drawerActive = false; // 抽屉操作期间禁止 resize 重绘画布 + 阻止触摸事件桥接
let _lastCanvasSize = { w: 0, h: 0 };
function handleMobileResize() {
  if (_drawerActive) return; // 抽屉操作期间跳过
  clearTimeout(_resizeTimer);
  _resizeTimer = setTimeout(() => {
    const container = proxy.$refs.containerRef;
    if (!container || !lf.value?.resize) return;
    const w = container.clientWidth;
    const h = container.clientHeight;
    // 容器尺寸无显著变化（<5px 容差）则跳过，过滤掉抽屉/overlay 导致的微调
    if (Math.abs(w - _lastCanvasSize.w) < 5 && Math.abs(h - _lastCanvasSize.h) < 5 && _lastCanvasSize.w > 0) return;
    _lastCanvasSize = { w, h };
    lf.value.resize();
  }, 300); // 提高到 300ms 确保布局稳定后再判断
}
window.addEventListener('resize', handleMobileResize);

// 抽屉打开时标记
window._markDrawerOpen = () => { _drawerActive = true; };
// 抽屉关闭动画完成后解除标记，并触发一次移动端画布适配
// 延迟 350ms：Element Plus drawer 关闭 transition 默认 300ms，加 50ms 缓冲
window._markDrawerClosed = () => {
  setTimeout(() => {
    _drawerActive = false;
    // 抽屉关闭后一次性适配画布（仅移动端/平板），替代 propertySetting 中分散的 fitView 调用
    if (isMobileDevice() && lf.value?.resize) {
      lf.value.resize();
      requestAnimationFrame(() => {
        if (lf.value?.fitView) lf.value.fitView(40, 20);
      });
    }
  }, 350);
};

/**
 * 自定义拖拽面板：监听子组件 dragInNode 事件，调用 lf.dnd.startDrag
 */
function handleDragInNode(type, properties = {}, text) {
  if (lf.value) {
    lf.value.dnd.startDrag({
      text,
      type,
      properties,
    })
  }
}

function getBaseInfo() {
  if (onlyDesignShow.value) {
    return
  }
  const baseInfoData = proxy.$refs.baseInfoRef.getFormData();
  logicJson.value = {
    ...logicJson.value,
    ...baseInfoData
  };
}

function handleFlowNameUpdate(newName) {
  logicJson.value.flowName = newName; // 更新父组件中的流程名称
}

function handleModelValueUpdate() {
  // 原设计器模型
  const modeOrg = logicJson.value.modelValue;
  // 获取基础信息
  getBaseInfo();
  const modeNew = logicJson.value.modelValue;

  if (!lf.value || modeOrg !== modeNew) {
    // 先隐藏侧边栏，等 initLogicFlow 完成后再显示
    sidebarVisible.value = false;
    nextTick(() => {
      if (!jsonString.value.nodeList || jsonString.value.nodeList.length === 0) {
        // 读取本地文件/initData.json文件，并将数据转换json对象
        let initData = isClassics(logicJson.value.modelValue) ? initClassicsData : initMimicData;
        logicJson.value = {
          ...logicJson.value,
          ...initData
        };
      }
      initLogicFlow();
    });
  }
}

async function saveJsonModel() {
  const loadingInstance = ElLoading.service(({fullscreen: true, text: "保存中，请稍等"}))
  if (!onlyDesignShow.value) {
    let validate = await proxy.$refs.baseInfoRef.validate();
    if (!validate) {
      loadingInstance.close();
      return;
    }
  }
  getBaseInfo();

  if (lf.value) {
    let graphData = lf.value.getGraphData()
    logicJson.value['nodes'] = graphData['nodes']
    logicJson.value['edges'] = graphData['edges']
  }
  logicJson.value['id'] = definitionId.value

  let jsonString = logicFlowJsonToWarmFlow(logicJson.value);
  saveJson(jsonString, onlyDesignShow.value).then(response => {
    if (response.code === 200) {
      proxy.$modal.msgSuccess("保存成功");
      // 延迟500ms后关闭页面
        setTimeout(() => {
            close()
        }, 500)
    }
  }).finally(() => {
    nextTick(() => {
      loadingInstance.close();
    });
  });
}

/**
 * 移动端触摸事件桥接：确保手机/平板上 LogicFlow 节点/边的点击、拖拽正常工作
 *
 * 核心问题：
 * LogicFlow v2 内部使用 mousedown/mousemove/mouseup + click/dblclick 事件，
 * 移动端浏览器不会自动将 touch 事件转换为 mouse 事件（除非设置了兼容模式）。
 *
 * 解决方案：
 * 使用统一的 Pointer Events API（pointerdown/pointermove/pointerup），
 * 它能同时覆盖鼠标、触摸、触控笔三种输入方式，
 * 并且自动映射为浏览器原生的 mousedown/mousemove/mouseup/click 事件链。
 */
function initTouchEventBridge() {
  const container = proxy.$refs.containerRef;
  if (!container) return;

  // 对容器及其所有后代元素启用 pointer-events 全局拦截
  // 这确保所有触摸输入都能正确转换为 mouse/click 事件
  setupPointerEventCapture(container);

  // 同时绑定到 canvas-overlay（确保画布拖拽也正常）
  const canvasOverlay = container.querySelector('.lf-canvas-overlay');
  if (canvasOverlay) {
    setupPointerEventCapture(canvasOverlay, { forDrag: true });
  } else {
    // 如果 CanvasOverlay 还未渲染，延迟绑定
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

/**
 * 使用 Pointer Events API 建立移动端触摸→鼠标事件的完整桥接
 *
 * Pointer Events 是 W3C 标准，现代浏览器均支持：
 * - pointerdown → 自动触发 mousedown（含 touches[0] 坐标）
 * - pointermove → 自动触发 mousemove
 * - pointerup   → 自动触发 mouseup + click
 * - pointercancel → 自动触发 mouseup（中断场景）
 *
 * 我们在此基础上额外补充：
 * 1. 阻止浏览器的默认手势行为（滚动/缩放）
 * 2. 长按模拟 contextmenu 右键菜单
 * 3. 双击检测并分发 dblclick
 *
 * @param {Element} el 目标 DOM 元素
 * @param {Object} options 配置项
 */
function setupPointerEventCapture(el) {
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
  let isDragging = false;
  let lastMoveX = 0, lastMoveY = 0;

  /**
   * 检查抽屉是否处于打开状态（打开期间禁止向 LogicFlow 转发触摸事件）
   *
   * 三级判断：
   * 1. _drawerActive 标志位 — 代码显式标记的打开/关闭窗口期
   * 2. .el-drawer__open — Element Plus drawer 打开时的 DOM class
   * 3. 排除 .el-drawer__close — 抽屉正在执行关闭动画（此时不应拦截用户对画布的操作）
   */
  function isDrawerOpen() {
    if (_drawerActive) return true;
    const drawerEl = document.querySelector('.el-drawer');
    if (!drawerEl) return false;
    // 有 __open 且无 __close → 确实是打开状态
    if (drawerEl.classList.contains('el-drawer__open') &&
        !drawerEl.classList.contains('el-drawer__close')) {
      return true;
    }
    return false;
  }

  /**
   * 统一获取坐标参数（用于构造 MouseEvent）
   */
  function getEventOpts(pointerEvent) {
    return {
      clientX: pointerEvent.clientX,
      clientY: pointerEvent.clientY,
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

    // 【修复】抽屉打开期间：阻止触摸事件转发给 LogicFlow，防止画布被意外拖拽
    if (isDrawerOpen() && e.pointerType === 'touch') {
      e.preventDefault();
      return;
    }

    startX = e.clientX;
    startY = e.clientY;
    lastMoveX = e.clientX;
    lastMoveY = e.clientY;
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
    // 【修复】抽屉打开期间：完全拦截触摸移动事件
    if (isDrawerOpen() && e.pointerType === 'touch') {
      e.preventDefault();
      return;
    }

    const dx = Math.abs(e.clientX - startX);
    const dy = Math.abs(e.clientY - startY);

    if (!isDragging && (dx > 5 || dy > 5)) {
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
    // 【修复】抽屉打开期间：拦截触摸释放事件
    if (isDrawerOpen() && e.pointerType === 'touch') {
      resetTapState();
      isDragging = false;
      return;
    }

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
 * 初始化菜单
 */
function initMenu() {
  // 只有仿钉钉模式才初始化菜单
  if (!isClassics(logicJson.value.modelValue)) {
    // 为菜单追加选项（必须在 lf.render() 之前设置）
    lf.value.extension.menu.setMenuConfig({
      nodeMenu: [],
      edgeMenu: [],
    });
  }
}

/**
 * 注册自定义节点和边
 */
function register() {
  if (isClassics(logicJson.value.modelValue)) {
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
}

/**
 * 添加扩展
 */
function use() {
  // 只有经典模式才有拖拽面板（已使用自定义 DiagramSidebar 替代内置 DndPanel）
  if (isClassics(logicJson.value.modelValue)) {
    LogicFlow.use(InsertNodeInPolyline)
  }
  LogicFlow.use(Menu);
  LogicFlow.use(Snapshot);
}
function initEvent() {
  const { eventCenter } = lf.value.graphModel

  if (!isClassics(logicJson.value.modelValue)) {
    // 更新节点名称
    eventCenter.on('update:nodeName', (data) => {
      lf.value.updateText(data.id, data.nodeName)
      lf.value.setProperties(data.id, {
        nodeName: data.nodeName
      })
    })

    // 网关节点单击事件
    eventCenter.on('node:click', (args) => {
      nodeClick.value = args.data
      if (isGateWay(nodeClick.value.type)) {
        gatewayAddNode(lf.value, nodeClick.value);
      }
    })

    eventCenter.on('edit:node', (args) => {
      if (args.click) {
        nodeClick.value = lf.value.getNodeModelById(args.id)
        let graphData = lf.value.getGraphData()
        nodes.value = graphData['nodes']
        skips.value = graphData['edges']
        proxy.$nextTick(() => {
          propertySettingRef.value.show()
        })
      }
    })

    // 单击边
    eventCenter.on('show:EdgeSetting', (args) => {
      nodeClick.value = lf.value.getEdgeModelById(args.id)
      const nodeModel = lf.value.getNodeModelById(nodeClick.value.sourceNodeId);
      skipConditionShow.value = ['serial', 'inclusive'].includes(nodeModel['type'])
      let graphData = lf.value.getGraphData()
      nodes.value = graphData['nodes']
      skips.value = graphData['edges']
      proxy.$nextTick(() => {
        propertySettingRef.value.show(['serial', 'inclusive'].includes(nodeModel['type']))
      })
    });

    // 鼠标进入边
    eventCenter.on('show:EdgeTooltip', (args) => {
      tooltipVisible.value = true;
      tooltipPosition.value = { x: args.e.clientX, y: args.e.clientY };
      tooltipEdge.value = lf.value.getEdgeModelById(args.id)
    });
    // 鼠标离开边
    eventCenter.on('hide:EdgeTooltip', () => {
      tooltipVisible.value = false;
    });
    // 删除节点事件
    eventCenter.on('delete:node', (args) => {
      const nodeModel = lf.value.getNodeModelById(args.id)
      removeNode(lf.value, nodeModel)
    })
  } else {
    // 中间节点双击事件
    eventCenter.on('node:click', (args) => {
      nodeClick.value = args.data
      let graphData = lf.value.getGraphData()
      nodes.value = graphData['nodes']
      skips.value = graphData['edges']
      proxy.$nextTick(() => {
        propertySettingRef.value.show()
      })
    })

    // 边双击事件
    eventCenter.on('edge:click  ', (args) => {
      nodeClick.value = args.data
      const nodeModel = lf.value.getNodeModelById(nodeClick.value.sourceNodeId);
      skipConditionShow.value = ['serial', 'inclusive'].includes(nodeModel['type'])
      let graphData = lf.value.getGraphData()
      nodes.value = graphData['nodes']
      skips.value = graphData['edges']
      proxy.$nextTick(() => {
        propertySettingRef.value.show(['serial', 'inclusive'].includes(nodeModel['type']))
      })
    })
  }

  eventCenter.on('edge:add', (args) => {
    let graphData = lf.value.getGraphData()
    const previousNodes = getPreviousNodes(graphData['nodes'], graphData['edges'], args.data.sourceNodeId);
    lf.value.changeEdgeType(args.data.id, 'skip')
    // 修改边类型
    lf.value.setProperties(args.data.id, {
      skipType: previousNodes.some(node => node.id === args.data.targetNodeId) ? 'REJECT' : 'PASS'
    })
  })

  eventCenter.on('blank:click', () => {
    nodeClick.value = null
    proxy.$nextTick(() => {
      propertySettingRef.value.handleClose()
    })
  })
}

/** 关闭按钮 */
function close() {
  window.parent.postMessage({ method: "close" }, "*");
}

/**
 * 缩放视口：放大/缩小/自适应
 * @param {boolean|string|number} zoom - true=放大(内置刻度), false=缩小(内置刻度), 'fit'=fitView自适应全部节点, number=直接设置缩放比例
 */
const zoomViewport = async (zoom) => {
  if (zoom === true) {
    // 放大（使用 LogicFlow 内置刻度，每次按固定比例放大）
    lf.value.zoom(true);
  } else if (zoom === false) {
    // 缩小（使用 LogicFlow 内置刻度，每次按固定比例缩小）
    lf.value.zoom(false);
  } else if (zoom === 1) {
    // PC 端自适应：zoom(1) 重置缩放 + translateCenter 居中
    lf.value.zoom(1);
    if (lf.value.translateCenter) {
      lf.value.translateCenter();
    }
  } else if (zoom === 'fit') {
    // 移动端/平板端自适应：fitView 将所有节点缩放并居中到画布可视区域
    if (lf.value.fitView) {
      lf.value.fitView(40, 20);
    } else {
      // fallback
      if (lf.value.translateCenter) lf.value.translateCenter();
      lf.value.resetZoom ? lf.value.resetZoom() : lf.value.zoom(1);
    }
  } else if (typeof zoom === 'number') {
    // 直接设置缩放比例
    lf.value.zoom(zoom);
  }
};

const undoOrRedo = async (undo) => {
  if(undo){
    lf.value.undo(undo)
  }else{
    lf.value.redo(undo)
  }
};
//清空
const clear = async () => {
    if (isClassics(logicJson.value.modelValue)) {
        lf.value.clearData()
    } else {
        logicJson.value = {
            ...logicJson.value,
            ...initMimicData
        };
        lf.value.render(logicJson.value);
    }

}

/**
 * 下载流程图
 */
function downLoad() {
  lf.value.getSnapshot(logicJson.value.flowName, {
    fileType: 'png',        // 可选：'png'、'webp'、'jpeg'、'svg'
    backgroundColor: isDark.value ? '#141414' : '#fff',
  })
}

async function downJson() {
  try {
    getBaseInfo();
    if (lf.value) {
      let graphData = lf.value.getGraphData()
      logicJson.value['nodes'] = graphData['nodes']
      logicJson.value['edges'] = graphData['edges']
    }
    let jsonString = logicFlowJsonToWarmFlow(logicJson.value);

    // 创建 Blob 并触发下载
    const filename = `${logicJson.value.flowName}_${logicJson.value.version}.json`;
    // 格式化用于下载
    const jsonPretty = JSON.stringify(JSON.parse(jsonString), null, 2); // 先 parse 成对象再 stringify
    const blob = new Blob([jsonPretty], { type: 'application/json' });
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(downloadUrl);
  } catch (error) {
    console.error('下载失败:', error);
  }
}

</script>

<style>


/* ========== 画布容器 ========== */
.container {
  flex: 1;
  width: 100%;
  /* 真机兼容：使用 dvh（动态视口高度）+ vh 兜底，解决移动端地址栏导致 100vh 不准确的问题 */
  height: calc(100dvh - 100px);
  min-height: 400px;
  border-radius: var(--wf-radius, 8px);
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  /* 关键：禁止浏览器默认触摸手势，让 LogicFlow 接管画布拖动 */
  touch-action: none;
  /* 确保容器有明确的定位上下文和尺寸 */
  position: relative;
}

/* 自定义拖拽侧边栏为浮动卡片，不再需要画布偏移 */
.container:has(.diagram-sidebar) {
  /* 浮动式侧边栏覆盖在画布上方，无需左侧 padding 偏移 */
}

@supports not (height: 100dvh) {
  .container {
    /* 浏览器不支持 dvh 时回退到标准 vh */
    height: calc(100vh - 100px);
    height: calc(-webkit-fill-available - 100px);
  }
}

/* LogicFlow 内部 SVG 画布层 */
.container :deep(.lf-canvas-overlay),
.container :deep(.lf-graph) {
  touch-action: none;
}

/* 确保 SVG 画布在移动端可见且有正确尺寸（真机关键修复） */
.container :deep(svg) {
  display: block !important;
  width: 100% !important;
  height: 100% !important;
}

.container :deep(.lf-container-bg) {
  display: block !important;
}

html.dark .container {
  background-color: #141414 !important;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.25) !important;
  border: 1px solid #333333 !important;
}

/* ========== Logo 水印 ========== */
.logo-text {
  position: absolute;
  font-weight: bold;
  right: 50px;
  bottom: 10px;
  font-size: 14px;
  color: var(--wf-text-placeholder, #c0c4cc);
  opacity: 0.5;
  z-index: 1;
  letter-spacing: 1px;
  user-select: none;
}

/* ========== 顶部导航栏 ========== */
.design-header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: var(--wf-bg-white, #fff);
  border-bottom: 1px solid #e2e8f0;
  min-height: 56px;
}

.header-left {
  flex: 0 0 auto;
  min-width: 180px;
}

.header-center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  justify-content: center;
}

.header-right {
  flex: 0 0 auto;
  min-width: 120px;
  display: flex;
  justify-content: flex-end;
}

/* ========== 流程名称 ========== */
.flow-name-wrapper {
  display: flex;
  align-items: center;
}

.flow-name {
  font-weight: 600;
  font-size: 15px;
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #1e293b;
  padding: 8px 16px;
  background: var(--wf-bg-white, #fff);
  border-radius: 8px;
  border: 1px solid var(--wf-border-light, #e2e8f0);
  cursor: default;
  transition: all 0.2s ease;
}

html.dark .flow-name {
  color: var(--wf-text-primary, #e0e0e0) !important;
}

.flow-name:hover {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* ========== 步骤切换标签 ========== */
.steps-tabs {
  display: flex;
  gap: 4px;
  padding: 4px;
  background: var(--wf-bg-white, #fff);
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08), 0 1px 2px rgba(0, 0, 0, 0.04);
}

html.dark .steps-tabs {
  background: var(--wf-bg-color, #141414) !important;
}

.step-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 28px;
  cursor: pointer;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  background: transparent;
  border: none;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
}

.step-tab:hover {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.06);
}

.step-tab .tab-icon {
  width: 18px;
  height: 18px;
  opacity: 0.7;
  transition: opacity 0.2s ease;
}

.step-tab:hover .tab-icon {
  opacity: 1;
}

.step-tab.active {
  color: #fff;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
}

.step-tab.active .tab-icon {
  opacity: 1;
}

.step-tab.active:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

/* ========== 保存按钮 ========== */
.save-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 500;
  font-size: 14px;
  padding: 10px 24px !important;
  border-radius: 10px !important;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  transition: all 0.25s ease !important;
}

.save-btn:hover {
  background: linear-gradient(135deg, #059669 0%, #047857 100%) !important;
  box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4) !important;
  transform: translateY(-1px);
}

.save-btn:active {
  transform: translateY(0);
}

.save-btn .save-icon {
  width: 16px;
  height: 16px;
}

/* ========== 暗黑模式适配（CSS 变量驱动） ========== */
html.dark .design-header {
  background: var(--wf-bg-white);
  border-bottom-color: var(--wf-border-color);
}

/* 暗黑模式：保存按钮降低饱和度，避免刺眼 */
html.dark .save-btn,
html.dark .toolbar-save-btn {
  background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%) !important;
  box-shadow: 0 2px 8px rgba(13, 148, 136, 0.25) !important;
}
html.dark .save-btn:hover,
html.dark .toolbar-save-btn:hover {
  background: linear-gradient(135deg, #0f766e 0%, #115e59 100%) !important;
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.35) !important;
}

/* 工具栏区域（el-header 第二行）暗黑模式 */
html.dark .el-header {
  --el-bg-color: var(--wf-bg-white, #1f1f1f);
  background-color: var(--wf-bg-white, #1f1f1f);
}

html.dark .toolbar-group {
  border-right-color: var(--wf-border-color, #333333);

  /* 工具栏内按钮暗黑模式 */
  .el-button {
    --el-button-bg-color: var(--wf-bg-color, #141414);
    --el-button-text-color: var(--wf-text-primary, #e0e0e0);
    --el-border-color: var(--wf-border-color, #333333);
    color: var(--wf-text-primary, #e0e0e0);
    background-color: transparent;
    border-color: var(--wf-border-color, #333333);

    &:hover,
    &:focus {
      color: var(--wf-primary, #409eff);
      background-color: rgba(64,158,255,.12);
      border-color: var(--wf-primary, #409eff);
    }
  }
}

/* flow-name 的暗黑样式已在 L830-832 通过 CSS 变量 + !important 处理 */

html.dark .steps-tabs {
  background: var(--wf-bg-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

html.dark .step-tab {
  color: var(--wf-text-regular);
}

html.dark .step-tab:hover {
  color: var(--wf-primary);
  background: rgba(96, 165, 250, 0.1);
}

html.dark .logo-text {
  color: var(--wf-text-secondary);
}

/* ========== 工具栏分组 ========== */
.toolbar-group {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding-right: 12px;
  margin-right: 12px;
  border-right: 1px solid #e2e8f0;
}

/* 纯图标按钮紧凑化 */
.toolbar-group .el-button {
  padding: 6px 8px;
}


.toolbar-group:last-child {
  border-right: none;
  padding-right: 0;
  margin-right: 0;
}

/* ========== 工具栏内保存按钮 ========== */
.toolbar-save-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 500;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.25);
  transition: all 0.25s ease !important;
}

.toolbar-save-btn:hover {
  background: linear-gradient(135deg, #059669 0%, #047857 100%) !important;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.35) !important;
  transform: translateY(-1px);
}

.toolbar-save-btn:active {
  transform: translateY(0);
}

/* ========== 响应式适配：平板端 (<= 1024px) ========== */
@media (max-width: 1024px) {
  .design-header {
    padding: 10px 12px;
    min-height: 50px;
  }

  .header-left {
    min-width: 120px;
  }

  .header-right {
    min-width: 80px;
  }

  /* 步骤标签缩小 */
  .step-tab {
    padding: 8px 18px;
    font-size: 13px;
    gap: 6px;
  }

  .step-tab .tab-icon {
    width: 16px;
    height: 16px;
  }

  .steps-tabs {
    gap: 2px;
    padding: 3px;
  }

  /* 流程名称缩短 */
  .flow-name {
    max-width: 180px;
    font-size: 14px;
    padding: 6px 12px;
  }

  /* 保存按钮缩小 */
  .save-btn {
    padding: 8px 16px !important;
    font-size: 13px;
  }

  .toolbar-group {
    gap: 2px;
    padding-right: 8px;
    margin-right: 8px;
  }

  /* Logo 水印隐藏，避免遮挡 */
  .logo-text {
    display: none;
  }
}

/* ========== 响应式适配：手机端 (<= 768px) ========== */
@media (max-width: 768px) {
  .design-header {
    padding: 6px 8px;
    min-height: 44px;
    /* 不换行，三栏强制一行 */
  }

  /* 步骤标签独占一行居中 */
  .header-left {
    min-width: auto;
    flex: 0 0 auto;
    overflow: hidden;
    max-width: 30%;
  }

  .header-center {
    position: static;
    left: auto;
    transform: none;
    flex: 1 1 auto; /* 占据中间剩余空间，不换行 */
    justify-content: center;
  }

  .header-right {
    min-width: auto;
    flex: 0 0 auto;
    justify-content: flex-end;
  }

  /* 步骤标签紧凑模式：纯图标+短文字 */
  .steps-tabs {
    gap: 2px;
    border-radius: 8px;
  }

  .step-tab {
    padding: 6px 12px;
    font-size: 12px;
    gap: 4px;
    border-radius: 6px;
  }

  .step-tab .tab-icon {
    width: 15px;
    height: 15px;
  }

  .step-tab.active {
    box-shadow: 0 2px 6px rgba(59, 130, 246, 0.25);
  }

  /* 流程名称进一步压缩 */
  .flow-name {
    max-width: 120px;
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 6px;
  }

  /* 保存按钮更小 */
  .save-btn {
    padding: 6px 10px !important;
    font-size: 12px;
    border-radius: 8px !important;
    box-shadow: 0 2px 6px rgba(16, 185, 129, 0.2);
  }

  .save-btn .save-icon {
    width: 14px;
    height: 14px;
  }

  /* 工具栏单行显示 + 横向滚动 */
  .toolbar-group {
    display: inline-flex;
    flex-wrap: nowrap;
    gap: 3px;
    padding-right: 6px;
    margin-right: 6px;
    border-right-color: #cbd5e1;
  }

  /* 工具栏容器可横向滚动 */
  .el-header > div {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none; /* Firefox 隐藏滚动条 */
  }
  .el-header > div::-webkit-scrollbar {
    display: none; /* Safari/Chrome 隐藏滚动条 */
  }

  /* 画布容器高度调整 */
  .container {
    height: calc(100dvh - 90px);
    min-height: 300px;
  }
}

/* ========== 响应式适配：超小屏 (<= 480px) ========== */
@media (max-width: 480px) {
  .design-header {
    padding: 4px 6px;
    min-height: 36px;
  }

  /* 步骤标签极简模式：更紧凑 */
  .step-tab {
    padding: 4px 10px;
    font-size: 11px;
    gap: 2px;
    border-radius: 5px;
  }

  .step-tab .tab-icon {
    width: 13px;
    height: 13px;
  }

  /* 流程名称极短 */
  .flow-name {
    max-width: 70px;
    font-size: 11px;
    padding: 3px 5px;
    border-radius: 4px;
  }

  .flow-name svg {
    display: none; /* 超小屏隐藏图标省空间 */
  }

  /* 保存按钮极小 */
  .save-btn {
    padding: 4px 7px !important;
    font-size: 11px;
    gap: 2px;
    border-radius: 6px !important;
  }

  .save-btn .save-icon {
    width: 12px;
    height: 12px;
  }

  /* 隐藏工具栏分隔线，节省空间 */
  .toolbar-group {
    padding-right: 2px;
    margin-right: 2px;
    border-right: none;
    gap: 2px;
  }

  /* 工具栏 el-header 缩进减少，给画布更多空间 */
  .el-header[style*="right"] {
    right: 8px !important;
    left: 44px !important;
  }

  /* 画布容器更矮 */
  .container {
    height: calc(100dvh - 75px);
    min-height: 220px;
  }
}
</style>
