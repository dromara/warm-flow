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
import { elementPlusAdapter } from '@/ui/elementPlusAdapter'
import type { UiAdapter } from '@/ui/uiAdapter'
import { registerWfComponents } from '@/ui/components'

/**
 * Warm-Flow 设计器「可复用层」统一出口（组件库 / npm 包入口）。
 *
 * 业务方可不经 iframe，直接以组件方式集成流程设计器：
 *   import { WarmFlowDesigner, FlowDesigner, setDataProvider, createMockProvider } from '@dromara/warm-flow-designer'
 *   import '@dromara/warm-flow-designer/style'
 *   app.use(WarmFlowDesigner)        // 注册 svg-icon + Element Plus 图标，图标零配置可用
 *   setDataProvider(myProvider)      // 注入自定义后端 / mock，实现数据层与具体后端解耦
 *
 * 该入口对外只暴露这一稳定门面，内部目录结构调整不影响下游 import 路径。
 *
 * @author warm
 */

/**
 * 安装为 Vue 插件：注册全局 svg-icon 组件。
 *
 * 图标走离线 iconify 集（ep + wf，见 src/icons），已在本模块加载时注册，
 * 因此 app.use(WarmFlowDesigner) 后设计器内置图标即可零配置渲染，且不依赖 element-plus 图标。
 */
const install = (app: App): void => {
  app.component('svg-icon', SvgIcon)
  // 默认注册 Element Plus UI 适配器；消费方若在此之前 setUiAdapter(antdvAdapter) 则不覆盖，实现 UI 库可切换
  if (!hasUiAdapter()) {
    setUiAdapter(elementPlusAdapter)
  }
  // 全局注册中性组件 wf-*（设计器视图与 UI 库解耦）
  registerWfComponents(app)
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
  // UI 适配层：切换 / 获取 UI 库适配器（默认 Element Plus，可换 antdv 等），默认实现 elementPlusAdapter
  setUiAdapter,
  getUiAdapter,
  elementPlusAdapter
}
export type { DataProvider }
export type { UiAdapter }

export default WarmFlowDesigner
