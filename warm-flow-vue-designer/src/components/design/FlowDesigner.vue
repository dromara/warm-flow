<template>
  <div :style="headerDiv">
    <!-- 顶部导航栏（抽为 FlowDesignerHeader 子组件；状态仍由容器持有，props 入 / 事件出） -->
    <FlowDesignerHeader
      v-if="!onlyDesignShow"
      :flow-name="logicJson.flowName"
      :active-step="activeStep"
      :steps="steps"
      :disabled="disabled"
      @step-click="handleStepClick"
      @save="saveJsonModel"
    >
      <!-- 仅转发消费方实际提供的具名插槽；未提供时由子组件渲染默认内容（保留默认回退） -->
      <template v-if="$slots['header-left']" #header-left="s"><slot name="header-left" v-bind="s" /></template>
      <template v-if="$slots['header-center']" #header-center="s"><slot name="header-center" v-bind="s" /></template>
      <template v-if="$slots['header-actions']" #header-actions="s"><slot name="header-actions" v-bind="s" /></template>
    </FlowDesignerHeader>

    <wf-header :style="headerStyle">
      <!-- 画布工具栏（抽为 FlowDesignerToolbar 子组件；画布操作仍由容器持有，props 入 / 事件出） -->
      <FlowDesignerToolbar
        :active-step="activeStep"
        :only-design-show="onlyDesignShow"
        :disabled="disabled"
        :lf="lf"
        @zoom-out="zoomViewport(false)"
        @fit-view="zoomViewport('fit')"
        @zoom-in="zoomViewport(true)"
        @undo="undoOrRedo(true)"
        @redo="undoOrRedo(false)"
        @clear="clear()"
        @download-image="downLoad"
        @download-json="downJson"
        @save="saveJsonModel"
      >
        <!-- 仅转发消费方实际提供的 toolbar-extra 插槽（透出 lf / disabled） -->
        <template v-if="$slots['toolbar-extra']" #toolbar-extra="s"><slot name="toolbar-extra" v-bind="s" /></template>
      </FlowDesignerToolbar>

      <BaseInfo :style="baseInfoStyle" ref="baseInfoRef" v-if="!onlyDesignShow" v-show="activeStep === 0"
                :logic-json="logicJson" :category-list="categoryList" :form-path-list="formPathList"
                :definition-id="definitionId" :disabled="disabled"
                @update:flow-name="handleFlowNameUpdate" @update:model-value="handleModelValueUpdate"
                @validate-error="handleBaseInfoValidateError"/>

      <!-- 自定义拖拽侧边栏：仅经典模式流程设计页签显示 + 延迟显示避免与 LogicFlow DOM 冲突。
           #sidebar 插槽可整体替换内置面板（透出命令式 dragInNode + lf / disabled）；
           paletteNodes 可仅自定义内置面板的节点列表（任一分组不传用内置默认）。 -->
      <template v-if="sidebarVisible && activeStep === 1">
        <slot name="sidebar" :drag-in-node="handleDragInNode" :lf="lf" :disabled="disabled">
          <DiagramSidebar
            class="diagram-sidebar"
            :flow-nodes="paletteNodes?.flowNodes"
            :gateway-nodes="paletteNodes?.gatewayNodes"
            @dragInNode="handleDragInNode"
          />
        </slot>
      </template>

      <div class="container" ref="containerRef" v-show="activeStep === 1">
        <PropertySetting ref="propertySettingRef" :node="nodeClick" :lf="lf" :disabled="disabled"
                         :skipConditionShow="skipConditionShow" :nodes="nodes" :skips="skips"
                         :form-path-list="formPathList">
          <!-- 属性面板插槽透传：FlowDesigner → PropertySetting → 节点属性子组件（start/between/gateway/end/skip）。
               消费方可用 #node-form-extra（透出 { form, disabled }）等具名插槽往节点属性抽屉注入自定义表单项。
               透传全部插槽：节点子组件未声明的插槽自动忽略，header-*/logo 等不会渲染到属性面板。 -->
          <template v-for="(_, name) in $slots" #[name]="slotProps">
            <slot :name="name" v-bind="slotProps || {}" />
          </template>
        </PropertySetting>
      </div>
      <div class="logo-text" v-if="activeStep === 1"><slot name="logo">Warm-Flow</slot></div>
    </wf-header>

    <!-- 弹框组件 -->
    <EdgeTooltip
        v-if="tooltipVisible"
        :position="tooltipPosition"
        :tooltipEdge="tooltipEdge"
        @option-click="handleOptionClick"
        @close-tooltip="tooltipVisible = false"
    />

    <!-- 加载态 / 空态覆盖层：数据加载中 / 加载后无可用定义（如 definitionId 失效）。
         均带默认回退，消费方可用 #loading / #empty 插槽自定义内容。 -->
    <div class="wf-state-overlay" v-if="loading">
      <slot name="loading"><div class="wf-state-default">{{ t('common.loading') }}</div></slot>
    </div>
    <div class="wf-state-overlay" v-else-if="isEmpty">
      <slot name="empty"><div class="wf-state-default">{{ t('flowDesigner.emptyDef') }}</div></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { getUiAdapter } from '@/ui/uiAdapter'
import PropertySetting from '@/components/design/common/vue/propertySetting.vue'
import { queryDef, saveJson } from "@/api/flow/definition";
import {
    getPreviousNodes,
    isClassics, isGateWay,
    json2LogicFlowJson,
    logicFlowJsonToWarmFlow
} from "@/components/design/common/js/tool";
import {computed, getCurrentInstance, nextTick, onMounted, onUnmounted, ref, watch} from "vue";
import BaseInfo from "@/components/design/common/vue/baseInfo.vue";
import initClassicsData from "@/components/design/classics/initClassicsData.json";
import initMimicData from "@/components/design/mimic/initMimicData.json";
import {addBetweenNode, addGatewayNode, gatewayAddNode, removeNode} from "@/components/design/mimic/js/mimic";
import EdgeTooltip from "@/components/design/mimic/vue/EdgeTooltip.vue";
import DiagramSidebar from "@/components/design/common/vue/DiagramSidebar.vue";
import FlowDesignerHeader from "@/components/design/FlowDesignerHeader.vue";
import FlowDesignerToolbar from "@/components/design/FlowDesignerToolbar.vue";
import { useLogicFlowCanvas } from '@/composables/useLogicFlowCanvas';
import { useI18n } from '@/i18n';
import type {
  FlowDesignerProps,
  FlowDesignerSavedPayload,
  FlowDesignerReadyPayload,
  FlowDesignerBeforeSavePayload,
  FlowDesignerChangePayload,
  FlowDesignerValidateErrorPayload,
  FlowDesignerNodeClickPayload,
  FlowStructureValidateResult
} from '@/designer/types';

/**
 * 可复用流程设计器组件：画布核心从「直接读 URL/appParams」改为「props 驱动」，
 * 宿主耦合（URL 主题、postMessage、关闭）由外层页面壳负责，组件本身可被业务方直接 import 使用。
 */
defineOptions({ name: 'FlowDesigner' });

/** props 定义见 @/designer/types（公共类型，消费方可直接 import 复用）。 */
const props = withDefaults(defineProps<FlowDesignerProps>(), {
  definitionId: null,
  initialJson: null,
  disabled: false,
  onlyDesignShow: false,
  showGrid: false,
  customNodes: () => [],
  extraExtensions: () => [],
  lfOptions: () => ({}),
});
/**
 * 对外事件：
 * - close：设计器请求关闭（保存成功后自动触发，宿主据此关闭弹窗 / 返回列表）
 * - saved：保存成功回传当前定义 id、后端返回数据（如新建后的 definitionId）与本次保存的流程 json
 * - ready：画布初始化完成，透出底层 LogicFlow 实例，便于高级定制（自定义事件 / 主题 / 扩展）
 * - before-save：保存提交前（同步），可改写 json 或取消本次保存
 * - change：画布图数据变更（基于 LogicFlow history:change，初次渲染不触发），带惰性 getter
 * - dirty：未保存状态翻转（首次变更 false→true，保存成功 / resetDirty true→false）
 * - validate-error：基础信息表单校验未通过（保存 / 切步骤 / 命令式 validate 触发），透出来源与无效字段
 * - node-click：画布节点被点击，透出节点 id / type / data 与 lf
 * - update:json：受控 json 回写（配合 v-model:json，仅 json prop 受控时派发）
 */
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'saved', payload: FlowDesignerSavedPayload): void;
  (e: 'ready', payload: FlowDesignerReadyPayload): void;
  (e: 'before-save', payload: FlowDesignerBeforeSavePayload): void;
  (e: 'change', payload: FlowDesignerChangePayload): void;
  (e: 'dirty', dirty: boolean): void;
  (e: 'validate-error', payload: FlowDesignerValidateErrorPayload): void;
  (e: 'node-click', payload: FlowDesignerNodeClickPayload): void;
  (e: 'update:json', json: string): void;
}>();

const { proxy } = getCurrentInstance()!;
const { t } = useI18n();

const definitionId = ref<string | null>(props.definitionId);
const nodeClick = ref<any>(null);
const disabled = ref<boolean>(props.disabled);
const propertySettingRef = ref<any>({});
const logicJson = ref<Record<string, any>>({});
const jsonString = ref<any>('');
const skipConditionShow = ref(true);
const nodes = ref<any[]>([]);
const skips = ref<any[]>([]);
const categoryList = ref<any[]>([]);
const formPathList = ref<any[]>([]);
// 画布容器 DOM（LogicFlow 挂载点），交给 useLogicFlowCanvas 管理
const containerRef = ref<HTMLElement>();
// 控制侧边栏显示：延迟到画布初始化完成后显示，避免与 LogicFlow DOM 初始化冲突
const sidebarVisible = ref(false);

// 数据加载态：初始流程定义加载中（queryDef 在途）；isEmpty：加载完成但无可用定义数据（如 definitionId 失效）
const loading = ref(false);
const isEmpty = ref(false);

// 受控模式（绑定了 v-model:json）：仅此时回写 update:json，避免未使用 v-model 的消费方承担序列化开销
const isJsonControlled = () => props.json !== undefined;

// —— 未保存（dirty）追踪：基于画布图数据变更（LogicFlow history:change），不含基础信息表单字段 ——
// dirty：当前是否有未保存的画布改动；canvasReady：画布是否渲染完成（守卫初始渲染触发的 history:change 不误标 dirty）
const dirty = ref(false);
const canvasReady = ref(false);

/** 画布内容变更：仅在画布就绪后计数，首次翻转 emit('dirty', true)，每次变更 emit('change', ...) 带惰性 getter。 */
function markDirty() {
  if (!canvasReady.value) return;
  if (!dirty.value) {
    dirty.value = true;
    emit('dirty', true);
  }
  emit('change', { dirty: true, getJson: getFlowJson, getGraphData });
  // v-model:json 回写（仅受控时）：变更后同步最新 json 给宿主
  if (isJsonControlled()) {
    emit('update:json', getFlowJson());
  }
}

/** 复位为「干净 / 已保存」基线：若此前为 dirty 则 emit('dirty', false)。 */
function markPristine() {
  if (dirty.value) {
    dirty.value = false;
    emit('dirty', false);
  }
}

// 记录当前一次基础信息校验的来源（save / step / api），供 BaseInfo 校验失败时归因 validate-error
const validateSource = ref<'save' | 'step' | 'api'>('save');

/** BaseInfo 校验失败回调：透出来源 + 无效字段（fields 随 UI 适配器，best-effort）。 */
function handleBaseInfoValidateError(fields?: Record<string, any>) {
  emit('validate-error', { source: validateSource.value, fields });
}

/** 透出节点点击事件（经典 / 仿钉钉双模式共用），node 为 LogicFlow 节点 data。 */
function emitNodeClick(node: any) {
  if (!node) return;
  emit('node-click', { id: node.id, type: node.type, data: node, lf: lf.value });
}

/**
 * LogicFlow 画布生命周期（实例 / 视口 / IO / resize / 触摸桥接 / 暗黑）抽到 useLogicFlowCanvas。
 * 组件保留向导、基础信息表单、属性面板编排与画布事件桥接（initEvent），通过 ref + 回调注入。
 */
const {
  lf,
  initLogicFlow,
  zoomViewport,
  undoOrRedo,
  clear,
  downLoad,
  downJson,
  fitViewAll,
  fitViewIfMobile,
  handleDragInNode,
  getGraphData,
} = useLogicFlowCanvas({
  props,
  containerRef,
  logicJson,
  disabled,
  getBaseInfo,
  bindEvents: initEvent,
  onReady: (lfInstance) => {
    // LogicFlow 完全初始化后，再显示自定义拖拽侧边栏（避免 DOM 操作冲突）
    if (isClassics(logicJson.value.modelValue)) {
      sidebarVisible.value = true;
    }
    // 画布渲染完成：以当前内容为「干净」基线，并在下一 tick 开放变更追踪
    // （跳过初始渲染同步触发的 history:change，避免一打开就被标记为 dirty）
    markPristine();
    nextTick(() => { canvasReady.value = true; });
    // 透出底层 LogicFlow 实例：消费方可据此做高级定制（自定义事件 / 主题 / 扩展）
    emit('ready', { lf: lfInstance });
  },
});

const activeStep = ref(0);
const onlyDesignShow = ref(props.onlyDesignShow);

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
        minHeight: "100vh",
        position: "relative",
    };
});

// 步骤数据
const steps = computed(() => [
  { title: t('flowDesigner.stepBaseInfo'), icon: 'baseInfo' },
  { title: t('flowDesigner.stepFlowDesign'), icon: 'flowDesign' },
  // { title: t('flowDesigner.stepFormDesign'), icon: 'formDesign' }
]);

const tooltipVisible = ref(false);
const tooltipPosition = ref({ x: 0, y: 0 });
const tooltipEdge = ref({});

const handleOptionClick = (item: any) => {
  // item.type 为节点类型（between/serial/parallel/inclusive）；item.icon 仅为弹层展示图标名
  const nodeType = item.type || item.icon;
  if (nodeType === "between") {
    addBetweenNode(lf.value, item.tooltipEdge);
  } else {
    addGatewayNode(lf.value, item.tooltipEdge, nodeType);
  }
  tooltipVisible.value = false;
  // 移动端/平板端：新增节点后自适应画布，确保所有节点可见
  proxy.$nextTick(() => { fitViewIfMobile(); });
};

async function handleStepClick(index: number) {
  if (activeStep.value === 0 && !onlyDesignShow.value) {
    validateSource.value = 'step';
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
    // 标记当前处于设计器页面：供经典节点设计态语义色判断（业务侧实例进度图不受影响）
    window.__WF_FLOW_DESIGN_MODE__ = true;
    // definitionId / initialJson / onlyDesignShow / disabled 均由 props 注入（见上方 ref 初始化）；
    // URL 主题与 postMessage 监听由外层页面壳负责，组件本身不直接依赖 URL。

    // 数据源优先级：props.json（v-model 受控）> props.initialJson（脱后端，直接喂流程 JSON）> queryDef（走 DataProvider 后端/mock）
    const localSource = (props.json != null && props.json !== '')
      ? props.json
      : ((props.initialJson != null && props.initialJson !== '') ? props.initialJson : null);
    if (localSource != null) {
      const data = typeof localSource === 'string' ? JSON.parse(localSource) : localSource;
      applyDefinition(data);
    } else {
      // 走后端/mock：标记加载态，供 #loading 插槽展示
      loading.value = true;
      queryDef(definitionId.value).then(res => applyDefinition(res.data)).catch(() => applyDefinition(null));
    }
})

/**
 * 应用流程定义数据（来源：props.initialJson 或 DataProvider.queryDef）。
 * 解析发布态/类别/表单路径 → 转换为 LogicFlow json → 空流程回退到内置初始模板。
 * 抽出为独立函数，使「脱后端 initialJson」与「后端 queryDef」两条入口复用同一套装配逻辑。
 */
function applyDefinition(data: any) {
  // 加载结束：关闭加载态；无可用定义数据（如 definitionId 失效 / 请求失败）则标记空态供 #empty 插槽展示
  loading.value = false;
  isEmpty.value = !data;
  jsonString.value = data;
  if (data?.isPublish && data.isPublish !== 0) {
    disabled.value = true;
  }
  if (data?.categoryList && data.categoryList.length > 0) {
    categoryList.value = data.categoryList;
  }
  if (data?.formPathList && data.formPathList.length > 0) {
    formPathList.value = data.formPathList;
  }
  if (jsonString.value) {
    logicJson.value = json2LogicFlowJson(jsonString.value);
    if (!logicJson.value.nodes || logicJson.value.nodes.length === 0) {
      // 空流程：回退到内置初始模板（经典 / 仿钉钉）
      let initData = isClassics(logicJson.value.modelValue) ? initClassicsData : initMimicData;
      logicJson.value = {
        ...logicJson.value,
        ...initData
      };
    }
    if (onlyDesignShow.value) {
      handleStepClick(1);
    }
  }
}


// —— 画布生命周期（initLogicFlow / scheduleMobileResize / 触摸桥接 / resize）已抽到 useLogicFlowCanvas ——

/**
 * 监听步骤切换：从"基础信息"切到"流程设计"时，自适应显示全部节点（所有端）
 */
watch(activeStep, (newVal) => {
  if (newVal === 1 && lf.value) {
    const doFit = () => {
      if (lf.value?.resize) {
        lf.value.resize();
        requestAnimationFrame(fitViewAll);
      }
    };
    // 多次延迟确保在各种时序下移动端能正确适配
    nextTick(doFit);
    setTimeout(doFit, 150);
    setTimeout(doFit, 400);
    setTimeout(doFit, 800);
  }
});

onUnmounted(() => {
  // 离开设计器页面，复位设计态标记（resize 监听 + __WF_DESIGNER_DISABLED__ 复位由 useLogicFlowCanvas 负责）
  window.__WF_FLOW_DESIGN_MODE__ = false;
});

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

function handleFlowNameUpdate(newName: string) {
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
    // 重新渲染期间关闭变更追踪：忽略初始渲染触发的 history:change（onReady 后下一 tick 重新开放）
    canvasReady.value = false;
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
  const loadingInstance = getUiAdapter().loading({ fullscreen: true, text: t('flowDesigner.saving') })
  if (!onlyDesignShow.value) {
    validateSource.value = 'save';
    let validate = await proxy.$refs.baseInfoRef.validate();
    if (!validate) {
      loadingInstance.close();
      return;
    }
  }
  // 装配本次提交的流程 json（基础信息 + 画布图数据 → warm-flow 结构）。与命令式 getFlowJson 同一来源，避免重复装配逻辑。
  let jsonString = getFlowJson();

  // before-save 钩子（同步）：消费方可改写本次提交的 json，或取消保存。
  // 异步逻辑不会被等待——setJson / preventDefault 须在处理函数同步执行期间调用。
  let saveCancelled = false;
  emit('before-save', {
    id: definitionId.value,
    json: jsonString,
    onlyDesignShow: onlyDesignShow.value,
    setJson: (next: string) => { if (typeof next === 'string') jsonString = next; },
    preventDefault: () => { saveCancelled = true; },
  });
  if (saveCancelled) {
    loadingInstance.close();
    return;
  }

  saveJson(jsonString, onlyDesignShow.value).then(response => {
    if (response.code === 200) {
      // $modal 由宿主（如 ruoyi 体系 plugins）提供；npm 组件库消费场景可能未注册，做降级避免报错
      if (proxy.$modal && proxy.$modal.msgSuccess) {
        proxy.$modal.msgSuccess(t('flowDesigner.saveSuccess'));
      }
      // 保存成功：复位未保存标记（dirty → false，必要时 emit('dirty', false)）
      markPristine();
      // 通知宿主保存成功（向后兼容加法）：回传当前定义 id、后端返回数据（如新建后的 definitionId）与本次保存的流程 json，
      // 宿主据此可实现「保存→修改/预览」闭环；不影响既有 close 行为。
      emit('saved', { id: definitionId.value, data: response.data, json: jsonString });
      // 延迟500ms后关闭页面
        setTimeout(() => {
            emit('close')
        }, 500)
    }
  }).finally(() => {
    nextTick(() => {
      loadingInstance.close();
    });
  });
}

// —— 移动端触摸事件桥接 / initMenu / register / use 已抽到 useLogicFlowCanvas ——
function initEvent() {
  const { eventCenter } = lf.value.graphModel

  // 画布图数据变更（增删改节点 / 边、移动、撤销 / 重做）的统一信号：标记未保存 + 透出 change / dirty。
  // 初始渲染同步触发的 history:change 由 canvasReady 守卫忽略（见 markDirty / onReady）。
  eventCenter.on('history:change', markDirty)

  if (!isClassics(logicJson.value.modelValue)) {
    // 更新节点名称
    eventCenter.on('update:nodeName', (data) => {
      if (disabled.value) return;
      lf.value.updateText(data.id, data.nodeName)
      lf.value.setProperties(data.id, {
        nodeName: data.nodeName
      })
    })

    // 网关节点单击事件
    eventCenter.on('node:click', (args) => {
      nodeClick.value = args.data
      emitNodeClick(args.data)
      // 只读态不响应网关「加节点」
      if (isGateWay(nodeClick.value.type) && !disabled.value) {
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
      // 只读态不弹出边「+」加节点菜单
      if (disabled.value) return;
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
      if (disabled.value) return;
      const nodeModel = lf.value.getNodeModelById(args.id)
      removeNode(lf.value, nodeModel)
    })
  } else {
    // 中间节点双击事件
    eventCenter.on('node:click', (args) => {
      nodeClick.value = args.data
      emitNodeClick(args.data)
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

// —— 视口操作（zoomViewport / undoOrRedo / clear）与下载（downLoad / downJson）已抽到 useLogicFlowCanvas ——

/**
 * 校验基础信息表单（命令式 API）。
 * onlyDesignShow 模式无基础信息页，恒为通过。
 */
async function validate(): Promise<boolean> {
  if (onlyDesignShow.value) return true;
  validateSource.value = 'api';
  return await proxy.$refs.baseInfoRef.validate();
}

/**
 * 收集当前可保存的 warm-flow 流程 json 字符串（命令式 API）。
 * 等价于保存前的数据装配：合并基础信息 + 画布图数据，再转 warm-flow 结构。
 */
function getFlowJson(): string {
  getBaseInfo();
  if (lf.value) {
    const graphData = lf.value.getGraphData();
    logicJson.value['nodes'] = graphData['nodes'];
    logicJson.value['edges'] = graphData['edges'];
  }
  logicJson.value['id'] = definitionId.value;
  return logicFlowJsonToWarmFlow(logicJson.value);
}

/**
 * 校验流程结构（命令式 API）。
 * 内置检查：≥1 开始节点（type=start）、≥1 结束节点（type=end）、无孤立节点（多节点时存在未连任何边的节点）；
 * 再追加调用消费方的 props.structureValidator（如有），合并错误信息。
 * 不自动拦截保存——消费方可在 before-save 里据返回的 valid 决定是否 preventDefault()。
 */
function validateStructure(): FlowStructureValidateResult {
  const errors: string[] = [];
  const graph = lf.value ? lf.value.getGraphData() : { nodes: [], edges: [] };
  const graphNodes: any[] = graph.nodes || [];
  const graphEdges: any[] = graph.edges || [];
  if (graphNodes.filter((n) => n.type === 'start').length < 1) errors.push(t('flowDesigner.errNoStart'));
  if (graphNodes.filter((n) => n.type === 'end').length < 1) errors.push(t('flowDesigner.errNoEnd'));
  if (graphNodes.length > 1) {
    const connected = new Set<string>();
    graphEdges.forEach((e) => { connected.add(e.sourceNodeId); connected.add(e.targetNodeId); });
    const isolated = graphNodes.filter((n) => !connected.has(n.id));
    if (isolated.length > 0) errors.push(t('flowDesigner.errIsolated', { n: isolated.length }));
  }
  const custom = props.structureValidator ? props.structureValidator({ nodes: graphNodes, edges: graphEdges }) : undefined;
  if (Array.isArray(custom)) {
    custom.forEach((msg) => { if (msg) errors.push(String(msg)); });
  }
  return { valid: errors.length === 0, errors };
}

/**
 * 对外暴露命令式 API（FlowDesignerInstance）。
 *
 * 消费方两种用法：
 * 1) 模板 ref：<FlowDesigner ref="designerRef" />，designerRef.value.save() ...
 * 2) 组合式（推荐）：const { designerRef, save, getFlowJson } = useFlowDesigner()
 *
 * 让宿主无需依赖内部按钮，即可程序化触发保存 / 缩放 / 导出，并可拿到底层 LogicFlow 做高级定制。
 */
defineExpose({
  save: saveJsonModel,
  validate,
  getGraphData,
  getFlowJson,
  getFlowName: () => logicJson.value.flowName ?? '',
  getLogicFlow: () => lf.value,
  zoom: zoomViewport,
  zoomIn: () => zoomViewport(true),
  zoomOut: () => zoomViewport(false),
  fitView: () => zoomViewport('fit'),
  resetZoom: () => zoomViewport(1),
  undo: () => undoOrRedo(true),
  redo: () => undoOrRedo(false),
  clear,
  downloadImage: downLoad,
  downloadJson: downJson,
  isDirty: () => dirty.value,
  resetDirty: markPristine,
  validateStructure,
});

</script>

<style>
/* 设计 token 静态层（--wf-* 颜色/圆角/阴影/间距/层级…）。
   置于设计器根组件的「全局（非 scoped）样式」中，经组件样式管线打进库唯一 CSS 产物，
   使组件库无需运行时注入即可正确渲染，且消费方可覆盖 :root --wf-* 整体换肤。
   （相比在入口 JS `import 'tokens.scss'`，此处走 CSS 抽取，避免 token 被内联进 JS 包。） */
@import '@/assets/styles/tokens.scss';


/* ========== 画布容器 ========== */
.container {
  flex: 1;
  width: 100%;
  /* 真机兼容：使用 dvh（动态视口高度）+ vh 兜底，解决移动端地址栏导致 100vh 不准确的问题 */
  height: calc(100dvh - 100px);
  min-height: 400px;
  border-radius: var(--wf-radius);
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
  background-color: var(--wf-bg-color) !important;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.25) !important;
  border: 1px solid var(--wf-border-color) !important;
}

/* ========== LogicFlow 节点/边右键菜单（Menu 扩展） ==========
   LF 默认样式白底且不设文字色：暗黑下页面继承浅色文字 → 白底白字不可见（gitee #IJVBTJ）。
   这里统一走 --wf-* token（html.dark 自动翻转），亮暗两态均显式着色。 */
.lf-menu {
  background: var(--wf-bg-white, #fff);
  border: 1px solid var(--wf-border-light, #efefee);
  color: var(--wf-text-primary, #303133);
  box-shadow: var(--wf-shadow-lg, 0 4px 16px rgba(0, 0, 0, 0.1));
}

.lf-menu .lf-menu-item:hover {
  background: var(--wf-bg-color, #f3f3f3);
}

.lf-menu .lf-menu-item__disabled {
  color: var(--wf-text-placeholder, #aaa);
}

.lf-menu .lf-menu-item__disabled:hover {
  background: transparent;
}

/* ========== 仿钉钉边「+」加节点按钮 / 条件徽标（SVG 组，类名由 mimic/js/skip.ts 注入） ==========
   悬浮放大 + 主色投影，让「可点击加节点」的示能更明显；transform-box 保证以按钮自身为中心缩放。 */
.wf-edge-plus {
  transform-box: fill-box;
  transform-origin: center;
  transition: transform 0.18s cubic-bezier(0.4, 0, 0.2, 1), filter 0.18s ease;
  filter: drop-shadow(0 2px 6px rgba(64, 158, 255, 0.35));
}

.wf-edge-plus:hover {
  transform: scale(1.18);
  filter: drop-shadow(0 4px 10px rgba(64, 158, 255, 0.5));
}

.wf-edge-cond {
  transform-box: fill-box;
  transform-origin: center;
  transition: transform 0.18s cubic-bezier(0.4, 0, 0.2, 1);
}

.wf-edge-cond:hover {
  transform: scale(1.08);
}

/* 仿钉钉网关胶囊 chip（类名由 mimic/js/gatewayView.ts 注入）：柔和投影 + hover 微放大 */
.wf-gateway-chip {
  transform-box: fill-box;
  transform-origin: center;
  transition: transform 0.18s cubic-bezier(0.4, 0, 0.2, 1), filter 0.18s ease;
  filter: drop-shadow(0 2px 6px rgba(15, 23, 42, 0.10));
}

.wf-gateway-chip:hover {
  transform: scale(1.06);
  filter: drop-shadow(0 4px 10px rgba(15, 23, 42, 0.16));
}

/* ========== 加载态 / 空态覆盖层 ========== */
.wf-state-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 20;
  background: var(--wf-bg-white);
}

.wf-state-default {
  font-size: 14px;
  color: var(--wf-text-secondary);
  letter-spacing: 1px;
}

html.dark .wf-state-overlay {
  background: var(--wf-bg-color) !important;
}

/* ========== Logo 水印 ========== */
.logo-text {
  position: absolute;
  font-weight: bold;
  right: 50px;
  bottom: 10px;
  font-size: 14px;
  color: var(--wf-text-placeholder);
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
  background: var(--wf-bg-white);
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
  background: var(--wf-bg-white);
  border-radius: 8px;
  border: 1px solid var(--wf-border-light);
  cursor: default;
  transition: all 0.2s ease;
}

html.dark .flow-name {
  color: var(--wf-text-primary) !important;
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
  background: var(--wf-bg-white);
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08), 0 1px 2px rgba(0, 0, 0, 0.04);
}

html.dark .steps-tabs {
  background: var(--wf-bg-color) !important;
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
  color: var(--wf-primary);
  background: var(--wf-primary-light);
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
  background: var(--wf-primary);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.28);
}

.step-tab.active .tab-icon {
  opacity: 1;
}

.step-tab.active:hover {
  background: var(--wf-primary-dark);
}

/* ========== 保存按钮 ========== */
.save-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--wf-primary) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 500;
  font-size: 14px;
  padding: 9px 22px !important;
  border-radius: 10px !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.28);
  transition: all 0.2s ease !important;
}

.save-btn:hover {
  background: var(--wf-primary-dark) !important;
}

/* Element Plus 会把按钮默认插槽内容包进一层 span，
   导致 .save-btn 上的 gap 作用不到「图标 + 文字」之间。
   这里让该包裹层成为 flex 容器，恢复图标与文字的间距与垂直居中。 */
.save-btn > span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
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

/* 暗黑模式：保存按钮统一主色，扁平 */
html.dark .save-btn,
html.dark .toolbar-save-btn {
  background: var(--wf-primary) !important;
  box-shadow: none !important;
}
html.dark .save-btn:hover,
html.dark .toolbar-save-btn:hover {
  background: var(--wf-primary-dark) !important;
}

/* 工具栏区域（el-header 第二行）暗黑模式 */
html.dark .el-header {
  --el-bg-color: var(--wf-bg-white);
  background-color: var(--wf-bg-white);
}

html.dark .toolbar-group {
  border-right-color: var(--wf-border-color);

  /* 工具栏内按钮暗黑模式 */
  .el-button {
    --el-button-bg-color: var(--wf-bg-color);
    --el-button-text-color: var(--wf-text-primary);
    --el-border-color: var(--wf-border-color);
    color: var(--wf-text-primary);
    background-color: transparent;
    border-color: var(--wf-border-color);

    &:hover,
    &:focus {
      color: var(--wf-primary);
      background-color: rgba(64,158,255,.12);
      border-color: var(--wf-primary);
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
  background: var(--wf-primary) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 500;
  border-radius: 8px !important;
  box-shadow: none;
  transition: background-color 0.2s ease !important;
}

.toolbar-save-btn:hover {
  background: var(--wf-primary-dark) !important;
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

  /* 平板端工具栏也居右显示 */
  .design-toolbar {
    text-align: right !important;
    padding-right: 8px !important;
    padding-left: 0 !important;
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

  /* 工具栏：单行显示，居右布局 */
  .design-toolbar {
    text-align: right !important;
    white-space: nowrap !important;
    display: block !important;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none; /* Firefox 隐藏滚动条 */
    -ms-overflow-style: none;
    padding-right: 16px !important;
    padding-left: 0 !important;
  }
  .design-toolbar::-webkit-scrollbar {
    display: none; /* Safari/Chrome 隐藏滚动条 */
  }

  /* 工具栏按钮组：去掉右边框分隔线，紧凑排列，强制不换行 */
  .toolbar-group {
    display: inline-flex;
    flex-wrap: nowrap;
    gap: 0;
    padding-right: 0;
    margin-right: 0;
    border-right: none;
    vertical-align: middle;
  }

  .toolbar-group .el-button {
    padding: 4px 4px !important;
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
