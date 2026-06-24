import { createApp } from 'vue'
import { createPinia } from 'pinia'

import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'

// 过渡态：设计器内部当前仍由 Element Plus 驱动；Phase 3 接入 antdv 适配器后，
// 改为 setUiAdapter(antdvAdapter) 并移除以下 element-plus 引入。
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import { WarmFlowDesigner, setDataProvider, setUiAdapter } from '@dromara/warm-flow-designer'
import '@dromara/warm-flow-designer/style'

import App from './App.vue'
import { createDemoProvider } from './demoProvider'
import { antdvAdapter } from './antdvAdapter'

setDataProvider(createDemoProvider())
// P3：切换 UI 适配器为 antdv（须在 app.use(WarmFlowDesigner) 之前，install 检测到已设则不覆盖为 EP）。
// 过渡期 antdvAdapter 内部分组件仍回退 EP，故仍需 app.use(ElementPlus) 提供回退组件。
setUiAdapter(antdvAdapter)

createApp(App)
  .use(createPinia())
  // 过渡期：尚未翻译的设计器组件回退 Element Plus（随 antdv 翻译推进逐步移除）
  .use(ElementPlus)
  // 本 demo 应用外壳 + 已翻译组件：Ant Design Vue 4
  .use(Antd)
  // 注册设计器（svg-icon + 默认适配器；因上面已 setUiAdapter(antdvAdapter)，install 不会覆盖）
  .use(WarmFlowDesigner)
  .mount('#app')
