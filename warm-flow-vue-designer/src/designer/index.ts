import type { App, Plugin } from 'vue'
import '@/icons'

import FlowDesigner from '@/components/design/FlowDesigner.vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import {
  setupDataProvider,
  setDataProvider,
  getDataProvider,
  createHttpProvider,
  createMockProvider,
  isMockEnabled
} from '@/data'
import type { DataProvider } from '@/data'
import { setUiAdapter, getUiAdapter, hasUiAdapter } from '@/ui/uiAdapter'
import type { UiAdapter, UiFeedbackType, UiFeedbackOptions, UiLoadingHandle, UiComponents } from '@/ui/uiAdapter'
import { registerWfComponents } from '@/ui/components'
import { setComponentSize, getComponentSize } from '@/ui/designerOptions'
import type { ComponentSize } from '@/ui/designerOptions'

/** WarmFlowDesigner 插件安装选项。 */
export interface WarmFlowDesignerOptions {
  /** 全局组件尺寸（small / default / large）；等价于安装前调用 setComponentSize(size)。 */
  size?: ComponentSize
}

/**
 * Warm-Flow 设计器「可复用层」统一出口（组件库 / npm 包入口）。
 *
 * 主入口 UI 库无关：不静态引入任何 UI 组件库（element-plus / ant-design-vue），
 * 消费方须显式注册一个 UI 适配器后再渲染设计器：
 *   import { WarmFlowDesigner, FlowDesigner, setUiAdapter, setDataProvider, createMockProvider } from '@dromara/warm-flow-designer'
 *   import { elementPlusAdapter } from '@dromara/warm-flow-designer/element-plus'  // 或 import { antdvAdapter } from '@dromara/warm-flow-designer/antdv'
 *   import '@dromara/warm-flow-designer/style'
 *   setUiAdapter(elementPlusAdapter)  // 选择 UI 库适配器，须在渲染 FlowDesigner 前调用
 *   app.use(WarmFlowDesigner)         // 注册 svg-icon + 中性组件 wf-*，图标零配置可用
 *   setDataProvider(myProvider)       // 注入自定义后端 / mock，实现数据层与具体后端解耦
 *
 * 该入口对外只暴露这一稳定门面，内部目录结构调整不影响下游 import 路径。
 *
 * @author warm
 */

/**
 * 安装为 Vue 插件：注册全局 svg-icon 组件与中性组件 wf-*。
 *
 * 主入口 UI 库无关，不在此注册任何 UI 适配器；消费方须在渲染前显式 setUiAdapter(...)
 * 选择 element-plus（@dromara/warm-flow-designer/element-plus）或 antdv（/antdv）适配器。
 * 图标走离线 iconify 集（ep + wf，见 src/icons），已在本模块加载时注册，零配置渲染，不依赖具体 UI 库图标。
 */
const install = (app: App, options: WarmFlowDesignerOptions = {}): void => {
  // 全局组件尺寸：安装时可一次性指定（small / default / large），运行期亦可 setComponentSize 调整
  if (options.size) setComponentSize(options.size)
  app.component('svg-icon', SvgIcon)
  // 全局注册中性组件 wf-*（设计器视图与 UI 库解耦，渲染时按已注册的适配器映射到具体 UI 库组件）
  registerWfComponents(app)
  // 注册 v-loading 指令（区域加载遮罩）：由当前 UI 适配器提供（EP 用内置 vLoading、antd 自实现），
  // 使设计器内组件（如 selectUser）的 v-loading 与具体 UI 库解耦。须在 app.use 前先 setUiAdapter。
  // 仅在宿主 app 尚未注册同名指令时才注册：避免与消费方 app.use(ElementPlus)（已自带 loading）重复注册告警。
  if (hasUiAdapter() && !app.directive('loading')) {
    const loadingDirective = getUiAdapter().loadingDirective
    if (loadingDirective) app.directive('loading', loadingDirective)
  }
}

const WarmFlowDesigner: Plugin = { install }

export {
  // 可复用画布组件（props 驱动，详见组件内 defineProps）
  FlowDesigner,
  // 全局图标组件（通常由 WarmFlowDesigner 插件自动注册，单独导出便于按需使用）
  SvgIcon,
  // Vue 插件：app.use 后自动注册图标
  WarmFlowDesigner,
  install,
  // 数据层：注入 / 获取数据源、内置 http 与 mock 实现、mock 开关、按环境装配
  setupDataProvider,
  setDataProvider,
  getDataProvider,
  createHttpProvider,
  createMockProvider,
  isMockEnabled,
  // UI 适配层：注册 / 获取 UI 库适配器。主入口 UI 无关，消费方须显式注册一个适配器：
  //   element-plus 适配器 import 自 @dromara/warm-flow-designer/element-plus
  //   ant-design-vue 适配器 import 自 @dromara/warm-flow-designer/antdv
  setUiAdapter,
  getUiAdapter,
  // 全局 UI 选项：设置 / 获取设计器组件尺寸（small / default / large）
  setComponentSize,
  getComponentSize
}
export type { DataProvider }
export type { UiAdapter, UiFeedbackType, UiFeedbackOptions, UiLoadingHandle, UiComponents }
export type { ComponentSize }

export default WarmFlowDesigner
