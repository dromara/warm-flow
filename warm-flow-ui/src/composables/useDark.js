import { ref, computed, onMounted, onUnmounted } from 'vue'
import useAppStore from '@/store/app'
import {
  getThemeColors,
  setCustomThemeColors,
  resetThemeColors,
  getUserOverride,
  applyCssVariables,
  lightColors,
  darkColors,
} from '@/config/themeConfig'

// 模块级单例：所有组件共享同一个 isDark 响应式引用
const sharedIsDark = ref(false)

/**
 * 全局暗黑模式 composable
 *
 * 功能：
 * - 统一管理 isDark 状态、URL 参数初始化、postMessage 监听
 * - 从 themeConfig 读取所有颜色（SSOT），消除硬编码
 * - 导出当前主题 colors（响应式），供模板/计算属性使用
 * - 提供 setCustomThemeColors() API，允许外部传入自定义暗黑颜色
 * - 提供 applyDarkTheme() 将颜色应用到 LogicFlow 画布
 */
export function useDark() {
  const appParams = computed(() => useAppStore().appParams)

  // ---- 响应式：根据 isDark 返回当前合并后的颜色 ----
  const themeColors = computed(() =>
    getThemeColors(sharedIsDark.value ? 'dark' : 'light')
  )

  /** 便捷：暗黑模式颜色（常量引用，不响应覆盖） */
  const dark = darkColors

  /** 便捷：亮色模式颜色（常量引用） */
  const light = lightColors

  /** 从 URL 参数初始化主题（支持 darkColors/lightColors JSON 覆盖） */
  function initFromUrl() {
    const params = appParams.value
    const urlTheme = params?.theme
    const isDark = urlTheme === 'theme-dark'
    const mode = isDark ? 'dark' : (urlTheme === 'theme-light' ? 'light' : 'light')

    if (isDark) {
      sharedIsDark.value = true
      document.documentElement.classList.add('dark')
    } else if (urlTheme === 'theme-light') {
      sharedIsDark.value = false
      document.documentElement.classList.remove('dark')
    }

    // 解析外部传入的自定义颜色（JSON 字符串），无自定义时也注入默认颜色到 CSS 变量
    _applyUrlColorParams(params, mode)
    // 始终同步 CSS 变量（SSOT：JS 颜色 → DOM CSS 变量）
    applyCssVariables(mode)
  }

  /**
   * 解析 URL 中的 darkColors / lightColors JSON 参数并应用
   * @private
   */
  function _applyUrlColorParams(params, mode) {
    const colorParam = params?.[mode === 'dark' ? 'darkColors' : 'lightColors']
    if (!colorParam) return

    let parsed
    try {
      parsed = typeof colorParam === 'string' ? JSON.parse(colorParam) : colorParam
    } catch (e) {
      console.warn(`[useDark] Invalid ${mode}Colors param:`, colorParam)
      return
    }

    if (parsed && typeof parsed === 'object') {
      setCustomThemeColors({ [mode]: parsed })
      applyCssVariables(mode)
    }
  }

  /** 监听父页面 postMessage 主题切换（支持携带自定义颜色） */
  function listeningMessage(e) {
    const { data } = e
    switch (data.type) {
      case 'theme-dark':
        sharedIsDark.value = true
        document.documentElement.classList.add('dark')
        _applyMessageColorParams(data, 'dark')
        applyCssVariables('dark')
        return
      case 'theme-light':
        sharedIsDark.value = false
        document.documentElement.classList.remove('dark')
        _applyMessageColorParams(data, 'light')
        applyCssVariables('light')
        return
    }
  }

  /**
   * 处理 postMessage 中附带的自定义颜色
   * @private
   */
  function _applyMessageColorParams(data, mode) {
    // 支持 data.darkColors / data.lightColors 或 data.colors（兼容两种写法）
    const customColors = data[mode + 'Colors'] || data.colors
    if (!customColors || typeof customColors !== 'object') return

    setCustomThemeColors({ [mode]: customColors })
    applyCssVariables(mode)
  }

  /**
   * 应用/取消暗黑主题到 LogicFlow 画布（背景 + 网格 + 边文字）
   * 所有颜色均从 themeConfig 读取，不再硬编码
   */
  function applyDarkTheme(lfInstance, isDarkMode) {
    if (!lfInstance) return
    const colors = getThemeColors(isDarkMode ? 'dark' : 'light')
    lfInstance.graphModel.background = {
      background: colors.bgPage,
    }
    if (lfInstance.graphModel.grid) {
      lfInstance.graphModel.grid.config = {
        ...lfInstance.graphModel.grid.config,
        color: colors.gridColor,
      }
    }
    lfInstance.setTheme({
      snapline: {
        stroke: colors.snaplineStroke,
        strokeWidth: 2,
      },
      nodeText: {
        color: colors.nodeTextColor,
        fill: colors.nodeTextFill,
        fontSize: 13,
        fontWeight: 500,
      },
      edgeText: {
        fontSize: 13,
        strokeWidth: 1,
        background: {
          fill: colors.edgeTextBg,
        },
      },
    })
  }

  /** 注册 message 事件监听 + 主动查询父页面主题状态 */
  function setupMessageListener() {
    window.addEventListener('message', listeningMessage)
    if (window.parent !== window) {
      window.parent.postMessage({ method: 'getTheme' }, '*')
    }
  }

  /** 清理 message 事件监听 */
  function cleanupMessageListener() {
    window.removeEventListener('message', listeningMessage)
  }

  return {
    isDark: sharedIsDark,
    themeColors,       // 响应式：当前模式的完整颜色对象（含用户覆盖）
    dark,              // 常量：默认暗黑配色
    light,             // 常量：默认亮色配色
    initFromUrl,
    applyDarkTheme,
    setupMessageListener,
    cleanupMessageListener,
    // 外部 API：传入自定义颜色
    setCustomThemeColors,
    resetThemeColors,
    getUserOverride,
    getThemeColors,
  }
}

// 导出非响应式的默认颜色常量（供非 Vue 上下文使用）
export { lightColors, darkColors }
