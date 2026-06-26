import type LogicFlow from '@logicflow/core'

/**
 * FlowDesigner 公共类型集中出口。
 *
 * 单独成文件的目的：组件实现（FlowDesigner.vue）、组合式 API（useFlowDesigner）、
 * 组件库入口（designer/index.ts）共享同一份类型定义，消费方可直接 import 这些类型
 * 用于模板 ref 标注、事件回调入参标注等，类型即文档。
 *
 * 放在 src/designer/ 下：与公共入口同处，且被库构建的 dts include 收录，类型可随包发布。
 *
 * @author warm
 */

/**
 * 经典模式左侧拖拽面板（DiagramSidebar）的单个可拖拽节点项。
 * 拖入画布即以 `type` 对应的已注册 LogicFlow 节点创建实例。
 */
export interface PaletteNode {
  /** 节点类型：对应已注册的 LogicFlow 节点 type（内置 start/between/end/serial/parallel/inclusive，或自定义节点 type） */
  type: string
  /** 面板显示文字 */
  label: string
  /** 拖入画布后节点的初始文本（不传则为空） */
  text?: string
  /** 图标（SVG 字符串，经 v-html 渲染；不传则只显示文字，可复用内置 sidebarIcons 或自定义 SVG） */
  icon?: string
  /** 拖入画布后节点的初始 properties（如中间节点的 collaborativeWay） */
  properties?: Record<string, any>
}

/**
 * 自定义经典模式左侧拖拽面板节点（覆盖内置默认）。
 * 仅经典模式生效；仿钉钉模式无拖拽面板。
 * 任一分组不传 = 用内置默认；传空数组 `[]` 可隐藏该分组。
 * 需要完全替换面板（自定义结构 / 交互）时改用 `#sidebar` 插槽。
 */
export interface FlowDesignerPaletteNodes {
  /** 基础节点组（默认：开始 / 中间节点 / 结束） */
  flowNodes?: PaletteNode[]
  /** 网关节点组（默认：互斥 / 并行 / 包含网关）；传 `[]` 隐藏网关组 */
  gatewayNodes?: PaletteNode[]
}

/** FlowDesigner 组件 props（props 驱动，宿主耦合由外层页面壳负责）。 */
export interface FlowDesignerProps {
  /** 流程定义 id（新建态传 null，走本地 initData 渲染） */
  definitionId?: string | null
  /**
   * 初始流程 JSON（脱后端驱动）：warm-flow 定义对象或其 JSON 字符串。
   * 形状与 DataProvider.queryDef 返回的 data、命令式 getFlowJson() / saved 事件输出一致（可直接 round-trip）。
   * 提供该值时优先于 queryDef：组件不再请求后端，直接用此 JSON 渲染/编辑，实现纯组件用法。
   */
  initialJson?: string | Record<string, any> | null
  /** 是否只读（已发布定义或宿主显式禁用） */
  disabled?: boolean
  /** 仅显示流程设计画布：隐藏顶部步骤栏、跳过基础信息校验直达画布 */
  onlyDesignShow?: boolean
  /** 是否显示画布网格点 */
  showGrid?: boolean
  /**
   * 自定义节点注册：在内置节点（经典 / 仿钉钉）之后追加 lf.register(...)。
   * 元素为 LogicFlow 自定义节点定义（{ type, view, model }），可新增节点类型或覆盖内置同名 type。
   */
  customNodes?: any[]
  /**
   * 自定义 LogicFlow 扩展（插件）：在内置扩展（Menu / Snapshot 等）之后追加 LogicFlow.use(...)。
   * 如 MiniMap / Control / Group 等官方或自研扩展。
   */
  extraExtensions?: any[]
  /**
   * 透传并合并到 LogicFlow 初始化选项（顶层覆盖内置默认值，如 grid / keyboard / 交互开关）。
   * 注意：container 由组件内部管理，请勿在此传入。
   */
  lfOptions?: Record<string, any>
  /**
   * LogicFlow 扩展注册钩子（命令式）：在内置扩展 + `extraExtensions` 之后、`new LogicFlow()` 之前调用，
   * 透出 LogicFlow 类（静态）。相比 `extraExtensions`（仅 `LogicFlow.use(ext)`），此钩子能力更强：
   * - 注册**带配置**的扩展：`LF.use(MiniMap, { ...options })`（数组形式无法传配置）；
   * - 按条件注册 / 覆盖内置扩展。
   * 与 `extraExtensions` 可同时使用（先数组、后钩子）。
   */
  onBeforeUse?: (LF: typeof import('@logicflow/core').default) => void
  /**
   * 自定义节点注册钩子（命令式）：在内置节点 + `customNodes` 之后调用，透出已创建的 LogicFlow 实例。
   * 相比 `customNodes`（仅 `lf.register(node)`），此钩子能访问 lf 实例做批量 / 条件注册、注册自定义边，
   * 或在首次渲染前对实例做额外设置（如 `lf.setTheme` / `lf.on`）。
   * 与 `customNodes` 可同时使用（先数组、后钩子）。
   */
  onRegister?: (lf: LogicFlow) => void
  /**
   * 自定义经典模式左侧拖拽面板（DiagramSidebar）的节点列表（覆盖内置默认）。
   * 仅经典模式生效。任一分组不传用内置默认，传空数组隐藏该分组。
   * 需要完全替换面板时改用 `#sidebar` 插槽（透出 dragInNode / lf / disabled）。
   */
  paletteNodes?: FlowDesignerPaletteNodes
}

/** `saved` 事件回传：当前定义 id、后端返回数据（如新建后的 definitionId）与本次保存的流程 json。 */
export interface FlowDesignerSavedPayload {
  /** 当前流程定义 id（新建首存前为 null） */
  id: string | null
  /** 后端保存接口返回的 data（如新建后生成的 definitionId） */
  data: any
  /** 本次保存提交的 warm-flow 流程 json 字符串 */
  json: string
}

/** `ready` 事件回传：画布初始化完成后透出底层 LogicFlow 实例，便于消费方做高级定制。 */
export interface FlowDesignerReadyPayload {
  /** 已初始化的底层 LogicFlow 实例 */
  lf: LogicFlow
}

/**
 * `before-save` 事件回传：保存提交「前」的可改写上下文。
 *
 * 用途：在调用后端保存接口之前，消费方可改写最终提交的 json，或取消本次保存。
 * 注意：该事件**同步派发**，处理函数内的异步逻辑不会被 `await`——`setJson` / `preventDefault`
 * 必须在处理函数同步执行期间调用才生效。
 */
export interface FlowDesignerBeforeSavePayload {
  /** 当前流程定义 id（新建首存前为 null） */
  id: string | null
  /** 即将提交的 warm-flow 流程 json 字符串（可经 setJson 改写） */
  json: string
  /** 是否仅设计画布模式（onlyDesignShow） */
  onlyDesignShow: boolean
  /** 改写本次提交的 json：传入新的 json 字符串覆盖默认提交内容 */
  setJson: (json: string) => void
  /** 取消本次保存：终止保存流程，不调用后端、不触发 saved / close */
  preventDefault: () => void
}

/**
 * `change` 事件回传：画布图数据发生变更（基于 LogicFlow `history:change`，初次渲染不触发）。
 *
 * 提供惰性 getter，按需获取当前 json / 图数据，避免每次变更都序列化造成开销。
 * 「变更」仅指画布图数据（增删改节点 / 边、移动、撤销 / 重做），不含基础信息表单字段。
 */
export interface FlowDesignerChangePayload {
  /** 当前是否有未保存的画布改动 */
  dirty: boolean
  /** 惰性获取当前可保存的 warm-flow 流程 json 字符串（按需调用） */
  getJson: () => string
  /** 惰性获取当前画布原始图数据（LogicFlow getGraphData：nodes / edges） */
  getGraphData: () => any
}

/**
 * FlowDesigner 通过 `defineExpose` 暴露的命令式 API。
 *
 * 两种获取方式：
 * 1) 模板 ref：`<FlowDesigner ref="designerRef" />`，`designerRef.value` 即本类型；
 * 2) 组合式：`const { designerRef, save } = useFlowDesigner()`（推荐，带空安全包装）。
 */
export interface FlowDesignerInstance {
  /** 触发保存（含基础信息校验 + 收集图数据 + 调用保存接口，等价点击「保存」按钮） */
  save: () => Promise<void>
  /** 校验基础信息表单；onlyDesignShow 模式恒为 true */
  validate: () => Promise<boolean>
  /** 获取当前画布图数据（LogicFlow getGraphData 原始结构：nodes / edges） */
  getGraphData: () => any
  /** 获取当前可保存的 warm-flow 流程 json 字符串（含基础信息 + 图数据） */
  getFlowJson: () => string
  /** 获取当前流程名称 */
  getFlowName: () => string
  /** 获取底层 LogicFlow 实例（高级定制：自定义事件 / 主题 / 扩展），未初始化时为 null */
  getLogicFlow: () => LogicFlow | null
  /** 缩放视口：true=放大 / false=缩小 / 'fit'=自适应全部节点 / number=指定比例 */
  zoom: (zoom: boolean | number | 'fit') => void
  /** 放大一档 */
  zoomIn: () => void
  /** 缩小一档 */
  zoomOut: () => void
  /** 自适应显示全部节点（fitView） */
  fitView: () => void
  /** 重置缩放并居中（PC 端默认行为） */
  resetZoom: () => void
  /** 撤销 */
  undo: () => void
  /** 重做 */
  redo: () => void
  /** 清空画布 */
  clear: () => void
  /** 下载流程图（PNG 快照） */
  downloadImage: () => void
  /** 下载流程 json 文件 */
  downloadJson: () => void
  /** 当前是否有未保存的画布改动（基于画布图数据变更，不含基础信息表单字段） */
  isDirty: () => boolean
  /** 将未保存标记复位为「干净 / 已保存」（如宿主自行保存后同步状态） */
  resetDirty: () => void
}
