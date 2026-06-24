import { createApp } from 'vue'
import { createPinia } from 'pinia'

import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'

// 像第三方一样，仅通过包名消费构建产物（alias → dist-lib）
import { WarmFlowDesigner, setDataProvider, setUiAdapter } from '@dromara/warm-flow-designer'
// 主入口 UI 库无关：显式选择 antd 适配器（库子入口，与 element-plus 子入口对称）；本 demo 完全不引 element-plus
import { antdvAdapter } from '@dromara/warm-flow-designer/antdv'
import '@dromara/warm-flow-designer/style'

import App from './App.vue'
import { createDemoProvider } from './demoProvider'

// 注册 UI 适配器为 antd（须在渲染 FlowDesigner 前调用）；28 个组件已全翻译，无需 EP 回退
setUiAdapter(antdvAdapter)
setDataProvider(createDemoProvider())

createApp(App)
  .use(createPinia())
  // 应用外壳 + 设计器全部组件：Ant Design Vue 4
  .use(Antd)
  // 注册设计器：svg-icon + 中性组件 wf-*（按上面注册的 antd 适配器渲染）
  .use(WarmFlowDesigner)
  .mount('#app')
