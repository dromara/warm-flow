/**
 * Warm-Flow UI 统一主题配置中心
 *
 * Single Source of Truth for all theme colors (light + dark).
 * - CSS 变量 (--wf-*) 用于静态样式
 * - JS 运行时颜色用于 LogicFlow 画布 / 动态内联样式 / computed 样式
 * - 外部可通过 useDark().setDarkColors(customColors) 覆盖暗黑模式颜色
 *
 * @module themeConfig
 */

// ============================================================
// 亮色模式配色（默认值，与 index.scss :root 保持一致）
// ============================================================
export const lightColors = {
  // ---- 基础调色板 ----
  primary: '#409eff',
  primaryDark: '#2b7de9',
  primaryLight: '#ecf5ff',
  primaryLighter: '#f0f7ff',
  success: '#67c23a',
  warning: '#e6a23c',
  danger: '#f56c6c',

  // ---- 文字颜色 ----
  textPrimary: '#303133',
  textRegular: '#606266',
  textSecondary: '#909399',
  textPlaceholder: '#c0c4cc',

  // ---- 边框颜色 ----
  borderColor: '#dcdfe6',
  borderLight: '#e4e7ed',
  borderLighter: '#ebeef5',

  // ---- 背景颜色 ----
  bgColor: '#f5f7fa',
  bgWhite: '#ffffff',
  bgPage: '#ffffff',         // 页面/画布背景
  bgContainer: '#f5f7fa',    // 容器背景

  // ---- LogicFlow 专用 ----
  gridColor: '#ccc',
  nodeTextColor: '#303133',
  nodeTextFill: '#303133',
  edgeTextBg: '#fff',
  snaplineStroke: '#1E90FF',

  // ---- 特殊组件 ----
  tooltipBg: '#fff',
  tooltipBorder: '#ccc',
  tooltipShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
  tooltipColor: '#333',

  // ---- 下载快照 ----
  snapshotBg: '#f5f5f5',

  // ---- 侧边栏 ----
  sidebarBg: 'rgba(255, 255, 255, 0.95)',
  sidebarIconBg: '#f8fafc',

  // ---- 步骤标签 ----
  stepTabColor: '#64748b',
  stepTabActiveBgStart: '#3b82f6',
  stepTabActiveBgEnd: '#2563eb',

  // ---- 保存按钮 ----
  saveBtnBgStart: '#10b981',
  saveBtnBgEnd: '#059669',
  saveBtnShadow: 'rgba(16, 185, 129, 0.3)',

  // ---- 流程名称 ----
  flowNameColor: '#1e293b',

  // ---- 工具栏分隔线 ----
  toolbarBorder: '#e2e8f0',

  // ---- 滚动条 ----
  scrollbarThumb: '#c0c4cc',
  scrollbarThumbHover: '#909399',

  // ---- Drawer ----
  drawerDisabledBg: '#f5f7fa',
}

// ============================================================
// 暗黑模式配色（默认值，与 index.scss html.dark 保持一致）
// ============================================================
export const darkColors = {
  // ---- 基础调色板 ----
  primary: '#409eff',
  primaryDark: '#2b7de9',
  primaryLight: '#1a3a5c',
  primaryLighter: '#1a2e4a',
  success: '#67c23a',
  warning: '#f56c6c',           // 暗黑下偏红
  danger: '#f56c6c',

  // ---- 文字颜色 ----
  textPrimary: '#e0e0e0',
  textRegular: '#b0b0b0',
  textSecondary: '#888888',
  textPlaceholder: '#5a5a5a',

  // ---- 边框颜色 ----
  borderColor: '#333333',
  borderLight: '#404040',
  borderLighter: '#4a4a4a',

  // ---- 背景颜色 ----
  bgColor: '#141414',
  bgWhite: '#1f1f1f',
  bgPage: '#141414',            // 页面/画布背景
  bgContainer: '#141414',       // 容器背景

  // ---- LogicFlow 专用 ----
  gridColor: '#404040',
  nodeTextColor: '#e0e0e0',
  nodeTextFill: '#e0e0e0',
  edgeTextBg: '#141414',
  snaplineStroke: '#1E90FF',

  // ---- 特殊组件 ----
  tooltipBg: '#1f1f1f',
  tooltipBorder: '#333333',
  tooltipShadow: '0 2px 12px rgba(0, 0, 0, 0.4)',
  tooltipColor: '#e0e0e0',

  // ---- 下载快照 ----
  snapshotBg: '#141414',

  // ---- 侧边栏 ----
  sidebarBg: 'rgba(30, 30, 35, 0.92)',
  sidebarIconBg: '#222',

  // ---- 步骤标签 ----
  stepTabColor: '#b0b0b0',
  stepTabActiveBgStart: '#3b82f6',
  stepTabActiveBgEnd: '#2563eb',

  // ---- 保存按钮（暗黑降低饱和度） ----
  saveBtnBgStart: '#0d9488',
  saveBtnBgEnd: '#0f766e',
  saveBtnShadow: 'rgba(13, 148, 136, 0.25)',

  // ---- 流程名称 ----
  flowNameColor: '#e0e0e0',

  // ---- 工具栏分隔线 ----
  toolbarBorder: '#333333',

  // ---- 滚动条 ----
  scrollbarThumb: '#4a4a4a',
  scrollbarThumbHover: '#606060',

  // ---- Drawer ----
  drawerDisabledBg: '#2a2d35',
}

/**
 * 用户自定义覆盖（运行时可变）
 * 格式：{ dark: { ...partialColors }, light: { ...partialColors } }
 */
let userOverride = { dark: {}, light: {} }

/**
 * 获取合并后的主题颜色
 * @param {'light'|'dark'} mode
 * @param {boolean} [skipMerge=false] 是否跳过用户覆盖（用于获取纯默认值）
 * @returns {Object} 合并后的完整颜色对象
 */
export function getThemeColors(mode, skipMerge = false) {
  const base = mode === 'dark' ? { ...darkColors } : { ...lightColors }
  if (skipMerge || !userOverride || !userOverride[mode]) return base
  return { ...base, ...userOverride[mode] }
}

/**
 * 设置外部传入的主题颜色（部分或全部覆盖）
 * 支持仅传暗黑模式颜色（最常用场景），也可同时传亮色
 *
 * @param {{dark?: Object, light?: Object}} customColors
 *
 * @example
 *   // 仅覆盖暗黑模式的几个关键颜色
 *   setCustomThemeColors({
 *     dark: {
 *       bgColor: '#0a0a0a',
 *       bgWhite: '#1a1a1a',
 *       textPrimary: '#cccccc',
 *     }
 *   })
 *
 * @example
 *   // 完全替换所有暗黑颜色
 *   setCustomThemeColors({ dark: myFullDarkPalette })
 */
export function setCustomThemeColors(customColors) {
  if (!customColors) return
  if (customColors.dark) {
    userOverride.dark = { ...userOverride.dark, ...customColors.dark }
  }
  if (customColors.light) {
    userOverride.light = { ...userOverride.light, ...customColors.light }
  }
}

/**
 * 重置用户覆盖为空（恢复全部默认值）
 */
export function resetThemeColors() {
  userOverride = { dark: {}, light: {} }
}

/**
 * 获取当前用户覆盖（调试用）
 */
export function getUserOverride() {
  return userOverride
}

// ============================================================
// CSS 变量映射表（JS 颜色字段名 → CSS 变量名）
// 用于 applyCssVariables() 将 JS 颜色同步到 DOM CSS 变量
// ============================================================
export const cssVarMap = {
  primary: '--wf-primary',
  primaryDark: '--wf-primary-dark',
  primaryLight: '--wf-primary-light',
  primaryLighter: '--wf-primary-lighter',
  success: '--wf-success',
  warning: '--wf-warning',
  danger: '--wf-danger',

  textPrimary: '--wf-text-primary',
  textRegular: '--wf-text-regular',
  textSecondary: '--wf-text-secondary',
  textPlaceholder: '--wf-text-placeholder',

  borderColor: '--wf-border-color',
  borderLight: '--wf-border-light',
  borderLighter: '--wf-border-lighter',

  bgColor: '--wf-bg-color',
  bgWhite: '--wf-bg-white',
  bgPage: '--wf-bg-page',
  bgContainer: '--wf-bg-container',

  gridColor: '--wf-grid-color',
  nodeTextColor: '--wf-node-text-color',
  nodeTextFill: '--wf-node-text-fill',
  edgeTextBg: '--wf-edge-text-bg',
  snaplineStroke: '--wf-snapline-stroke',

  tooltipBg: '--wf-tooltip-bg',
  tooltipBorder: '--wf-tooltip-border',
  tooltipShadow: '--wf-tooltip-shadow',
  tooltipColor: '--wf-tooltip-color',

  snapshotBg: '--wf-snapshot-bg',

  sidebarBg: '--wf-sidebar-bg',
  sidebarIconBg: '--wf-sidebar-icon-bg',

  stepTabColor: '--wf-step-tab-color',
  stepTabActiveBgStart: '--wf-step-tab-active-bg-start',
  stepTabActiveBgEnd: '--wf-step-tab-active-bg-end',

  saveBtnBgStart: '--wf-save-btn-bg-start',
  saveBtnBgEnd: '--wf-save-btn-bg-end',
  saveBtnShadow: '--wf-save-btn-shadow',

  flowNameColor: '--wf-flow-name-color',

  toolbarBorder: '--wf-toolbar-border',

  scrollbarThumb: '--wf-scrollbar-thumb',
  scrollbarThumbHover: '--wf-scrollbar-thumb-hover',

  drawerDisabledBg: '--wf-disabled-bg',
}

/**
 * 将 JS 颜色对象同步到 DOM CSS 变量（--wf-*）
 * 外部传入自定义颜色后调用此函数，确保 CSS 样式也能感知变化
 *
 * @param {'light'|'dark'} mode 当前主题模式
 * @param {Object} [colors] 颜色对象，不传则自动从 getThemeColors 获取
 *
 * @example
 *   setCustomThemeColors({ dark: { bgColor: '#0a0a0a' } })
 *   applyCssVariables('dark')   // 立即同步到 CSS
 */
export function applyCssVariables(mode, colors) {
  const root = document.documentElement
  const resolved = colors || getThemeColors(mode)
  for (const [jsKey, cssVar] of Object.entries(cssVarMap)) {
    if (resolved[jsKey] !== undefined) {
      root.style.setProperty(cssVar, resolved[jsKey])
    }
  }
}
