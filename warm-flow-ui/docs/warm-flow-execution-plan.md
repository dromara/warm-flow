# Warm-Flow UI 改造执行计划

> **版本**: v1.0 | **基于**: warm-flow-npm-refactoring-plan.md v1.2 (21项问题已修复)
> **核心目标**: 从「SPA + iframe」演进为 **NPM 组件包分发**, 同时兼容 Vue 2 和 Vue 3 用户

---

## 总览: 三阶段路线图

```
┌─────────────────────────────────────────────────────────────────┐
│                        改造总路线图                                │
│                                                                   │
│  阶段一 (本周)          阶段二 (1-2周)         阶段三 (后续)       │
│  ══════════           ══════════════        ════════════        │
│                                                                   │
│  ┌─────────────┐     ┌──────────────┐     ┌───────────────┐     │
│  │ Phase 0     │     │ Phase 1      │     │ Phase 2       │     │
│  │ 基础解耦     │ ──→ │ Vue3 NPM包   │ ──→ │ Vue2 兼容层    │     │
│  │ 核心抽象提取  │     │ 双模构建      │     │ WC / vue2包    │     │
│  └──────┬──────┘     └──────┬───────┘     └───────┬───────┘     │
│         │                   │                      │             │
│         ▼                   ▼                      ▼             │
│  src/core/ 创建         npm publish           @warm-flow/        │
│  config/request/auth    @warm-flow/            vue-designer-vue2 │
│  无行为变更!            vue-designer v1.9     或 Web Component   │
│                       (Vue3 only)            (全框架兼容)        │
│                                                                   │
│  ✅ SPA 模式不变        ✅ Vue3用户可用NPM     ✅ Vue2用户也可用  │
│  ✅ iframe照常工作       ✅ SPA继续发布         ✅ React/Angular   │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

### 关键决策: 为什么先做 Vue 3 NPM，再做 Vue 2？

| 因素 | 分析 | 结论 |
|------|------|------|
| **现有代码基础** | warm-flow-ui 已是纯 Vue 3 项目 | 先出 Vue 3 NPM 包成本最低 (~8h) |
| **Vue 2 用户现状** | 通过 iframe 使用 SPA，零改动 | iframe 方式继续服务 Vue 2 用户 |
| **Vue 2 NPM 成本** | 需要维护两套构建或用 WC 包装 | 作为 Phase 2 后续迭代 |
| **市场趋势** | Vue 3 已稳定 3 年+, 新项目几乎都是 Vue 3 | 优先满足主流需求 |

---

## 阶段一: Phase 0 — 核心解耦层 (预估 2h)

**原则**: 纯新增文件，不修改任何现有代码。SPA 行为 100% 不变。

### Step 0.1: 创建目录和基础配置 (~15 min)

```bash
cd warm-flow/warm-flow-ui
mkdir -p src/core
```

### Step 0.2: 创建 `src/core/config.js` (~80行)

这是整个改造的**配置中枢**，所有外部注入的入口。

```javascript
// src/core/config.js
// 功能: 单例配置管理 + 默认值 + 合并逻辑 + Library 模式检测

let currentConfig = {}

export const defaultConfig = {
  // API 相关
  baseURL: '',
  urlPrefix: '/',
  timeout: 10000,

  // 认证相关
  getToken: null,              // 外部 Token 获取函数
  tokenName: 'Authorization',
  tokenNameList: [],

  // 行为相关
  onClose: null,               // NPM 模式关闭回调 (替代 postMessage)
  darkMode: 'auto',            // 'auto' | true | false

  // 扩展
  customNodeTypes: {},
  requestInterceptor: null,
  elementPlusInstalled: false, // 宿主是否已安装 EP (控制样式注入)
}

export function setConfig(userConfig) {
  currentConfig = { ...defaultConfig, ...userConfig }
  syncInitDeps(currentConfig)
  return currentConfig
}

export function getConfig() { return currentConfig }

/** 判断当前是否在 NPM/Library 模式下运行 */
export function isLibraryMode() { return !!currentConfig._libraryMode }

/** 初始化入口 (由包装组件或 install 插件调用) */
export function initWarmFlow(config = {}) {
  const merged = { ...defaultConfig, ...config, _libraryMode: true }
  setConfig(merged)
  return merged
}

// ===== 注册式解耦 (解决 ESM 循环引用) =====
let _requestInitFn = null
export function registerRequestInit(initFn) { _requestInitFn = initFn }
function syncInitDeps(config) {
  if (_requestInitFn) _requestInitFn(config)
}
```

### Step 0.3: 创建 `src/core/auth.js` (~55行)

```javascript
// src/core/auth.js
// 功能: TokenProvider 接口 + 自定义 Token 注入 + 默认 localStorage 实现

import { getConfig } from './config.js'

let customGetToken = null

/** [API] 设置外部 Token 获取函数 (替代默认 localStorage 读取) */
export function setCustomGetToken(fn) {
  customGetToken = fn
}

/**
 * 获取当前 Token
 * 优先级: 自定义函数 > localStorage(Warm-Token / Authorization)
 *
 * @param {string} tokenName - Token header 名称
 * @returns {string|null}
 */
export function getToken(tokenName) {
  // 1. 优先使用外部注入的自定义函数
  if (customGetToken) {
    const token = customGetToken(tokenName)
    if (token !== undefined && token !== null) return token
  }

  // 2. 默认: 从 localStorage 读取 (SPA 模式的原始行为)
  const config = getConfig()
  const names = config?.tokenNameList || ['Authorization', 'Warm-Token']
  for (const name of Array.isArray(tokenName) ? [tokenName] : names) {
    if (!tokenName || tokenName === name) {
      const token = localStorage.getItem(name)
      if (token && token !== 'undefined') return token
    }
  }
  return null
}
```

### Step 0.4: 创建 `src/core/request.js` (~120行)

```javascript
// src/core/request.js
// 功能: axios 实例工厂 + 动态 baseURL/urlPrefix + 拦截器注册

import axios from 'axios'
import { getConfig } from './config.js'
import { registerRequestInit } from './config.js'
import { getToken } from './auth.js'

// 向 config 注册自身初始化函数 (解决循环引用)
registerRequestInit(initRequestConfig)

let axiosInstance = null

/**
 * 创建/获取 axios 实例
 * @param {Object} overrides - 可选覆盖配置
 * @returns {AxiosInstance}
 */
export function createRequest(overrides = {}) {
  const config = getConfig()

  if (!axiosInstance) {
    axiosInstance = axios.create({
      baseURL: config.baseURL || '',
      timeout: config.timeout || 10000,
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
    })

    // 请求拦截器: 自动附加 Token
    axiosInstance.interceptors.request.use(
      (cfg) => {
        // 使用自定义拦截器 (如果提供)
        if (config.requestInterceptor) {
          cfg = config.requestInterceptor(cfg)
        }

        // 附加 Token 到 header
        const tokenNames = config.tokenNameList || ['Authorization', 'Warm-Token']
        for (const name of tokenNames) {
          const token = getToken(name)
          if (token) {
            cfg.headers[name] = getToken(name)
          }
        }

        return cfg
      },
      (error) => Promise.reject(error)
    )

    // 响应拦截器: 统一错误处理 (可扩展)
    axiosInstance.interceptors.response.use(
      (response) => response.data,
      (error) => {
        if (error.response?.status === 401) {
          console.warn('[warm-flow] 401 未授权, 请检查 Token 配置')
        }
        return Promise.reject(error)
      }
    )
  }

  return axiosInstance
}

/** 由 config.setConfig() 级联调用 — 更新 baseURL 等运行时参数 */
function initRequestConfig(config) {
  if (axiosInstance && config.baseURL !== undefined) {
    axiosInstance.defaults.baseURL = config.baseURL
  }
}

/** 获取 API URL 前缀 (供 api/*.js 使用) */
export function getUrlPrefix() {
  return getConfig().urlPrefix || '/'
}

export { axiosInstance }
```

### Step 0.5: 创建 `src/core/store.js` (~20行)

```javascript
// src/core/store.js
// 功能: Pinia 多实例管理 — 支持 NPM 模式下多实例共存

import { createPinia } from 'pinia'

let piniaInstance = null

/** 获取或创建 Pinia 实例 */
export function getPinia() {
  if (!piniaInstance) {
    piniaInstance = createPinia()
  }
  return piniaInstance
}

/** 重置 Pinia (用于测试或卸载) */
export function resetPinia() {
  piniaInstance = null
}
```

### Step 0.6: 创建 `src/core/useAppParams.js` (~40行)

```javascript
// src/core/useAppParams.js
// 功能: 参数获取的双源合并 (URL 参数 + 外部 Props)

import { ref, computed } from 'vue'
import { isLibraryMode } from './config.js'

/**
 * 合并 URL 参数和外部 props 为统一的 appParams
 *
 * @param {Object} externalParams - 来自 props 的参数 (NPM 模式)
 * @returns {{ appParams: import('vue').Ref<Object>, fetchAppParams: Function }}
 */
export function useAppParams(externalParams = null) {
  const urlParams = ref(null)

  /** 从 URL 解析参数 (SPA 模式使用) */
  function parseUrlParams() {
    if (typeof window === 'undefined') return {}
    const searchParams = new URLSearchParams(window.location.search)
    const params = {}
    for (const [key, value] of searchParams.entries()) {
      params[key] = value
    }
    return params
  }

  /** 获取合并后的参数 */
  const appParams = computed(() => {
    // NPM 模式: 优先使用外部传入参数
    if (isLibraryMode() && externalParams) {
      return { ...externalParams }
    }
    // SPA 模式: 从 URL 解析 (延迟解析, 首次访问时才解析)
    if (!urlParams.value) {
      urlParams.value = parseUrlParams()
    }
    return urlParams.value
  })

  /** 手动刷新 URL 参数 (SPA 模式下由 store/app.js 调用) */
  function refreshUrlParams(additional = {}) {
    const fresh = parseUrlParams()
    urlParams.value = { ...fresh, ...additional }
  }

  return { appParams, refreshUrlParams }
}
```

### Step 0.7: 验证 — 必须通过!

```bash
cd warm-flow/warm-flow-ui
npm run dev          # → SPA 开发模式正常启动, 控制台无报错
npm run build:prod   # → SPA 构建成功, dist/ 正常输出
```

> **验收标准**: core/ 目录下的文件存在但未被任何地方 import, 对 SPA 运行时零影响。

---

## 阶段二: Phase 1 — Vue 3 NPM 包 + 双模构建 (预估 6h)

### Step 1.1: 现有代码逐步委托到 core/ 层 (~2h)

**顺序很重要** — 按依赖链从底层到上层逐个改造:

#### 1.1a: `utils/auth.js` → 委托给 `core/auth.js`

```diff
  // utils/auth.js (原有代码保留作为 fallback)
+ // [委托模式] 优先使用 core/auth.js 的增强实现
+ export { getToken, setCustomGetToken } from '@/core/auth.js'
+
+ // 以下为原有 localStorage 实现 (保留不删, core 层也会调用它)
  export function getTokenFromStorage(tokenName) {
    // ... 原有实现 ...
  }
```

#### 1.1b: `utils/request.js` → 委托给 `core/request.js`

```diff
  // utils/request.js
+ // [委托模式] 导出 core 层的工厂方法供 api/*.js 使用
+ export { createRequest, getUrlPrefix } from '@/core/request.js'

  // 保留原有的 service/request 实例 (SPA 内部使用)
  const service = axios.create({ /* ...原有配置... */ })
  export default service
```

#### 1.1c: `store/app.js` → fetchTokenName 支持外部参数

```diff
  // store/app.js
+ import { useAppParams } from '@/core/useAppParams.js'

  export const useAppStore = defineStore('app', {
    // ...

+   actions: {
-     fetchTokenName() {
+     fetchTokenName(externalParams = null) {   // [C2] 支持外部参数注入
+       const { appParams, refreshUrlParams } = useAppParams(externalParams)
+
+       // 合并参数: 外部参数 > URL 参数 > 默认值
+       if (externalParams) {
+         refreshUrlParams(externalParams)
+       }

        // ... 原有从 URL 取参数的逻辑不变 ...
        // 只是把 new URLSearchParams(window.location.search)
        // 替换为 appParams.value 的读取
      }
    }
  })
```

#### 1.1d: `api/*.js` → urlPrefix 动态化

所有 `api/` 目录下的文件中, 将硬编码的前缀替换为动态读取:

```diff
  // api/flowDefinition.js (示例)
+ import { getUrlPrefix } from '@/core/request.js'

- export function queryDef(id) { return request({ url: '/flowable/definition/' + id }) }
+ export function queryDef(id) { return request({ url: getUrlPrefix() + 'flowable/definition/' + id }) }
```

> **注意**: 如果原代码中使用的是 `VITE_URL_PREFIX` 环境变量, 这里需要做一层映射:
> ```javascript
> // SPA 模式: getUrlPrefix() 返回 import.meta.env.VITE_URL_PREFIX (即 '../')
> // NPM 模式: getUrlPrefix() 返回 '/' (由 config.urlPrefix 控制)
> ```

#### 1.1e: `composables/useDark.js` → 多实例安全

详见 v1.2 方案文档 P8 修复后的代码:
- `sharedIsDark` 单例 → 每次调用返回独立 `isDark` ref
- `_instances` 回调列表实现 SPA postMessage 广播同步

#### 1.1f: `plugins/cache.js`, `plugins/modal.js` → 增加 named export

```diff
  // plugins/cache.js
  // 原有全局注册方式保持不变 (SPA)
+ // [NEW] 导出 composable 函数供 NPM 模式使用
+ export function useCache() {
+   return {
+     setCache: (...args) => $cache.setCache(...args),
+     getCache: (...args) => $cache.getCache(...args),
+   }
+ }
```

**每步验证**:
```bash
npm run dev          # 开发模式正常
npm run build:prod   # SPA 构建成功
# 浏览器打开 → 流程设计器功能正常 (设计/查看/保存/关闭)
```

---

### Step 1.2: 抽取触摸事件桥接 Composable (~1h)

index.vue 和 flowChart.vue 中各有 ~200 行重复的触摸/Pointer Events 代码。

**新建**: `src/composables/useTouchEventBridge.js` (~250行)

```javascript
// src/composables/useTouchEventBridge.js
// 功能: 统一的触摸事件桥接, 解决移动端 Pointer Events 兼容性

export function useTouchEventBridge(containerRef, options = {}) {
  // setupPointerEventCapture(el, options)  — Pointer Events → Touch 转换
  // initTouchEventBridge(containerEl)     — 初始化绑定到容器元素
  // destroyTouchEventBridge()             — 清理所有监听器
  // markDrawerOpen() / markDrawerClosed() — 抽屉状态标记 (替代 window.xxx)
}
```

**改造 index.vue 和 flowChart.vue**:
- 删除内联的触摸事件代码块 (~197 行 + ~207 行)
- 替换为 `useTouchEventBridge(containerRef.value)` 调用

**关键字定位** (不用行号):
```
搜索并删除: function initTouchEventBridge (整块函数)
搜索并删除: function setupPointerEventCapture (整块函数)
搜索并删除: window._markDrawerOpen / window._markDrawerClosed (全部出现)
新增导入: import { useTouchEventBridge } from '@/composables/useTouchEventBridge'
```

**验证**: 手机/平板真机测试触摸交互 (节点点击/拖拽/双指缩放/长按菜单)

---

### Step 1.3: 视图组件解耦 C1-C8 (~1.5h)

对 4 个视图组件进行最小改动, 使其能脱离 URL/window 依赖运行。

#### 改造矩阵

| 改造项 | index.vue | flowChart.vue | form-design/index.vue | formCreate.vue |
|--------|-----------|---------------|---------------------|----------------|
| **defineProps** (id/type/darkMode/onClose) | ✅ | ✅ | ✅ | ✅ |
| **C2** fetchTokenName(externalParams) | ✅ | ✅ | ✅ | ✅ |
| **C6** close() emit\|postMessage 双模式 | ✅ | ✅ | ✅ | - |
| **C7** useDark({ darkMode }) prop 优先 | ✅ | ✅ | - | - |
| **C8** body overflow 条件化 | ✅ | ✅ | ✅ | ✅ |
| **触摸事件** useBridge 引用 | ✅ | ✅ | - | - |

#### index.vue onMounted 改造示例 (核心改动):

```diff
  onMounted(() => {
-   document.body.style.overflow = 'hidden';                    // C8: 无条件设置
+   if (window.parent !== window)                              // C8: 仅 SPA 模式
+     document.body.style.overflow = 'hidden';

-   appStore.fetchTokenName();                                 // C2: 仅从 URL
+   const extProps = props.id ? {                              // C2: 合并 props
+     id: String(props.id),
+     type: props.type || 'design',
+   } : null;
+   appStore.fetchTokenName(extProps);

+   if (props.id) definitionId.value = props.id;               // [NEW] NPM 模式 ID 来源

-   initFromUrl();                                            // C7: 仅 URL/postMessage
+   initFromUrl();                                             // C7: useDark 内部已处理 prop 优先

    // ... 其余业务逻辑完全不变 ...
  })

+ // [NEW] 定义 props (NPM 模式注入)
+ const props = defineProps({
+   id: { type: [String, Number], default: null },
+   type: { type: String, default: 'design' },
+   onClose: { type: Function, default: null },
+   darkMode: { type: [String, Boolean], default: 'auto' },
+   onlyDesignShow: { type: [Boolean, String], default: null },
+   disabled: { type: Boolean, default: false },
+ })
+ defineEmits(['save', 'close', 'error'])
```

---

### Step 1.4: 创建包装组件 (~1h)

4 个包装组件定义统一的对外 API, 是 NPM 用户直接使用的组件。

#### `src/components/WarmFlowDesigner.vue` (~60行)

```vue
<template>
  <flow-design-index
    v-bind="$attrs"
    :on-close="props.onClose"
    :dark-mode="props.darkMode"
    @save="(data) => emit('save', data)"
    @close="emit('close')"
    @error="(err) => emit('error', err)"
  >
    <template #toolbar><slot name="toolbar" /></template>
    <template #header><slot name="header" /></template>
  </flow-design-index>
</template>

<script setup>
import FlowDesignIndex from '../views/flow-design/index.vue'

defineOptions({ name: 'WarmFlowDesigner' })

const props = withDefaults(defineProps({
  id: { type: [String, Number], default: null },
  type: { type: String, default: 'design' },
  onClose: { type: Function, default: null },
  darkMode: { type: [String, Boolean], default: 'auto' },
  onlyDesignShow: { type: [Boolean, String], default: undefined },
  disabled: { type: Boolean, default: undefined },
  showGrid: { type: Boolean, default: undefined },
}), {})

const emit = defineEmits(['save', 'close', 'error'])
</script>
```

#### 其他 3 个包装组件 (各 ~40 行):

| 组件名 | 内部引用视图 | 特殊 props |
|--------|------------|-----------|
| `WarmFlowChart.vue` | views/flow-design/flowChart.vue | id, instanceId, taskId, readOnly |
| `WarmFormDesigner.vue` | views/form-design/index.vue | id, onClose |
| `WarmFormCreate.vue` | views/form-design/formCreate.vue | id, taskId, definitionId |

---

### Step 1.5: 配置 Vite 双模式构建 (~30min)

#### 新建 `.env.library`

```bash
# .env.library
VITE_APP_ENV=library
VITE_APP_BASE_API=
VITE_URL_PREFIX=/
BUILD_MODE=library
ELEMENT_PLUS_BUNDLED=true
```

#### 修改 `vite.config.js` (关键变更)

```javascript
// vite.config.js — 在文件顶部添加:
export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd())
  const isLib = env.BUILD_MODE === 'library'  // ← 核心: 构建模式判断

  return {
    base: isLib ? undefined : './',           // SPA 需要 './', Library 不需要

    plugins: createVitePlugins(env, command === 'build', isLib),

    build: isLib ? {
      lib: {
        entry: path.resolve(__dirname, 'src/index.js'),
        name: 'WarmFlowDesigner',
        formats: ['es', 'umd'],
        fileName: (format) => `warm-flow-designer.${format}.js`,
      },
      rollupOptions: {
        external: [
          'vue', 'element-plus', 'axios', 'pinia',
          // bundled 依赖 (不列入 external):
          // @logicflow/core, @logicflow/extension, @form-create/*, file-saver
        ],
        output: {
          globals: { vue: 'Vue', 'element-plus': 'ElementPlus',
                     axios: 'axios', pinia: 'Pinia' },
        },
      },
      cssCodeSplit: false,
    } : {
      // SPA 构建配置 (完全保持原有!)
      chunkSizeWarningLimit: 3000,
      rollupOptions: { /* ... */ },
    },

    resolve: { /* 保持不变 */ },
    server: { /* 保持不变 */ },
    css: { /* 保持不变 */ },
  }
})
```

#### 修改 `vite/plugins/index.js` (插件条件化)

```diff
  export default function createVitePlugins(viteEnv, isBuild = false, isLib = false) {
      const vitePlugins = [vue()]

-     vitePlugins.push(createAutoImport())
-     vitePlugins.push(createSetupExtend())
-     vitePlugins.push(createSvgIcon(isBuild))
-     vitePlugins.push(createCompression(isBuild))

+     if (!isLib) {
+         // SPA 模式: 使用全套插件
+         vitePlugins.push(createAutoImport())
+         vitePlugins.push(createSetupExtend())
+         vitePlugins.push(createSvgIcon(isBuild))       // SVG sprite (SPA 用)
+         vitePlugins.push(createCompression(isBuild))    // Gzip (生产构建)
+     } else {
+         // Library 模式: 使用轻量替代
+         vitePlugins.push(createSvgInline(isBuild, path.resolve('.')))  // [P1/P18] SVG inline
+     }

      return vitePlugins
  }
```

---

### Step 1.6: 更新 package.json (~20min)

```json
{
  "name": "@warm-flow/vue-designer",
  "version": "1.9.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build:prod": "vite build",
    "build:lib": "vite build --mode library",
    "preview": "vite preview"
  },
  "main": "./dist-lib/warm-flow-designer.umd.js",
  "module": "./dist-lib/warm-flow-designer.es.js",
  "exports": {
    ".": {
      "import": "./dist-lib/warm-flow-designer.es.js",
      "require": "./dist-lib/warm-flow-designer.umd.js"
    },
    "./style": "./dist-lib/style.css"
  },
  "files": ["dist-lib"],
  "publishConfig": { "access": "public" },
  "dependencies": {
    "@element-plus/icons-vue": "2.3.1",
    "@form-create/designer": "^3.2.6",
    "@form-create/element-ui": "^3.2.10",
    "@logicflow/core": "^2.1.11",
    "@logicflow/extension": "^2.1.15",
    "file-saver": "2.0.5"
  },
  "peerDependencies": {
    "vue": "^3.3.0",
    "element-plus": "^2.4.0",
    "axios": ">=0.27.0",
    "pinia": ">=2.1.0"
  },
  "peerDependenciesMeta": {
    "axios": { "optional": true },
    "pinia": { "optional": true }
  }
}
```

---

### Step 1.7: 创建库入口 `src/index.js` (~50行)

```javascript
// src/index.js — NPM 包的主入口
// 用户: import { WarmFlowDesigner } from '@warm-flow/vue-designer'

// ===== 组件导出 =====
export { default as WarmFlowDesigner } from './components/WarmFlowDesigner.vue'
export { default as WarmFlowChart } from './components/WarmFlowChart.vue'
export { default as WarmFormDesigner } from './components/WarmFormDesigner.vue'
export { default as WarmFormCreate } from './components/WarmFormCreate.vue'

// ===== Vue Plugin 安装方式 (可选) =====
export { default as install } from './core/install.js'

// ===== Composable (高级用法) =====
export { useDark } from './composables/useDark.js'
export { useCache } from './plugins/cache.js'
export { useModal } from './plugins/modal.js'

// ===== 配置 API =====
export { setConfig, getConfig, initWarmFlow, isLibraryMode } from './core/config.js'
export { setCustomGetToken, getToken } from './core/auth.js'
export { createRequest, getUrlPrefix } from './core/request.js'
```

---

### Step 1.8: 创建 `src/core/install.js` (~60行) — Vue Plugin

```javascript
// src/core/install.js
// 功能: app.use(WarmFlowPlugin, options) 全局安装

import { initWarmFlow } from './config.js'
import { setCustomGetToken } from './auth.js'
import { initRequestConfig } from './request.js'
import { getPinia } from './store.js'
import { registerRequestInit } from './config.js'

registerRequestInit(initRequestConfig)

function install(app, options = {}) {
  const config = initWarmFlow(options)

  if (options.getToken) setCustomGetToken(options.getToken)
  initRequestConfig(config)

  // Pinia 注册 (如果宿主未安装)
  if (!app.config.globalProperties.$pinia) {
    app.use(getPinia())
  }

  // 全局组件注册 (便捷使用, 用户也可以按需 import)
  app.component('WarmFlowDesigner', () =>
    import('../components/WarmFlowDesigner.vue')
  )
  app.component('WarmFlowChart', () =>
    import('../components/WarmFlowChart.vue')
  )

  app.provide('$warmFlowConfig', config)
}

export default install
```

---

### Step 1.9: 创建 SVG Inline 插件 (~35行)

```javascript
// vite/plugins/svg-inline.js
// Library 模式替代 vite-plugin-svg-icons 的内联方案

import { readFileSync, readdirSync } from 'fs'
import { resolve } from 'path'

export function createSvgInline(isBuild, viteRootDir) {
  const rootDir = viteRootDir || process.cwd()

  return {
    name: 'svg-inline',
    resolveId(id) {
      if (id === 'virtual:svg-icons-register' || id === '@/components/SvgIcon/svgicon')
        return id
    },
    load(id) {
      if (id === 'virtual:svg-icons-register')
        return '// svg-inline: no-op, icons loaded on-demand'
      if (id.endsWith('svgicon')) {
        const iconDir = resolve(rootDir, 'src/assets/icons/svg')
        const files = readdirSync(iconDir).filter(f => f.endsWith('.svg'))
        const imports = files.map(f => {
          const name = f.replace('.svg', '')
          const content = readFileSync(resolve(iconDir, f), 'utf-8')
            .replace(/"/g, "'").replace(/\n/g, '')
          return `'${name}': '${content}'`
        })
        return `export default { ${imports.join(',\n')} }`
      }
      return null
    }
  }
}
```

---

### Step 1.10: 构建验证 (~30min)

```bash
# 1. SPA 模式验证 (必须先过!)
npm run dev              # → 启动开发服务器, 验证所有功能
npm run build:prod       # → SPA 构建, dist/ 输出正常

# 2. Library 构建
npm run build:lib        # → dist-lib/ 应包含:
                          #   warm-flow-designer.es.js   (~150-250KB)
                          #   warm-flow-designer.umd.js   (~180-300KB)
                          #   style.css                   (~45-80KB)

# 3. 本地测试 (可选但推荐)
cd ..
mkdir test-npm && cd test-npm
npm init -y
# 安装本地链接 + 依赖
# 编写测试组件 (见下方)
```

#### 测试组件代码:

```vue
<!-- TestProject/src/App.vue -->
<template>
  <div class="test-container">
    <h2>WarmFlow NPM 集成测试</h2>

    <button @click="showDesigner = !showDesigner">
      {{ showDesigner ? '隐藏' : '显示' }} 设计器
    </button>

    <WarmFlowDesigner
      v-if="showDesigner"
      :id="defId"
      type="design"
      :get-token="getToken"
      @save="onSave"
      @close="showDesigner = false"
      style="width: 100%; height: 600px;"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { WarmFlowDesigner } from '@warm-flow/vue-designer'
import '@warm-flow/vue-designer/style.css'

const defId = ref('')
const showDesigner = ref(false)

function getToken() {
  return localStorage.getItem('token') || 'test-token'
}
function onSave(data) {
  console.log('流程保存:', JSON.stringify(data))
}
</script>
```

---

## 阶段三: Phase 2 — Vue 2 兼容与全框架支持 (后续迭代)

> **时机建议**: Phase 1 发布稳定后 1-2 个月, 收集用户反馈后再决定具体方案

### 方案选择对比

| 方案 | 适用场景 | 工作量 | 维护成本 | 推荐度 |
|------|---------|--------|---------|-------|
| **A: Web Component** | Vue2/React/Angular/原生JS 全兼容 | 高 (~3天) | 低 (一套代码) | ⭐⭐⭐ 长期最优 |
| **B: Vue 2 子包** | 仅需 Vue 2 + Vue 3 双包 | 中 (~2天) | 高 (两套代码) | ⭐⭐ 快速可用 |
| **C: 保持 iframe** | Vue 2 用户少且不急 | 零 | 零 | ⭐ 最省事 |

### 推荐: 方案 A — Web Component (渐进实施)

```
Phase 2.1: 将 WarmFlowDesigner.vue 包装为 Custom Element (~1天)
  ↓
Phase 2.2: <warm-flow-designer> 标签可在任何框架中使用 (~1天)
  ↓
Phase 2.3: 发布 @warm-flow/designer (框架无关包) (~0.5天)
  ↓
Phase 2.4: 文档迁移引导, Vue 2 用户切换到 WC 方式 (持续)
```

**WC 包装示意:**
```typescript
// src/wc/register.ts
import { defineCustomElement } from 'vue'
import WarmFlowDesigner from './components/WarmFlowDesigner.vue'

const WarmFlowCE = defineCustomElement(WarmFlowDesigner)
customElements.define('warm-flow-designer', WarmFlowCE)

// 使用方式 (任意框架):
// <warm-flow-designer id="xxx" type="design"></warm-flow-designer>
```

### 方案 B 备选: Vue 2 子包 (如急需)

如果短期内就有大量 Vue 2 用户要求 NPM 支持:

```
@warm-flow/vue-designer      → Vue 3 版本 (Phase 1 产物)
@warm-flow/vue2-designer      → Vue 2 版本 (新仓库/分支, 用 vue2 + element-ui 重写视图层)
```

> **注意**: 这意味着需要同时维护 Vue 2 和 Vue 3 两套 UI 层代码, 但 core/(config/request/auth) 业务逻辑层可以共享。

---

## 完整文件变更清单 (按阶段汇总)

### Phase 0 — 新增文件 (5个)

| 文件 | 行数 | 说明 |
|------|------|------|
| `src/core/config.js` | ~70 | 配置中枢: 单例/默认值/合并/Library 检测 |
| `src/core/auth.js` | ~55 | Token 管理: 自定义注入 + localStorage fallback |
| `src/core/request.js` | ~120 | axios 工厂: 动态 baseURL/拦截器/Token 附着 |
| `src/core/store.js` | ~20 | Pinia 多实例管理 |
| `src/core/useAppParams.js` | ~40 | 参数双源: URL + Props 合并 |

### Phase 1 — 新增文件 (7个)

| 文件 | 行数 | 说明 |
|------|------|------|
| `src/composables/useTouchEventBridge.js` | ~250 | 触摸事件桥接 (从视图组件抽取) |
| `src/components/WarmFlowDesigner.vue` | ~60 | 设计器包装组件 |
| `src/components/WarmFlowChart.vue` | ~45 | 流程图包装组件 |
| `src/components/WarmFormDesigner.vue` | ~40 | 表单设计器包装组件 |
| `src/components/WarmFormCreate.vue` | ~40 | 表单审批包装组件 |
| `src/core/install.js` | ~60 | Vue Plugin 安装函数 |
| `src/index.js` | ~50 | NPM 库主入口 |
| `.env.library` | ~5 | Library 构建环境变量 |
| `vite/plugins/svg-inline.js` | ~35 | SVG 内联插件 (Library 模式) |

### Phase 1 — 修改文件 (12个)

| 文件 | 改动量 | 改动类型 |
|------|--------|---------|
| `utils/auth.js` | +3 行 | 增加 re-export |
| `utils/request.js` | +3 行 | 增加 re-export |
| `store/app.js` | +10 行 | fetchTokenName 加参 |
| `store/index.js` | +2 行 | 增加 Pinia 初始化选项 |
| `api/*.js` (约 8 个) | 各 +2 行 | urlPrefix 动态化 |
| `composables/useDark.js` | ±30 行 | 多实例重写 (P8) |
| `plugins/cache.js` | +10 行 | 增加 useCache() export |
| `plugins/modal.js` | +10 行 | 增加 useModal() export |
| `views/flow-design/index.vue` | ±40 行 | C1-C8 解耦 |
| `views/flow-design/flowChart.vue` | ±35 行 | C1-C8 解耦 + 触摸抽取 |
| `views/form-design/index.vue` | ±15 行 | C6/C8 解耦 |
| `views/form-design/formCreate.vue` | ±12 行 | C2/C8 解耦 |
| `src/components/SvgIcon/index.vue` | ±20 行 | SVG fallback 渲染 (P1) |
| `vite.config.js` | ±30 行 | 双模式构建 |
| `vite/plugins/index.js` | ±10 行 | 插件条件化 |
| `package.json` | ±25 行 | NPM 元数据/依赖分离 |

### 不变的文件

- `src/main.js` — SPA 入口完全不动
- `src/router/` — 路由配置不变
- `src/views/error/` — 错误页不变
- `components/design/` — 业务组件内部逻辑不变

---

## 验证检查清单

### Phase 0 验收

- [ ] `src/core/` 目录下 5 个文件创建完成
- [ ] `npm run dev` SPA 模式启动无报错
- [ ] `npm run build:prod` 构建成功
- [ ] 浏览器打开 SPA: 设计器/查看器/表单功能正常

### Phase 1 验收

- [ ] `npm run dev` SPA 模式 — 所有功能与改造前一致
- [ ] `npm run build:prod` SPA 构建 — dist/ 正常输出
- [ ] `npm run build:lib` Library 构建 — dist-lib/ 生成无误
- [ ] dist-lib 包含: es.js + umd.js + style.css
- [ ] 测试项目中 `<WarmFlowDesigner>` 可正常渲染
- [ ] API 请求正确发送到后端 baseURL
- [ ] Token 认证头正确携带
- [ ] save/close 事件正常触发
- [ ] 暗黑模式切换正常
- [ ] 页面放置 2 个设计器互不干扰
- [ ] 宿主已安装 Element Plus 时无双重注册警告
- [ ] 移动端触摸交互正常

### 回滚策略

每个 Phase 单独 commit:
```
commit 1: feat(core): add decoupled layer - config/auth/request/store/appParams
commit 2: refactor: delegate utils/api/store to core layer
commit 3: refactor: extract touch event bridge composable
commit 4: refactor: decouple view components C1-C8
commit 5: feat(components): add wrapper components
commit 6: build: configure dual-mode Vite build
commit 7: feat: add library entry and publish config
```

任意一步出问题可 `git revert <commit>` 回退, 不影响已完成步骤。

---

## 时间估算汇总

| 阶段 | 内容 | 预估工时 | 产出物 |
|------|------|---------|--------|
| **Phase 0** | 核心解耦层 | 2h | 5 个 core/*.js 文件, SPA 零影响 |
| **Step 1.1** | 代码委托到 core/ | 2h | utils/store/api 改造完成 |
| **Step 1.2** | 触摸事件抽取 | 1h | useTouchEventBridge.js |
| **Step 1.3** | 视图组件解耦 | 1.5h | 4 个视图组件双模就绪 |
| **Step 1.4** | 包装组件 | 1h | 4 个 Wrapper *.vue |
| **Step 1.5-1.6** | 构建配置 | 0.5h | vite.config + package.json |
| **Step 1.7-1.10** | 入口 + 验证 | 1h | 可发布的 NPM 包 |
| **总计** | **Phase 0 + 1** | **~9h** | **@warm-flow/vue-designer v1.9.0** |
| **Phase 2** | Vue 2 / WC (后续) | 2-3 天 | 全框架兼容 (按需) |
