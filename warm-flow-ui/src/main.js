/**
 * 浏览器兼容兜底：拦截无效属性名的 setAttribute 调用，防止 DOMException 白屏（gitee #IJV7GC / PR #393）。
 * 当第三方组件把数组展开为 DOM 属性时（如 v-bind 收到数组），Vue 会尝试 setAttribute('0', value)，
 * 属性名以数字开头不符合 XML/HTML 规范，钉钉内嵌等旧 Chromium 内核浏览器会直接抛异常导致设计器崩溃。
 * 这里跳过该类调用并告警，作为最后一道防线（根因侧修复见 baseInfo.vue 的 emits 声明与数组防护）。
 */
(function patchSetAttribute() {
  const nativeSetAttr = Element.prototype.setAttribute;
  Element.prototype.setAttribute = function (name, value) {
    if (typeof name === 'string' && /^\d/.test(name)) {
      console.warn(
        '[Warm-Flow compat] Blocked invalid setAttribute("' + name + '", ...) on <'
        + (this.tagName || 'unknown').toLowerCase()
        + '>. Likely an array spread into DOM attrs by a third-party component.'
      );
      return;
    }
    return nativeSetAttr.call(this, name, value);
  };
})();

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
