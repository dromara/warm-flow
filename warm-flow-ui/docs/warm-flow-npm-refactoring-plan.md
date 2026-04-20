# Warm-Flow UI NPM 包改造完整实施方案

> **双模架构(Vue3 NPM + SPA) + SPA 渐进淘汰路线图**
> 文档版本: v1.2 | 2026-04-20 | 修复 P1-P21 共 21 个问题 (两轮审查)

---

## 目录

1. [背景与目标](#第一章-背景与目标)
2. [架构设计](#第二章-架构设计)
3. [12 大耦合点改造详解](#第三章-12-大耦合点改造详解)
4. [文件变更清单](#第四章-完整文件变更清单)
5. [分步实施计划](#第五章-8阶段实施计划)
6. [SPA 淘汰路线图](#第六章-spa-渐进淘汰路线图)
7. [验证清单](#第七章-验证清单)

---

## 第一章 背景与目标

### 1.1 当前架构

warm-flow-ui 当前通过 **SPA + iframe 嵌入** 方式集成：

**前端使用方式 (hh-vue/warm_chart.vue):**
```vue
<iframe :src="`${VUE_APP_FLOW_API}/warm-flow-ui/index.html?id=${insId}&type=FlowChart&Authorization=Bearer ${getToken()}`" />
```

**后端静态资源映射 (WarmFlowUiConfig.java):**
```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/warm-flow-ui/**")
        .addResourceLocations("classpath:/META-INF/resources/warm-flow-ui/", "classpath:/warm-flow-ui/");
}
```

**构建流程:**
```
warm-flow-ui (Vue3项目) → npm run build:prod → dist/ 
    → 手动复制到 warm-flow-plugin-vue3-ui/src/main/resources/warm-flow-ui/
    → Maven 打包进 JAR 的 META-INF/resources/
    → Spring Boot 启动后 ResourceHandler 映射 /warm-flow-ui/**
```

### 1.2 改造目标矩阵

> **范围说明**: 本文档覆盖 **Vue 3 NPM 组件 + SPA 双模** 改造。Web Component 全框架兼容 (Vue2/React/Angular/原生JS) 作为 **Phase 2 后续迭代**, 详见第六章路线图。

| 目标维度 | 描述 | 本文档覆盖 |
|---------|------|----------|
| **NPM Vue 3 组件模式** | `npm install @warm-flow/vue-designer` → 直接 `<WarmFlowDesigner>` 使用 | ✅ 核心 |
| **SPA 向后兼容** | 已有 iframe 方式保留不动，零破坏性变更 | ✅ 核心 |
| **配置注入体系** | API baseURL、Token 函数、请求拦截器等全部外部注入 | ✅ 核心 |
| **渐进淘汰 SPA** | 长期可移除 SPA 模式（删除成本 < 15 分钟） | ✅ 覆盖 |
| **Web Component 模式** | Vue2/React/Angular/原生JS 均可通过 `<warm-flow-designer>` 使用 | ⏭ Phase 2 (后续) |

---

## 第二章 架构设计

### 2.1 双模架构总览

```
┌──────────────────────────────────────────────────────────┐
│                    同一套源码 (src/)                       │
│                                                           │
│  ┌─────────────────┐   ┌────────────────────────────┐    │
│  │   SPA 入口        │   │   Library 入口              │    │
│  │   main.js:41     │   │   src/index.js [NEW]       │    │
│  │   app.mount()    │   │   export components         │    │
│  └────────┬─────────┘   └────────────┬───────────────┘    │
│           │                          │                     │
│           └──────────┬───────────────┘                     │
│                      ▼                                     │
│  ┌───────────────────────────────────────────────┐       │
│  │           核心层 (src/core/) [NEW]             │       │
│  │  ├── config.js     配置工厂(单例/注入)          │       │
│  │  ├── request.js    axios实例工厂                │       │
│  │  ├── auth.js       TokenProvider接口+默认实现    │       │
│  │  └── useAppParams.js  参数获取(URL/Props双源)   │       │
│  └─────────────────────┬─────────────────────────┘       │
│                        ▼                                   │
│  ┌───────────────────────────────────────────────┐       │
│  │      组件层 (views/ + components/)             │       │
│  │  4个视图组件 + 4个包装组件 + 业务子组件          │       │
│  │  所有耦合点 C1-C12 已解耦                      │       │
│  └───────────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────┘
                    │                    │
         SPA build:prod│            build:lib│
                    ▼                    ▼
          ┌──────────────┐      ┌──────────────────┐
          │ dist/ 静态文件 │      │ dist-lib/        │
          │ (iframe 用)   │      │ ES + UMD         │
          └──────────────┘      │ npm publish       │
                                 └──────────────────┘
```

### 2.2 Web Component 全框架分发设计

> ⏭ **本节为 Phase 2 (后续迭代) 的设计预览, 不在当前实施范围内。** 当前 Phase 1 仅实现 Vue 3 NPM 组件 + SPA 双模。WC 实施详见第六章路线图。

**@warm-flow/designer NPM 包结构:**

```
@warm-flow/designer/
├── package.json
├── dist/
│   ├── index.js              # 自动检测环境 + 注册 WC
│   ├── vue3.js               # Vue 3 组件导出
│   ├── web-components.js     # 纯 WC (bundled Vue runtime)
│   └── style.css             # 独立样式(Shadow DOM用)
```

**各框架使用方式对比:**

| 框架 | 引入方式 | 示例 |
|------|---------|------|
| **Vue 3** | `import { WarmFlowDesigner } from '@warm-flow/vue-designer'` | `<WarmFlowDesigner :id="defId" @save="onSave" />` |
| **Vue 2** | `import '@warm-flow/designer'` | `<warm-flow-designer id="defId" />` |
| **React** | `import '@warm-flow/designer'` | `<warm-flow-designer ref={ref} />` + ref.addEventListener |
| **Angular** | `import '@warm-flow/designer'` | `<warm-flow-designer [id]="defId" (save)="onSave($event)" />` |
| **原生 JS** | `import '@warm-flow/designer'` | `document.createElement('warm-flow-designer')` |

### 2.3 核心 API 设计

#### WarmFlowConfig 接口

```javascript
// src/core/config.js - 所有外部依赖通过此处注入
const defaultConfig = {
  // --- API 相关 ---
  baseURL: '',                  // axios baseURL, 默认空字符串
  urlPrefix: '/',               // API 路径前缀, 默认 '/' (NPM) 或 '../' (SPA)
  timeout: 10000,               // 请求超时 ms

  // --- 认证相关 ---
  getToken: null,               // 外部 Token 获取函数: () => string|null
  tokenName: 'Authorization',   // Token header 名称
  tokenNameList: [],            // 多 Token 场景的名称列表

  // --- 行为相关 ---
  onClose: null,                // 关闭回调 (NPM 替代 postMessage close)
  darkMode: 'auto',             // 'auto' | true | false

  // --- 扩展 ---
  customNodeTypes: {},          // 自定义节点类型注册
  requestInterceptor: null,     // 自定义请求拦截器: (config)=>config
  elementPlusInstalled: false,  // 宿主是否已安装 Element Plus (避免重复注册)
}
```

#### 组件 Props & Events API

| 属性 | 类型 | 默认值 | 说明 |
|------|------|-------|------|
| `id` | string\|number | 必填 | 流程定义 ID 或实例 ID |
| `type` | string | `'design'` | 视图类型: design/chart/form/formCreate |
| `apiBase` | string | `'/'` | API 基础路径 |
| `urlPrefix` | string | `'/'` | API URL 前缀 |
| `getToken` | Function | null | Token 获取函数 |
| `darkMode` | boolean\|string | `'auto'` | 暗黑模式 |
| `disabled` | boolean | false | 是否禁用编辑 |
| `onlyDesignShow` | boolean | false | 仅显示画布(隐藏基础信息) |
| `showGrid` | boolean\|string | 'true' | 是否显示网格 |
| `@save` | event(data) | - | 保存成功回调 |
| `@close` | event() | - | 关闭回调 |
| `@error` | event(err) | - | 错误回调 |
| `#toolbar` | slot | - | 自定义工具栏区域 |
| `#header` | slot | - | 自定义头部区域 |

---

## 第三章 12 大耦合点改造详解

### 3.0 耦合点总览表

| # | 耦合点位置 | 当前代码 | 改造策略 |
|---|-----------|---------|---------|
| **C1** | main.js:41 | `app.mount('#app')` | 新增 index.js export, main.js 不变 |
| **C2** | store/app.js:12 | `new URLSearchParams(window.location.search)` | 支持 externalParams 参数注入 |
| **C3** | utils/auth.js:9 | `localStorage.getItem(Prefix + key)` | TokenProvider 接口 + 外部函数注入 |
| **C4** | utils/request.js:11 | `baseURL: import.meta.env.VITE_APP_BASE_API` | createRequest(config) 工厂函数 |
| **C5** | api/*.js:3 | `import.meta.env.VITE_URL_PREFIX` | 读 config.urlPrefix |
| **C6** | index.vue:965 | `window.parent.postMessage({method:"close"})` | emit('close') \|\| postMessage 双模式 |
| **C7** | useDark.js:28-39 | window.addEventListener('message') 监听主题 | watch(prop) + postMessage fallback |
| **C8** | index.vue:244, flowChart.vue:298 | `document.body.style.overflow='hidden'` | 条件执行(NPM 不操作 body) |
| **C9** | store/index.js:1 | `createPinia()` 单例 | provide/inject 多实例支持 |
| **C10** | plugins/index.js:6-8 | `app.config.globalProperties.$cache/$modal` | composable 导出 + 全局注册并存 |
| **C11** | main.js:17 | `import 'virtual:svg-icons-register'` | Library 模式内联处理 |
| **C12** | vite/plugins/index.js | compression/svg-icons/auto-import | isLib 时移除 |

---

### 3.1 C1: 入口分离 (main.js vs index.js)

**现状 (main.js 完整内容 — 保持不变):**
```javascript
// src/main.js (41行, SPA入口, 不修改!)
import { createApp } from 'vue'
import FcDesigner from '@form-create/designer';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import locale from 'element-plus/es/locale/lang/zh-cn'
import '@/assets/styles/index.scss'
import App from './App'
import store from './store'
import plugins from './plugins'
import 'virtual:svg-icons-register'
import SvgIcon from '@/components/SvgIcon'
import elementIcons from '@/components/SvgIcon/svgicon'
import { parseTime } from '@/utils/ruoyi'

const app = createApp(App)
app.config.globalProperties.parseTime = parseTime
app.use(store).use(plugins).use(elementIcons)
app.component('svg-icon', SvgIcon)
app.use(ElementPlus, { locale })
app.use(FcDesigner);
app.use(FcDesigner.formCreate);
app.mount('#app')
```

**新增 (src/index.js — Library 入口):**
```javascript
// ====== 新增文件: src/index.js ======
// Library 模式导出入口 — 不调用 app.mount()

export { WarmFlowDesigner } from './components/WarmFlowDesigner.vue'
export { WarmFlowChart } from './components/WarmFlowChart.vue'
export { WarmFormDesigner } from './components/WarmFormDesigner.vue'
export { WarmFormCreate } from './components/WarmFormCreate.vue'

// 插件形式安装 (可选)
export { default as install } from './core/install'

// composable (高级用法)
export { useWarmFlowConfig } from './core/config'
export { useDark } from './composables/useDark'
export { useCache } from './plugins/cache'
export { useModal } from './plugins/modal'

// 类型提示
export { defaultConfig as WarmFlowDefaultConfig } from './core/config'
```

**变更说明:**
| 操作 | 文件 | 行号 | 内容 |
|------|------|------|------|
| **新增** | `src/index.js` | 全部 | Library 入口, ~20 行 |
| **不变** | `src/main.js` | 全部 | SPA 入口保持原样 |

---

### 3.2 C2: URL 参数解耦 (store/app.js)

**现状 (store/app.js 第 4-41 行):**
```javascript
// ====== 现有代码 (需要修改) ======
import {setTokenName, setToken, setFramework} from "@/utils/auth.js";
import {config} from "@/api/anony.js";

const useAppStore = defineStore(
  'app',
  {
    state: () => ({
      appParams: null
    }),
    actions: {
      async fetchTokenName() {
        const urlParams = new URLSearchParams(window.location.search);  // ← 第12行: 耦合点!
        const params = {};
        let tokenNames = []
        await config().then(res => {
          if (res.code === 200 && res.data) {
            if (res.data.tokenNameList && res.data.tokenNameList.length > 0) {
              tokenNames = res.data.tokenNameList
              setTokenName(res.data.tokenNameList)
            }
            if (res.data.framework) {
              setFramework(res.data.framework)
            }
          }
        }).catch(() => {});
        this.appParams= null
        for (const [key, value] of urlParams.entries()) {  // ← 第28行: 仅从URL取参数!
          if (value && 'undefined' !== value) {
            if (tokenNames && tokenNames.length > 0 && tokenNames.includes(key)) {
              setToken(key, value);
            }
            params[key] = value;
          }
        }
        this.appParams = params;
      }
    }
  })
export default useAppStore;
```

**改造后 (store/app.js):**
```javascript
// ====== 修改后的 store/app.js ======
import {setTokenName, setToken, setFramework} from "@/utils/auth.js";
import {config} from "@/api/anony.js";

const useAppStore = defineStore(
  'app',
  {
    state: () => ({
      appParams: null,
      _externalParams: null  // [NEW] 存储外部注入的参数
    }),
    actions: {
      /**
       * [MODIFIED] 支持外部参数注入
       * @param {Object} externalParams - NPM模式下从props传入; SPA模式下不传(自动从URL读取)
       */
      async fetchTokenName(externalParams) {
        const params = {};
        let tokenNames = []

        // 1. 获取 tokenNameList (逻辑不变)
        await config().then(res => {
          if (res.code === 200 && res.data) {
            if (res.data.tokenNameList && res.data.tokenNameList.length > 0) {
              tokenNames = res.data.tokenNameList
              setTokenName(res.data.tokenNameList)
            }
            if (res.data.framework) {
              setFramework(res.data.framework)
            }
          }
        }).catch(() => {});

        this.appParams = null

        // 2. [KEY CHANGE] 双源参数获取
        if (externalParams && Object.keys(externalParams).length > 0) {
          // NPM 模式: 使用外部传入的参数
          for (const [key, value] of Object.entries(externalParams)) {
            if (value != null && String(value) !== 'undefined') {
              if (tokenNames.length > 0 && tokenNames.includes(key)) {
                setToken(key, String(value));
              }
              params[key] = value;
            }
          }
          this._externalParams = externalParams
        } else {
          // SPA 模式: 从 URL 读取 (原有逻辑不变)
          const urlParams = new URLSearchParams(window.location.search);
          for (const [key, value] of urlParams.entries()) {
            if (value && 'undefined' !== value) {
              if (tokenNames && tokenNames.length > 0 && tokenNames.includes(key)) {
                setToken(key, value);
              }
              params[key] = value;
            }
          }
        }

        this.appParams = params;
      },

      /** [NEW] 设置外部参数 (供包装组件调用) */
      setExternalParams(params) {
        this._externalParams = params
        this.appParams = params
      }
    }
  })

export default useAppStore;
```

**逐行变更清单:**

| 操作 | 行号(旧) | 变更内容 |
|------|----------|---------|
| **修改** | 11 | `async fetchTokenName()` → `async fetchTokenName(externalParams)` |
| **新增** | 13 | `state 中增加: _externalParams: null` |
| **修改** | 27-36 | 在 `for...of urlParams` 循环前增加 `if (externalParams...)` 分支 |
| **新增** | 48-52 | 新增 `setExternalParams()` action |
| 其余行 | - | 保持不变 |

---

### 3.3 C3: Token 认证解耦 (utils/auth.js)

**现状 (utils/auth.js 完整 37 行):**
```javascript
// ====== 现有代码 (需要修改) ======
// 接收外部业务系统token，并存入localStorage中
export const Prefix = 'Warm-'
// 设置外部token保存到header中的名称
export const TokenName = Prefix + 'TokenName'
export const FrameworkName = Prefix + 'Framework'

export function getToken(key) {
  return localStorage.getItem(Prefix + key)  // ← 第9行: 硬编码 localStorage
}

export function getTokenName() {
  return localStorage.getItem(TokenName)
}

export function remove(key) {
  return localStorage.removeItem(key)
}

export function setToken(key, value) {
  return localStorage.setItem(Prefix + key, value)
}

export function setTokenName(value) {
  return localStorage.setItem(TokenName, value)
}

export function setFramework(value) {
  return localStorage.setItem(FrameworkName, value)
}

export function getFramework() {
  return localStorage.getItem(FrameworkName)
}
```

**新增核心层 (src/core/auth.js):**
```javascript
// ====== 新增文件: src/core/auth.js ======

/**
 * TokenProvider 接口 - 支持多种 Token 来源
 * 默认实现: localStorage (向后兼容 SPA)
 */

let _customGetToken = null  // 外部注入的 Token 获取函数

/**
 * 设置自定义 Token 获取函数
 * @param {Function} fn - 格式: (key: string) => string|null
 */
export function setCustomGetToken(fn) {
  _customGetToken = fn
}

/**
 * 获取 Token (优先使用外部函数, 回退到 localStorage)
 * @param {string} key - Token 名称
 * @returns {string|null}
 */
export function getToken(key) {
  // 优先: 外部注入的函数
  if (_customGetToken) {
    try {
      const val = _customGetToken(key)
      if (val !== undefined && val !== null) return val
    } catch (e) {
      console.warn('[warm-flow] customGetToken error:', e)
    }
  }
  // 回退: localStorage (SPA 兼容)
  return localStorage.getItem('Warm-' + key)
}

// ========== 以下是原有的 localStorage 操作函数 (保持不变作为 fallback) ==========
export const Prefix = 'Warm-'
export const TokenName = Prefix + 'TokenName'
export const FrameworkName = Prefix + 'Framework'

/** [COMPAT] 从 localStorage 获取 (SPA 模式专用) */
export function getTokenFromStorage(key) {
  return localStorage.getItem(Prefix + key)
}

export function getTokenName() {
  return localStorage.getItem(TokenName)
}

export function remove(key) {
  return localStorage.removeItem(key)
}

export function setToken(key, value) {
  return localStorage.setItem(Prefix + key, value)
}

export function setTokenName(value) {
  return localStorage.setItem(TokenName, value)
}

export function setFramework(value) {
  return localStorage.setItem(FrameworkName, value)
}

export function getFramework() {
  return localStorage.getItem(FrameworkName)
}
```

**改造后的 utils/auth.js (变为 core/auth 的 re-export):**
```javascript
// ====== 修改后的 src/utils/auth.js ======
// 改为委托给核心层, 保持向后兼容 import 路径
export {
  Prefix, TokenName, FrameworkName,
  getToken, getTokenName, remove, setToken, setTokenName,
  setFramework, getFramework,
  setCustomGetToken, getTokenFromStorage
} from '@/core/auth.js'
```

**逐行变更清单:**

| 操作 | 文件 | 说明 |
|------|------|------|
| **新增** | `src/core/auth.js` | 全新文件, ~55 行, TokenProvider 实现 |
| **重写** | `src/utils/auth.js` | 37 行 → 14 行, 改为 re-export |

---

### 3.4 C4-C5: Request 和 API 前缀解耦

**现状 (utils/request.js 关键行):**
```javascript
// 第 9-14 行:
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,   // ← 第11行: 硬编码 env
  timeout: 10000
})
```

**现状 (api/anony.js, definition.js, form.js 第3行):**
```javascript
const urlPrefix = import.meta.env.VITE_URL_PREFIX  // ← 每个 API 文件都有这行
```

**新增核心层 (src/core/request.js):**
```javascript
// ====== 新增文件: src/core/request.js ======
import axios from 'axios'
import { ElNotification, ElMessage } from 'element-plus'
import { tansParams } from '@/utils/ruoyi'
import cache from '@/plugins/cache'
import { getToken, getTokenName } from '@/core/auth.js'  // 注意: 引用新的 auth

let _baseURL = ''
let _urlPrefix = '/'
let _timeout = 10000
let _requestInterceptor = null

/**
 * 初始化请求配置 (由包装组件或 install 插件时调用)
 */
export function initRequestConfig(config = {}) {
  if (config.baseURL !== undefined) _baseURL = config.baseURL
  if (config.urlPrefix !== undefined) _urlPrefix = config.urlPrefix
  if (config.timeout !== undefined) _timeout = config.timeout
  if (config.requestInterceptor) _requestInterceptor = config.requestInterceptor
}

/** 获取当前 baseURL */
export function getBaseURL() { return _baseURL }
/** 获取当前 urlPrefix */
export function getUrlPrefix() { return _urlPrefix }

/**
 * 创建 axios 实例 (工厂函数, 替代原来的单例)
 */
export function createService() {
  const instance = axios.create({
    baseURL: _baseURL || '',
    timeout: _timeout
  })

  // request 拦截器 (与原 request.js 相同逻辑, 但使用动态 config)
  instance.interceptors.request.use(config => {
    const isRepeatSubmit = (config.headers || {}).repeatSubmit === false
    if (getTokenName()) {
      let tokenName = getTokenName();
      if (tokenName) {
        let tokenNames = tokenName.split(",");
        for (let i = 0; i < tokenNames.length; i++) {
          if (getToken(tokenNames[i])) {
            config.headers[tokenNames[i]] = getToken(tokenNames[i])
          }
        }
      }
    }
    // ... (其余拦截器逻辑与原 request.js:33-66 完全相同, 此处省略)
    if (_requestInterceptor) config = _requestInterceptor(config)
    return config
  }, error => Promise.reject(error))

  // response 拦截器 (与原 request.js:73-101 完全相同)
  instance.interceptors.response.use(res => {
    const code = res.data.code || 200;
    const msg = res.data.msg
    if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
      return res.data
    }
    if (code !== 200) {
      ElNotification.error({ title: msg })
      return Promise.reject(new Error(msg))
    } else {
      return Promise.resolve(res.data)
    }
  }, error => {
    // ... (同原 request.js:89-100)
  })

  return instance
}

// 默认单例 (保持向后兼容)
const service = createService()
export default service
```

**改造后的 utils/request.js (委托到核心层):**
```javascript
// ====== 修改后的 src/utils/request.js ======
// 103 行 → 3 行, 委托给核心层
export { default, initRequestConfig, createService, getBaseURL, getUrlPrefix } from '@/core/request.js'
```

**改造所有 API 文件 (以 anony.js 为例):**

| 操作 | 文件 | 行号 | 变更 |
|------|------|------|------|
| **修改** | `src/api/anony.js` | 3 | `import.meta.env.VITE_URL_PREFIX` → `import { getUrlPrefix } from '@/core/request.js'` |
| **修改** | `src/api/flow/definition.js` | 3 | 同上 |
| **修改** | `src/api/form/form.js` | 3 | 同上 |

**anony.js 改造前后对比:**

```diff
  // src/api/anony.js
- import request from '@/utils/request'
- const urlPrefix = import.meta.env.VITE_URL_PREFIX
+ import request from '@/utils/request'  // 不变
+ import { getUrlPrefix } from '@/core/request.js'  // [NEW]

  export function config() {
-   return request({ url: urlPrefix + 'warm-flow-ui/config', method: 'get' })
+   return request({ url: getUrlPrefix() + 'warm-flow-ui/config', method: 'get' })
  }
```

**definition.js 同理, 8 处 `urlPrefix +` 全部替换为 `getUrlPrefix() +`:**

| 行号(旧) | 原 API 路径 | 改造后 |
|----------|------------|--------|
| 8 | `urlPrefix + 'warm-flow/save-json'` | `getUrlPrefix() + 'warm-flow/save-json'` |
| 25 | `urlPrefix + 'warm-flow/query-def' + id` | `getUrlPrefix() + 'warm-flow/query-def' + id` |
| 33 | `urlPrefix + 'warm-flow/query-flow-chart/' + id` | `getUrlPrefix() + ...` |
| 41 | `urlPrefix + 'warm-flow/handler-type'` | `getUrlPrefix() + ...` |
| 49 | `urlPrefix + 'warm-flow/handler-result'` | `getUrlPrefix() + ...` |
| 57 | `urlPrefix + 'warm-flow/handler-feedback'` | `getUrlPrefix() + ...` |
| 65 | `urlPrefix + 'warm-flow/handler-dict'` | `getUrlPrefix() + ...` |
| 73 | `urlPrefix + 'warm-flow/published-form'` | `getUrlPrefix() + ...` |
| 81 | `urlPrefix + 'warm-flow/node-ext'` | `getUrlPrefix() + ...` |
| 89 | `urlPrefix + 'warm-flow/listener-list'` | `getUrlPrefix() + ...` |

**form.js 同理, 6 处替换:**

| 行号(旧) | 原 API 路径 |
|----------|------------|
| 8 | `urlPrefix + 'warm-flow/form-content/' + id` |
| 16 | `urlPrefix + 'warm-flow/form-content'` |
| 25 | `urlPrefix + 'warm-flow/execute/load/' + id` |
| 33 | `urlPrefix + 'warm-flow/execute/handle?...` |
| 40 | `urlPrefix + 'warm-flow/execute/hisLoad/' + taskId` |

---

### 3.5 C6: 关闭行为解耦 (index.vue:965)

**现状:**
```javascript
// src/views/flow-design/index.vue 第964-966行
function close() {
  window.parent.postMessage({ method: "close" }, "*");  // ← 只支持 iframe
}
```

**改造方案: 通过 props.onClose 或 emit 替代**

在包装组件 `WarmFlowDesigner.vue` 中:
```vue
<script setup>
// 包装组件接收 onClose prop 并传递给内部视图
const props = defineProps({
  onClose: { type: Function, default: null }
})
</script>
<template>
  <!-- 将 onClose 传递给内部视图 -->
  <flow-design-index :on-close="onClose" v-bind="$attrs" />
</template>
```

**index.vue 改造:**
```diff
  // src/views/flow-design/index.vue
+
+ // [NEW] 接受外部 onClose prop
+ const props = defineProps({
+   onClose: { type: Function, default: null }
+ })
+
  function close() {
-   window.parent.postMessage({ method: "close" }, "*");
+   // [MODIFIED] 优先使用外部 onClose, 否则回退 postMessage
+   if (props.onClose) {
+     props.onClose()
+   } else if (window.parent !== window) {
+     window.parent.postMessage({ method: "close" }, "*");
+   }
  }
```

**saveJsonModel 方法中调用 close() 的地方无需改动**, 因为 close() 函数内部已经处理了双模式。

---

### 3.6 C7: 暗黑模式解耦 (composables/useDark.js)

**现状 (useDark.js 完整 93 行):**
```javascript
// 核心耦合点:
// 1. 第17-24行: initFromUrl() — 从 appParams.theme 读取初始值
// 2. 第28-40行: listeningMessage() — 监听 postMessage theme-dark/light
// 3. 第72-78行: setupMessageListener() — 注册 message 事件监听
// 4. 第81-83行: cleanupMessageListener() — 清理 message 监听
```

**改造后 (composables/useDark.js):**
```javascript
// ====== 修改后的 composables/useDark.js ======
// [P8修复] 多实例安全: 每次调用 useDark() 返回独立的 isDark ref,
//          SPA 模式通过 postMessage 广播保持多实例同步

import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import useAppStore from '@/store/app'

// SPA 模式下共享的主题状态 (仅用于 iframe 场景的多组件同步)
const _spaGlobalDark = ref(false)
// 所有活跃实例的回调列表 (postMessage 变更时通知所有实例)
let _instances = []

export function useDark(options = {}) {
  // [P8修复] 每个调用者拥有独立的 isDark ref — 不再是模块级单例!
  const isDark = ref(false)
  const externalDarkMode = options.darkMode

  const appStore = useAppStore()
  const appParams = computed(() => useAppStore().appParams)

  /** [MODIFIED] 初始化暗黑模式 (三源: prop > URL > 默认) */
  function initFromUrl() {
    // 优先: 外部 prop 控制
    if (externalDarkMode === true || externalDarkMode === false) {
      isDark.value = externalDarkMode
      applyToDOM(externalDarkMode)
      return
    }

    // 其次: URL 参数 / SPA 全局状态 (SPA 兼容)
    const urlTheme = appParams.value?.theme
    if (urlTheme === 'theme-dark') {
      isDark.value = true
      _spaGlobalDark.value = true
      applyToDOM(true)
    } else if (urlTheme === 'theme-light') {
      isDark.value = false
      _spaGlobalDark.value = false
      applyToDOM(false)
    }
  }

  /** [NEW] 统一应用/取消暗黑主题到 DOM */
  function applyToDOM(dark) {
    // 注意: document.documentElement 是全局的,
    //       多实例时后调用的会覆盖前一个 — 这符合预期
    //       (整个页面只有一套暗黑主题)
    if (dark) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  /** [KEEP] 监听父页面 postMessage (SPA 兼容) */
  function listeningMessage(e) {
    const { data } = e
    switch (data.type) {
      case 'theme-dark':
        _spaGlobalDark.value = true
        // [P8修复] 通知所有实例更新
        _instances.forEach(fn => fn(true))
        applyToDOM(true)
        return
      case 'theme-light':
        _spaGlobalDark.value = false
        _instances.forEach(fn => fn(false))
        applyToDOM(false)
        return
    }
  }

  /** [NEW] 如果有外部 prop, 监听变化并响应 */
  let unwatchProp = null
  if (externalDarkMode !== undefined) {
    unwatchProp = watch(() => options.darkMode, (val) => {
      if (val === true || val === false) {
        isDark.value = val          // [P16修复] 使用实例独立的 isDark ref (P8已将 sharedIsDark 替换为 isDark)
        applyToDOM(val)
      }
    })
  }

  /** 应用/取消暗黑主题到 LogicFlow 画布 (保持不变) */
  function applyDarkTheme(lfInstance, isDarkMode) {
    if (!lfInstance) return
    lfInstance.graphModel.background = {
      background: isDarkMode ? '#141414' : '#fff',
    }
    if (lfInstance.graphModel.grid) {
      lfInstance.graphModel.grid.config = {
        ...lfInstance.graphModel.grid.config,
        color: isDarkMode ? '#404040' : '#ccc',
      }
    }
    lfInstance.setTheme({
      nodeText: { color: isDarkMode ? '#e0e0e0' : '#303133', fill: isDarkMode ? '#e0e0e0' : '#303133', fontSize: 13, fontWeight: 500 },
      edgeText: { fontSize: 13, strokeWidth: 1, background: { fill: isDarkMode ? '#141414' : '#fff' } },
    })
  }

  function setupMessageListener() {
    window.addEventListener('message', listeningMessage)
    // [KEEP] SPA 模式下查询父窗口主题
    if (window.parent !== window) {
      window.parent.postMessage({ method: 'getTheme' }, '*')
    }
  }

  function cleanupMessageListener() {
    window.removeEventListener('message', listeningMessage)
    // [P8修复] 从实例列表移除
    const idx = _instances.indexOf(setInstanceDark)
    if (idx >= 0) _instances.splice(idx, 1)
    if (unwatchProp) unwatchProp()
  }

  /** [P8新增] 实例级暗黑状态更新回调 (供 postMessage 全局广播调用) */
  function setInstanceDark(dark) {
    isDark.value = dark
  }

  // [P8修复] 注册到全局实例列表
  _instances.push(setInstanceDark)

  return {
    isDark,                    // [P8修正] 返回实例独立的 ref
    initFromUrl,
    applyDarkTheme,
    setupMessageListener,
    cleanupMessageListener,
  }
}
```

**调用处改造 (index.vue 和 flowChart.vue):**

```diff
  // src/views/flow-design/index.vue
- const { isDark, initFromUrl, applyDarkTheme, setupMessageListener, cleanupMessageListener } = useDark()
+ const props = defineProps({ darkMode: { type: [String, Boolean], default: 'auto' } })
+ const { isDark, initFromUrl, applyDarkTheme, setupMessageListener, cleanupMessageListener } = useDark({
+   darkMode: props.darkMode
+ })
```

```diff
  // src/views/flow-design/flowChart.vue (同样改造)
- const { isDark, initFromUrl, applyDarkTheme, setupMessageListener, cleanupMessageListener } = useDark()
+ const props = defineProps({ darkMode: { type: [String, Boolean], default: 'auto' } })
+ const { isDark, initFromUrl, applyDarkTheme, setupMessageListener, cleanupMessageListener } = useDark({
+   darkMode: props.darkMode
+ })
```

---

### 3.7 C8: body 样式污染解耦

**现状 (两处):**
```javascript
// index.vue 第244行 (onMounted中):
document.body.style.overflow = 'hidden';

// flowChart.vue 第298行 (queryFlowChart.then中):
document.body.style.overflow = 'hidden';
```

**改造方案: 检测运行模式, NPM 下跳过**

```diff
  // src/views/flow-design/index.vue 第242-244行
  onMounted(() => {
-   document.body.style.overflow = 'hidden';
+   // [MODIFIED] SPA 模式才设置 body overflow (iframe 需要); NPM 模式由容器控制
+   if (window.parent !== window) {
+     document.body.style.overflow = 'hidden';
+   }
```

```diff
  // src/views/flow-design/flowChart.vue 第298行
  queryFlowChart(instanceId.value).then((res) => {
    // ...
-   document.body.style.overflow = 'hidden';
+   if (window.parent !== window) {
+     document.body.style.overflow = 'hidden';
+   }
    // ...
  })
```

> **原理:** `window.parent !== window` 在 iframe 中为 true, 在 NPM 组件嵌入中为 false (除非嵌套 iframe)。这是最简单可靠的环境检测方式。

---

### 3.8 C9: Pinia 多实例支持

**现状 (store/index.js):**
```javascript
const store = createPinia()
export default store
```

**问题:** 如果宿主项目也用了 Pinia 且版本不同, 可能冲突。Library 模式下需要独立的 Pinia 实例。

**新增 (src/core/store.js):**
```javascript
// ====== 新增文件: src/core/store.js ======
import { createPinia } from 'pinia'

let piniaInstance = null

/**
 * 获取或创建 Pinia 实例
 * @param {App} app - 可选的 Vue app 实例 (用于 provide/inject)
 */
export function getPinia(app) {
  if (!piniaInstance) {
    piniaInstance = createPinia(app)
  }
  return piniaInstance
}

/** 重置 Pinia (测试或多实例场景) */
export function resetPinia() {
  piniaInstance = null
}

// 默认导出 (向后兼容)
export default getPinia()
```

**改造后的 store/index.js:**
```javascript
// ====== 修改后的 src/store/index.js ======
export { default, getPinia, resetPinia } from '@/core/store.js'
```

**main.js 无需改动** (SPA 继续使用单例)。

---

### 3.9 C10: $cache/$modal 全局属性解耦

**现状 (plugins/index.js):**
```javascript
export default function installPlugins(app){
  app.config.globalProperties.$cache = cache   // 只能通过全局属性访问
  app.config.globalProperties.$modal = modal
}
```

**改造后 (plugins/cache.js 增加 named export):**
```javascript
// ====== 修改后的 plugins/cache.js ======
const sessionCache = { /* 原有37行不变 */ }

export default {
  session: sessionCache,
}

// [NEW] named export 供 composable 使用
export const useCache = {
  session: sessionCache,
}
```

**改造后 (plugins/modal.js 增加 named export):**
```javascript
// ====== 修改后的 plugins/modal.js ======
let loadingInstance;

export default {
  // 原有 82 行方法不变...
  msg(content) { ElMessage.info(content) },
  msgError(content) { ElMessage.error(content) },
  // ... (全部保留)
}

// [NEW] named export
export const useModal = {
  msg(content) { ElMessage.info(content) },
  msgError(content) { ElMessage.error(content) },
  msgSuccess(content) { ElMessage.success(content) },
  msgWarning(content) { ElMessage.warning(content) },
  alert(content) { ElMessageBox.alert(content, "\u7cfb\u7edf\u63d0\u793a") },
  // ... (映射所有方法)
  confirm(content) {
    return ElMessageBox.confirm(content, "\u7cfb\u7edf\u63d0\u793a", {
      confirmButtonText: '\u786e\u5b9a', cancelButtonText: '\u53d6\u6d88', type: "warning",
    })
  },
  loading(content) { loadingInstance = ElLoading.service({ lock: true, text: content, background: "rgba(0, 0, 0, 0.7)" }) },
  closeLoading() { loadingInstance?.close() },
}
```

**plugins/index.js (保持不变)** — 全局属性注册仍对 SPA 有效。

---

### 3.10 C11: SVG 图标处理

**现状 (main.js:17-18):**
```javascript
import 'virtual:svg-icons-register'  // Vite 虚拟模块, 仅构建时可用
import SvgIcon from '@/components/SvgIcon'
```

**问题分析:**
`virtual:svg-icons-register` 是 `vite-plugin-svg-icons` 生成的 **Vite 构建时虚拟模块**, 运行时不存在于文件系统中。因此:
1. ❌ 不能在运行时通过 `import('virtual:svg-icons-register')` 动态检测 — 必然失败
2. ✅ SPA 模式下 Vite 构建时会自动注入 SVG sprite 到 index.html
3. ❌ Library 模式下 vite-plugin-svg-icons 被移除 (见 C12), 无 SVG sprite 可用

**解决方案: 双轨策略**

#### 策略 A: Library 构建 — 将 SVG 转为内联 data URI (推荐)

创建 `vite/plugins/svg-inline.js`, 在 Library 模式下将所有 SVG 文件转为 base64 data URI:

```javascript
// ====== 新增文件: vite/plugins/svg-inline.js ======
// Library 构建用: 将 icons/ 目录下的 SVG 文件转为内联组件
// 替代 vite-plugin-svg-icons 的虚拟模块方案

import { readFileSync, readdirSync } from 'fs'
import { resolve } from 'path'

export function createSvgInline(isBuild, viteRootDir) {
  // [P18修复] 使用传入的 Vite 项目根目录定位图标目录, 而非不可靠的 process.cwd()
  const _rootDir = viteRootDir || process.cwd()

  return {
    name: 'svg-inline',
    resolveId(id) {
      if (id === 'virtual:svg-icons-register' || id === '@/components/SvgIcon/svgicon') {
        return id  // 拦截虚拟模块请求
      }
    },
    load(id) {
      if (id === 'virtual:svg-icons-register') {
        // 返回空操作 — SVG 改为按需内联
        return '// svg-inline: SVG icons loaded on-demand via data URI'
      }
      if (id.endsWith('svgicon')) {
        // 扫描所有 SVG 文件, 生成图标映射表
        const iconDir = resolve(_rootDir, 'src/assets/icons/svg')  // [P18修复] 使用 _rootDir 替代 process.cwd()
        const files = readdirSync(iconDir).filter(f => f.endsWith('.svg'))
        const imports = files.map(f => {
          const name = f.replace('.svg', '')
          const content = readFileSync(resolve(iconDir, f), 'utf-8')
            .replace(/"/g, "'").replace(/\n/g, '')
          return `  '${name}': '${content}'`
        })
        return `export default { ${imports.join(',\n')} }`
      }
      return null
    }
  }
}
```

#### 策略 B: SvgIcon 组件改造 — 支持 fallback 渲染

```vue
<!-- ====== 修改: src/components/SvgIcon/index.vue ====== -->
<template>
  <!-- 有 SVG 内容时渲染 inline SVG, 否则显示文字 fallback -->
  <span v-if="svgContent" class="svg-icon" v-html="svgContent" />
  <span v-else class="wf-icon-fallback" :title="iconClass">{{ iconClass }}</span>
</template>

<script setup>
import { computed, inject } from 'vue'

const props = defineProps({ iconClass: { type: String, required: true } })

// 从全局图标映射中查找 SVG 内容 (Library 模式由 svg-inline 插件提供)
// SPA 模式仍使用原有的 <svg use href="#icon-xxx"> 方式 (sprite 已注入 DOM)
const iconMap = inject('wfSvgIconMap', null)
const svgContent = computed(() => {
  if (iconMap && iconMap[props.iconClass]) {
    return iconMap[props.iconClass]
  }
  return null
})
</script>

<style scoped>
.svg-icon { display: inline-block; width: 1em; height: 1em; fill: currentColor; vertical-align: middle; }
.wf-icon-fallback { color: #909399; font-size: 0.85em; vertical-align: middle; }
</style>
```

#### Vite 插件链更新 (C12 联动):

```diff
  // vite/plugins/index.js (最终版)
  import createSvgInline from './svg-inline'   // [NEW]

  export default function createVitePlugins(viteEnv, isBuild = false, isLib = false) {
      const vitePlugins = [vue()]

      if (!isLib) {
          // SPA 模式: 使用原有插件
          vitePlugins.push(createAutoImport())
          vitePlugins.push(createSetupExtend())
          vitePlugins.push(createSvgIcon(isBuild))       // 原有 SVG sprite 方案
+     } else {
+         // Library 模式: 使用内联方案替代
+         vitePlugins.push(createSvgInline(isBuild))     // [NEW] SVG → data URI
+     }

      return vitePlugins
  }
```

> **效果对比:**
>
> | 模式 | SVG 图标渲染方式 | 图标数量 | 额外体积 |
> |------|-----------------|---------|---------|
> | SPA (不变) | `<use href="#icon-xxx">` 引用 sprite | 全部可用 | ~30KB (sprite) |
> | Library (新) | inline SVG `v-html` 按需加载 | 全部可用 | 仅使用的图标 (~1KB/个) |

---

### 3.10.5 [P7补充] FcDesigner (表单设计器/表单渲染) 处理

**现状 (main.js:19-20):**
```javascript
app.use(FcDesigner);           // 全局注册表单设计器组件
app.use(FcDesigner.formCreate); // 全局注册表单渲染器组件
```

**问题:** FcDesigner (`@form-create/designer`) 和 formCreate (`@form-create/element-ui`) 是全局组件注册。Library 模式下没有 `app.use()`，如何处理？

**方案: FcDesigner 作为 bundled 依赖，包装组件内局部注册**

理由：
1. 表单设计是 **warm-flow 的可选功能**（非所有用户都用到）
2. `@form-create/*` 版本与宿主冲突风险低（宿主一般不装这个）
3. 作为 bundled 打入包内，用户无需额外安装

**具体实现:**

```vue
<!-- ====== 包装组件中局部注册 FcDesigner ====== -->
<!-- src/components/WarmFormDesigner.vue -->
<template>
  <fc-designer ... />
</template>

<script setup>
import { defineAsyncComponent } from 'vue'
// 延迟加载: 只有使用表单设计功能时才加载 FcDesigner (~200KB)
const FcDesigner = defineAsyncComponent(() =>
  import('@form-create/designer')
)
</script>
```

**SPA 模式不变** — main.js 中继续全局注册。

---

### 3.11 C12: Vite 插件链条件化

**现状 (vite/plugins/index.js):**
```javascript
export default function createVitePlugins(viteEnv, isBuild = false) {
    const vitePlugins = [vue()]
    vitePlugins.push(createAutoImport())     // auto-import
    vitePlugins.push(createSetupExtend())
    vitePlugins.push(createSvgIcon(isBuild))  // svg-icons
    return vitePlugins
}
```

**改造后 (vite/plugins/index.js):**
```javascript
// ====== 修改后的 vite/plugins/index.js ======
import vue from '@vitejs/plugin-vue'
import createAutoImport from './auto-import'
import createSetupExtend from './setup-extend'
import createSvgIcon from './svg-icon'

/**
 * 创建 Vite 插件列表
 * @param {Object} viteEnv - 环境变量对象
 * @param {boolean} isBuild - 是否生产构建
 * @param {boolean} isLib - [NEW] 是否 Library 模式构建
 */
export default function createVitePlugins(viteEnv, isBuild = false, isLib = false) {
    const vitePlugins = [vue()]

    // [KEY CHANGE] Library 模式下跳过以下插件
    if (!isLib) {
        vitePlugins.push(createAutoImport())     // auto-import (库模式不需要)
        vitePlugins.push(createSetupExtend())
        vitePlugins.push(createSvgIcon(isBuild))  // svg-icons (库模式内联处理)
    }

    // compression 插件也在主 vite.config.js 中条件化
    return vitePlugins
}
```

---

## 第四章 完整文件变更清单

### 4.1 新增文件 (8 个)

| # | 文件路径 | 行数(约) | 说明 |
|---|---------|----------|------|
| N1 | `src/core/config.js` | ~80 | 配置工厂: 创建/合并/提供默认配置 |
| N2 | `src/core/request.js` | ~120 | axios 实例工厂 + 动态 baseURL/urlPrefix |
| N3 | `src/core/auth.js` | ~55 | TokenProvider 接口 + 自定义 Token 函数注入 |
| N3.5 | `vite/plugins/svg-inline.js` | ~35 | Library 模式 SVG → data URI 内联插件 (C11 方案) |
| N4 | `src/core/install.js` | ~30 | Vue plugin 安装函数 (app.use(WarmFlowPlugin)) |
| N5 | `src/core/store.js` | ~20 | Pinia 多实例管理 |
| N6 | `src/index.js` | ~20 | Library 入口: export 所有组件和工具 |
| N7 | `src/composables/useTouchEventBridge.js` | ~250 | 触摸事件桥接 (从 index.vue/flowChart.vue 抽取) |
| N8 | `.env.library` | ~5 | Library 模式环境变量 |
| N9 | `src/components/WarmFlowDesigner.vue` | ~80 | 流程设计器包装组件 |
| N10 | `src/components/WarmFlowChart.vue` | ~50 | 流程图查看器包装组件 |
| N11 | `src/components/WarmFormDesigner.vue` | ~40 | 表单设计器包装组件 |
| N12 | `src/components/WarmFormCreate.vue` | ~40 | 表单审批包装组件 |

#### [P10补充] N4: install.js 完整实现

> **用途**: 提供 Vue Plugin 形式的安装方式, 用户可 `app.use(WarmFlowPlugin, config)` 全局注册。

```javascript
// ====== 新增文件: src/core/install.js ======
import { initWarmFlow } from './config.js'
import { setCustomGetToken } from './auth.js'
import { initRequestConfig } from './request.js'
import { getPinia } from './store.js'

// 预注册 request 的初始化函数到 config (解决循环引用)
import { registerRequestInit } from './config.js'
registerRequestInit(initRequestConfig)

/**
 * Vue Plugin 安装函数
 *
 * 使用方式:
 *   import WarmFlowPlugin from '@warm-flow/vue-designer'
 *   app.use(WarmFlowPlugin, {
 *     baseURL: '/api',
 *     getToken: () => localStorage.getItem('token'),
 *     darkMode: 'auto'
 *   })
 */
function install(app, options = {}) {
  // 1. 合并配置
  const config = initWarmFlow(options)

  // 2. 设置 Token 获取函数
  if (options.getToken) {
    setCustomGetToken(options.getToken)
  }

  // 3. 初始化请求配置
  initRequestConfig(config)

  // 4. 注册 Pinia (如果宿主尚未安装)
  if (!app.config.globalProperties.$pinia) {
    const pinia = getPinia()
    app.use(pinia)
  }

  // 5. 注册全局组件 (可选便捷使用)
  //    用户也可以按需 import 单个组件
  app.component('WarmFlowDesigner', () =>
    import('../components/WarmFlowDesigner.vue')
  )
  app.component('WarmFlowChart', () =>
    import('../components/WarmFlowChart.vue')
  )
  app.component('WarmFormDesigner', () =>
    import('../components/WarmFormDesigner.vue')
  )
  app.component('WarmFormCreate', () =>
    import('../components/WarmFormCreate.vue')
  )

  // 6. 提供全局配置访问
  app.provide('$warmFlowConfig', config)
}

export default install
```

### 4.2 修改文件 (18 个)

| # | 文件路径 | 修改量 | 主要变更 |
|---|---------|--------|---------|
| M1 | `src/utils/request.js` | 103→3行 | 重写为 re-export core/request.js |
| M2 | `src/utils/auth.js` | 37→14行 | 重写为 re-export core/auth.js |
| M3 | `src/store/index.js` | 3→3行 | 重写为 re-export core/store.js |
| M4 | `src/store/app.js` | 41→55行 | fetchTokenName 支持 externalParams 参数 |
| M5 | `src/api/anony.js` | 11→12行 | urlPrefix → getUrlPrefix() |
| M6 | `src/api/flow/definition.js` | 94→94行 | 10处 urlPrefix 替换 |
| M7 | `src/api/form/form.js` | 45→45行 | 5处 urlPrefix 替换 |
| M8 | `src/composables/useDark.js` | 93→130行 | 支持 darkMode prop 注入 |
| M9 | `src/views/flow-design/index.vue` | 1635→1650行 | C6/C7/C8 解耦 + 触摸事件引用提取 |
| M10 | `src/views/flow-design/flowChart.vue` | 766→780行 | C6/C7/C8 解耦 + 触摸事件引用提取 |
| M11 | `src/views/form-design/index.vue` | 8→25行 | 增加 defineProps + close() 双模式 (同 C6/C8) |
| M12 | `src/views/form-design/formCreate.vue` | 106→120行 | 增加 defineProps + fetchTokenName(externalParams) (同 C2/C8) |
| M13 | `vite.config.js` | 70→110行 | 双模式构建配置 |
| M14 | `package.json` | 42→65行 | exports/peerDependencies/scripts |
| M15 | `vite/plugins/index.js` | 13→18行 | 增加 isLib 参数 |
| M16 | `src/plugins/cache.js` | 38→43行 | 增加 named export |
| M17 | `src/plugins/modal.js` | 83→100行 | 增加 named export useModal |
| M18 | `src/App.vue` | 26→35行 | 增加接收外部 props 能力 |

#### [P9补充] M18: App.vue 具体改造内容

> **说明**: SPA 模式下 App.vue 是根组件, 通过 `<router-view>` 渲染路由页面。NPM 模式下 App.vue **不被使用**（包装组件直接渲染视图组件）。因此 App.vue 的改动仅影响 SPA 模式, 目的是让 SPA 模式也能从外部配置中受益 (如统一 baseURL)。

**现状 (App.vue 完整 26 行):**
```vue
<!-- src/App.vue -->
<template>
  <router-view />
</template>

<script setup>
import useAppStore from '@/store/app'
const appStore = useAppStore()
onMounted(() => {
  appStore.fetchTokenName()   // 从 URL 读取参数
})
</script>
```

**改造后 (35 行):**
```vue
<!-- ====== 修改后的 src/App.vue ====== -->
<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import useAppStore from '@/store/app'

const appStore = useAppStore()

onMounted(() => {
  // [MODIFIED] SPA 模式仍从 URL 读取参数 (行为不变)
  // NPM 模式不会执行此代码 (App.vue 不被使用)
  appStore.fetchTokenName()
})

// [NEW] 暴露 appStore 引用供调试/外部访问
// (SPA 模式下可通过 window.__warmFlowAppStore 访问)
if (typeof window !== 'undefined') {
  window.__warmFlowAppStore = appStore
}
</script>
```

**变更清单:**

| 行号(旧) | 变更 | 原因 |
|----------|------|------|
| 1 | 增加 `import { onMounted } from 'vue'` | 显式导入 (auto-import Library 模式不可用) |
| 5 | 增加 `window.__warmFlowAppStore = appStore` | 调试便利, 非必需 |

#### [P11补充] M11-M12: form-design 组件改造内容

**M11: `src/views/form-design/index.vue` (表单设计器)**

> **耦合分析**: 表单设计器也有关闭行为 (postMessage close) 和 body overflow 设置, 需要与 flow-design 做相同的 C6/C8 解耦。

```diff
  // src/views/form-design/index.vue
+
+ // [NEW] 接受外部 props (NPM 模式)
+ const props = defineProps({
+   id: { type: [String, Number], default: null },
+   onClose: { type: Function, default: null }
+ })

  // 关闭方法 (如有)
  function close() {
-   window.parent.postMessage({ method: "close" }, "*");
+   if (props.onClose) {
+     props.onClose()
+   } else if (window.parent !== window) {
+     window.parent.postMessage({ method: "close" }, "*");
+   }
  }

  // onMounted 中的 body overflow
  onMounted(() => {
-   document.body.style.overflow = 'hidden'
+   if (window.parent !== window) {    // [C8] SPA 模式才设置
+     document.body.style.overflow = 'hidden'
+   }
    // ... 其余逻辑不变
```

**M12: `src/views/form-design/formCreate.vue` (表单审批)**

> **耦合分析**: 表单审批组件从 URL 获取 `id` 和 token 参数, 需要 C2 解耦 + C8 overflow 处理。

```diff
  // src/views/form-design/formCreate.vue
+
+ // [NEW] 接受外部 props
+ const props = defineProps({
+   id: { type: [String, Number], default: null },
+   taskId: { type: [String, Number], default: null }
+ })

  onMounted(() => {
-   appStore.fetchTokenName()              // [C2] 仅从 URL 取参数
+   const extParams = {}
+   if (props.id) extParams.id = String(props.id)
+   if (props.taskId) extParams.taskId = String(props.taskId)
+   appStore.fetchTokenName(extParams)      // [C2修正] 支持外部参数注入

-   document.body.style.overflow = 'hidden'  // [C8]
+   if (window.parent !== window) {
+     document.body.style.overflow = 'hidden' // [C8] 条件化
+   }

    // ... 其余逻辑不变
  })
```

### 4.3 不变的文件 (大量)

以下文件 **不需要任何修改**:
- `src/components/SvgIcon/*` — SVG 图标组件 (C11 仅做降级处理)
- `src/components/design/*` — 所有业务组件 (baseInfo/between/propertySetting 等)
- `src/components/form/*` — 表单组件
- `src/assets/styles/*` — SCSS 样式文件
- `src/utils/ruoyi.js` — 工具函数
- `.env.development` — 开发环境变量 (SPA 继续使用)
- `.env.production` — 生产环境变量 (SPA 继续使用)

---

## 第五章 8 阶段实施计划

### Phase 1: 创建核心解耦层 (~预估 2h)

**目标:** 建立 `src/core/` 目录和所有基础抽象, 不影响任何现有代码。

**步骤 1.1: 创建目录结构**
```bash
mkdir -p src/core
```

**步骤 1.2: 创建 src/core/config.js (~80 行)**

关键实现要点:
```javascript
// src/core/config.js
let currentConfig = {}

export const defaultConfig = {
  baseURL: '', urlPrefix: '/', timeout: 10000,
  getToken: null, tokenName: 'Authorization', tokenNameList: [],
  onClose: null, darkMode: 'auto',
  customNodeTypes: {}, requestInterceptor: null,
  elementPlusInstalled: false,
}

// [P3修复] 延迟导入: 避免循环引用 + 兼容 ESM (项目 "type": "module", require 不可用)
let _requestModule = null
async function getRequestModule() {
  if (!_requestModule) {
    _requestModule = await import('./request.js')
  }
  return _requestModule
}

export function setConfig(userConfig) {
  currentConfig = { ...defaultConfig, ...userConfig }
  // 级联初始化依赖模块 (延迟导入避免循环引用)
  if (currentConfig.baseURL !== undefined || currentConfig.urlPrefix !== undefined) {
    // 同步场景: 直接调用 (request.js 的 initRequestConfig 已通过模块级变量暴露)
    try {
      // 使用顶层已 import 的方式 — 见文件底部的同步初始化函数 syncInitDeps()
      syncInitDeps(currentConfig)
    } catch (e) {
      console.warn('[warm-flow] config 级联初始化失败(非致命):', e.message)
    }
  }
  return currentConfig
}

/**
 * ===== 注册式解耦 (解决 ESM 循环引用) =====
 *
 * 核心思路: config.js 不主动 import request.js, 而是暴露注册接口,
 *         由 request.js 在模块加载时反向注册初始化函数。
 *         这是 Node.js 生态解决循环依赖的标准模式。
 */

let _requestInitFn = null  // 存储来自 request.js 的初始化函数

/** [API] 供 request.js 调用 — 注册其 initRequestConfig 函数 */
export function registerRequestInit(initFn) {
  _requestInitFn = initFn
}

function syncInitDeps(config) {
  if (_requestInitFn) {
    _requestInitFn(config)
  }
}

export function getConfig() { return currentConfig }

/** [重要] 判断当前是否在 NPM/Library 模式下运行 */
export function isLibraryMode() { return !!currentConfig._libraryMode }

/** 初始化 (由包装组件或 install 插件调用) */
export function initWarmFlow(config = {}) {
  const merged = { ...defaultConfig, ...config, _libraryMode: true }
  setConfig(merged)
  return merged
}
```

**步骤 1.3-1.5:** 按 3.3/3.4/3.8 节创建 `core/auth.js`, `core/request.js`, `core/store.js`

**验证:** `npm run dev` 启动 SPA 模式, 确认无报错 (此时 core/ 文件只是存在, 还未被引用)。

---

### Phase 2: 逐步委托到核心层 (~预估 2h)

**目标:** 让现有代码逐步使用 core/ 层, 每改一个文件就验证一次。

**顺序 (按依赖关系):**

1. **M2: utils/auth.js** → re-export core/auth.js → 验证 SPA 登录/Token 正常
2. **M1: utils/request.js** → re-export core/request.js → 验证 API 请求正常
3. **M3: store/index.js** → re-export core/store.js → 验证状态管理正常
4. **M5-M7: api/*.js** → urlPrefix → getUrlPrefix() → 验证所有 API 调用
5. **M16-M17: plugins/cache.js, modal.js** → 增加 named export → 验证全局属性可用
6. **M4: store/app.js** → fetchTokenName(externalParams) → 验证 SPA URL 参数解析不受影响

**每步验证命令:**
```bash
npm run dev        # 开发模式验证
npm run build:prod # SPA 构建验证
```

---

### Phase 3: 抽取触摸事件桥接 (~预估 1h)

**目标:** 将 index.vue 和 flowChart.vue 中重复的触摸事件桥接代码抽取为共享 composable。

> **[P13修正] 代码定位方式**: 以下使用**函数名/关键字搜索**定位代码块, 不使用硬编码行号 (行号随每次编辑偏移)。
>
> 触摸事件代码的关键识别特征:
> - `function setupPointerEventCapture` — Pointer Events 桥接初始化
> - `function initTouchEventBridge` — 事件绑定入口
> - `window._markDrawerOpen` / `window._markDrawerClosed` — 抽屉状态标记
> - `pointerdown` / `pointermove` / `pointerup` — 指针事件监听
> - `touchstart` / `touchmove` / `touchend` — 触摸事件监听
> - `drawerOpen` 变量声明和赋值

**步骤 3.1:** 创建 `src/composables/useTouchEventBridge.js` (~250 行)

包含以下导出函数:
```javascript
export function useTouchEventBridge(containerRef, options = {}) {
  // setupPointerEventCapture(el, options)  — Pointer Events 桥接
  // initTouchEventBridge()                 — 初始化绑定
  // destroyTouchEventBridge()              — 清理事件监听
  // markDrawerOpen() / markDrawerClosed()  — 抽屉状态标记
}
```

**步骤 3.2:** 修改 index.vue (搜索以下关键字定位要删除/替换的代码块):

| 操作 | 搜索关键字 | 说明 |
|------|-----------|------|
| **删除整块** | 搜索 `function initTouchEventBridge` 到其闭合 `}` 的完整函数体 (~197行) | 触摸事件桥接初始化 |
| **删除整块** | 搜索 `function setupPointerEventCapture` 到其闭合 `}` 的完整函数体 | Pointer Events 转换 |
| **删除** | 搜索 `window._markDrawerOpen` 和 `window._markDrawerClosed` 的所有出现 (通常2处) | 抽屉状态标记 |
| **新增** | 文件顶部 import 区域添加: `import { useTouchEventBridge } from '@/composables/useTouchEventBridge'` | 引入 composable |
| **新增** | 在 onMounted 中调用: `const { destroyTouchEventBridge } = useTouchEventBridge(containerRef.value)` | 替代原有内联逻辑 |
| **新增** | 在 onUnmounted 中调用: `destroyTouchEventBridge()` | 确保清理 |

**步骤 3.3:** 修改 flowChart.vue (同样的抽取操作, 搜索相同关键字)

> **验证方法**: 改造后搜索两个文件中是否还存在 `initTouchEventBridge` 或 `_markDrawerOpen` — 应该找不到 (已移入 composable)。

**验证:** 手机/平板真机测试触摸交互 (节点点击/拖拽/双击/长按)

---

### Phase 4: 创建包装组件 (~预估 1.5h)

**目标:** 创建 4 个包装组件, 定义统一的 props/events/slot API。

**步骤 4.1: WarmFlowDesigner.vue (~80 行)**
```vue
<!-- src/components/WarmFlowDesigner.vue -->
<template>
  <flow-design-index
    v-bind="$attrs"
    :on-close="onClose"
    :dark-mode="darkMode"
    @save="(data) => $emit('save', data)"
    @error="(err) => $emit('error', err)"
  >
    <template #toolbar><slot name="toolbar" /></template>
    <template #header><slot name="header" /></template>
  </flow-design-index>
</template>

<script setup>
import FlowDesignIndex from '../views/flow-design/index.vue'

defineOptions({ name: 'WarmFlowDesigner' })

withDefaults(defineProps({
  onClose: { type: Function, default: null },
  darkMode: { type: [String, Boolean], default: 'auto' },
}), {})

defineEmits(['save', 'close', 'error'])
</script>
```

**步骤 4.2-4.4:** 同理创建 WarmFlowChart.vue / WarmFormDesigner.vue / WarmFormCreate.vue (各 40-50 行)

---

### Phase 5: 视图组件解耦 C1-C8 (~预估 2h)

**目标:** 改造 4 个视图组件, 使其在 NPM 模式下脱离 URL/window 依赖。

**每个视图组件的改造清单:**

| 改造项 | index.vue | flowChart.vue | form-design/index.vue | formCreate.vue |
|--------|-----------|---------------|---------------------|----------------|
| defineProps (id/type/darkMode/onClose) | ✅ | ✅ | ✅ | ✅ |
| C2: fetchTokenName(externalParams) | ✅ | ✅ | ✅ | ✅ |
| C6: close() emit\|postMessage | ✅ (搜索 `postMessage({ method: \"close\" })`) | 类似 | ✅ | - |
| C7: useDark({ darkMode }) | ✅ (搜索 `import useDark` 调用处) | ✅ | - | - |
| C8: body overflow 条件化 | ✅ (搜索 `body.style.overflow`) | ✅ | ✅ | ✅ |
| 触摸事件引用 useBridge | ✅ (搜索 `initTouchEventBridge` 整块替换) | ✅ (同左, 相同关键字) | - | - |

> **[P13/P19统一] 定位方式**: 上表中所有改造项均通过**搜索关键字**定位代码块 (见 Phase 3 关键字列表), 不使用硬编码行号。

**重点: index.vue 的 onMounted 改造 (L242-287):**
```diff
  onMounted(() => {
-   document.body.style.overflow = 'hidden';                              // C8
-   if (!appParams.value) appStore.fetchTokenName();                     // C2 (无条件)
+   // C8: SPA 模式才设置 body
+   if (window.parent !== window) document.body.style.overflow = 'hidden';
+   // C2: 传递 externalParams (NPM 模式从 props 来, SPA 从 URL 来)
+   const extParams = props.id ? { id: props.id, type: 'design',
+     onlyDesignShow: props.onlyDesignShow, disabled: props.disabled,
+     showGrid: props.showGrid } : null
+   if (!appParams.value) appStore.fetchTokenName(extParams);
    
    if (appParams.value.id) {
      definitionId.value = appParams.value.id;
+   } else if (props.id) {
+     // [NEW] NPM 模式: 从 props 获取 ID
+     definitionId.value = props.id;
    }
    onlyDesignShow.value = appParams.value.onlyDesignShow === 'true' ||
        appParams.value.onlyDesignShow === true;
+   
+   // [NEW] NPM 模式 props 覆盖
+   if (props.onlyDesignShow === true) onlyDesignShow.value = true
+   if (props.disabled === true) disabled.value = true

-   initFromUrl();                                                         // C7
+   initFromUrl(); // (useDark 内部已处理 prop 优先)
    setupMessageListener();

    queryDef(definitionId.value).then(res => { /* ... 不变 ... */ });
  })
```

---

### Phase 6: 配置双模式构建 (~预估 1h)

**步骤 6.1: 创建 .env.library**
```bash
# .env.library (新增)
VITE_APP_ENV=library
VITE_APP_BASE_API=
VITE_URL_PREFIX=/
BUILD_MODE=library
```

**步骤 6.2: 修改 vite.config.js**
```javascript
// ====== 改造后的 vite.config.js 核心部分 ======
import { defineConfig, loadEnv } from 'vite'
import path from 'path'
import createVitePlugins from './vite/plugins'

export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd())
  const isLib = env.BUILD_MODE === 'library'  // [KEY] 判断构建模式

  return {
    base: isLib ? undefined : './',  // SPA 需要 base='./', Library 不需要

    plugins: createVitePlugins(env, command === 'build', isLib),

    build: isLib ? {
      // ====== Library 模式构建配置 ======
      lib: {
        entry: path.resolve(__dirname, 'src/index.js'),
        name: 'WarmFlowDesigner',
        formats: ['es', 'umd'],
        fileName: (format) => `warm-flow-designer.${format}.js`,
      },
      rollupOptions: {
        // [P17修复] 外部化策略: 仅宿主必须提供的依赖列入 external (与 P5 dependencies/peerDependencies 分离一致)
        //   - vue / element-plus / axios / pinia → external (peerDep, 由宿主安装)
        //   - @logicflow/* / @form-create/* / file-saver / @element-plus/icons-vue → 打入包内 (bundled dep, 见 P5)
        external: [
          'vue', 'element-plus', 'axios', 'pinia',
          // 注意: 以下依赖已在 dependencies 中 bundled, 不再列入 external:
          //   @logicflow/core, @logicflow/extension, @element-plus/icons-vue,
          //   @form-create/designer, @form-create/element-ui, file-saver
        ],
        output: {
          globals: {
            // [P17修正] 仅保留 external 列表中的依赖的 globals 映射
            vue: 'Vue',
            'element-plus': 'ElementPlus',
            axios: 'axios',
            pinia: 'Pinia',
            // 以下已移入 bundled, 不需要 UMD globals:
            // '@logicflow/core': 'LogicFlow',          (bundled)
            // '@logicflow/extension': 'LogicFlowExtension', (bundled)
            // '@element-plus/icons-vue': 'ElementPlusIconsVue', (bundled)
          },
          },
        },
      },
      cssCodeSplit: false,  // [P6] CSS 打包到单个文件
      // 注意: Vite Library 模式下, CSS 会自动提取为独立 .css 文件(不内联到 JS),
      //       输出路径为 dist-lib/style.css (与 package.json exports["./style"] 对应)
    } : {
      // ====== SPA 模式构建配置 (完全保持原有) ======
      chunkSizeWarningLimit: 3000,
      rollupOptions: {
        output: {
          chunkFileNames: 'js/[name]-[hash].js',
          entryFileNames: 'js/[name]-[hash].js',
          assetFileNames: '[ext]/[name]-[hash].[ext]',
        },
      },
    },

    resolve: {
      alias: { '~': path.resolve(__dirname, './'), '@': path.resolve(__dirname, './src') },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    },

    server: {
      port: 8083, host: true, open: true,
      proxy: { '/dev-api': { target: 'http://localhost:8080', changeOrigin: true, rewrite: (p) => p.replace(/^\/dev-api/, '') } },
    },

    css: { /* 保持原有 postcss 配置不变 */ },
  }
})
```

**步骤 6.3: 修改 package.json**
```diff
  {
-   "name": "Warm-Flow",
+   "name": "@warm-flow/vue-designer",    // [CHANGED] scoped package name
    "version": "1.9.0",                     // [CHANGED] 大版本号升级
    "description": "Warm-Flow 流程设计器 Vue 组件库",
    "type": "module",
    "scripts": {
-     "dev": "vite",
-     "build:prod": "vite build",
-     "preview": "vite preview"
+     "dev": "vite",
+     "build:prod": "vite build",                           // SPA 构建 (不变)
+     "build:lib": "vite build --mode library",                       // [NEW] Library 构建 (使用同一 vite.config.js, 通过 env.BUILD_MODE 切换)
+     "preview": "vite preview"
    },
+   "main": "./dist-lib/warm-flow-designer.umd.js",        // [NEW] UMD 入口
+   "module": "./dist-lib/warm-flow-designer.es.js",        // [NEW] ES Module 入口
+   "exports": {                                              // [NEW] 子路径导出
+     ".": {
+       "import": "./dist-lib/warm-flow-designer.es.js",
+       "require": "./dist-lib/warm-flow-designer.umd.js",
+     },
+     "./style": "./dist-lib/style.css",                      // 独立样式文件
+   },
+   "files": ["dist-lib"],                                    // [NEW] npm 发布范围
+   "publishConfig": { "access": "public" },                   // [NEW] scoped 包必须
+   // [P5修复] dependencies vs peerDependencies 分离原则:
+   //   - dependencies: 库内部使用、不期望宿主提供的依赖 (bundled)
+   //   - peerDependencies: 必须由宿主提供、避免版本冲突的依赖
    "dependencies": {
-     // [UNCHANGED] 所有 runtime deps 保持不变 (作为 bundled 或 peerDep)
      "@element-plus/icons-vue": "2.3.1",
      "@form-create/designer": "^3.2.6",       // 表单设计器 (bundled, 小众功能)
      "@form-create/element-ui": "^3.2.10",    // [P7] FcDesigner 依赖 (bundled)
      "@logicflow/core": "^2.1.11",            // [P5修正] bundled — 版本敏感, 宿主可能不装
      "@logicflow/extension": "^2.1.15",       // 同上
      "file-saver": "2.0.5"                     // 文件保存工具 (bundled, 无外部依赖风险)
-     "axios": "0.27.2",
-     "element-plus": "2.4.3",
-     "file-saver": "2.0.5",
-     "pinia": "2.1.7",
-     "vue": "3.3.9",
-     "vue-router": "4.2.5"
    },
+   "peerDependencies": {                                      // [NEW] 宿主必须安装
+     "vue": "^3.3.0",                                         // Vue 3 运行时
+     "element-plus": "^2.4.0",                                // UI 组件库
+     "axios": ">=0.27.0",                                     // HTTP 客户端
+     "pinia": ">=2.1.0"                                       // 状态管理
+   },
+   "peerDependenciesMeta": {                                  // [NEW] 标记可选 peerDep
+     "axios": { "optional": true },                           // 如果自定义了 requestInterceptor 可能不需要默认 axios
+     "pinia": { "optional": true }
+   },
    "devDependencies": { /* 不变 */ }
  }
```

---

### Phase 7: 创建库入口 & 验证 (~预估 1h)

**步骤 7.1:** 确认 `src/index.js` 正确导出所有组件 (见 3.1 节)

**步骤 7.2:** 执行 Library 构建
```bash
npm run build:lib
```

**预期输出:**
```
dist-lib/
├── warm-flow-designer.es.js      # ES Module 格式 (~200KB gzipped?)
├── warm-flow-designer.umd.js      # UMD 格式
└── style.css                      # 提取的样式 (~50KB)
```

**步骤 7.3:** 创建测试项目验证
```bash
mkdir test-npm-integration && cd test-npm-integration
npm init vue@latest  # 或使用现有 Vue 3 项目
npm install ../warm-flow-ui  # 本地链接
```

**测试代码:**
```vue
<template>
  <WarmFlowDesigner
    :id="'test-def-001'"
    type="design"
    :api-base="'/api'"
    :get-token="() => localStorage.getItem('token')"
    @save="handleSave"
    @close="handleClose"
  />
</template>

<script setup>
import { WarmFlowDesigner } from '@warm-flow/vue-designer'
import '@warm-flow/vue-designer/style.css'  // 引入样式!

function handleSave(data) { console.log('saved!', data) }
function handleClose() { console.log('closed!') }
</script>
```

---

### Phase 8: 双向兼容验证 (~预估 1h)

**验证清单:**

| # | 验证项 | 命令/方式 | 预期结果 |
|---|--------|----------|---------|
| V1 | SPA `dev` 模式启动 | `npm run dev` | 与改造前完全一致 |
| V2 | SPA `build:prod` 构建 | `npm run build:prod` | dist/ 输出正常 |
| V3 | SPA iframe 嵌入 | hh-vue 项目访问 | 行为与改造前一致 |
| V4 | Library 构建 | `npm run build:lib` | dist-lib/ 生成无误 |
| V5 | NPM import 组件 | 测试项目中 `<WarmFlowDesigner>` | 组件正常渲染 |
| V6 | NPM API 请求 | 测试项目打开设计器 | API 请求到达后端 |
| V7 | NPM Token 传递 | 测试项目登录后打开 | 认证头正确携带 |
| V8 | NPM 暗黑模式 | `:dark-mode="true"` | 主题切换正常 |
| V9 | NPM save/close 事件 | 点击保存按钮 | `@save`/`@close` 触发 |
| V10 | NPM 多实例 | 页面放 2 个设计器 | 互不干扰 |
| V11 | Element Plus 冲突 | 宿主已有 EP | 无双重注册警告 |
| V12 | 样式隔离 | 宿主页面样式 | 设计器样式不被污染 |

---

## 第六章 SPA 渐进淘汰路线图

### 时间线规划

```
Phase A: 现在 ────────────────────── 双轨并行发布
│
│  ✓ npm publish @warm-flow/vue-designer v1.9.0
│  ✓ SPA dist/ 继续随 warm-flow-plugin 发布
│  ✓ 文档引导 Vue 3 用户使用 NPM 方式
│  ✓ 其他框架继续 iframe (零感知)
│
│
Phase B: 6-12 月后 ──────────────── 标记 SPA 为 Legacy
│
│  ⚠ 文档中 iframe 标注 "[Legacy Mode]"
│  ⚠ warm-flow-plugin 仍然内嵌 SPA 资源
│  ⚠ 新特性优先 NPM 方式上线
│  ⚠ SPA 模式的 Bug 仅修 Critical, 不再增强
│
│
Phase C: 18 月后 ───────────────── 移除 SPA
│
│  ✗ 删除以下文件 (< 15分钟工作量):
│     ├─ src/main.js                        (SPA入口)
│     ├─ src/App.vue                         (SPA根组件)
│     ├─ .env.development                    (SPA开发环境)
│     ├─ .env.production                     (SPA生产环境)
│     └─ vite.plugins 中的 SPA 分支
│
│  ✗ 删除/归档 warm-flow-plugin-vue3-ui 模块
│  ✗ 删除 WarmFlowUiConfig.java
│  ✗ package.json 中移除 build:prod 脚本
│  ✗ 非 Vue 3 用户引导: CDN iframe 或自建网关
│
│  最终架构:
│  @warm-flow/designer (纯 NPM/WC 分发)
│  ├── vue3/     (Vue 3 组件)
│  ├── wc/       (Web Component)
│  └── react/    (可选 React Hook wrapper)
```

### 移除 SPA 时的完整删除清单

| # | 删除项 | 类型 | 耗时 | 影响 |
|---|--------|------|------|------|
| D1 | `src/main.js` | 文件 | 10s | SPA 入口消失 |
| D2 | `src/App.vue` | 文件 | 10s | SPA 根组件消失 |
| D3 | `.env.development` | 文件 | 10s | SPA 开发配置消失 |
| D4 | `.env.production` | 文件 | 10s | SPA 生产配置消失 |
| D5 | `vite.config.js` SPA 分支 | 代码 | 5min | 仅保留 Library 构建 |
| D6 | `package.json` build:prod | 配置 | 10s | SPA 构建脚本移除 |
| D7 | `warm-flow-plugin-vue3-ui/` | 目录 | 1min | 后端插件不再内嵌UI |
| D8 | `WarmFlowUiConfig.java` | 文件 | 1min | 静态资源映射移除 |
| D9 | `vite/plugins/` SPA 插件 | 代码 | 5min | auto-import/svg-icons/compression 移除 |
| D10 | `hh-vue/warm_chart.vue` iframe | 代码 | 5min | 宿主改为 NPM 组件方式 |

**总计: < 15 分钟纯删除工作, 零风险 (因为 NPM 方式已稳定运行 6+ 个月)**

---

## 第七章 风险控制与注意事项

### 7.1 必须遵守的原则

1. **每步独立可验证**: 完成 Phase N 后, SPA `dev` + `build:prod` 必须正常运行
2. **绝不一次性重写**: core/ 层建立后, 旧代码逐步委托, 不做 Big Bang
3. **保持 git 友好**: 每个Phase一个 commit, 方便回滚
4. **测试覆盖**: 核心路径 (API请求/Token传递/保存/关闭) 每步验证

### 7.2 常见风险与应对

| 风险 | 概率 | 应对措施 | 状态 |
|------|------|---------|------|
| Element Plus 双份注册 | 中 | `elementPlusInstalled` 配置项跳过内置 `app.use(ElementPlus)` | 待验证 |
| LogicFlow 版本冲突 | 低 | **已 bundled** (P5修正), 不放入 external, 版本锁定 `^2.1.11` | ✅ 已解决 |
| CSS 污染宿主 | 低-中 | 已使用 `--wf-*` CSS 变量 + scoped style; 建议改造后做一次全局类名扫描 | 待验证 |
| Pinia 多实例冲突 | 低 | **已改进** (P8修正), useDark 改为每实例独立 isDark ref | ✅ 已缓解 |
| SVG 图标 Library 不可用 | 高→低 | **已解决** (P1修正), Library 模式使用 svg-inline 插件内联 data URI + 文字 fallback | ✅ 已解决 |
| Vue 版本不匹配 | 中 | peerDependencies 严格限定 `^3.3.0`, UMD 模式提供 globals 映射 | 待验证 |
| FcDesigner 全局注册失效 | 中 | **已解决** (P7修正), 作为 bundled 依赖 + 包装组件内 defineAsyncComponent 局部注册 | ✅ 已解决 |
| ESM 循环引用 | 中 | **已解决** (P3修正), config/request 之间使用 registerRequestInit 注册式解耦 | ✅ 已解决 |
| 暗黑模式多实例冲突 | 中 | **已解决** (P8修正), sharedIsDark 单例 → 每调用独立 isDark ref + _instances 广播列表 | ✅ 已解决 |
| CSS 独立文件提取失败 | 低 | Vite Library 模式自动提取 CSS (cssCodeSplit:false), 输出 dist-lib/style.css | ✅ 已确认可行 |
| Element Plus 样式重复引入 | 低-中 | **[P21补充]** 见下方详细说明和应对方案 | ⚠ 需用户注意 |

---

#### [P21补充] Element Plus CSS 重复引入问题分析

**背景:**

| 模式 | Element Plus 样式引入方式 |
|------|--------------------------|
| **SPA** | `main.js` 中 `import 'element-plus/dist/index.css'` — 全量引入 (~300KB 未压缩) |
| **NPM Library** | Vite 构建自动提取 → `dist-lib/style.css` (含 EP 子集样式) |

**问题场景:**
```
宿主项目已安装 Element Plus 并在 main.js 中:
  import 'element-plus/dist/index.css'    // 宿主: 引入完整 EP 样式

又使用 warm-flow NPM 组件:
  import '@warm-flow/vue-designer/style'   // warm-flow: 也包含 EP 样式子集
                                              → 样式重复! 选择器覆盖/优先级冲突风险
```

**style.css 内容构成:**

| 组成部分 | 预估体积 (gzipped) | 说明 |
|----------|-------------------|------|
| warm-flow 业务样式 | ~10-20KB | `.wf-*` / `--wf-*` 变量 / 设计器布局 |
| Element Plus 子集 | ~30-50KB | 仅设计器用到的组件样式 (el-dialog, el-form, el-button, el-table 等) |
| LogicFlow 内联样式 | ~5-10KB | 节点/边/工具栏样式 |

> **总计**: ~45-80KB gzipped (不含完整 Element Plus)

**推荐解决方案:**

```javascript
// 方案 A (推荐): 通过配置项控制是否注入 EP 样式
import WarmFlowPlugin from '@warm-flow/vue-designer'

app.use(WarmFlowPlugin, {
  baseURL: '/api',
  elementPlusInstalled: true,  // [P21] 告诉 warm-flow "宿主已有EP样式, 不要再注入"
  getToken: () => localStorage.getItem('token')
})
// 此时 import '@warm-flow/vue-designer/style' 只导入业务样式, 不包含 EP 子集

// 方案 B: 用户自行选择是否导入 style
// 只导入 JS (不含样式), 由宿主确保 EP 样式已加载:
import { WarmFlowDesigner } from '@warm-flow/vue-designer'
// 不 import style → 用户负责确保 EP 样式已存在
```

**Vite 配置联动 (实现 elementPlusInstalled):**

```diff
  // vite.config.js (Library 构建)
+ // 根据 .env.library 中 ELEMENT_PLUS_BUNDLED 变量决定
+ const bundleEpStyle = process.env.ELEMENT_PLUS_BUNDLED !== 'false'   // 默认打包
+
  build: {
    lib: { ... },
+   cssExtract: !bundleEpStyle,   // false = 打入 style.css; true = 不提取(由宿主提供)
  }
```

### 7.3 性能预期

> **[P15修正]** 以下数据为估算值, 实际体积取决于使用场景和 Tree-shaking 效果。
> LogicFlow 核心库本身 gzipped 约 120-150KB, Element Plus 部分样式约 50-80KB。

| 指标 | SPA 模式 (当前) | NPM 模式 (改造后) | 变化 |
|------|-----------------|-------------------|------|
| 首次加载体积 | ~800KB (gzipped ~200KB, 含全部功能) | ~250-400KB (gzipped, 按需加载单组件) | ↓ 更小 (Tree-shaking 去掉未使用的组件) |
| JS 主包体积 | ~500KB (gzipped ~130KB) | ~150-250KB (gzipped, 单组件+LogicFlow+业务逻辑) | ↓ 显著减小 (去掉路由/App壳/其他视图) |
| 运行时内存 | ~30MB | ~20-25MB | ↓ 略低 (按需加载) |
| 初始化速度 | ~1.5s (含 iframe 加载 + SPA 启动) | ~0.3-0.8s (组件直接渲染, 无 iframe) | ↑ 快 2-5x |
| 样式隔离 | iframe 天然隔离 | CSS Variables (`--wf-*`) + Scoped style | 等效 (需确认无全局类名污染) |

> **注意**: 如果用户同时使用 WarmFlowDesigner + WarmFlowChart 两个组件, 体积会叠加共享部分 (LogicFlow/Element Plus 只算一次)。

> **文档结束**
> 如有疑问请参考代码注释或联系维护者。

---

## 附录 A: 修订记录

### v1.0 → v1.1 (2026-04-20) — 15 项问题修复

> **审查人**: AI Code Review | **状态**: P1-P15 全部已修复 ✅

#### P0 阻塞级 (4项)

| 编号 | 问题 | 修复内容 |
|------|------|---------|
| **P1** | C11 SVG 图标: `import('virtual:svg-icons-register')` 运行时必崩 | svg-inline 插件方案 (Library 模式 SVG→data URI 内联 + fallback) |
| **P2** | `getToken(tokenNames[i]] )` 多余 `]`, 语法错误 | 删除多余字符 |
| **P3** | config.js 使用 ESM 不支持的 `require()` | 注册式解耦: `registerRequestInit()` 模式 |
| **P4** | `vite mode library` 命令不存在 | 改为 `vite build --mode library` |

#### P1 重要级 (4项)

| 编号 | 问题 | 修复内容 |
|------|------|---------|
| **P5** | dependencies/peerDependencies 冲突 | 彻底分离 bundled vs peerDep |
| **P6** | CSS 独立文件导出方案缺失 | 补充 Vite Library 自动提取说明 |
| **P7** | FcDesigner 全局注册未处理 | 新增 3.10.5 节 (bundled + defineAsyncComponent) |
| **P8** | useDark 共享单例多实例冲突 | 重写为每实例独立 isDark ref + _instances 广播 |

#### P2 完善级 (7项)

| 编号 | 问题 | 修复内容 |
|------|------|---------|
| **P9** | App.vue 无实现代码 | 新增完整改造前后对比 (~35行) |
| **P10** | install.js 无实现代码 | 新增完整 ~60 行 Vue Plugin 安装函数 |
| **P11** | form-design 只有 "微调" | 补充 index.vue/formCreate.vue 具体 diff |
| **P12** | WC 与实施计划脱节 | 加 Phase 2 标注, 明确本文档范围 |
| **P13** | 触摸事件硬编码行号 | 改为关键字搜索定位方式 |
| **P14** | .ts/.js 扩展名混用 | 已统一为 .js |
| **P15** | 性能数据偏乐观 | 修正为 ~250-400KB gzipped |

---

### v1.1 → v1.2 (2026-04-20) — 二审 6 项问题修复

> **审查人**: AI Code Review (第二轮) | **状态**: P16-P21 全部已修复 ✅

#### 阻塞级 (2项 — 会导致运行时错误)

| 编号 | 问题 | 修复内容 |
|------|------|---------|
| **P16** | useDark watch 回调引用已删除的 `sharedIsDark` 变量 — P8 遗漏 | 第821行 `sharedIsDark.value = val` → `isDark.value = val` |
| **P17** | vite.config.js external 含 LogicFlow/icons-vue, 与 P5 bundled 决策矛盾 | 从 external/globals 中移除所有 bundled 依赖; 新增分类注释说明 |

#### 重要级 (1项)

| 编号 | 问题 | 修复内容 |
|------|------|---------|
| **P18** | svg-inline 插件用 `process.cwd()` 定位图标目录不可靠 | 增加 `viteRootDir` 参数 + `_rootDir` 变量; `resolve(process.cwd(),...)` → `resolve(_rootDir,...)` |

#### 完善级 (3项)

| 编号 | 问题 | 修复内容 |
|------|------|---------|
| **P19** | Phase 5 表格仍用硬编码行号 (L245/L270 等10+个), 与 P13 矛盾 | 移除全部行号, 统一为 ✅ + 搜索关键字定位 |
| **P20** | 新增文件清单遗漏 `vite/plugins/svg-inline.js` (~35行) | 在 N3 后新增 N3.5 条目 |
| **P21** | Element Plus CSS 重复引入问题未讨论 | 新增完整分析: 场景/构成/方案A(配置控制)/方案B(自管)/Vite联动 |
