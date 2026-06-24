import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 像第三方一样，仅通过包名消费构建产物（alias → dist-lib）
import { WarmFlowDesigner, setUiAdapter, setDataProvider } from '@dromara/warm-flow-designer'
// 主入口 UI 库无关：显式选择 Element Plus 适配器（antd demo 则改 import @dromara/warm-flow-designer/antdv）
import { elementPlusAdapter } from '@dromara/warm-flow-designer/element-plus'
import '@dromara/warm-flow-designer/style'

import App from './App.vue'
// 注入「带 localStorage 持久化的 demo 数据源」（包装库内置 mock），支撑保存/修改/预览闭环
import { createDemoProvider } from './demoProvider'

// 注册 UI 适配器：本 demo 用 Element Plus（须在渲染 FlowDesigner 前调用）
setUiAdapter(elementPlusAdapter)
// 演示业务方如何用 setDataProvider 注入自定义后端：这里用带持久化的 demo 数据源脱后端运行
setDataProvider(createDemoProvider())

createApp(App)
  .use(createPinia())
  .use(ElementPlus)
  // 注册全局 svg-icon 组件 + Element Plus 图标（图标零配置）
  .use(WarmFlowDesigner)
  .mount('#app')
