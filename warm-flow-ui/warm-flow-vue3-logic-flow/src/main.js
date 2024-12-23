import { createApp } from 'vue'

import FcDesigner from '@form-create/designer';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import locale from 'element-plus/es/locale/lang/zh-cn'

import '@/assets/styles/index.scss' // global css

import App from './App'
import store from './store'

// 注册指令
import plugins from './plugins' // plugins

// svg图标
import 'virtual:svg-icons-register'
import SvgIcon from '@/components/SvgIcon'
import elementIcons from '@/components/SvgIcon/svgicon'

import { parseTime } from '@/utils/ruoyi'

const app = createApp(App)


// 全局方法挂载
app.config.globalProperties.parseTime = parseTime
app.use(store)
app.use(plugins)
app.use(elementIcons)
app.component('svg-icon', SvgIcon)

// 使用element-plus 并且设置全局的大小
app.use(ElementPlus, {
  locale: locale,
})

app.use(FcDesigner);
app.use(FcDesigner.formCreate);

app.mount('#app')
