import { ref, computed, onMounted, onUnmounted } from 'vue'
import useAppStore from '@/store/app'

// 模块级单例：所有组件共享同一个 isDark 响应式引用
const sharedIsDark = ref(false)

/**
 * 全局暗黑模式 composable
 * 统一管理 isDark 状态、URL 参数初始化、postMessage 监听
 * 消除各页面重复的暗黑模式逻辑
 */
export function useDark() {
  const appParams = computed(() => useAppStore().appParams)

  /** 从 URL 参数初始化主题 */
  function initFromUrl() {
    const urlTheme = appParams.value?.theme
    if (urlTheme === 'theme-dark') {
      sharedIsDark.value = true
      document.documentElement.classList.add('dark')
    } else if (urlTheme === 'theme-light') {
      sharedIsDark.value = false
      document.documentElement.classList.remove('dark')
    }
  }

  /** 监听父页面 postMessage 主题切换 */
  function listeningMessage(e) {
    const { data } = e
    switch (data.type) {
      case 'theme-dark':
        sharedIsDark.value = true
        document.documentElement.classList.add('dark')
        return
      case 'theme-light':
        sharedIsDark.value = false
        document.documentElement.classList.remove('dark')
        return
    }
  }

  /** 应用/取消暗黑主题到 LogicFlow 画布（背景 + 网格 + 边文字） */
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
      edgeText: {
        fontSize: 13,
        strokeWidth: 1,
        background: {
          fill: isDarkMode ? '#141414' : '#fff',
        },
      },
    })
  }

  /** 注册 message 事件监听 + 主动查询父页面主题状态 */
  function setupMessageListener() {
    window.addEventListener('message', listeningMessage)
    // 初始化时检测父页面暗黑模式状态
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
    initFromUrl,
    applyDarkTheme,
    setupMessageListener,
    cleanupMessageListener,
  }
}
