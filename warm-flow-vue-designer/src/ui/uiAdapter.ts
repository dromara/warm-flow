import type { Component, Directive } from 'vue'

/**
 * 反馈级别：消息 / 通知 / 弹框的语义类型。
 */
export type UiFeedbackType = 'info' | 'success' | 'warning' | 'error'

/**
 * loading 句柄：由 adapter.loading() 返回，调用方负责在合适时机 close()。
 */
export interface UiLoadingHandle {
  close(): void
}

/**
 * 命令式反馈选项（消息 / 通知 / 弹框 / loading 通用），不同 UI 库各取所需字段。
 */
export interface UiFeedbackOptions {
  title?: string
  message?: string
  duration?: number
  text?: string
  [key: string]: unknown
}

/**
 * 中性组件注册表：把设计器用到的 UI 组件按「语义名」映射到具体 UI 库的实现，
 * 供中性组件 Wf*（如 WfButton / WfInput）通过 `getUiAdapter().components?.xxx` 解析渲染目标。
 * 随组件中性化逐步补全；未注册的语义名对应组件不渲染（调用方需容错）。
 */
export interface UiComponents {
  button?: Component
  input?: Component
  [name: string]: Component | undefined
}

/**
 * Warm-Flow 设计器「UI 适配器」契约。
 *
 * 把设计器对具体 UI 库（Element Plus / Ant Design Vue …）的命令式 API（消息 / 通知 / 弹框 / loading）
 * 与指令（clickOutside / loading）依赖收敛到这一层，实现「核心逻辑与 UI 库解耦、可按需切换 UI 库」。
 *
 * 默认实现为 Element Plus（见 elementPlusAdapter）；消费方可 setUiAdapter(antdvAdapter) 切换为其它实现。
 *
 * @author warm
 */
export interface UiAdapter {
  /** 适配器名称，便于调试 / 识别当前 UI 库 */
  name: string
  /** 轻量消息提示（toast） */
  message(type: UiFeedbackType, content: string | UiFeedbackOptions): void
  /** 通知（notification） */
  notify(type: UiFeedbackType, content: string | UiFeedbackOptions): void
  /** 信息弹框（alert），返回 Promise 便于等待关闭 */
  alert(content: string, options?: UiFeedbackOptions): Promise<unknown>
  /** 确认弹框（confirm），确认 resolve、取消 reject */
  confirm(content: string, options?: UiFeedbackOptions): Promise<unknown>
  /** 输入弹框（prompt），确认 resolve({ value }) */
  prompt(content: string, options?: UiFeedbackOptions): Promise<{ value: string }>
  /** 全屏 / 区域 loading，返回句柄，调用方 close() */
  loading(options?: UiFeedbackOptions): UiLoadingHandle
  /** 「点击外部」指令，供自定义节点视图等注册（v-click-outside） */
  clickOutside?: Directive
  /** 「加载中」遮罩指令（v-loading）：EP 用内置 vLoading，antd 自实现区域遮罩，供设计器内组件解耦使用 */
  loadingDirective?: Directive
  /** 中性组件注册表：语义名 -> 具体 UI 库组件，供 Wf* 中性组件解析渲染目标 */
  components?: UiComponents
}

let currentAdapter: UiAdapter | null = null

/**
 * 注册当前 UI 适配器。应用 / 消费方在挂载设计器前调用；
 * 想用 Ant Design Vue 时传入对应 adapter 即可替换默认的 Element Plus。
 */
export function setUiAdapter(adapter: UiAdapter): void {
  currentAdapter = adapter
}

/**
 * 获取当前 UI 适配器。未注册时抛错，提示调用方先注册（app.use(WarmFlowDesigner) 会默认注册 Element Plus）。
 */
export function getUiAdapter(): UiAdapter {
  if (!currentAdapter) {
    throw new Error(
      '[warm-flow] UI 适配器未注册：请先 setUiAdapter(elementPlusAdapter) 或自定义适配器，' +
        '或 app.use(WarmFlowDesigner)（会默认注册 Element Plus 适配器）'
    )
  }
  return currentAdapter
}

/** 是否已注册 UI 适配器 */
export function hasUiAdapter(): boolean {
  return currentAdapter !== null
}
