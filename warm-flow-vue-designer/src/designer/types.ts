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

/** FlowDesigner 组件 props（props 驱动，宿主耦合由外层页面壳负责）。 */
export interface FlowDesignerProps {
  /** 流程定义 id（新建态传 null，走本地 initData 渲染） */
  definitionId?: string | null
  /** 是否只读（已发布定义或宿主显式禁用） */
  disabled?: boolean
  /** 仅显示流程设计画布：隐藏顶部步骤栏、跳过基础信息校验直达画布 */
  onlyDesignShow?: boolean
  /** 是否显示画布网格点 */
  showGrid?: boolean
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
}
