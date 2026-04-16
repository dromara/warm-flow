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
import {DndPanel, InsertNodeInPolyline, Menu, Snapshot} from '@logicflow/extension';
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
      edgeText: {
        fontSize: 13,
        strokeWidth: 1,
        background: {
          fill: isDark.value ? "#141414" : "#fff",
        },
      },
    });

    initDndPanel();
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

/** 移动端屏幕尺寸变化（含旋转）时重绘画布 */
function handleMobileResize() {
  if (lf.value && lf.value.resize) {
    // 使用 requestAnimationFrame 确保 DOM 布局完成后再 resize
    requestAnimationFrame(() => {
      lf.value.resize();
    });
  }
}

// 监听窗口变化，真机旋转屏幕时重绘
window.addEventListener('resize', handleMobileResize);

/**
 * 初始化拖拽面板
 */
function initDndPanel() {
  // 只有经典模式才有拖拽面板
  if (isClassics(logicJson.value.modelValue)) {
    lf.value.extension.dndPanel.setPatternItems([
      {
        type: 'start',
        text: '开始',
        label: '开始节点',
        icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1OTQ3Mzg4IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjIwODA5IiB3aWR0aD0iMzYiIGhlaWdodD0iMzYiPjxwYXRoIGQ9Ik01MTIgMTAyNEMyMjkuMjMwNDMxIDEwMjQgMCA3OTQuNzY5NTY5IDAgNTEyUzIyOS4yMzA0MzEgMCA1MTIgMHM1MTIgMjI5LjIzMDQzMSA1MTIgNTEyLTIyOS4yMzA0MzEgNTEyLTUxMiA1MTJ6IG0wLTk1MC4zNzkwODVDMjY5Ljg4OTI1NSA3My42MjA5MTUgNzMuNjIwOTE1IDI2OS44ODkyNTUgNzMuNjIwOTE1IDUxMnMxOTYuMjY4MzQgNDM4LjM3OTA4NSA0MzguMzc5MDg1IDQzOC4zNzkwODUgNDM4LjM3OTA4NS0xOTYuMjY4MzQgNDM4LjM3OTA4NS00MzguMzc5MDg1Uzc1NC4xMTA3NDUgNzMuNjIwOTE1IDUxMiA3My42MjA5MTV6IiBmaWxsPSIjMDAwMDAwIiBwLWlkPSIyMDgxMCI+PC9wYXRoPjwvc3ZnPg==',
      },
      {
        type: 'between',
        text: '中间节点',
        label: '中间节点',
        icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1Mzc1ODI3IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjgzMTkiIHdpZHRoPSIzNiIgaGVpZ2h0PSIzNiI+PHBhdGggZD0iTTMzNS43NTE4MDQgMjQ0Ljc4MzE0N0MyODMuNjA3MDI5IDI0NC43ODMxNDcgMjQ2LjMwMzg5OSAyODQuODY5MDYgMjQ2LjE5OTI3IDMzMC41Mjk2NDJsMCAwLjAxMzIwNyAwIDAuMDEyNjQ4YzAuMDAzMjk0IDEzLjgwODQ2NSAzLjczOTc0MyAyOC4zODE3NDcgOS41Nzc0MzEgNDEuNTI2MzQyIDQuMjE1MTMyIDkuNDkxMTE1IDkuNDU1Nzk4IDE4LjIxNjY3NiAxNS44NDE3NDQgMjUuMjA2NjE0QzIzMy42NjU1ODggNDEwLjI3Mjg1MyAxODkuMjAxOTQ5IDQzMS42NDI4ODMgMTY2LjcyOCA0NzMuNzgxNTNMMTY1LjUxNjk2IDQ3Ni4wNTI1MTRsMCAxMzYuNDA4NDgyIDM0MC40Njk2ODkgMCAwLTEzNi40MDg0ODItMS4yMTEwNDMtMi4yNzA5ODRjLTIyLjE1MDcwOC00MS41MzI1NjYtNjUuNjUyMjA0LTYyLjg3Mjg0OC0xMDMuMjM4MjA0LTc1LjkxMTExNiAxOC4zNDg0MTktMTguNjU4MjE4IDIzLjc2MTA0OS00Mi43NDA1NzMgMjMuNzY2OTMyLTY3LjMxNDkybDAtMC4wMTI2NDggMC0wLjAxMzIwN0M0MjUuMTk5NzA3IDI4NC44NjkwNjMgMzg3Ljg5NjU4MyAyNDQuNzgzMTQ3IDMzNS43NTE4MDQgMjQ0Ljc4MzE0N1pNMzAwLjE0ODUyMyAyOTMuNDA0NTIxYzIuNDEwMzI4IDAuMDA2MDU5IDUuMDU2NjUgMC4wODY1NzIgNy45NzQwMTUgMC4yNTg1MjIgMjMuMjQ0MDI5IDEuMzcwMDI5IDMxLjA2Njk5NiA1LjU1NDA1OSAzNy4wODAzMjMgOS41MjIyODMgNi4wMTMyOTggMy45NjgyMjMgMTAuMjUyNDE3IDcuNzQ1Njk5IDI2LjE0NDE4OSA4LjIwODk4MmwwLjAwNDcwNiAwIDAuMDA1Mjk1IDBjMTIuMzgzODktMC40NjMyMTUgMTguMzM5NTA2LTIuNjcxMTM1IDIyLjYxMDQ1Mi01LjE3MjE5MiAxLjczMDY0NS0xLjAxMzQ1MyAzLjE4MzgyNS0yLjA2NzAwMyA0LjY3Mjk1LTMuMDcyOTg0IDMuOTM2MDM2IDguNDM2NjY4IDYuMDQ5NjU0IDE3Ljc2MjkzOCA2LjA3MzU5NyAyNy40MTQ5ODEtMC4wMDgyMzYgMjcuNDg0NjUyLTQuNzMzMzA4IDQ2LjczMjMwMi0yOS45MzQxNTQgNjIuNDgyODNsMi40NjUxNzcgMTguNTgwOTQ0YzUuMjQ1MzUxIDEuNTkyODg5IDEwLjY2NzMwNSAzLjM0MDY3MSAxNi4xNzAzNTQgNS4yNTcyMiAwLjc2ODUzNSAzLjIwNjE4MyAxLjY1NjQ5MiA3LjQxMTMwNiAyLjI1Mzc0OCAxMS44ODE3MzkgMC42MjU2NyA0LjY4MzQ1NCAwLjg3MTc0OSA5LjU1NjMzMyAwLjQ4NjAxMSAxMy4yMTUxMzktMC4zODU3MzggMy42NTg4MDYtMS41MjE3MTYgNS42MzM5NzItMS43MjExNzQgNS44MzM0NTktMTIuODA4OTg0IDEyLjgwODk1NS0zNS41NDYwMzYgMjAuMjc5MTM5LTU4LjYwODQzOCAyMC4yNzkxMzktMjMuMDYyMzkzIDAtNDUuNzk5NDQ0LTcuNDcwMTg0LTU4LjYwODQyMy0yMC4yNzkxMzktMC4xOTk0NjEtMC4xOTk0ODctMS4zMzU0NTYtMi4xNzQ2NTMtMS43MjExOTQtNS44MzM0NTktMC4zODU3MzUtMy42NTg4MDYtMC4xMzk2NjItOC41MzE2ODUgMC40ODYwMjYtMTMuMjE1MTM5IDAuNjAwNTI3LTQuNDk1MTE1IDEuNDk1NTUyLTguNzI1MDI4IDIuMjY2OTYzLTExLjkzNzQ2NyA1LjQ0ODA4Ni0xLjg5NDYwNiAxMC44MTU1NDctMy42MjQwNDIgMTYuMDEwMDczLTUuMjAxNDkxbDEuNDY5NTYxLTE5LjkwOTc1NmMtMS4xOTY1NzEtMS41MzQ1NjQtMi40MTU3ODItMi41NTExNjctMy44NzA5NTktMy42NDI4ODQtNS42MjQzNzctNC4yMTk1NjUtMTIuNDQ1MTQyLTEzLjUwMjI0NS0xNy4yNjMwNDktMjQuMzUwNjEzLTQuODE2MTg5LTEwLjg0NDQ5OS03LjgwMDAzOC0yMy4yNDAwMjMtNy44MDQ1MzYtMzMuMTYyODE4IDAuMDI5OTI2LTExLjg5NjM4MiAzLjIzMTM3OS0yMy4yOTkzMDQgOS4xMTE1MTYtMzMuMTQ5MDMyIDEuMDUyMTE1LTAuMzkxNjE4IDIuMTYxNTY2LTAuODA1NTE3IDMuNDA4NDg4LTEuMjE1NjM0IDQuMzg1MDE3LTEuNDQyMjUgMTAuMzkzOTczLTIuODE4ODg5IDIwLjgzODcxNi0yLjc5MjYyOHpNMjU1LjYzMDc4IDQyNS42MzgxMzZjLTAuMDE4NTAyIDAuMTM0OTYxLTAuMDM5MzUzIDAuMjY2NjgxLTAuMDU3NDQ5IDAuNDAyMTQ4LTAuNzYwOTE0IDUuNjk1NjM2LTEuMjA4MDMxIDExLjg5NTI3My0wLjU1MzgxNyAxOC4xMDA2NzUgMC42NTQyMTQgNi4yMDU0MDIgMi4yOTE1NjggMTIuODg2NDMyIDcuNjM4NTA3IDE4LjIzMzM1IDE4LjI1MDg2MSAxOC4yNTA4ODEgNDUuODcxNzU5IDI2LjMxMDIzMyA3My4xNjczMTggMjYuMzEwMjMzIDI3LjI5NTU1MSAwIDU0LjkxNjQ1Mi04LjA1OTM1MSA3My4xNjczMDQtMjYuMzEwMjMzIDUuMzQ2OTQ4LTUuMzQ2OTE4IDYuOTg0MzItMTIuMDI3OTQ4IDcuNjM4NTIyLTE4LjIzMzM1IDAuNjU0MjAyLTYuMjA1NDMxIDAuMjA3MTA2LTEyLjQwNTAzOS0wLjU1MzgxMS0xOC4xMDA2NzUtMC4wMTUwMDEtMC4xMTIyNDgtMC4wMzI0MTQtMC4yMjEzMDctMC4wNDc2OC0wLjMzMzIxIDI3Ljc0NzUzIDEyLjE2ODM2IDU0LjU2Nzc0NiAyOS41OTUyNjEgNjkuMzY3MDE1IDU1LjYxNDczNGwwIDExMC41NDkyMjgtNDkuMjY4ODMyIDAgMC03Ny45NDc3MDQtMjAuNTg5OTYgMCAwIDc3Ljk0NzcwNC0xNjAuMDEzNCAwIDAtNzcuOTQ3NzA0LTIwLjU4OTk2IDAgMCA3Ny45NDc3MDQtNDguODI3NjE4IDAgMC0xMTAuNTQ5MjI4YzE0LjgyNzE1Ni0yNi4wNjg1MzYgNDEuNzIwNTQ3LTQzLjUxMjIzMiA2OS41MjM4Ni01NS42ODM2NzJ6TTIxOS45ODEgMTA3LjUxOTU3NWMtMTA5LjkzNDgyNCAwLTE5OS41MDEgODkuMTg4MzQ1LTE5OS41MDEgMTk4LjkxMTk5OWwwIDQxMS4xMzU5ODljMCAxMDkuNzIzNjU5IDg5LjU2NjE3NiAxOTguOTEyMDExIDE5OS41MDEgMTk4LjkxMjAxMWw1ODQuMDM3OTg5IDBjMTA5LjkzNDg1MyAwIDE5OS41MDEwMDEtODkuMTg4MzUxIDE5OS41MDEwMDEtMTk4LjkxMjAxMWwwLTQxMS4xMzU5ODljMC0xMDkuNzIzNjUzLTg5LjU2NjE0OC0xOTguOTExOTk5LTE5OS41MDEwMDEtMTk4LjkxMTk5OWwtNTg0LjAzNzk4OSAwem0wIDYxLjQ0MDAwMWw1ODQuMDM3OTg5IDBjNzcuMDc0OTU1IDAgMTM4LjA2MTAwMyA2MC44Mzg5MTUgMTM4LjA2MTAwMyAxMzcuNDcxOTk4bDAgNDExLjEzNTk4OWMwIDc2LjYzMzA5NC02MC45ODYwNDggMTM3LjQ3MjAxMi0xMzguMDYxMDAzIDEzNy40NzIwMTJsLTU4NC4wMzc5ODkgMGMtNzcuMDc0OTYgMC0xMzguMDYxLTYwLjgzODkxOC0xMzguMDYxLTEzNy40NzIwMTJsMC00MTEuMTM1OTg5YzAtNzYuNjMzMDgyIDYwLjk4NjA0LTEzNy40NzE5OTggMTM4LjA2MS0xMzcuNDcxOTk4eiIgcC1pZD0iODMyMCI+PC9wYXRoPjwvc3ZnPg==',
        properties: {collaborativeWay: '1'},
      },
      {
        type: 'serial',
        text: '',
        label: '互斥网关',
        properties: {},
        icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1NTgyNzMwIiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjkzNjciIHdpZHRoPSI0NSIgaGVpZ2h0PSI0NSI+PHBhdGggZD0iTTAgMGgxMDI0djEwMjRIMHoiIGZpbGw9IiNGRkZGRkYiIGZpbGwtb3BhY2l0eT0iMCIgcC1pZD0iOTM2OCI+PC9wYXRoPjxwYXRoIGQ9Ik00NjUuMjI3Mjk0IDE0Mi4yNDU2NDdMMTQyLjI0NTY0NyA0NjUuMjI3Mjk0YTYxLjUwMDIzNSA2MS41MDAyMzUgMCAwIDAgMCA4Ni45Nzk3NjVsMzIyLjk4MTY0NyAzMjIuOTgxNjQ3YzI0LjAzMzg4MiAyNC4wMzM4ODIgNjIuOTQ1ODgyIDI0LjAzMzg4MiA4Ni45Nzk3NjUgMGwzMjIuOTgxNjQ3LTMyMi45ODE2NDdhNjEuNTAwMjM1IDYxLjUwMDIzNSAwIDAgMCAwLTg2Ljk3OTc2NUw1NTIuMjA3MDU5IDE0Mi4yNDU2NDdhNjEuNTAwMjM1IDYxLjUwMDIzNSAwIDAgMC04Ni45Nzk3NjUgMHogbTQ5LjY5NDExOCAzNy4yNTU1MjlsMzIzLjAxMTc2NCAzMjMuMDExNzY1YTguNzk0MzUzIDguNzk0MzUzIDAgMCAxIDAgMTIuNDA4NDcxTDUxNC45MjE0MTIgODM3LjkzMzE3NmE4Ljc5NDM1MyA4Ljc5NDM1MyAwIDAgMS0xMi40MDg0NzEgMEwxNzkuNTAxMTc2IDUxNC45MjE0MTJhOC43OTQzNTMgOC43OTQzNTMgMCAwIDEgMC0xMi40MDg0NzFMNTAyLjUxMjk0MSAxNzkuNTAxMTc2YTguNzk0MzUzIDguNzk0MzUzIDAgMCAxIDEyLjQwODQ3MSAweiIgZmlsbD0iIzAwMDAwMCIgcC1pZD0iOTM2OSI+PC9wYXRoPjxwYXRoIGQ9Ik01OTIuNTM0NTg4IDM4NS4wODQyMzVhMjYuMzUyOTQxIDI2LjM1Mjk0MSAwIDAgMSAzOS41NzQ1ODggMzQuNjM1Mjk0bC0yLjM3OTI5NCAyLjcxMDU4OS0yMTAuODIzNTI5IDIxMC4xMzA4MjNhMjYuMzUyOTQxIDI2LjM1Mjk0MSAwIDAgMS0zOS41NDQ0NzEtMzQuNjM1Mjk0bDIuMzQ5MTc3LTIuNzEwNTg4IDIxMC44MjM1MjktMjEwLjEzMDgyNHoiIGZpbGw9IiMwMDAwMDAiIHAtaWQ9IjkzNzAiPjwvcGF0aD48cGF0aCBkPSJNMzgxLjg2MTY0NyAzODQuOTMzNjQ3YTI2LjM1Mjk0MSAyNi4zNTI5NDEgMCAwIDEgMzQuNTQ0OTQxLTIuMzQ5MTc2bDIuNzEwNTg4IDIuMzQ5MTc2IDIxMC40OTIyMzYgMjEwLjUyMjM1M2EyNi4zNTI5NDEgMjYuMzUyOTQxIDAgMCAxLTM0LjU3NTA1OSAzOS42MDQ3MDZsLTIuNzEwNTg4LTIuMzQ5MTc3LTIxMC40NjIxMTgtMjEwLjUyMjM1M2EyNi4zNTI5NDEgMjYuMzUyOTQxIDAgMCAxIDAtMzcuMjU1NTI5eiIgZmlsbD0iIzAwMDAwMCIgcC1pZD0iOTM3MSI+PC9wYXRoPjwvc3ZnPg==',
      },
      {
        type: 'parallel',
        text: '',
        label: '并行网关',
        properties: {},
        icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1NjIwMDAyIiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjEwNDQxIiB3aWR0aD0iNDUiIGhlaWdodD0iNDUiPjxwYXRoIGQ9Ik0wIDBoMTAyNHYxMDI0SDB6IiBmaWxsPSIjRkZGRkZGIiBmaWxsLW9wYWNpdHk9IjAiIHAtaWQ9IjEwNDQyIj48L3BhdGg+PHBhdGggZD0iTTQ2NS4yMjcyOTQgMTQyLjI0NTY0N0wxNDIuMjQ1NjQ3IDQ2NS4yMjcyOTRhNjEuNTAwMjM1IDYxLjUwMDIzNSAwIDAgMCAwIDg2Ljk3OTc2NWwzMjIuOTgxNjQ3IDMyMi45ODE2NDdjMjQuMDMzODgyIDI0LjAzMzg4MiA2Mi45NDU4ODIgMjQuMDMzODgyIDg2Ljk3OTc2NSAwbDMyMi45ODE2NDctMzIyLjk4MTY0N2E2MS41MDAyMzUgNjEuNTAwMjM1IDAgMCAwIDAtODYuOTc5NzY1TDU1Mi4yMDcwNTkgMTQyLjI0NTY0N2E2MS41MDAyMzUgNjEuNTAwMjM1IDAgMCAwLTg2Ljk3OTc2NSAweiBtNDkuNjk0MTE4IDM3LjI1NTUyOWwzMjMuMDExNzY0IDMyMy4wMTE3NjVhOC43OTQzNTMgOC43OTQzNTMgMCAwIDEgMCAxMi40MDg0NzFMNTE0LjkyMTQxMiA4MzcuOTMzMTc2YTguNzk0MzUzIDguNzk0MzUzIDAgMCAxLTEyLjQwODQ3MSAwTDE3OS41MDExNzYgNTE0LjkyMTQxMmE4Ljc5NDM1MyA4Ljc5NDM1MyAwIDAgMSAwLTEyLjQwODQ3MUw1MDIuNTEyOTQxIDE3OS41MDExNzZhOC43OTQzNTMgOC43OTQzNTMgMCAwIDEgMTIuNDA4NDcxIDB6IiBmaWxsPSIjMDAwMDAwIiBwLWlkPSIxMDQ0MyI+PC9wYXRoPjxwYXRoIGQ9Ik01MDUuNzM1NTI5IDMzMy42NDMyOTRjMTMuNDMyNDcxIDAgMjQuNTE1NzY1IDEwLjA1OTI5NCAyNi4xNDIxMTggMjMuMDRsMC4yMTA4MjQgMy4zMTI5NDF2Mjk3LjY1MjcwNmEyNi4zNTI5NDEgMjYuMzUyOTQxIDAgMCAxLTUyLjQ5NTA1OSAzLjMxMjk0MWwtMC4yMTA4MjQtMy4zMTI5NDF2LTI5Ny42NTI3MDZjMC0xNC41NzY5NDEgMTEuNzc2LTI2LjM1Mjk0MSAyNi4zNTI5NDEtMjYuMzUyOTQxeiIgZmlsbD0iIzAwMDAwMCIgcC1pZD0iMTA0NDQiPjwvcGF0aD48cGF0aCBkPSJNNjU0LjU3Njk0MSA0ODIuNDg0NzA2YTI2LjM1Mjk0MSAyNi4zNTI5NDEgMCAwIDEgMy4zMTI5NDEgNTIuNDk1MDU5bC0zLjMxMjk0MSAwLjIxMDgyM0gzNTYuODk0MTE4YTI2LjM1Mjk0MSAyNi4zNTI5NDEgMCAwIDEtMy4zMTI5NDItNTIuNTI1MTc2bDMuMzEyOTQyLTAuMTgwNzA2aDI5Ny42ODI4MjN6IiBmaWxsPSIjMDAwMDAwIiBwLWlkPSIxMDQ0NSI+PC9wYXRoPjwvc3ZnPg==',
      },
      {
          type: 'inclusive',
          text: '',
          label: '包含网关',
          properties: {},
          icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzNiIgaGVpZ2h0PSIzNiIgdmlld0JveD0iMCAwIDEwMCAxMDAiPgogIDxwYXRoIGQ9Ik01MCwwIEwxMDAsNTAgTDUwLDEwMCBMMCw1MCBaIiAKICAgICAgICBzdHJva2U9IiMwMDAwMDAiIHN0cm9rZS13aWR0aD0iNiIgZmlsbD0ibm9uZSIgLz4KICA8Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSIyNSIgCiAgICAgICAgICBzdHJva2U9IiMwMDAwMDAiIHN0cm9rZS13aWR0aD0iNiIgZmlsbD0ibm9uZSIgLz4KPC9zdmc+Cg==',
      },
      {
        type: 'end',
        text: '结束',
        label: '结束节点',
        icon: "data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzUwMzg4OTY4OTA4IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIg0KICAgICB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjY5MTciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIg0KICAgICB3aWR0aD0iMzYiIGhlaWdodD0iMzYiPg0KICA8cGF0aCBkPSJNNTEyLjAwNTExNyA5NTguNzA4OTcxQzI2NS42ODMwMzUgOTU4LjcwODk3MSA2NS4yOTAwMDUgNzU4LjMxNjk2NSA2NS4yOTAwMDUgNTExLjk5Mzg2YzAtMjQ2LjMxMDgyNSAyMDAuMzkzMDMtNDQ2LjcwMzg1NSA0NDYuNzE1MTExLTQ0Ni43MDM4NTUgMjQ2LjMxMDgyNSAwIDQ0Ni43MDM4NTUgMjAwLjM5MzAzIDQ0Ni43MDM4NTUgNDQ2LjcwMzg1NUM5NTguNzA4OTcxIDc1OC4zMTY5NjUgNzU4LjMxNjk2NSA5NTguNzA4OTcxIDUxMi4wMDUxMTcgOTU4LjcwODk3MXpNNTEyLjAwNTExNyAxNjkuNzE2MzU2Yy0xODguNzM4NTk1IDAtMzQyLjI4OTc4NCAxNTMuNTQ1MDQ4LTM0Mi4yODk3ODQgMzQyLjI3NzUwNCAwIDE4OC43Mzg1OTUgMTUzLjU1MTE4OCAzNDIuMjg5Nzg0IDM0Mi4yODk3ODQgMzQyLjI4OTc4NCAxODguNzMzNDc5IDAgMzQyLjI3ODUyNy0xNTMuNTUxMTg4IDM0Mi4yNzg1MjctMzQyLjI4OTc4NEM4NTQuMjgzNjQ0IDMyMy4yNjE0MDUgNzAwLjczODU5NSAxNjkuNzE2MzU2IDUxMi4wMDUxMTcgMTY5LjcxNjM1NnoiIHAtaWQ9IjY5MTgiPjwvcGF0aD4NCjwvc3ZnPg=="
      },
    ]);
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
  let hasSignificantMove = false;

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

    startX = e.clientX;
    startY = e.clientY;
    hasSignificantMove = false;
    isLongPressFired = false;

    // 阻止浏览器默认的触摸行为（滚动/缩放/选择）
    // 但不调用 stopPropagation()，让事件继续冒泡到 LogicFlow
    e.preventDefault();

    // 启动长按计时器（500ms 无显著移动则视为右键菜单）
    longPressTimer = setTimeout(() => {
      if (hasSignificantMove) return; // 已移动，取消长按
      isLongPressFired = true;
      el.dispatchEvent(new MouseEvent('contextmenu', getEventOpts(e)));
    }, 500);

    // 对于 touch 类型：主动分发一次 mousedown 确保 LogicFlow 收到
    // （部分浏览器对 touch→mouse 的转换不够及时）
    if (e.pointerType === 'touch') {
      el.dispatchEvent(new MouseEvent('mousedown', getEventOpts(e)));
    }
  }, { passive: false });

  // ========== pointermove ==========
  el.addEventListener('pointermove', (e) => {
    if (hasSignificantMove) return; // 已经标记过就不再重复计算

    const dx = Math.abs(e.clientX - startX);
    const dy = Math.abs(e.clientY - startY);
    if (dx > 8 || dy > 8) {
      hasSignificantMove = true;
      // 有明显移动，取消长按
      if (longPressTimer) {
        clearTimeout(longPressTimer);
        longPressTimer = null;
      }
    }

    // 触摸时阻止默认滚动行为
    if (e.pointerType === 'touch') {
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

    // 长按已触发（右键菜单），不再处理 click/dblclick
    if (isLongPressFired) {
      resetTapState();
      return;
    }

    // 有明显移动 = 拖拽操作，不处理 click/dblclick
    if (hasSignificantMove) {
      resetTapState();
      return;
    }

    // ---- 点击/双击检测 ----
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
  el.addEventListener('pointercancel', () => {
    // 中断时只清理状态，不触发 click
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
  // 只有经典模式才有拖拽面板
  if (isClassics(logicJson.value.modelValue)) {
    LogicFlow.use(DndPanel);
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
    eventCenter.on('node:dbclick', (args) => {
      nodeClick.value = args.data
      let graphData = lf.value.getGraphData()
      nodes.value = graphData['nodes']
      skips.value = graphData['edges']
      proxy.$nextTick(() => {
        propertySettingRef.value.show()
      })
    })

    // 边双击事件
    eventCenter.on('edge:dbclick  ', (args) => {
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

html.dark .toolbar-group {
  border-right-color: #475569;
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

  html.dark .toolbar-group {
    border-right-color: #334155;
  }

  /* 画布容器高度调整 */
  .container {
    height: calc(100dvh - 90px);
    min-height: 300px;
  }

  /* DndPanel 缩窄 + 图标文字紧凑 */
  .lf-dndpanel {
    width: 44px !important;
    left: 4px !important;
    top: 8px !important;
    border-radius: 6px !important;
    overflow-y: auto !important;
    max-height: calc(100vh - 160px) !important;
  }

  .lf-dndpanel .lf-dnd-item {
    padding: 4px !important;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1px;
  }

  /* DndPanel 节点图标缩小 */
  .lf-dndpanel .lf-dnd-icon {
    width: 28px !important;
    height: 28px !important;
  }

  /* DndPanel 文字缩小并限制换行 */
  .lf-dndpanel .lf-dnd-text {
    font-size: 10px !important;
    line-height: 1.2 !important;
    word-break: break-all;
    white-space: normal;
    max-width: 42px;
    text-align: center;
    padding: 0 1px !important;
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

  /* DndPanel 极窄模式：图标为主 */
  .lf-dndpanel {
    width: 38px !important;
    left: 2px !important;
    top: 4px !important;
    max-height: calc(100vh - 150px) !important;
    padding: 2px !important;
  }

  .lf-dndpanel .lf-dnd-item {
    padding: 3px 1px !important;
  }

  .lf-dndpanel .lf-dnd-icon {
    width: 26px !important;
    height: 26px !important;
  }

  .lf-dndpanel .lf-dnd-text {
    font-size: 9px !important;
    line-height: 1.1 !important;
    max-width: 36px;
  }

  /* 工具栏 el-header 缩进减少，给画布更多空间 */
  .el-header[style*="right"] {
    right: 8px !important;
    left: 48px !important;
  }

  /* 画布容器更矮 */
  .container {
    height: calc(100dvh - 75px);
    min-height: 220px;
  }
}
</style>
