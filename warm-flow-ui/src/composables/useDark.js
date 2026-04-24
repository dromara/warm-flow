import { ref, computed, onMounted, onUnmounted } from 'vue'
import useAppStore from '@/store/app'
import {
  getThemeColors,
  setCustomThemeColors,
  resetThemeColors,
  getUserOverride,
  applyCssVariables,
  replaceDarkBaseColor,
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

  /** 从 URL 参数初始化主题（支持 darkColors 底色替换） */
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

    // 解析 darkColors 参数（如 darkColors=111827），替换 8 个暗黑底色字段
    _applyUrlColorParams(params, mode)
    // 始终同步 CSS 变量（SSOT：JS 颜色 → DOM CSS 变量）
    applyCssVariables(mode)
  }

  /**
   * 解析 URL 中的 darkColors 参数
   * 格式：darkColors=111827（不带#号），替换 8 个底色字段
   * @private
   */
  function _applyUrlColorParams(params, mode) {
    const colorParam = params?.darkColors
    if (!colorParam || mode !== 'dark') return

    // 接受不带#号的十六进制颜色值，如 111827
    if (typeof colorParam === 'string' && /^[0-9a-fA-F]{3,8}$/.test(colorParam)) {
      const hex = '#' + colorParam
      setCustomThemeColors({ dark: replaceDarkBaseColor(hex) })
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
   * 处理 postMessage 中附带的 darkColors 参数
   * 格式：darkColors: '111827'（不带#号），替换 8 个底色字段
   * @private
   */
  function _applyMessageColorParams(data, mode) {
    const colorValue = data.darkColors
    if (!colorValue || mode !== 'dark') return

    if (typeof colorValue === 'string' && /^[0-9a-fA-F]{3,8}$/.test(colorValue)) {
      const hex = '#' + colorValue
      setCustomThemeColors({ dark: replaceDarkBaseColor(hex) })
      applyCssVariables(mode)
    }
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
