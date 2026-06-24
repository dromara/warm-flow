/**
 * Warm-Flow UI 统一主题配置中心
 *
 * Single Source of Truth for all theme colors (light + dark).
 * - CSS 变量 (--wf-*) 用于静态样式
 * - JS 运行时颜色用于 LogicFlow 画布 / 动态内联样式 / computed 样式
 * - 外部可通过 URL 参数 darkColors=#111827 替换暗黑底色（8 个字段）
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
  primary: '#409eff',            // 主题色（按钮/链接/高亮）
  primaryDark: '#2b7de9',        // 主题色深色态（hover/active）
  primaryLight: '#1a3a5c',       // 主题色浅色背景（选中态/标签）
  primaryLighter: '#1a2e4a',     // 主题色更浅背景（表头/次要区域）
  success: '#67c23a',            // 成功状态绿
  warning: '#f56c6c',            // 警告/提示（暗黑下偏红，增强警示感）
  danger: '#f56c6c',             // 危险/错误红

  // ---- 文字颜色 ----
  textPrimary: '#e0e0e0',        // 主文字（标题/正文）
  textRegular: '#b0b0b0',        // 常规文字（说明/描述）
  textSecondary: '#888888',      // 次要文字（辅助信息/占位符）
  textPlaceholder: '#5a5a5a',    // 输入框 placeholder 灰

  // ---- 边框颜色 ----
  borderColor: '#333333',        // 主边框（输入框/面板边框）
  borderLight: '#404040',        // 浅边框（分割线/次要边框）
  borderLighter: '#4a4a4a',      // 更浅边框（卡片内部分隔）

  // ---- 背景颜色 ----
  bgColor: '#141414',            // 全局底色（body/页面主背景）
  bgWhite: '#141414',            // 白色替代（卡片/弹窗背景）
  bgPage: '#141414',             // 页面/画布背景（LogicFlow 画布底色）
  bgContainer: '#141414',        // 容器背景（侧栏/面板容器）

  // ---- LogicFlow 专用 ----
  gridColor: '#404040',          // 画布网格线颜色
  nodeTextColor: '#e0e0e0',      // 节点内文字颜色
  nodeTextFill: '#e0e0e0',       // 节点文字填充色（SVG text fill）
  edgeTextBg: '#141414',         // 连线文字背景（避免遮挡线段）
  snaplineStroke: '#1E90FF',     // 对齐辅助线颜色（拖拽对齐时显示）

  // ---- 特殊组件（Tooltip / Popover） ----
  tooltipBg: '#1f1f1f',          // 弹出层背景
  tooltipBorder: '#333333',      // 弹出层边框
  tooltipShadow: '0 2px 12px rgba(0, 0, 0, 0.4)',  // 弹出层阴影（暗黑加深）
  tooltipColor: '#e0e0e0',       // 弹出层文字颜色

  // ---- 下载快照 ----
  snapshotBg: '#141414',         // 导出快照图片的背景色

  // ---- 侧边栏 ----
  sidebarBg: '#141414',   // 左侧属性栏背景（半透明毛玻璃效果）
  sidebarIconBg: '#141414',                   // 侧边栏图标按钮背景

  // ---- 步骤标签（流程设计步骤条） ----
  stepTabColor: '#b0b0b0',                 // 未激活步骤文字颜色
  stepTabActiveBgStart: '#3b82f6',         // 当前步骤渐变起始色
  stepTabActiveBgEnd: '#2563eb',           // 当前步骤渐变结束色

  // ---- 保存按钮（暗黑降低饱和度） ----
  saveBtnBgStart: '#0d9488',               // 保存按钮渐变起始色（青绿色）
  saveBtnBgEnd: '#0f766e',                 // 保存按钮渐变结束色
  saveBtnShadow: 'rgba(13, 148, 136, 0.25)',  // 保存按钮阴影

  // ---- 流程名称 ----
  flowNameColor: '#e0e0e0',                // 流程名称标题文字颜色

  // ---- 工具栏分隔线 ----
  toolbarBorder: '#333333',                // 顶部工具栏底部边框

  // ---- 滚动条 ----
  scrollbarThumb: '#4a4a4a',              // 滚动块颜色
  scrollbarThumbHover: '#606060',         // 滚动块 hover 颜色

  // ---- Drawer 抽屉 ----
  drawerDisabledBg: '#2a2d35',            // 抽屉禁用态背景色（已发布不可编辑时）
}

/**
 * 暗黑模式默认底色，darkColors 参数传入新值时替换以下 8 个字段：
 *   bgColor / bgWhite / bgPage / bgContainer / edgeTextBg / snapshotBg / sidebarBg / sidebarIconBg
 */
export const DARK_BASE_COLOR = '#141414'

/** 需要被 darkColors 参数替换的字段列表 */
export const DARK_BASE_FIELDS = [
  'bgColor', 'bgWhite', 'bgPage', 'bgContainer',
  'edgeTextBg', 'snapshotBg', 'sidebarBg', 'sidebarIconBg',
]

/**
 * 根据新底色生成覆盖对象
 * 将 8 个暗黑底色字段全部替换为新颜色
 *
 * @param {string} newColor 新的底色值，如 '#111827'
 * @returns {Object} 包含 8 个字段的覆盖对象
 */
export function replaceDarkBaseColor(newColor) {
  if (!newColor || typeof newColor !== 'string') return {}
  const override = {}
  for (const key of DARK_BASE_FIELDS) {
    override[key] = newColor
  }
  return override
}

/**
 * 用户自定义覆盖（运行时可变，仅暗黑模式）
 */
let userOverride = {}

/**
 * 获取合并后的主题颜色
 * @param {'light'|'dark'} mode
 * @param {boolean} [skipMerge=false] 是否跳过用户覆盖（用于获取纯默认值）
 * @returns {Object} 合并后的完整颜色对象
 */
export function getThemeColors(mode, skipMerge = false) {
  const base = mode === 'dark' ? { ...darkColors } : { ...lightColors }
  if (skipMerge || mode !== 'dark' || !userOverride) return base
  return { ...base, ...userOverride }
}

/**
 * 设置外部传入的主题颜色覆盖（由 replaceDarkBaseColor 生成）
 * @param {{dark?: Object}} customColors 覆盖对象，通常为 { dark: replaceDarkBaseColor('#111827') }
 */
export function setCustomThemeColors(customColors) {
  if (!customColors?.dark) return
  userOverride = { ...userOverride, ...customColors.dark }
}

/**
 * 重置用户覆盖为空（恢复全部默认值）
 */
export function resetThemeColors() {
  userOverride = {}
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
 *   replaceDarkBaseColor('#111827')  // 生成覆盖对象
 *   applyCssVariables('dark')        // 立即同步到 CSS
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
