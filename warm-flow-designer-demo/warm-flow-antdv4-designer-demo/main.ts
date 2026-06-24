import { createApp } from 'vue'
import { createPinia } from 'pinia'

import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'

// 过渡态：设计器内部当前仍由 Element Plus 驱动；Phase 3 接入 antdv 适配器后，
// 改为 setUiAdapter(antdvAdapter) 并移除以下 element-plus 引入。
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import { WarmFlowDesigner, setDataProvider } from '@dromara/warm-flow-designer'
import '@dromara/warm-flow-designer/style'

import App from './App.vue'
import { createDemoProvider } from './demoProvider'

setDataProvider(createDemoProvider())

createApp(App)
  .use(createPinia())
  // 设计器内部 UI（过渡期）：Element Plus 默认适配器
  .use(ElementPlus)
  // 本 demo 应用外壳：Ant Design Vue 4
  .use(Antd)
  // 注册设计器（默认 Element Plus UI 适配器；Phase 3 将提供 antdv 适配器并 setUiAdapter 切换）
  .use(WarmFlowDesigner)
  .mount('#app')
